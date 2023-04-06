package ilda;

import java.util.List;

/**
 * Optimises a frame or frame segments according to its OptimisationSettings.
 */
public class Optimiser {

    private OptimisationSettings settings;

    public Optimiser(OptimisationSettings settings) {
        this.settings = settings;
    }

    public OptimisationSettings getSettings() {
        return settings;
    }

    public void setSettings(OptimisationSettings settings) {
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

    public List<IldaPoint> optimiseSegment(List<IldaPoint> points) {
        float maxDistBlankSQ = settings.maxDistBlank * settings.maxDistBlank;
        float maxDistLitSQ = settings.maxDistLit * settings.maxDistLit;
        if (settings.isClippingEnabled()) {clip(points);}
        for (int i = points.size() - 2; i >= 1; i--) {
            IldaPoint previousPoint = points.get(i + 1);
            IldaPoint point = points.get(i);
            IldaPoint nextPoint = points.get(i - 1);

            float distancePreviousSQ = calculateDistanceSquared(previousPoint, point);

            if (settings.isReduceData() && arePointsCollinear(previousPoint, point, nextPoint)) {
                points.remove(point);
                continue;
            }
            interpolate(points, maxDistBlankSQ, maxDistLitSQ, i, previousPoint, point,
                distancePreviousSQ);
            if (settings.isAngleDwell()) {
                addAngleDwellPoints(points, i, previousPoint, point, nextPoint, distancePreviousSQ);
            }

        }

        return points;
    }

    private void clip(List<IldaPoint> points) {
        float[] clipBounds = settings.getClipBounds();
        if (points.size() < 2) {return;}
        for (int index = 0; index < points.size() - 1; index++) {
            IldaPoint point = points.get(index);
            IldaPoint nextPoint = points.get(index + 1);

        }

    }

    private boolean arePointsCollinear(IldaPoint previousPoint, IldaPoint point,
        IldaPoint nextPoint) {
        // Find slope of the two lines between the three points and if they are (about) equal,
        // they're collinear
        boolean collinear = false;
        return collinear;
    }

    private void interpolate(List<IldaPoint> points, float maxDistBlankSQ, float maxDistLitSQ,
        int i,
        IldaPoint previousPoint, IldaPoint point, float distancePreviousSQ) {
        if (settings.interpolateBlanked && previousPoint.blanked && point.blanked &&
            distancePreviousSQ > maxDistBlankSQ
            || settings.interpolateLit && !previousPoint.blanked && !point.blanked &&
               distancePreviousSQ > maxDistLitSQ) {
            double dist = Math.sqrt(distancePreviousSQ);
            double maxDist = previousPoint.blanked ? settings.maxDistBlank : settings.maxDistLit;
            int addedPoints = (int) (dist / maxDist);

            for (int j = 0; j <= addedPoints; j++) {
                IldaPoint newPoint = new IldaPoint(previousPoint);
                float factor = (float) (1 - (dist - j * maxDist) / dist);
                newPoint.setPosition(
                    previousPoint.getX() + (point.getX() - previousPoint.getX()) * factor,
                    previousPoint.getY() + (point.getY() - previousPoint.getY()) * factor,
                    previousPoint.getZ() + (point.getZ() - previousPoint.getZ()) * factor);
                points.add(i + 1, newPoint);
            }
        }
    }

    private void addAngleDwellPoints(List<IldaPoint> points, int i, IldaPoint previousPoint,
        IldaPoint point,
        IldaPoint nextPoint, float distancePreviousSQ) {
        if (nextPoint == null) {return;}
        float distanceNextSQ = calculateDistanceSquared(point, nextPoint);
        float distancePreviousNextSQ = calculateDistanceSquared(previousPoint, nextPoint);
        float factor = (distancePreviousSQ + distanceNextSQ) / distancePreviousNextSQ;
        int dwellPoints = (int) (settings.angleDwellFactor * factor);
        for (int dwellPoint = 0; dwellPoint < dwellPoints; dwellPoint++) {
            points.add(i, new IldaPoint(point));
        }

    }

    private float calculateDistanceSquared(IldaPoint point1, IldaPoint point2) {
        return (point1.getX() - point2.getX()) * (point1.getX() - point2.getX())
               + (point1.getY() - point2.getY()) * (point1.getY() - point2.getY())
               + (point1.getZ() - point2.getZ()) * (point1.getZ() - point2.getZ());
    }

}
