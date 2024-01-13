package be.cmbsoft.ilda;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.Math.max;
import static java.lang.Math.min;
import processing.core.PVector;

/**
 * Optimises a frame or frame segments according to its OptimisationSettings.
 */
public class Optimiser
{

    private OptimisationSettings settings;

    public Optimiser(OptimisationSettings settings)
    {
        this.settings = settings;
    }

    public OptimisationSettings getSettings()
    {
        return settings;
    }

    public void setSettings(OptimisationSettings settings)
    {
        this.settings = settings;
    }

    /**
     * Optimise a list of points according to the settings loaded from the OptimisationSettings
     * object. Note that this
     * will not make a copy of the points, the original points might be adjusted and points can
     * be removed/added to the
     * list! If the original points need to be preserved, make a hard copy first.
     *
     * @param points some points that need to learn how to behave
     * @return the same list of points that should now be safer to scan - no guarantees!
     */

    public List<IldaPoint> optimiseSegment(List<IldaPoint> points)
    {

        if (settings.isClippingEnabled())
        {
            List<IldaPoint> clipped = clip(points);
            points.clear();
            points.addAll(clipped);
        }
        if (settings.interpolateBlanked || settings.interpolateLit)
        {
            float           maxDistBlankSQ = settings.maxDistBlank * settings.maxDistBlank;
            float           maxDistLitSQ   = settings.maxDistLit * settings.maxDistLit;
            List<IldaPoint> interpolated   = interpolate(points, maxDistBlankSQ, maxDistLitSQ);
            points.clear();
            points.addAll(interpolated);
        }
        if (settings.blankDwell)
        {
            List<IldaPoint> dwelled = dwell(points);
            points.clear();
            points.addAll(dwelled);
        }

        return points;
    }

    private List<IldaPoint> dwell(List<IldaPoint> points)
    {
        List<IldaPoint> output = new ArrayList<>();
        for (int i = 0; i < points.size() - 1; i++)
        {
            IldaPoint point     = points.get(i);
            IldaPoint nextPoint = points.get(i + 1);
            if (i == 0)
            {
                addBlankedPoints(point, output, settings.blankDwellAmount);
                if (!point.blanked)
                {
                    output.add(point);
                }
            }
            else if (point.blanked != nextPoint.blanked)
            {
                if (!point.blanked)
                {
                    output.add(point);
                }
                addBlankedPoints(point, output, settings.blankDwellAmount);
            }
            else
            {
                output.add(point);
            }
        }
        if (points.size() > 1)
        {
            output.add(points.get(points.size() - 1));
        }
        return output;
    }

    private void addBlankedPoints(IldaPoint originalPoint, List<IldaPoint> output, int amount)
    {
        IldaPoint copy = new IldaPoint(originalPoint);
        copy.setBlanked(true);
        for (int i = 0; i < amount; i++)
        {
            output.add(copy);
        }
    }

    private List<IldaPoint> interpolate(List<IldaPoint> points, float maxDistBlankSQ, float maxDistLitSQ)
    {
        List<IldaPoint> output = new ArrayList<>();
        if (points.size() > 1) {
            output.add(points.get(points.size() - 1));
        }
        for (int i = points.size() - 2; i >= 1; i--)
        {
            IldaPoint previousPoint = points.get(i + 1);
            IldaPoint point         = points.get(i);

            float distancePreviousSQ = calculateDistanceSquared(previousPoint, point);
            if (settings.interpolateBlanked && previousPoint.blanked && point.blanked &&
                distancePreviousSQ > maxDistBlankSQ
                || settings.interpolateLit && !previousPoint.blanked && !point.blanked &&
                distancePreviousSQ > maxDistLitSQ)
            {
                double dist        = Math.sqrt(distancePreviousSQ);
                double maxDist     = previousPoint.blanked ? settings.maxDistBlank : settings.maxDistLit;
                int    addedPoints = (int) (dist / maxDist);

                for (int j = 0; j <= addedPoints; j++)
                {
                    IldaPoint newPoint = new IldaPoint(previousPoint);
                    float     factor   = (float) (1 - (dist - j * maxDist) / dist);
                    newPoint.setPosition(
                        previousPoint.getX() + (point.getX() - previousPoint.getX()) * factor,
                        previousPoint.getY() + (point.getY() - previousPoint.getY()) * factor,
                        previousPoint.getZ() + (point.getZ() - previousPoint.getZ()) * factor);
                    output.add(i + 1, newPoint);
                }
            }
            output.add(point);

        }
        if (!points.isEmpty()) {
            output.add(points.get(0));
        }

        return output;
    }

    private List<IldaPoint> clip(List<IldaPoint> points)
    {
        List<IldaPoint> output     = new ArrayList<>();
        float[]         clipBounds = settings.getClipBounds();
        if (clipBounds.length != 4)
        {
            throw new IllegalStateException("Invalid clipping bounds");
        }
        float left  = clipBounds[0];
        float up    = clipBounds[1];
        float right = clipBounds[2];
        float down  = clipBounds[3];
        for (int index = 0; index < points.size() - 1; index++)
        {
            IldaPoint point = points.get(index);
            IldaPoint nextPoint = points.get(index + 1);

            PVector position     = point.position;
            PVector nextPosition = nextPoint.position;

            boolean in     = isIn(position, left, up, right, down);
            boolean nextIn = isIn(nextPosition, left, up, right, down);
            if (in)
            {
                // Always add points in the canvas
                output.add(point);

                if (!nextIn && !nextPoint.blanked)
                {
                    // Add an extra point at the location where it goes out of the canvas. This point should be lit.
                    addIntersectingPoint(output, left, up, right, down, nextPoint, position, nextPosition, false);
                }
            }
            if (!in)
            {
                // The next point can be in, or the line can go through the canvas in which case we do need to add a
                // new point. Though, the current point should not be added!
                if (nextIn && !nextPoint.blanked)
                {
                    // Add an extra point at the location where it goes out of the canvas. This point should be blanked.
                    addIntersectingPoint(output, left, up, right, down, nextPoint, position, nextPosition, true);
                }
                else
                {
                    // Verify the line connecting the two out-of-bounds points goes through the canvas. In this edge
                    // case, we need to add two new points! The first one blanked, the second one not.
                    addTwoIntersectingPoints(output, left, up, right, down, point, position, nextPosition);
                }
            }

        }
        processLastPoint(points, output, left, up, right, down);
        return output;
    }

    private void processLastPoint(List<IldaPoint> points, List<IldaPoint> output, float left, float up, float right,
        float down)
    {
        // Don't forget the last point
        if (!points.isEmpty())
        {
            IldaPoint lastPoint = points.get(points.size() - 1);
            if (lastPoint != null && !lastPoint.blanked && isIn(lastPoint.position, left, up, right, down))
            {
                output.add(lastPoint);
            }
        }
    }

    private void addTwoIntersectingPoints(List<IldaPoint> output, float left, float up, float right, float down,
        IldaPoint point, PVector position, PVector nextPosition)
    {

        List<PVector> allIntersections = findAllIntersections(position, nextPosition, left, up, right, down);
        if (allIntersections.size() == 2)
        {
            PVector firstIntersection  = allIntersections.get(0);
            PVector secondIntersection = allIntersections.get(1);
            // Create the point closest to the first point first to preserve animation order
            float distToFirst  = firstIntersection.dist(position);
            float distToSecond = secondIntersection.dist(position);
            if (distToFirst < distToSecond)
            {
                addPoint(output, point, true, firstIntersection);
                addPoint(output, point, false, secondIntersection);
            }
            else
            {
                addPoint(output, point, true, secondIntersection);
                addPoint(output, point, false, firstIntersection);
            }
        }
    }

    private void addIntersectingPoint(List<IldaPoint> output, float left, float up, float right, float down,
        IldaPoint nextPoint, PVector position, PVector nextPosition, boolean blanked)
    {
        PVector intersection = findSingleIntersection(position, nextPosition, left, up, right, down);
        if (intersection != null)
        {
            // Mathematically, intersection should never be null here.
            addPoint(output, nextPoint, blanked, intersection);
        }
    }

    private void addPoint(List<IldaPoint> output, IldaPoint point, boolean blanked, PVector newPointPosition)
    {
        IldaPoint newPoint = new IldaPoint(point);
        newPoint.setPosition(newPointPosition.x, newPointPosition.y, newPoint.getZ());
        newPoint.setBlanked(blanked);
        output.add(newPoint);
    }

    private List<PVector> findAllIntersections(PVector position, PVector nextPosition, float left, float up,
        float right, float down)
    {
        List<PVector> result = new ArrayList<>();
        result.add(findIntersection(position.x, position.y, nextPosition.x, nextPosition.y, left, down, right, down));
        result.add(findIntersection(position.x, position.y, nextPosition.x, nextPosition.y, right, up, right, down));
        result.add(findIntersection(position.x, position.y, nextPosition.x, nextPosition.y, left, up, right, up));
        result.add(findIntersection(position.x, position.y, nextPosition.x, nextPosition.y, left, up, left, down));
        return result.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    private PVector findSingleIntersection(PVector position, PVector nextPosition, float left, float up, float right,
        float down)
    {
        // Find the intersection between the line segment between position and nextPosition, and the four edges

        // left edge
        PVector result = findIntersection(position.x, position.y, nextPosition.x, nextPosition.y, left, up, left, down);
        if (result == null)
        {
            //upper edge edge
            result = findIntersection(position.x, position.y, nextPosition.x, nextPosition.y, left, up, right, up);
        }
        if (result == null)
        {
            // right edge
            result = findIntersection(position.x, position.y, nextPosition.x, nextPosition.y, right, up, right, down);
        }
        if (result == null)
        {
            // lower edge
            result = findIntersection(position.x, position.y, nextPosition.x, nextPosition.y, left, down, right, down);
        }
        return result;
    }

    private PVector findIntersection(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4)
    {
        PVector result = null;

        // First line: a1x + b1y = c1
        double a1 = y2 - y1;
        double b1 = x1 - x2;
        double c1 = a1 * x1 + b1 * y1;

        // Second line: a2x + b2y = c2
        double a2 = y4 - y3;
        double b2 = x3 - x4;
        double c2 = a2 * x3 + b2 * y3;

        double determinant = a1 * b2 - a2 * b1;
        if (determinant == 0)
        {
            return null;
        }
        double intersectX = (b2 * c1 - b1 * c2) / determinant;
        double intersectY = (a1 * c2 - a2 * c1) / determinant;

        // Verify point is in the line segments
        if (min(x1, x2) <= intersectX && intersectX <= max(x1, x2)
            && min(x3, x4) <= intersectX && intersectX <= max(x3, x4)
            && min(y1, y2) <= intersectY && intersectY <= max(y1, y2)
            && min(y3, y4) <= intersectY && intersectY <= max(y3, y4))
        {
            result = new PVector((float) intersectX, (float) intersectY);
        }
        return result;
    }

    private boolean isIn(PVector position, float left, float up, float right, float down)
    {
        float x = position.x;
        float y = position.y;
        return x >= left && x <= right && y <= up && y >= down;
    }

    private void addAngleDwellPoints(List<IldaPoint> points, int i, IldaPoint previousPoint,
        IldaPoint point,
        IldaPoint nextPoint, float distancePreviousSQ)
    {
        if (nextPoint == null) {return;}
        float distanceNextSQ         = calculateDistanceSquared(point, nextPoint);
        float distancePreviousNextSQ = calculateDistanceSquared(previousPoint, nextPoint);
        float factor                 = (distancePreviousSQ + distanceNextSQ) / distancePreviousNextSQ;
        int   dwellPoints            = (int) (settings.angleDwellFactor * factor);
        for (int dwellPoint = 0; dwellPoint < dwellPoints; dwellPoint++)
        {
            points.add(i, new IldaPoint(point));
        }

    }

    private float calculateDistanceSquared(IldaPoint point1, IldaPoint point2)
    {
        return (point1.getX() - point2.getX()) * (point1.getX() - point2.getX())
            + (point1.getY() - point2.getY()) * (point1.getY() - point2.getY())
            + (point1.getZ() - point2.getZ()) * (point1.getZ() - point2.getZ());
    }

}
