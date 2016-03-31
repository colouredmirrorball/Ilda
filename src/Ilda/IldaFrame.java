package Ilda;

import processing.core.PApplet;
import processing.core.PGraphics;

import java.util.ArrayList;

/**
 * A frame is essentially a collection of points.
 */
public class IldaFrame {
    ArrayList<IldaPoint> points = new ArrayList<IldaPoint>();
    //The Points in the Frame


    protected int ildaVersion = 4;    //Data retrieved from header

    public void setFrameName(String frameName) {
        this.frameName = frameName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setFrameNumber(int frameNumber) {
        this.frameNumber = frameNumber;
    }

    public void setTotalFrames(int totalFrames) {
        this.totalFrames = totalFrames;
    }

    public void setScannerHead(int scannerHead) {
        this.scannerHead = scannerHead;
    }

    public void setPalette(boolean palette) {
        this.palette = palette;
    }

    protected String frameName = "";
    protected String companyName = "Processing";
    protected int pointCount;
    protected int frameNumber;
    protected int totalFrames;
    protected int scannerHead;
    protected boolean palette = false;


    /*Ilda frame*/
    public IldaFrame() {
    }

    /**
     * Set the Ilda version this frame uses.
     * 0 = 3D, palette
     * 1 = 2D, palette
     * 4 = 3D, RGB
     * 5 = 3D, RGB
     * Internally, all frames are 3D and use RGB.
     *
     * @param versionNumber integer, can be 0, 1, 4 or 5
     * @throws IllegalArgumentException
     */

    public void setIldaFormat(int versionNumber) throws IllegalArgumentException {


        if (versionNumber != 0 && versionNumber != 1 && versionNumber != 4 && versionNumber != 5) {
            throw new IllegalArgumentException("Unsupported ILDA format " + versionNumber);
        } else ildaVersion = versionNumber;
    }

    public void addPoint(IldaPoint point) {
        if (point != null) points.add(point);
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

    public PGraphics renderFrame(PGraphics pg, boolean showBlanking, float rotx, float roty, float rotz) {
        return renderFrame(pg, showBlanking, pg.width, pg.height, rotx, roty, rotz);
    }

    public PGraphics renderFrame(PApplet parent, boolean showBlanking, float rotx, float roty, float rotz) {
        return renderFrame(parent.g, showBlanking, rotx, roty, rotz);
    }

    public PGraphics renderFrame(PApplet parent, boolean showBlanking) {
        return renderFrame(parent, showBlanking, parent.width, parent.height);
    }


    /**
     * Renders a PGraphics
     * The PGraphics should be 3D
     * a 2D version might get implemented
     * beginDraw() and endDraw() should always have been called!
     *
     * @param pg           A reference to the PGraphics (it can't generate its own as this usually results in memory leaks)
     * @param showBlanking Should blanking lines be displayed?
     * @param sizex        Size of the PGraphics element it returns
     * @param sizey
     * @param rotx         Rotation of the frame
     * @param roty
     * @param rotz
     * @return a PGraphics with the frame drawn
     */

    public PGraphics renderFrame(PGraphics pg, boolean showBlanking, int sizex, int sizey, float rotx, float roty, float rotz) {


        //pg.beginDraw();
        //parent.println("Began drawing frame " + frameName);


        pg.background(0, 0);

        if (pg.is2D()) {
            //For now...
            pg.text("2D frame", 1, 15);
            pg.endDraw();
            return pg;
        }
/*
//Set half of the pixels red just to make sure something is drawn to the PGraphics...
        pg.loadPixels();
        for (int i = 0; i < pg.pixels.length * 0.5; i++) {
            pg.pixels[i] = pg.color(255, 0, 0);
        }
        pg.updatePixels();
*/
        //pg.frustum(-sizex, sizex, sizey, -sizey, (sizex + sizey)*0.5f, 0);

        //Nop. You are getting orthogonal projection for now.
       // pg.ortho(0, sizex, sizey, 0, 0, sizex + sizey);
        pg.ortho();
        pg.pushMatrix();

        pg.translate((sizex * 0.5f), (float) (sizey * 0.5), (float) ((sizex + sizey) * 0.25));
        pg.rotateX(rotx);
        pg.rotateY(roty);
        pg.rotateZ(rotz);
        pg.translate((float) (-sizex * 0.5), (float) (-sizey * 0.5), (float) (-(sizex + sizey) * 0.25));


        if (points.size() > 0) {
            boolean firstPoint = true;
            float oldpositionx = 0;
            float oldpositiony = 0;
            float oldpositionz = 0;
            for (IldaPoint point : points) {
                float pointx = (point.x + 1) * sizex * 0.5f;
                float pointy = (point.y + 1) * sizey * 0.5f;
                float pointz = (point.z + 1) * (sizex + sizey) * 0.25f;
                if (showBlanking || !point.blanked) {
                    pg.strokeWeight(3);
                    //pg.stroke(point.colour); //??? y u no work ლ(ಠ益ಠლ)
                    pg.stroke((point.colour >> 16) & 0xFF, (point.colour >> 8) & 0xFF, point.colour & 0xFF);
                    if (point.blanked) {
                        pg.stroke(75);
                    }
                    pg.point(pointx, pointy, pointz);
                }


                if (!firstPoint) {
                    pg.strokeWeight(1);
                    if (!showBlanking && point.blanked) pg.stroke(0);
                    else {
                        pg.line(pointx, pointy, pointz, oldpositionx, oldpositiony, oldpositionz);
                    }
                    oldpositionx = pointx;
                    oldpositiony = pointy;
                    oldpositionz = pointz;
                } else {
                    firstPoint = false;
                    oldpositionx = pointx;
                    oldpositiony = pointy;
                    oldpositionz = pointz;
                }
            }
        }

        pg.popMatrix();
        //pg.endDraw();
        return pg;
    }

    public void palettePaint(IldaPalette palette) {
        for (IldaPoint point : points) {
            point.colour = palette.getColour(point.palIndex);
        }
    }

    public ArrayList<IldaPoint> getPoints() {
        return points;
    }

    @Override
    public String toString() {
        return "This frame has " + points.size() + " points.\nIt's called " + frameName + ".";
    }

    public int getIldaVersion() {
        return ildaVersion;
    }

    public String getFrameName() {
        return frameName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public int getPointCount() {
        return pointCount;
    }

    public int getFrameNumber() {
        return frameNumber;
    }

    public int getTotalFrames() {
        return totalFrames;
    }

    public int getScannerHead() {
        return scannerHead;
    }

    public boolean isPalette() {
        return palette;
    }


}
