package ilda;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;

/**
 * Writes IldaFrames to an ilda file.
 * By default, ilda v4 is used.
 */
public class IldaWriter {
    /**
     * Writes a valid ilda file to a certain location with specified format.
     * @param location The path to where the ilda file should be exported
     * @param frames All frames that should be included in the ilda file
     * @param ildaVersion ilda format:
     *                    0 = 3D, palette;
     *                    1 = 2D, palette;
     *                    (2 = palette header);
     *                    (3 = deprecated);
     *                    4 = 3D, RGB;
     *                    5 = 2D, RGB
     */

    public static void writeFile(String location, List<IldaFrame> frames, int ildaVersion) {
        if (frames == null)
            return;

        IldaFrame.fixHeaders(frames);

        byte[] b = getBytesFromFrames(frames, ildaVersion);
        if (b.length < 32)
            return;

        writeFile(location, b);

    }

    public static void writeFile(String location, IldaFrame[] frames, int ildaVersion) {
        if (frames == null)
            return;

        IldaFrame.fixHeaders(frames);

        byte[] b = getBytesFromFrames(frames, ildaVersion);
        if (b.length < 32)
            return;

        writeFile(location, b);

    }

    private static void writeFile(String location, byte[] b)
    {
        try {
            File file = new File(location);

            Files.write(file.toPath(), b);

        } catch (Exception e) {
            PApplet.println("Error when exporting ilda file: ", e);
            e.printStackTrace();
        }
    }

    /**
     * Writes a valid ilda file to a certain location with specified format.
     * It does not check if the specified location has a valid .ild extension.
     *
     * @param location    The path to where the ilda file should be exported
     * @param frames      All frames that should be included in the ilda file
     * @param palette     An IldaPalette that will be appended in front of the ilda file with a format 2 header
     * @param ildaVersion ilda format: should be 0 or 1 since only those two formats use a palette for their colour information
     *                    but nobody is stopping you from appending a palette to a format 4/5 file, though that would be pointless
     */

    public static void writeFile(String location, List<IldaFrame> frames, IldaPalette palette,
        int ildaVersion) {
        if (frames == null)
            return;
        IldaFrame.fixHeaders(frames);

        byte[] b = getBytesFromFrames(frames, palette, ildaVersion);
        if (b.length < 32)
            return;

        writeFile(location, b);

    }

    public static void writeFile(String location, IldaFrame[] frames, IldaPalette palette, int ildaVersion)
    {
        if (frames == null) return;
        IldaFrame.fixHeaders(frames);

        byte[] b = getBytesFromFrames(frames, palette, ildaVersion);
        if (b.length < 32)
            return;

        writeFile(location, b);

    }

    public static void writeFile(String location, IldaFrame[] frames)
    {
        writeFile(location, frames, 4);
    }

    /**
     * Writes a valid ILDA file to the specified location in format 4
     *
     * @param location Where to write the file to
     * @param frames   Frames that will go into the file
     */

    public static void writeFile(String location, List<IldaFrame> frames) {
        writeFile(location, frames, 4);
    }

    public static byte[] getBytesFromFrames(List<IldaFrame> frames) {
        return getBytesFromFrames(frames.toArray(new IldaFrame[0]), 4);
    }

    public static byte[] getBytesFromFrames(List<IldaFrame> frames, int ildaVersion) {
        return getBytesFromFrames(frames.toArray(new IldaFrame[0]), ildaVersion);
    }

    /**
     * This method returns a byte array which can be exported directly as an ilda file from a palette and an ArrayList of IldaFrames.
     * It will insert the palette as a format 2 header before all frames.
     * It assumes the colours already have the correct colour index, no recolourisation happens.
     *
     * @param frames      an ArrayList of IldaFrames which get converted to ilda-compliant bytes
     * @param palette     an IldaPalette which gets appended before the laser art in the ilda file
     * @param ildaVersion the ilda format the frames get saved as, can be 0, 1, 4 or 5 but only 0 and 1 use a palette. It makes no sense to export as format 4 or 5 with a palette included.
     * @return ilda compliant byte array which can be directly exported as an ilda file
     */

    public static byte[] getBytesFromFrames(List<IldaFrame> frames, IldaPalette palette,
        int ildaVersion) {
        return getBytesFromFrames(frames.toArray(new IldaFrame[0]), palette, ildaVersion);
    }

    public static byte[] getBytesFromFrames(IldaFrame[] frames, IldaPalette palette, int ildaVersion)
    {
        byte[] pbytes = palette.paletteToBytes();
        byte[] fbytes = getBytesFromFrames(frames, ildaVersion);
        byte[] cbytes = new byte[pbytes.length + fbytes.length];
        System.arraycopy(pbytes, 0, cbytes, 0, pbytes.length);
        System.arraycopy(fbytes, 0, cbytes, pbytes.length, fbytes.length);
        return cbytes;
    }

    /**
     * This method returns a byte array from only an ArrayList of IldaFrames. This array can be saved to disk directly as a valid ilda file (binary file).
     * @param frames The frames
     * @param ildaVersion The ilda format version, can be 0, 1, 4 or 5.
     * @return Valid bytes that compose an ilda file
     */

    public static byte[] getBytesFromFrames(IldaFrame[] frames, int ildaVersion) {
        ArrayList<Byte> theBytes = new ArrayList<>();
        int frameNum = 0;

        if (frames.length == 0)
            return new byte[0];

        for (IldaFrame frame : frames) {
            writeCommonIldaHeader(theBytes);

            if (ildaVersion == 0 || ildaVersion == 1 || ildaVersion == 2 || ildaVersion == 4 || ildaVersion == 5)
                theBytes.add((byte) ildaVersion);
            else {
                return new byte[0];
            }

            for (int i = 0; i < 8; i++)    //Bytes 9-16: Name
            {
                char letter;
                if (frame.frameName.length() < i + 1) letter = ' ';
                else letter = frame.frameName.charAt(i);
                theBytes.add((byte) letter);
            }

            if (frame.companyName.length() == 0)   //Bytes 17-24: Company Name
            {
                writeCustomCompanyName(theBytes);
            } else {
                for (int i = 0; i < 8; i++) {
                    char letter;
                    if (frame.companyName.length() < i + 1) letter = ' ';
                    else letter = frame.companyName.charAt(i);
                    theBytes.add((byte) letter);
                }
            }

            //Bytes 25-26: Total point count
            theBytes.add((byte) ((frame.points.size() >> 8) & 0xff));    //This better be correct
            theBytes.add((byte) (frame.points.size() & 0xff));

            //Bytes 27-28: Frame number (automatically increment each frame)
            theBytes.add((byte) ((++frameNum >> 8) & 0xff));    //This better be correct
            theBytes.add((byte) (frameNum & 0xff));

            //Bytes 29-30: Number of frames
            theBytes.add((byte) ((frames.length >> 8) & 0xff));    //This better be correct
            theBytes.add((byte) (frames.length & 0xff));

            theBytes.add((byte) (frame.scannerHead));    //Byte 31 is scanner head
            theBytes.add((byte) (0));                    //Byte 32 is future

            for (IldaPoint point : frame.points) {
                short posx = (short) (((point.getX() < -1) ? -1 :
                    ((point.getX() > 1) ? 1 : point.getX())) * 32767);
                theBytes.add((byte) ((posx >> 8) & 0xff));
                theBytes.add((byte) (posx & 0xff));

                short posy = (short) (((point.getY() < -1) ? -1 :
                    ((point.getY() > 1) ? 1 : point.getY())) * 32767);
                theBytes.add((byte) ((posy >> 8) & 0xff));
                theBytes.add((byte) (posy & 0xff));

                if (ildaVersion == 0 || ildaVersion == 4) //a 3D frame
                {

                    int posz = (int) (((point.getZ() < -1) ? -1 :
                        ((point.getZ() > 1) ? 1 : point.getZ())) * 32767);
                    theBytes.add((byte) ((posz >> 8) & 0xff));
                    theBytes.add((byte) (posz & 0xff));
                }

                if (point.blanked) {
                    theBytes.add((byte) 0x40);
                } else {
                    theBytes.add((byte) 0);
                }

                if (ildaVersion == 0 || ildaVersion == 1) theBytes.add((point.palIndex));
                else {
                    int c = point.colour;

                    if (point.blanked) c = 0;  //some programs only use colour information to determine blanking

                    int red = (c >> 16) & 0xFF;  // Faster way of getting red(argb)
                    int green = ((c >> 8) & 0xFF);   // Faster way of getting green(argb)
                    int blue = (c & 0xFF);          // Faster way of getting blue(argb)

                    theBytes.add((byte) (blue));
                    theBytes.add((byte) (green));
                    theBytes.add((byte) (red));
                }

            }

        }

        //File should always end with a header
        writeCommonIldaHeader(theBytes);
        theBytes.add((byte) ildaVersion);

        theBytes.add((byte) 'L');
        theBytes.add((byte) 'A');
        theBytes.add((byte) 'S');
        theBytes.add((byte) 'T');
        theBytes.add((byte) ' ');
        theBytes.add((byte) 'O');
        theBytes.add((byte) 'N');
        theBytes.add((byte) 'E');

        writeCustomCompanyName(theBytes);

        theBytes.add((byte) 0);
        theBytes.add((byte) 0);

        theBytes.add((byte) 0);
        theBytes.add((byte) 0);

        theBytes.add((byte) 0);
        theBytes.add((byte) 0);

        theBytes.add((byte) 0);

        theBytes.add((byte) 0);

        byte[] bt = new byte[theBytes.size()]; //Ugh! Get your shit fixed, Java!
        for (int i = 0; i < theBytes.size(); i++) {
            bt[i] = theBytes.get(i);
        }

        return bt;
    }

    static void writeCustomCompanyName(ArrayList<Byte> theBytes) {
        theBytes.add((byte) 'I');     //If empty: call it "Ilda4P5"
        theBytes.add((byte) 'l');
        theBytes.add((byte) 'd');
        theBytes.add((byte) 'a');
        theBytes.add((byte) '4');
        theBytes.add((byte) 'P');
        theBytes.add((byte) '5');
        theBytes.add((byte) ' ');
    }

    static void writeCommonIldaHeader(List<Byte> theBytes) {
        theBytes.add((byte) 'I');       //Bytes 1-4: "ILDA"
        theBytes.add((byte) 'L');
        theBytes.add((byte) 'D');
        theBytes.add((byte) 'A');
        theBytes.add((byte) 0);         //Bytes 5-8: Format Code 2
        theBytes.add((byte) 0);
        theBytes.add((byte) 0);
    }
}
