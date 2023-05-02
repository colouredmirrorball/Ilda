package ilda;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import processing.core.PApplet;

import static ilda.Utilities.logException;

/**
 * This class reads a file and passes the data to frames and points.
 * <p>
 * Ilda files are explained <a href="https://www.ilda.com/resources/StandardsDocs/ILDA_IDTF14_rev011.pdf">here</a>
 * This document only
 * mentions Ilda V0, 1, 2 and 3, no V4 and V5 so here's a breakdown: ILDA V0 is 3D and uses
 * palettes ILDA V1 is 2D and
 * uses palettes ILDA V2 is a palette ILDA V3 is a 24-bit palette, but was discontinued and is
 * not a part of the
 * official standard anymore ILDA V4 is 3D with true-colour information in BGR format ILDA V5 is
 * 2D with true-colour
 * information in BGR format.</p>
 * <p>
 * An Ilda file is composed of headers that always start with "ILDA", followed by three zeros and
 * the version number. A
 * complete header is 32 bytes. After the header, the data follows. In case of a palette (V2),
 * each data point has three
 * bytes: R, G and B. In case of a frame (V0/1/4/5), the X, Y and Z (for 3D frames) values are
 * spread out over two bytes
 * Then either two status bytes follow with a blanking bit and palette colour number, or BGR
 * values.</p>
 */
public class IldaReader extends FileParser
{

    private IldaPalette palette;

    public IldaReader(String location) throws FileNotFoundException
    {
        this(new File(location));
    }

    public IldaReader(PApplet applet, String location)
    {
        super(applet, location);
    }

    public IldaReader(File file) throws FileNotFoundException
    {
        super(file);
    }

    /**
     * Read an Ilda file from disk and convert it to a list of IldaFrame objects. The location may be relative to the
     * sketch path if the parameter applet is set
     *
     * @param applet   Reference to applet (usually <code>this</code>), necessary for retrieving relative sketch paths.
     * @param location Either absolute file path or name of a file in the data folder of the sketch.
     * @return Parsed frames in an ArrayList
     */

    public static List<IldaFrame> readFile(PApplet applet, String location)
    {
        IldaReader reader = new IldaReader(applet, location);
        return reader.getFramesFromBytes();
    }

    /**
     * Parse an ilda file from disk and retrieve its contents. Note that since there is no
     * information here about canvas
     * size, all positions are normalised between -1 and 1.
     *
     * @param location absolute path to the ilda file
     * @return list of all loaded frames
     */

    public static List<IldaFrame> readFile(String location)
    {
        IldaReader reader;
        try
        {
            reader = new IldaReader(location);
        }
        catch (FileNotFoundException exception)
        {
            logException(exception);
            return Collections.emptyList();
        }
        return reader.getFramesFromBytes();
    }

    public void setPalette(IldaPalette palette) {
        this.palette = palette;
    }

    private List<IldaFrame> getFramesFromBytes() {
        reset();
        ArrayList<IldaFrame> theFrames = new ArrayList<>();
        if (b == null) {
            return Collections.emptyList();
        }

        if (b.length < 32) {
            //There isn't even a complete header here!
            throw new RuntimeException("Error: file is not long enough to be a valid ILDA file!");
        }

        //Check if the first four bytes read ILDA:
        String header = parseString(4);
        if (!header.equals("ILDA")) {
            throw new RuntimeException(
                "Error: invalid ILDA file, found " + header + ", expected ILDA instead");
        }

        reset();

        loadIldaFrame(theFrames);
        return theFrames;
    }

    /**
     * Iterative method to load ilda frames, the new frames are appended to an ArrayList.
     *
     * @param frames IldaFrame List where the new frame will be appended
     */

    private void loadIldaFrame(List<IldaFrame> frames) {
        if (position >= b.length - 32) {
            return;        //no complete header
        }

        //Bytes 0-3: ILDA
        String hdr = parseString(4);
        if (!hdr.equals("ILDA")) {
            return;
        }

        //Bytes 4-6: Reserved
        skip(3);

        //Byte 7: format code
        int ildaVersion = parseByte();

        //Bytes 8-15: frame name
        String name = parseString(8);

        //Bytes 16-23: company name
        String company = parseString(8);

        //Bytes 24-25: point count
        int pointCount = parseShort();

        //Bytes 26-27: frame number in frames or palette number in palettes
        int frameNumber = parseShort();

        //Bytes 28-29: total frames
        skip(2);

        //Byte 30: projector number
        int scannerhead = parseByte() & 0xff;

        //Byte 31: Reserved
        skip(1);


        if (ildaVersion == 2) {

            palette = new IldaPalette();

            palette.name = name;
            palette.companyName = company;
            palette.totalColors = pointCount;

            //Byte 30: scanner head.
            palette.scannerHead = scannerhead;


            // ILDA V2: Palette information

            for (int i = 0; i < pointCount; i++) {
                palette.addColour(parseByte(), parseByte(), parseByte());
            }
        } else {
            IldaFrame frame = new IldaFrame();

            frame.setIldaFormat(ildaVersion);
            frame.setFrameName(name);
            frame.setCompanyName(company);
            frame.setFrameNumber(frameNumber);
            frame.setPalette(ildaVersion == 0 || ildaVersion == 1);

            boolean is3D = ildaVersion == 0 || ildaVersion == 4;


            for (int i = 0; i < pointCount; i++) {
                float x = parseShort();
                float y = parseShort();
                float z = 0;
                if (is3D) {z = parseShort();}
                boolean bl = (parseByte() & 0x40) == 64;
                if (ildaVersion == 0 || ildaVersion == 1) {
                    IldaPoint point = new IldaPoint(x * 0.00003051757f, y * -0.00003051757f,
                        z * 0.00003051757f, parseByte() & 0xff, bl);
                    frame.addPoint(point);
                } else if (ildaVersion == 4 || ildaVersion == 5) {
                    int blue = parseByte();
                    int g = parseByte();
                    int r = parseByte();
                    IldaPoint point = new IldaPoint(x * 0.00003051757f, y * -0.00003051757f,
                        z * 0.00003051757f, blue & 0xff, g & 0xff, r & 0xff, bl);
                    frame.addPoint(point);
                }
            }

            if (frame.isPalette()) {
                if (palette == null) {
                    palette = new IldaPalette();
                    palette.setDefaultPalette();
                }

                frame.palettePaint(palette);
            }
            frames.add(frame);

            loadIldaFrame(frames);
        }
    }


}
