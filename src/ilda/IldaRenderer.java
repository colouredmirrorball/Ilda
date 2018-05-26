package ilda;

import processing.core.*;

import java.io.File;
import java.util.ArrayList;

import static processing.core.PApplet.map;
import static processing.core.PApplet.println;

/**
 * This class can be used to render ilda files as a subclass of PGraphics.
 * Well, it might be.
 * Sometime in the future.
 *<p>
 * You can use this class in the same way as you would use another PGraphics.
 * For example you can use most graphic calls on an instance of this class.
 * Usage: as opposed to most PGraphic subclasses, you may use the constructor to create an instance of IldaRenderer.
 * You must call beginDraw() and endDraw() around any graphic calls.</p>
 *<p>
 * Example:
 * r = new IldaRenderer(ilda);<br>
 * r.beginDraw();<br>
 * r.stroke(255,0,0);<br>
 * r.line(50,100,25,60); //draws a red line<br>
 * r.endDraw();<br>
 *</p>
 * Then you can retrieve the frame(s) with r.getFrames() to export or display.
 * <p>
 * It is ill advised to create a new IldaRenderer instance in draw(). Multiple frames can be created sequentially
 * using the same instance of IldaRenderer.
 * </p>
 */

//TODO stroke(r, g, b) not working




public class IldaRenderer extends PGraphics {
    protected File file;
    protected ArrayList<IldaFrame> theFrames = new ArrayList<IldaFrame>();
    protected IldaFrame currentFrame;
    protected int count = 0;
    protected float invWidth, invHeight, invDepth;
    protected boolean shouldBlank = false;
    protected boolean closedShape = false;
    protected IldaPoint firstPoint = new IldaPoint(0, 0, 0, 0, 0, 0, true);
    protected float depth;
    protected float ellipseDetail = 1f;
    private float circleCorrection = 0f;

    protected boolean renderingText = false;
    protected double textDetail = 0.01;
    protected PVector prevVector = new PVector();

    static protected final int MATRIX_STACK_DEPTH = 32;
    protected PMatrix3D matrix = new PMatrix3D();

    protected PMatrix3D[] matrixStack = new PMatrix3D[MATRIX_STACK_DEPTH];


    protected IldaPoint currentPoint = new IldaPoint(0, 0, 0, 0, 0, 0, true);
    protected boolean overwrite = false;

    Optimiser optimiser;
    boolean optimise = true;
    private int matrixStackDepth;

    PApplet parent;

    public IldaRenderer(PApplet parent) {

        this(parent, parent.width, parent.height);
    }


    public IldaRenderer(PApplet parent, int width, int height) {
        this.parent= parent;
        this.width = width;
        this.height = height;
        depth = width;//(float) Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2));
        invDepth = 1f / depth;


        defaultSettings();
        textMode(SHAPE);

        if (width != 0 && height != 0) {


            invWidth = 1f / width;
            invHeight = 1f / height;

        }

        optimiser = new Optimiser(new OptimisationSettings());
    }

    /**
     * If set to true, no new frame will be added to the frame list when calling EndDraw().
     * Instead the renderer will keep writing on the same frame.
     * This is useful for drawing applications where you want to keep adding content to the same frame instead of making an animation.
     * False by default.
     *
     * @param overwrite boolean - should the renderer keep writing on the same frame?
     */

    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }

    /**
     * Returns the overwrite setting, whether the renderer keeps drawing on the same frame or if it creates a new frame each time beginDraw() is called.
     *
     * @return boolean - is false if a new IldaFrame is created upon each draw.
     */

    public boolean isOverwrite() {
        return overwrite;
    }

    /**
     * Currently saving is not implemented in the renderer,
     * use writeFile() in the main ilda class instead.
     *
     * @param path should be a path leading to a file
     */

    public void setPath(String path) {

        this.path = path;
        if (path != null) {
            file = new File(path);
            if (!file.isAbsolute()) file = null;
        }
        if (file == null) {
            throw new RuntimeException("Something went wrong creating an output file for the ilda renderer.");
        }

    }

    public void resize(int newWidth, int newHeight)
    {
        this.width = newWidth;
        this.height = newHeight;
    }

    /**
     * Always call this before drawing!
     */

    public void beginDraw() {
        if (!overwrite || currentFrame == null) {


            currentFrame = new IldaFrame();
            currentFrame.ildaVersion = 4;
            currentFrame.frameName = "P5Frame";
            currentFrame.companyName = "Ilda4P5";
            currentFrame.frameNumber = count;
        }


    }

    /**
     * Always call this after drawing!
     */

    public void endDraw() {
        if (optimise) optimiser.optimiseSegment(currentFrame.points);
        currentFrame.pointCount = currentFrame.points.size();
        if (!overwrite) theFrames.add(currentFrame);
        count++;
        resetMatrix();
    }

    public void beginShape(int kind)
    {
        shape = kind;

        shouldBlank = true;
        vertexCount = 0;
        closedShape = false;

        switch (kind)
        {
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
            default:
                closedShape = false;
                break;

        }
    }

    public void beginContour()
    {
        shouldBlank = true;
        //PApplet.println("contour", shape, shouldBlank);
    }

    public void endContour()
    {
        if (closedShape) currentFrame.points.add(firstPoint);
        //PApplet.println("End contour");
    }

    public void translate(float x, float y) {
        translate(x, y, 0);
    }

    public void translate(float x, float y, float z) {
        matrix.translate(x, y, z);
    }


    public void resetMatrix() {
        matrix.reset();
    }

    public void rotate(float angle) {
        rotate(angle, 0, 0, 1);
    }

    public void rotateX(float angle) {
        rotate(angle, 1, 0, 0);
    }

    public void rotateY(float angle) {
        rotate(angle, 0, 1, 0);
    }

    public void rotateZ(float angle) {
        rotate(angle, 0, 0, 1);
    }

    public void rotate(float angle, float v0, float v1, float v2) {
        float norm = v0 * v0 + v1 * v1 + v2 * v2;
        if (norm < PConstants.EPSILON) {
            return;
        }
        if (PApplet.abs(1f - norm) > PConstants.EPSILON) {
            norm = PApplet.sqrt(norm);
            v0 /= norm;
            v1 /= norm;
            v2 /= norm;
        }
        matrix.rotate(angle, v0, v1, v2);
    }

    public void scale(float x, float y, float z) {
        matrix.scale(x, y, z);
    }

    public void pushMatrix() {
        if (matrixStackDepth == MATRIX_STACK_DEPTH) {
            throw new RuntimeException(ERROR_PUSHMATRIX_OVERFLOW);
        }
        if (matrixStack[matrixStackDepth] == null) {
            matrixStack[matrixStackDepth] = new PMatrix3D(matrix);
        } else matrixStack[matrixStackDepth].set(matrix);
        matrixStackDepth++;
    }

    public void popMatrix() {
        if (matrixStackDepth == 0) {
            throw new RuntimeException(ERROR_PUSHMATRIX_UNDERFLOW);
        }
        matrixStackDepth--;
        matrix.set(matrixStack[matrixStackDepth]);
    }

    /**
     * Get the current translation vector
     *
     * @return the center of the origin relative to the sketch' origin
     */

    public PVector getTranslation() {
        return new PVector(matrix.m03, matrix.m13, matrix.m23);
    }

    protected void setTranslation(float x, float y, float z) {
        matrix.m03 = x;
        matrix.m13 = y;
        matrix.m23 = z;
    }



    public void vertex(float x, float y) {
        vertex(x, y, 0);
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

        PVector pos = new PVector(x, y, z);

        matrix.mult(pos, pos);


        float xpos = 2 * ((pos.x) * invWidth - 0.5f);
        float ypos = 2 * ((pos.y) * invHeight - 0.5f);
        float zpos = 2 * ((pos.z) * invDepth - 0.5f);
        int red = (int) (strokeR * 255);
        int green = (int) (strokeG * 255);
        int blue = (int) (strokeB * 255);

        //when drawing points, add a blanked point before every point
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
            if(renderingText) closedShape = false;
        }




        //ilda.parent.println(x, y, z, 2*(x*invWidth-0.5f), -2*(y*invHeight-0.5f), z* (invHeight + invWidth) * 0.5f-1);
        currentPoint = new IldaPoint(xpos, ypos, zpos, red, green, blue, shouldBlank);
        //PApplet.println(xpos, ypos, zpos, red, green, blue, shouldBlank, " ----- shape: ", shape);

        if(renderingText)
        {
            PVector currPos = new PVector(x, y, z);
            //PApplet.println(PVector.dist(currPos, prevVector), textDetail * textSize, currPos, prevVector, PVector.dist(currPos, prevVector) > textDetail*textSize);
            float dist= PVector.dist(currPos, prevVector);
            if ( dist > textDetail*textSize && dist != 0)
            {
                currentFrame.points.add(currentPoint);  //blargh should probably look at angle as well
                prevVector = currPos;
            }
            //else currentFrame.points.add(currentPoint);


        }

        else currentFrame.points.add(currentPoint);

        if (shouldBlank) shouldBlank = false;


    }

    public void endShape() {
        if (closedShape) currentFrame.points.add(firstPoint);
        //ilda.parent.println(closedShape);
        //currentFrame.points.add(currentPoint);
    }

    protected void ellipseImpl(float x, float y, float w, float h) {
        float m = (w + h) * ellipseDetail;
        boolean first = true;

        for (float i = 0; i < m + 1 + circleCorrection; i++) {
            float xpos = (float) (1 * ((x + w * Math.sin(TWO_PI * i / m) + matrix.m03) * invWidth ));
            float ypos = (float) (-1 * ((y + h * Math.cos(TWO_PI * i / m) + matrix.m13) * invHeight ));
            float zpos = 2 * ((0.5f * depth + matrix.m23) * invDepth - 0.5f);
            int red = (int) (strokeR * 255);
            int green = (int) (strokeG * 255);
            int blue = (int) (strokeB * 255);
            if (first) {
                IldaPoint p = new IldaPoint(xpos, ypos, zpos, red, green, blue, true);
                currentFrame.points.add(p);
                first = false;
            }
            IldaPoint p = new IldaPoint(xpos, ypos, zpos, red, green, blue, false);
            currentFrame.points.add(p);
        }
    }

    /**
     * Well wouldn't it be boss if this "just worked"!!!
     * Should be automatically called by the several text() implementations of the parent PGraphics class
     * @param ch the character
     * @param x position x
     * @param y position y
     */

    protected void textCharImpl(char ch, float x, float y) {

        PShape glyph = this.textFont.getShape(ch);
        //glyph.scale(1,-1);
        //PApplet.println("=== Printing char", ch, "family:", glyph.getFamily());
        renderingText = true;   //for haxx
        closedShape = true;
        this.shape(glyph, x, y);
        renderingText = false;

    }

    public void applyMatrix(float n00, float n01, float n02, float n10, float n11, float n12) {
        this.applyMatrixImpl(n00, n01, 0.0F, n02, n10, n11, 0.0F, n12, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
    }

    public void applyMatrix(float n00, float n01, float n02, float n03, float n10, float n11, float n12, float n13, float n20, float n21, float n22, float n23, float n30, float n31, float n32, float n33) {
        this.applyMatrixImpl(n00, n01, n02, n03, n10, n11, n12, n13, n20, n21, n22, n23, n30, n31, n32, n33);
    }

    /**
     * NOT interested in debugging this if it's even bugged maybe it magically works idk idc
     *
     */

    protected void applyMatrixImpl(float n00, float n01, float n02, float n03, float n10, float n11, float n12, float n13, float n20, float n21, float n22, float n23, float n30, float n31, float n32, float n33) {
        this.matrix.apply(n00, n01, n02, n03, n10, n11, n12, n13, n20, n21, n22, n23, n30, n31, n32, n33);
        //this.modelviewInv.set(this.modelview);
        //this.modelviewInv.invert();
        //this.projmodelview.apply(n00, n01, n02, n03, n10, n11, n12, n13, n20, n21, n22, n23, n30, n31, n32, n33);
    }

    /**
     * Number reflecting the amount of points in an ellipse.
     * The lower, the less points there are. The higher, the more.
     * The default value is 1. Finding a good value is a bit arbitrary.
     * This value is multiplied by the sum of the third and fourth arguments of the ellipse method
     * (the width and height).
     *
     * @param detail gets multiplied with (width+height) of the ellipse arguments to get the total points
     */

    public void setEllipseDetail(float detail)
    {
        ellipseDetail = detail;
    }

    /**
     * Sets the amount of points the ellipse should overshoot or continue drawing, to avoid a gap.
     * Can be negative to leave a gap.
     * Default is 0.
     *
     * @param correction for how much extra points the ellipse continues
     */


    public void setEllipseCorrection(int correction) {
        circleCorrection = correction;
    }

    protected void defaultFontOrDeath(String method, float size)
    {
        this.textFont = parent.createFont("Lucida Sans", size, true, null);
    }

    /**
     * Draw an existing ilda frame inside the renderer
     * @param frame frame to be drawn
     * @param x x position offset
     * @param y y position offset
     * @param w width (rescaling)
     * @param h height (rescaling)
     */

    public void drawIldaFrame(IldaFrame frame, int x, int y, int w, int h)
    {
        for (IldaPoint p : frame.points)
        {
            IldaPoint newPoint = new IldaPoint(p.clone());
            PVector position = newPoint.getPosition();
            position.x = map(position.x, 0, width, x, x+w);
            position.y = map(position.y, 0,height,y,y+w);
            currentFrame.addPoint(newPoint);
        }
    }

    public void drawIldaFrame(IldaFrame frame, int x, int y)
    {
        drawIldaFrame(frame, x, y, width, height);
    }

    public void drawIldaFrame(IldaFrame frame)
    {
        drawIldaFrame(frame, 0,0,width, height);
    }





    public void dispose() {
        theFrames.clear();
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

        IldaFrame.fixHeaders(theFrames);
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
     * How many frames are there?
     *
     * @return how many frames are currently in the renderer
     */

    public int getFramesAmount() {
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

    public void background() {
        clearFrame();
    }

    /**
     * Should the frame be optimised for rendering?
     * If set to false, as little points as possible are used to represent the art.
     * If set to true, the frame will be optimised for display using this renderer's OptimisationSettings.
     *
     * @param shouldOptimise should the frame be optimised?
     */

    public void setOptimise(boolean shouldOptimise) {
        optimise = shouldOptimise;
    }

    public void setOptimisationSettings(OptimisationSettings settings) {
        this.optimiser.setSettings(settings);
    }

    public OptimisationSettings getOptimisationSettings() {
        return this.optimiser.getSettings();
    }

    public double getTextDetail()
    {
        return textDetail;
    }

    /**
     * Defines a minimal distance, in terms of fraction of the size of the font, in which no points are generated.
     * Necessary to reduce point count in curved letters.
     * @param textDetail  minimal distance between two points in text characters
     */

    public void setTextDetail(double textDetail)
    {
        this.textDetail = textDetail;
    }
}
