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
    protected float invWidth, invHeight;

    protected IldaPoint currentPoint = new IldaPoint(0, 0, 0, 0, 0, 0, true);

    public IldaRenderer(Ilda ilda) {
        this.ilda = ilda;
        ilda.parent.println("attempting to create renderer");
        width = ilda.parent.width;
        height = ilda.parent.height;

        if (width != 0 && height != 0) {


            invWidth = 1f / width;
            invHeight = 1f / height;

        } else {
            ilda.parent.println("Width or height were 0");
        }
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
        ilda.parent.println("attempting to begin drawing");
        currentFrame = new IldaFrame();
        currentFrame.ildaVersion = 4;
        currentFrame.frameName = "P5Frame";
        currentFrame.companyName = "Ilda4P5";
        currentFrame.frameNumber = count;
        ilda.parent.println("began drawing");
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
        //ilda.parent.println("vertex " + x + " " + y + " " + z);
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

        if (vertexCount == 2) {
            writeLine(0, 1);
            vertexCount = 0;
        } else if (shape == TRIANGLES && vertexCount == 3) {
            writeTriangle();
        }

    }

    public void endShape() {
        currentPoint.blanked = true;
        currentFrame.points.add(currentPoint);
    }

    protected void writeLine(int index1, int index2) {
        float x = vertices[index1][X] * invWidth;
        float y = -vertices[index1][Y] * invHeight;
        float z = vertices[index1][Z] * (invHeight + invWidth) * 0.5f;
        currentFrame.points.add(new IldaPoint(x, y, z, (int) (vertices[index1][SR] * 255), (int) (vertices[index1][SG] * 255), (int) (vertices[index1][SB] * 255), true));
        ilda.parent.println("First point x: " + x + " original x: " + vertices[index1][X] + " y: " + y + " original y: " + vertices[index1][Y]);
        ilda.parent.println("First point r: " + (int) (vertices[index1][SR] * 255));
        x = vertices[index2][X] * invWidth;
        y = -vertices[index2][Y] * invHeight;
        z = vertices[index2][Z] * (invHeight + invWidth) * 0.5f;
        currentPoint = new IldaPoint(x, y, z, (int) (vertices[index2][SR] * 255), (int) (vertices[index2][SG] * 255), (int) (vertices[index2][SB] * 255), false);
        currentFrame.points.add(currentPoint);
        ilda.parent.println("Second point x: " + x + " original x: " + vertices[index2][X] + " y: " + y + " original y: " + vertices[index2][Y]);

    }

    protected void writeTriangle() {
        currentPoint = new IldaPoint(vertices[0][X], vertices[0][Y], vertices[0][Z], (int) vertices[0][SR], (int) vertices[0][SG], (int) vertices[0][SB], true);
        currentFrame.points.add(currentPoint);
        for (int i = 0; i < 3; i++) {
            currentPoint = new IldaPoint(vertices[i][X], vertices[i][Y], vertices[i][Z], (int) vertices[i][SR], (int) vertices[i][SG], (int) vertices[i][SB], false);
            currentFrame.points.add(currentPoint);
        }

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
        Ilda.fixHeaders(theFrames);
        return theFrames;
    }
}
