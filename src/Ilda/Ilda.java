/**
 * Main class
 */

package Ilda;

import processing.core.PApplet;

import java.util.ArrayList;

/**
 * This package allows you to render Ilda files straight from Processing!
 */

public class Ilda {
    PApplet parent;
    boolean beta = true;
    String version = "0.0.1";
    public ArrayList<String> status = new ArrayList<String>();


    public Ilda(PApplet parent) {

        this.parent = parent;
        if (beta) parent.println("Ilda for Processing version " + version + " started up successfully.");
    }

    /**
     * Any error, quirk, remark, success and such is stored within the class.
     * Use this method to retrieve it for eg a console-like text box.
     * This is for testing purposes and will get eliminated when stuff gets more mature.
     *
     * @return The status
     */

    public ArrayList<String> getStatus() {
        return status;
    }

    public ArrayList<IldaFrame> readFile(String location) {
        IldaReader reader = new IldaReader(this, location);
        return reader.getFramesFromBytes();
    }

    public void writeFile(ArrayList<IldaFrame> frames, String location) {
        writeFile(frames, location, 4);
    }

    public void writeFile(ArrayList<IldaFrame> frames, String location, int ildaVersion) {
        IldaWriter writer = new IldaWriter(this);
        if (location.length() < 4) location += ".ild";
        if (!location.substring(location.length() - 4).equalsIgnoreCase(".ild")) location += ".ild";

        writer.writeFile(location, frames, ildaVersion);
    }

    /**
     * Fixes the frame headers
     * eg. updates point count, frame number, total frames, ...
     * It leaves the frame name and company name untouched.
     * It assumes the frames form a complete sequence.
     *
     * @param frames A reference to the frames whose headers need to get fixed.
     */

    public static void fixHeaders(ArrayList<IldaFrame> frames) {
        int i = 1;
        for (IldaFrame frame : frames) {
            frame.frameNumber = i++;
            frame.totalFrames = frames.size();
            frame.pointCount = frame.points.size();
        }
    }

    /**
     * Fixes the frame headers
     * eg.updates point count, frame number, total frames
     * It sets the frame name and company name to the arguments you gave it.
     * It assumes the frames form a complete sequence (for the total frame entry).
     * Call this before rendering to ilda file.
     *
     * @param frames      A reference to the frames whose headers need to get fixed.
     * @param frameName   A name you want to give the frame
     * @param companyName Another name
     */

    public static void fixHeaders(ArrayList<IldaFrame> frames, String frameName, String companyName) {
        int i = 1;
        for (IldaFrame frame : frames) {
            frame.frameName = frameName;
            frame.companyName = companyName;
            frame.frameNumber = i++;
            frame.totalFrames = frames.size();
            frame.pointCount = frame.points.size();
        }
    }
}
