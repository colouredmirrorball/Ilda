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
    String version = "0.0.3 Alpha";
    public ArrayList<String> status = new ArrayList<String>();


    public Ilda(PApplet parent) {

        this.parent = parent;
        if (beta) parent.println("Ilda for Processing version " + version + " started up successfully.");
    }

    /*
     * Any error, quirk, remark, success and such is stored within the class.
     * Use this method to retrieve it for eg a console-like text box.
     * This is for testing purposes and will get eliminated when stuff gets more mature.
     *
     * @return The status
     */
/*
    public ArrayList<String> getStatus() {
        return status;
    }
*/
    /**
     * Parse an Ilda file from disk
     * @param location path to the Ilda file
     * @return list of all loaded frames
     */

    public ArrayList<IldaFrame> readFile(String location) {
        IldaReader reader = new IldaReader(this, location);
        return reader.getFramesFromBytes();
    }

    public IldaFrame readPicFile(String location)
    {
        PicReader reader = new PicReader(location);
        return reader.getFrame();
    }

    /**
     * <b>You should call fixHeaders() first before using this method!</b>
     * Writes a valid Ilda file to a certain location in format 4 (3D, RGB).
     * Checks if the specified location has a valid .ild extension.
     * You should call fixHeaders() first before using this method! Otherwise the Ilda file will not be valid.
     * This does not happen automatically for maximum flexibility (but is maybe a bad idea)
     * @param location The path to where the ilda file should be exported
     * @param frames All frames that should be included in the Ilda file
     */

    public static void writeFile(ArrayList<IldaFrame> frames, String location) {
        writeFile(frames, location, 4);
    }

    /**
     * <b>You should call fixHeaders() first before using this method!</b>
     * Writes a valid Ilda file to a certain location with specified format.
     * Checks if the specified location has a valid .ild extension.
     * You should call fixHeaders() first before using this method! Otherwise the Ilda file will not be valid.
     * This does not happen automatically for maximum flexibility (but is maybe a bad idea)
     * @param location The path to where the ilda file should be exported
     * @param frames All frames that should be included in the Ilda file
     * @param ildaVersion Ilda format:
     *                    0 = 3D, palette;
     *                    1 = 2D, palette;
     *                    (2 = palette header);
     *                    (3 = deprecated);
     *                    4 = 3D, RGB;
     *                    5 = 2D, RGB
     */

    public static void writeFile(ArrayList<IldaFrame> frames, String location, int ildaVersion) {

        if (location.length() < 4) location += ".ild";
        if (!location.substring(location.length() - 4).equalsIgnoreCase(".ild")) location += ".ild";

        IldaWriter.writeFile(location, frames, ildaVersion);
    }

    /**
     * <b>You should call fixHeaders() first before using this method!</b>
     * Writes a valid Ilda file to a certain location with specified format including a palette.
     * Checks if the specified location has a valid .ild extension.
     * You should call fixHeaders() first before using this method! Otherwise the Ilda file will not be valid.
     * This does not happen automatically for maximum flexibility (but is maybe a bad idea)
     * @param location The path to where the ilda file should be exported
     * @param frames All frames that should be included in the Ilda file
     * @param palette An IldaPalette that will be appended in front of the Ilda file with a format 2 header
     * @param ildaVersion Ilda format: should be 0 or 1 since only those two formats use a palette for their colour information
     *                    but nobody is stopping you from appending a palette to a format 4/5 file, though that would be pointless
     */

    public static void writeFile(String location, ArrayList<IldaFrame> frames, IldaPalette palette, int ildaVersion) {
        if (location.length() < 4) location += ".ild";
        if (!location.substring(location.length() - 4).equalsIgnoreCase(".ild")) location += ".ild";

        IldaWriter.writeFile(location, frames,palette, ildaVersion);

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
     * Call this before writing to an Ilda file
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
