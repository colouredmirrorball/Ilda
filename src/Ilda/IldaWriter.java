package Ilda;


import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;


/**
 * Writes an ilda file.
 * By default, Ilda v4 is used.
 */
public class IldaWriter {
    ArrayList<IldaFrame> frames;
    Ilda ilda;

    public IldaWriter(Ilda ilda) {
        this.ilda = ilda;
    }

    public IldaWriter(ArrayList<IldaFrame> frames) {
        this.frames = frames;
    }

    public void writeFile(String location, ArrayList<IldaFrame> frames, int ildaVersion) {
        if (frames == null) return;
        ilda.status.add("frames size: " + frames.size());
        byte[] b = getBytesFromFrames(frames, ildaVersion);
        if (b == null) return;
        ilda.status.add("b_length " + b.length);
        try {
            File file = new File(location);
            if (file.createNewFile()) ilda.status.add("Created a new file at " + location);
            else ilda.status.add("Wrote file over " + location);
            Files.write(file.toPath(), b);

        } catch (Exception e) {
            ilda.status.add("Error upon writing file to " + location);
            ilda.status.add(e.toString());
        }

    }

    public byte[] getBytesFromFrames(ArrayList<IldaFrame> frames) {
        return getBytesFromFrames(frames, 4);
    }

    /**
     * This method returns a byte array which can be exported directly as an Ilda file.
     * It will insert the palette as a format 2 header before all frames.
     * It assumes the colours already have the correct colour index, no recolourisation happens.
     *
     * @param frames      an ArrayList of IldaFrames which get converted to ilda-compliant bytes
     * @param palette     an IldaPalette which gets appended before the laser art in the ilda file
     * @param ildaVersion the ilda format the frames get saved as, can be 0, 1, 4 or 5 but only 0 and 1 use a palette. It makes no sense to export as format 4 or 5 with a palette included.
     * @return Ilda compliant byte array which can be directly exported as an ilda file
     */

    public byte[] getBytesFromFrames(ArrayList<IldaFrame> frames, IldaPalette palette, int ildaVersion) {
        byte[] pbytes = palette.paletteToBytes();
        byte[] fbytes = getBytesFromFrames(frames, ildaVersion);
        byte[] cbytes = new byte[pbytes.length + fbytes.length];
        System.arraycopy(pbytes, 0, cbytes, 0, pbytes.length);
        System.arraycopy(fbytes, 0, cbytes, pbytes.length, fbytes.length);
        return cbytes;
    }

    public byte[] getBytesFromFrames(ArrayList<IldaFrame> frames, int ildaVersion) {
        ArrayList<Byte> theBytes = new ArrayList<Byte>();
        int frameNum = 0;

        if (frames.isEmpty()) return null;

        for (IldaFrame frame : frames) {
            theBytes.add((byte) 'I');
            theBytes.add((byte) 'L');
            theBytes.add((byte) 'D');
            theBytes.add((byte) 'A');
            theBytes.add((byte) 0);
            theBytes.add((byte) 0);
            theBytes.add((byte) 0);

            if (ildaVersion == 0 || ildaVersion == 1 || ildaVersion == 2 || ildaVersion == 4 || ildaVersion == 5)
                theBytes.add((byte) ildaVersion);
            else {
                ilda.status.clear();
                ilda.status.add("Error: invalid ilda version when writing to file");
                return null;
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
                theBytes.add((byte) 'I');     //If empty: call it "Ilda4P5"
                theBytes.add((byte) 'l');
                theBytes.add((byte) 'd');
                theBytes.add((byte) 'a');
                theBytes.add((byte) '4');
                theBytes.add((byte) 'P');
                theBytes.add((byte) '5');
                theBytes.add((byte) ' ');
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
            theBytes.add((byte) ((frames.size() >> 8) & 0xff));    //This better be correct
            theBytes.add((byte) (frames.size() & 0xff));

            theBytes.add((byte) (frame.scannerHead));    //Byte 31 is scanner head
            theBytes.add((byte) (0));                    //Byte 32 is future



            for (IldaPoint point : frame.points) {
                short posx = (short) ((point.x < -1 ? -1 : point.x > 1 ? 1 : point.x) * 32767);
                theBytes.add((byte) ((posx >> 8) & 0xff));
                theBytes.add((byte) (posx & 0xff));

                short posy = (short) ((point.y < -1 ? -1 : point.y > 1 ? 1 : point.y) * 32767);
                theBytes.add((byte) ((posy >> 8) & 0xff));
                theBytes.add((byte) (posy & 0xff));

                if (ildaVersion == 0 || ildaVersion == 4) //a 3D frame
                {

                    int posz = (int) ((point.z < -1 ? -1 : point.z > 1 ? 1 : point.z) * 32767);
                    theBytes.add((byte) ((posz >> 8) & 0xff));
                    theBytes.add((byte) (posz & 0xff));
                }
                //ilda.parent.println(posx + " " + posy + " " + point.blanked);

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

        theBytes.add((byte) 'I');
        theBytes.add((byte) 'L');
        theBytes.add((byte) 'D');
        theBytes.add((byte) 'A');
        theBytes.add((byte) 0);
        theBytes.add((byte) 0);
        theBytes.add((byte) 0);
        theBytes.add((byte) ildaVersion);

        theBytes.add((byte) 'L');
        theBytes.add((byte) 'A');
        theBytes.add((byte) 'S');
        theBytes.add((byte) 'T');
        theBytes.add((byte) ' ');
        theBytes.add((byte) 'O');
        theBytes.add((byte) 'N');
        theBytes.add((byte) 'E');

        theBytes.add((byte) 'I');
        theBytes.add((byte) 'l');
        theBytes.add((byte) 'd');
        theBytes.add((byte) 'a');
        theBytes.add((byte) '4');
        theBytes.add((byte) 'P');
        theBytes.add((byte) '5');
        theBytes.add((byte) ' ');

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
}
