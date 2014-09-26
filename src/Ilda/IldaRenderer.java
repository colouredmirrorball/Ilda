package Ilda;

import processing.core.PGraphics;

import java.io.File;
import java.util.ArrayList;

/**
 * This class can be used to render Ilda files.
 * Well, it might be.
 * Sometime in the future.
 */
public class IldaRenderer extends PGraphics {
    protected File file;
    protected ArrayList<IldaFrame> theFrames = new ArrayList<IldaFrame>();
    protected IldaFrame currentFrame;
    //protected Ilda ilda;
    protected int count = 0;

    protected IldaPoint currentPoint = new IldaPoint(0, 0, 0, 0, 0, 0, true);

    public IldaRenderer() {

    }

    public void setPath(String path) {

        this.path = path;
        if (path != null) {
            file = new File(path);
            if (!file.isAbsolute()) file = null;
        }
        if (file == null) {
            throw new RuntimeException("Something went wrong creating an output file for the Ilda renderer.");
        }

    }

    public void beginDraw() {
        currentFrame = new IldaFrame();
        currentFrame.ildaVersion = 4;
        currentFrame.frameName = "P5Frame";
        currentFrame.companyName = "Ilda4P5";
        currentFrame.frameNumber = count;
    }

    public void endDraw() {
        currentFrame.pointCount = currentFrame.points.size();
        theFrames.add(currentFrame);
        count++;
    }

    public void beginShape(int kind) {

    }

    public void vertex(float x, float y) {
        vertex(x, y, 0);
    }

    public void vertex(float x, float y, float z) {
        short rx = (short) ((x - width * 0.5) / width * 65536);
        short ry = (short) ((y - width * 0.5) / width * 65536);
        short rz = (short) ((z - width * 0.5) / width * 65536);
        currentPoint = new IldaPoint(rx, ry, rz, (int) strokeR, (int) strokeG, (int) strokeB, false);
        currentFrame.points.add(currentPoint);
    }

    public void endShape() {
        currentPoint.blanked = true;
        currentFrame.points.add(currentPoint);
    }


    public void dispose() {

    }

    public boolean is2D() {
        return false;
    }

    public boolean is3D() {
        return true;
    }

    public ArrayList<IldaFrame> getFrames() {
        return theFrames;
    }
}
