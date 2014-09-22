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

    public void writeFile(String location, ArrayList<IldaFrame> frames) {
        byte[] b = getBytesFromFrames(frames);
        try {
            Files.write(new File(location).toPath(), b);
        } catch (Exception e) {
            ilda.status.add("Error upon writing file to " + location);
            ilda.status.add(e.toString());
        }

    }

    public byte[] getBytesFromFrames(ArrayList<IldaFrame> frames) {
        return getBytesFromFrames(frames, 4);
    }

    public byte[] getBytesFromFrames(ArrayList<IldaFrame> frames, IldaPalette palette, int ildaVersion) {
        byte[] pbytes = palette.paletteToBytes();
        byte[] fbytes = getBytesFromFrames(frames, ildaVersion);
        byte[] cbytes = new byte[pbytes.length + fbytes.length];
        System.arraycopy(pbytes, 0, cbytes, 0, pbytes.length);
        System.arraycopy(fbytes, 0, cbytes, 0 + pbytes.length, fbytes.length);
        return cbytes;
    }

    public byte[] getBytesFromFrames(ArrayList<IldaFrame> frames, int ildaVersion) {
        ArrayList<Byte> theBytes;
        theBytes = new ArrayList<Byte>();
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

            // Ilda V0: 3D, palette
            if (ildaVersion == 0) {
                for (IldaPoint point : frame.points) {
                    int posx = (int) point.x;
                    theBytes.add((byte) ((posx >> 8) & 0xff));
                    theBytes.add((byte) (posx & 0xff));

                    int posy = (int) point.y;
                    theBytes.add((byte) ((posy >> 8) & 0xff));
                    theBytes.add((byte) (posy & 0xff));

                    int posz = (int) point.z;
                    theBytes.add((byte) ((posz >> 8) & 0xff));
                    theBytes.add((byte) (posz & 0xff));

                    if (point.blanked) {
                        theBytes.add((byte) 0x40);
                    } else {
                        theBytes.add((byte) 0);
                    }
                    theBytes.add((point.palIndex));
                }
            }

            //Ilda V1: 2D, palettes
            if (ildaVersion == 1) {
                for (IldaPoint point : frame.points) {
                    int posx = (int) point.x;
                    theBytes.add((byte) ((posx >> 8) & 0xff));
                    theBytes.add((byte) (posx & 0xff));

                    int posy = (int) point.y;
                    theBytes.add((byte) ((posy >> 8) & 0xff));
                    theBytes.add((byte) (posy & 0xff));

                    if (point.blanked) {
                        theBytes.add((byte) 0x40);
                    } else {
                        theBytes.add((byte) 0);
                    }
                    theBytes.add((point.palIndex));
                }
            }


            //Ilda V4: 3D, BGR (why not RGB? Because reasons)
            if (ildaVersion == 4) {
                for (IldaPoint point : frame.points) {
                    int posx = (int) point.x;
                    theBytes.add((byte) ((posx >> 8) & 0xff));
                    theBytes.add((byte) (posx & 0xff));

                    int posy = (int) point.y;
                    theBytes.add((byte) ((posy >> 8) & 0xff));
                    theBytes.add((byte) (posy & 0xff));

                    int posz = (int) point.z;
                    theBytes.add((byte) ((posz >> 8) & 0xff));
                    theBytes.add((byte) (posz & 0xff));

                    if (point.blanked) {
                        theBytes.add((byte) 0x40);
                    } else {
                        theBytes.add((byte) 0);
                    }

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

            //Ilda V5: 2D, BGR
            if (ildaVersion == 5) {
                for (IldaPoint point : frame.points) {
                    int posx = (int) point.x;
                    theBytes.add((byte) ((posx >> 8) & 0xff));
                    theBytes.add((byte) (posx & 0xff));

                    int posy = (int) point.y;
                    theBytes.add((byte) ((posy >> 8) & 0xff));
                    theBytes.add((byte) (posy & 0xff));


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
