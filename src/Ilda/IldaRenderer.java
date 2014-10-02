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
        float vertex[] = vertices[vertexCount];

        vertex[X] = (float) ((x - width * 0.5) / width * 65536);
        vertex[Y] = (float) ((y - width * 0.5) / width * 65536);
        vertex[Z] = (float) ((z - width * 0.5) / width * 65536);

        vertex[SR] = strokeR;
        vertex[SG] = strokeG;
        vertex[SB] = strokeB;
        vertex[SA] = strokeA;
        vertex[SW] = strokeWeight;
        vertexCount++;

        if ((shape == LINES) && vertexCount == 2) {
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
        currentFrame.points.add(new IldaPoint(vertices[index1][X], vertices[index1][Y], vertices[index1][Z], (int) vertices[index1][SR], (int) vertices[index1][SG], (int) vertices[index1][SB], true));
        currentPoint = new IldaPoint(vertices[index2][X], vertices[index2][Y], vertices[index2][Z], (int) vertices[index2][SR], (int) vertices[index2][SG], (int) vertices[index2][SB], false);
        currentFrame.points.add(currentPoint);

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
