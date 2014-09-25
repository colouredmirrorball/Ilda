package Ilda;

import processing.core.PGraphics;

import java.io.File;
import java.util.ArrayList;

/**
 * This class can be used to render Ilda files.
 * Well, it might be.
 * Sometime in the future.
 */
public class IldaRender extends PGraphics {
    protected File file;
    protected ArrayList<IldaFrame> theFrames = new ArrayList<IldaFrame>();
    protected IldaFrame currentFrame;
    //protected Ilda ilda;
    protected int count = 0;

    public IldaRender() {

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


    public void dispose() {

    }

    public boolean is2D() {
        return false;
    }

    public boolean is3D() {
        return true;
    }
}
