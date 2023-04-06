package ilda;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import processing.core.PApplet;
import processing.core.PGraphics;

/**
 * A frame is essentially a collection of points, and some metadata. It can render its geometry
 * to a PGraphics.
 */
public class IldaFrame {
    final List<IldaPoint> points = new ArrayList<>();
    protected int ildaVersion = 4;
    protected String frameName = "";
    protected String companyName = "Processing";
    protected int pointCount;
    protected int frameNumber;
    protected int totalFrames;
    protected int scannerHead;
    protected boolean palette = false;

    /**
     * Fixes the frame headers eg. updates point count, frame number, total frames, ... It leaves
     * the frame name and
     * company name untouched. It assumes the frames form a complete sequence.
     *
     * @param frames A reference to the frames whose headers need to get fixed.
     */

    public static void fixHeaders(List<IldaFrame> frames) {
        fixHeaders(frames.toArray(new IldaFrame[0]));
    }

    /**
     * Fixes the frame headers eg.updates point count, frame number, total frames It sets the
     * frame name and company
     * name to the arguments you gave it. It assumes the frames form a complete sequence (for the
     * total frame entry).
     * Call this before writing to an ilda file
     *
     * @param frames      A reference to the frames whose headers need to get fixed.
     * @param frameName   A name you want to give the frame
     * @param companyName Another name
     */

    public static void fixHeaders(List<IldaFrame> frames, String frameName, String companyName) {
        int i = 1;
        for (IldaFrame frame : frames) {

            fixHeader(frame, i++, frames.size(), frameName, companyName);
        }
    }

    /**
     * Static version of fixHeader() See documentation there
     */

    public static void fixHeader(IldaFrame frame, int frameNumber, int totalFrames,
        String frameName, String companyName) {
        frame.frameNumber = frameNumber;
        frame.totalFrames = totalFrames;
        frame.pointCount = frame.points.size();
        frame.frameName = frameName;
        frame.companyName = companyName;
    }

    public static void fixHeaders(IldaFrame[] frames) {
        int i = 1;
        for (IldaFrame frame : frames) {
            fixHeader(frame, i++, frames.length, frame.frameName, frame.companyName);
        }
    }

    public void addPoint(IldaPoint point) {
        if (point != null) {points.add(point);}
    }

    public PGraphics renderFrame(PApplet parent) {
        return renderFrame(parent, parent.width, parent.height);
    }

    public PGraphics renderFrame(PApplet parent, int sizex, int sizey) {
        return renderFrame(parent, true, sizex, sizey);
    }

    public PGraphics renderFrame(PGraphics pg, boolean showBlanking, int sizex, int sizey) {
        return renderFrame(pg, showBlanking, sizex, sizey, 0, 0, 0);
    }

    public PGraphics renderFrame(PApplet parent, boolean showBlanking, int sizex, int sizey) {
        return renderFrame(parent.g, showBlanking, sizex, sizey, 0, 0, 0);
    }

    public PGraphics renderFrame(PGraphics pg, boolean showBlanking) {
        return renderFrame(pg, showBlanking, pg.width, pg.height, 0, 0, 0);
    }

    public PGraphics renderFrame(PGraphics pg, boolean showBlanking, float rotx, float roty,
        float rotz) {
        return renderFrame(pg, showBlanking, pg.width, pg.height, rotx, roty, rotz);
    }

    public PGraphics renderFrame(PApplet parent, boolean showBlanking) {
        return renderFrame(parent, showBlanking, parent.width, parent.height);
    }

    public PGraphics renderFrame(PApplet parent, boolean showBlanking, float rotx, float roty,
        float rotz) {
        return renderFrame(parent.g, showBlanking, rotx, roty, rotz);
    }

    public void palettePaint(IldaPalette palette) {
        for (IldaPoint point : points) {
            point.colour = palette.getColour(point.palIndex);
        }
    }

    public int getIldaVersion() {
        return ildaVersion;
    }

    public String getFrameName() {
        return frameName;
    }

    public void setFrameName(String frameName) {
        this.frameName = frameName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getPointCount() {
        return points.size();
    }

    public int getFrameNumber() {
        return frameNumber;
    }

    public void setFrameNumber(int frameNumber) {
        this.frameNumber = frameNumber;
    }

    public int getTotalFrames() {
        return totalFrames;
    }

    public void setTotalFrames(int totalFrames) {
        this.totalFrames = totalFrames;
    }

    public int getScannerHead() {
        return scannerHead;
    }

    public void setScannerHead(int scannerHead) {
        this.scannerHead = scannerHead;
    }

    public boolean isPalette() {
        return palette;
    }

    public void setPalette(boolean palette) {
        this.palette = palette;
    }

    /**
     * Fix the header of the current frame
     *
     * @param frameNumber index of the frame in the animation
     * @param totalFrames total frames in the animation
     * @param frameName   name of the frame
     * @param companyName name of the owner/program/company that owns or created the frame
     */

    public void fixHeader(int frameNumber, int totalFrames, String frameName, String companyName) {
        fixHeader(this, frameNumber, totalFrames, frameName, companyName);
    }

    /**
     * Set the ilda version this frame uses. 0 = 3D, palette 1 = 2D, palette 4 = 3D, RGB 5 = 3D,
     * RGB Internally, all
     * frames are 3D and use RGB. This just stores the version number as a variable, so it can be
     * resaved in the same
     * format if required.
     *
     * @param versionNumber integer, can be 0, 1, 4 or 5
     */

    public void setIldaFormat(int versionNumber) {
        if (versionNumber != 0 && versionNumber != 1 && versionNumber != 4 && versionNumber != 5) {
            throw new IllegalArgumentException("Unsupported ILDA format " + versionNumber);
        } else {ildaVersion = versionNumber;}
    }

    /**
     * Renders the frame to a PGraphics to be displayed in the sketch. The PGraphics should be 3D
     * a 2D version might get
     * implemented You must call beginDraw() and endDraw() yourself!
     *
     * @param pg           A reference to the PGraphics (it can't generate its own as this
     *                     usually results in memory
     *                     leaks)
     * @param showBlanking Should blanking lines be displayed?
     * @param sizex        Size of the PGraphics element it returns
     * @param sizey
     * @param rotx         Rotation of the frame
     * @param roty
     * @param rotz
     * @return a PGraphics with the frame drawn
     */

    public PGraphics renderFrame(PGraphics pg, boolean showBlanking, int sizex, int sizey,
        float rotx, float roty, float rotz) {

        if (pg.is2D()) {
            //For now...
            pg.text("2D frame", 1, 15);
            pg.endDraw();
            return pg;
        }

        pg.ortho();
        pg.pushMatrix();

        pg.translate((sizex * 0.5f), (float) (sizey * 0.5), (float) ((sizex + sizey) * 0.25));
        pg.rotateX(rotx);
        pg.rotateY(roty);
        pg.rotateZ(rotz);
        pg.translate((float) (-sizex * 0.5), (float) (-sizey * 0.5),
            (float) (-(sizex + sizey) * 0.25));

        if (!points.isEmpty()) {
            boolean firstPoint = true;
            float oldpositionx = 0;
            float oldpositiony = 0;
            float oldpositionz = 0;
            for (IldaPoint point : points) {
                float pointx = (point.getX() + 1) * sizex * 0.5f;
                float pointy = (point.getY() + 1) * sizey * 0.5f;
                float pointz = (point.getZ() + 1) * (sizex + sizey) * 0.25f;
                if (showBlanking || !point.blanked) {
                    pg.strokeWeight(3);
                    //pg.stroke(point.colour); //??? y u no work ლ(ಠ益ಠლ)
                    pg.stroke((point.colour >> 16) & 0xFF, (point.colour >> 8) & 0xFF,
                        point.colour & 0xFF);
                    if (point.blanked) {
                        pg.stroke(75);
                    }
                    pg.point(pointx, pointy, pointz);
                }

                if (!firstPoint) {
                    pg.strokeWeight(1);
                    if (!showBlanking && point.blanked) {pg.stroke(0);} else {
                        pg.line(pointx, pointy, pointz, oldpositionx, oldpositiony, oldpositionz);
                    }
                } else {
                    firstPoint = false;
                }
                oldpositionx = pointx;
                oldpositiony = pointy;
                oldpositionz = pointz;
            }
        }

        pg.popMatrix();
        return pg;
    }

    public List<IldaPoint> getPoints() {
        return points;
    }

    public List<IldaPoint> getCopyOnWritePoints() {
        return new CopyOnWriteArrayList<>(points);
    }

    @Override
    public String toString() {
        return "[" + frameName + "] - points: " + points.size();
    }

}
