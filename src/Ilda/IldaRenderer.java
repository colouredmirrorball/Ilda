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
    protected Ilda ilda;
    protected int count = 0;
    protected float invWidth, invHeight, invDepth;
    protected float originx, originy, originz;
    protected boolean shouldBlank = false;
    protected boolean closedShape = false;
    protected IldaPoint firstPoint = new IldaPoint(0, 0, 0, 0, 0, 0, true);
    float depth;

    protected IldaPoint currentPoint = new IldaPoint(0, 0, 0, 0, 0, 0, true);
    protected boolean overwrite = false;

    public IldaRenderer(Ilda ilda) {
        this.ilda = ilda;

        width = ilda.parent.width;
        height = ilda.parent.height;
        depth = width;//(float) Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2));
        invDepth = 1f / depth;


        originx = 0;
        originy = 0;
        originz = 0;

        defaultSettings();
        textMode(SHAPE);

        if (width != 0 && height != 0) {


            invWidth = 1f / width;
            invHeight = 1f / height;

        } else {

        }
    }

    /**
     * If set to true, no new frame will be added to the frame list when calling EndDraw().
     * This is useful for drawing applications where you want to keep adding content to the same frame.
     * False by default.
     *
     * @param overwrite boolean - should the renderer keep writing on the same frame?
     */

    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }

    /**
     * Returns the overwrite setting, whether the renderer keeps drawing on the same frame or not.
     *
     * @return boolean - is false if a new IldaFrame is created upon each draw.
     */

    public boolean isOverwrite() {
        return overwrite;
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
        if (!overwrite || currentFrame == null) {


            currentFrame = new IldaFrame();
            currentFrame.ildaVersion = 4;
            currentFrame.frameName = "P5Frame";
            currentFrame.companyName = "Ilda4P5";
            currentFrame.frameNumber = count;
        }

    }

    public void endDraw() {
        currentFrame.pointCount = currentFrame.points.size();
        if (!overwrite) theFrames.add(currentFrame);
        count++;
    }

    public void beginShape(int kind) {
        shape = kind;
        //ilda.parent.println("begin shape");
        shouldBlank = true;
        vertexCount = 0;

        switch (kind) {
            case TRIANGLE:
            case TRIANGLES:
            case QUAD:
            case QUADS:
            case POLYGON:
            case RECT:
            case ELLIPSE:
            case SPHERE:
            case BOX:
                closedShape = true;

                break;
        }
    }

    public void translate(float x, float y) {
        translate(x, y, 0.5f * depth);
    }

    public void translate(float x, float y, float z) {
        originx = x;
        originy = y;
        originz = z;
    }

    public void resetMatrix() {
        originx = 0;
        originy = 0;
        originz = 0;
    }



    public void vertex(float x, float y) {
        vertex(x, y, 0.5f * depth);
    }

    public void vertex(float x, float y, float z) {
        if (vertexCount >= 512) vertexCount = 0;

        float vertex[] = vertices[vertexCount];

        vertex[X] = x;
        vertex[Y] = y;
        vertex[Z] = z;

        vertex[SR] = strokeR;
        vertex[SG] = strokeG;
        vertex[SB] = strokeB;
        vertex[SA] = strokeA;
        vertex[SW] = strokeWeight;
        vertexCount++;

        //ilda.parent.println(shape, vertexCount);

        float xpos = 2 * ((x + originx) * invWidth - 0.5f);
        float ypos = -2 * ((y + originy) * invHeight - 0.5f);
        float zpos = 2 * ((z + originz) * invDepth - 0.5f);
        int red = (int) (strokeR * 255);
        int green = (int) (strokeG * 255);
        int blue = (int) (strokeB * 255);

        if ((shape == POINT) || shape == POINTS) {
            currentPoint = new IldaPoint(xpos, ypos, zpos, red, green, blue, true);
            currentFrame.points.add(currentPoint);
            shouldBlank = false;
            vertexCount = 0;
        }

        if ((shape == LINES) && vertexCount == 2) {
            //shouldBlank = !shouldBlank;
            vertexCount = 0;
        }

        if (closedShape && vertexCount == 1) {
            firstPoint = new IldaPoint(xpos, ypos, zpos, red, green, blue, false);
        }


        //ilda.parent.println(x, y, z, 2*(x*invWidth-0.5f), -2*(y*invHeight-0.5f), z* (invHeight + invWidth) * 0.5f-1);
        currentPoint = new IldaPoint(xpos, ypos, zpos, red, green, blue, shouldBlank);

        currentFrame.points.add(currentPoint);

        if (shouldBlank) shouldBlank = false;


    }

    public void endShape() {
        if (closedShape) currentFrame.points.add(firstPoint);
        //ilda.parent.println("end shape");
        //currentFrame.points.add(currentPoint);
    }

    protected void defaultFontOrDeath(String method, float size)
    {
        this.textFont = ilda.parent.createFont("Lucida Sans", size, true, null);
    }

    @Override
    protected void textCharImpl(char ch, float x, float y)
    {
        this.rect(x, y, 10, 10);
    }


    public void dispose() {

    }

    public boolean is2D() {
        return false;
    }

    public boolean is3D() {
        return true;
    }

    /**
     * Get all frames stored in the renderer
     *
     * @return all frames in the renderer
     */

    public ArrayList<IldaFrame> getFrames() {
        Ilda.fixHeaders(theFrames);
        return theFrames;
    }

    /**
     * Get the current frame that's being drawn on
     *
     * @return current frame
     */

    public IldaFrame getCurrentFrame() {
        return currentFrame;
    }

    /**
     * @return how many frames are currently in the renderer
     */

    public int framesAmount() {
        return theFrames.size();
    }

    /**
     * Clears the frame.
     */

    public void clearFrame() {
        currentFrame.points.clear();
    }

    public void clearAllFrames()
    {
        theFrames.clear();
    }
}
