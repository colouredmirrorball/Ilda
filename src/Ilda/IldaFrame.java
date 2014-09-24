package Ilda;

import java.util.ArrayList;

import processing.core.*;

/**
 * A frame is essentially a collection of points.
 */
public class IldaFrame {
    ArrayList<IldaPoint> points = new ArrayList<IldaPoint>();
    //The Points in the Frame


    protected int ildaVersion = 4;    //Data retrieved from header
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
            throw new IllegalArgumentException();
        } else ildaVersion = versionNumber;
    }

    public PGraphics renderFrame(PApplet parent) {
        return renderFrame(parent, parent.width, parent.height);
    }

    public PGraphics renderFrame(PApplet parent, int sizex, int sizey) {
        return renderFrame(parent, true, sizex, sizey);
    }

    public PGraphics renderFrame(PApplet parent, boolean showBlanking, int sizex, int sizey) {
        return renderFrame(parent, showBlanking, sizex, sizey, 0, 0, 0);
    }

    public PGraphics renderFrame(PApplet parent, boolean showBlanking, int sizex, int sizey, float rotx, float roty, float rotz) {
        PGraphics pg = new PGraphics();
        pg.setParent(parent);
        pg.setPrimary(false);
        pg.setSize(sizex, sizey);
        pg.beginDraw();
        //parent.println("Began drawing frame " + frameName);

        //Set half of the pixels red just to make sure something is drawn to the PGraphics...
        pg.loadPixels();
        for (int i = 0; i < pg.pixels.length * 0.5; i++) {
            pg.pixels[i] = pg.color(255, 0, 0);
        }

        if (points.size() > 0) {
            boolean firstPoint = true;
            float oldpositionx = 0;
            float oldpositiony = 0;
            float oldpositionz = 0;
            for (IldaPoint point : points) {
                float pointx = (float) (((point.x) + 32768) * sizex * 0.00001525878);
                float pointy = (float) (((point.y) + 32768) * sizey * 0.00001525878);
                float pointz = 0;
                //float pointz = (float) (((point.z) + 32768) * sizex * 0.00001525878);
                if (showBlanking || !point.blanked) {
                    pg.strokeWeight(3);
                    //pg.stroke(point.colour);
                    pg.stroke(255);
                    if (point.blanked) {
                        //pg.stroke(75 << 16 + 75 << 8 + 75);
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
        pg.endDraw();
        //parent.println("Ended drawing frame " + frameName);
        return pg;
    }

    public void palettePaint(IldaPalette palette) {
        for (IldaPoint point : points) {
            point.colour = palette.getColour(point.palIndex);
        }
    }

    @Override
    public String toString() {
        return "This frame has " + points.size() + " points.\nIt's called " + frameName + ".";
    }


}
