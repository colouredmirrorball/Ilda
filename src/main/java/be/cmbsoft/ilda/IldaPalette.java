package be.cmbsoft.ilda;

import java.util.ArrayList;
import java.util.List;

/**
 * A Palette is a collection of colours. Points in a format 0 or 1 file have an index referring
 * to a colour in a palette.
 * Changing a palette results in changing the colours of a frame.
 */
public class IldaPalette {
    String name;
    String companyName;
    int totalColors;
    int paletteNumber;
    int scannerHead;
    List<Integer> colours = new ArrayList<>();

    public void addColour(int r, int g, int b) {
        colours.add(((r & 0xFF) << 16) + ((g & 0xFF) << 8) + (b & 0xFF));
    }

    /**
     * Colors are stored as 32-bit integers, first eight bits are not used, next eight bits are red,
     * following eight bits are green and last eight bits are blue
     *
     * @param index number referring to the palette
     * @return the color in the aforementioned format
     */

    public int getColour(int index) {
        if (index >= colours.size() || index < 0) {return 0;} else {return colours.get(index);}
    }

    /**
     * Converts the palette to bytes which can be added in front of an ilda file or stored
     * separately
     *
     * @return array of bytes with ilda-compliant palette
     */

    public byte[] paletteToBytes() {
        byte[] result;
        ArrayList<Byte> theBytes;
        theBytes = new ArrayList<>();

        IldaWriter.writeCommonIldaHeader(theBytes);
        theBytes.add((byte) 2); //version byte

        for (int i = 0; i < 8; i++)    //Bytes 9-16: Name
        {
            char letter;
            if (name.length() < i + 1) {letter = ' ';} else {letter = name.charAt(i);}
            theBytes.add((byte) letter);
        }

        if (companyName == null)   //Bytes 17-24: Company Name
        {
            IldaWriter.writeCustomCompanyName(theBytes);
        } else {
            for (int i = 0; i < 8; i++) {
                char letter;
                if (companyName.length() < i + 1) {letter = ' ';} else {
                    letter = companyName.charAt(i);
                }
                theBytes.add((byte) letter);
            }
        }

        int totalSize = colours.size();
        if (totalSize < 1) {
            result = new byte[0];
        } else {
            if (totalSize > 255) {totalSize = 256;}
            theBytes
                .add((byte) ((totalSize >> 8) & 0xff));              //Bytes 25-26: total colours
            theBytes.add((byte) (totalSize & 0xff)); //Limited to 256 so byte 25 is redundant
            //Bytes 27-28: Palette number
            theBytes.add((byte) 0);    //This better be correct
            theBytes.add((byte) 0);
            theBytes.add((byte) 0);    //Bytes 29-30: Future
            theBytes.add((byte) 0);
            theBytes.add((byte) scannerHead); //Byte 31: Scanner head
            theBytes.add((byte) 0);    //Also Future
            for (int i = 0; i < Math.min(256, colours.size()); i++)    //Rest: colour data
            {
                int colour = colours.get(i);
                theBytes.add((byte) ((colour >> 16) & 0xFF));
                theBytes.add((byte) ((colour >> 8) & 0xFF));
                theBytes.add((byte) (colour & 0xFF));
            }
            byte[] bt = new byte[theBytes.size()];
            for (int i = 0; i < theBytes.size(); i++) {
                bt[i] = theBytes.get(i);
            }
            result = bt;
        }

        return result;
    }

    /**
     * Converts this palette to the standard 64 color palette used in most programs
     */

    public void setDefaultPalette() {
        name = "Ilda64";
        companyName = "Ilda4P5";
        totalColors = 64;
        paletteNumber = 0;
        scannerHead = 0;

        colours.clear();
        addColour(255, 0, 0);
        addColour(255, 16, 0);
        addColour(255, 32, 0);
        addColour(255, 48, 0);
        addColour(255, 64, 0);
        addColour(255, 80, 0);
        addColour(255, 96, 0);
        addColour(255, 112, 0);
        addColour(255, 128, 0);
        addColour(255, 144, 0);
        addColour(255, 160, 0);
        addColour(255, 176, 0);
        addColour(255, 192, 0);
        addColour(255, 208, 0);
        addColour(255, 224, 0);
        addColour(255, 240, 0);
        addColour(255, 255, 0);
        addColour(224, 255, 0);
        addColour(192, 255, 0);
        addColour(160, 255, 0);
        addColour(128, 255, 0);
        addColour(96, 255, 0);
        addColour(64, 255, 0);
        addColour(32, 255, 0);
        addColour(0, 255, 0);
        addColour(0, 255, 32);
        addColour(0, 255, 64);
        addColour(0, 255, 96);
        addColour(0, 255, 128);
        addColour(0, 255, 160);
        addColour(0, 255, 192);
        addColour(0, 255, 224);
        addColour(0, 130, 255);
        addColour(0, 114, 255);
        addColour(0, 104, 255);
        addColour(10, 96, 255);
        addColour(0, 82, 255);
        addColour(0, 74, 255);
        addColour(0, 64, 255);
        addColour(0, 32, 255);
        addColour(0, 0, 255);
        addColour(32, 0, 255);
        addColour(64, 0, 255);
        addColour(96, 0, 255);
        addColour(128, 0, 255);
        addColour(160, 0, 255);
        addColour(192, 0, 255);
        addColour(224, 0, 255);
        addColour(255, 0, 255);
        addColour(255, 32, 255);
        addColour(255, 64, 255);
        addColour(255, 96, 255);
        addColour(255, 128, 255);
        addColour(255, 160, 255);
        addColour(255, 192, 255);
        addColour(255, 224, 255);
        addColour(255, 255, 255);
        addColour(255, 224, 224);
        addColour(255, 192, 192);
        addColour(255, 160, 160);
        addColour(255, 128, 128);
        addColour(255, 96, 96);
        addColour(255, 64, 64);
        addColour(15, 32, 32);

    }

}
