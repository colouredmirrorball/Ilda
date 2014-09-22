package Ilda;

import java.util.ArrayList;

/**
 * A Palette is a collection of colours. V0 and V1 files have an index referring to a colour in a palette.
 * Changing a palette results in changing the colours of a frame.
 */
public class IldaPalette {
    String name;
    String companyName;
    int totalColors;
    int paletteNumber;
    int scannerHead;

    ArrayList<Integer> colours = new ArrayList<Integer>();

    public IldaPalette() {

    }

    public void addColour(int r, int g, int b) {
        colours.add(r << 16 + g << 8 + b);
    }

    public int getColour(int index) {
        return colours.get(index);
    }

    public byte[] paletteToBytes() {
        ArrayList<Byte> theBytes;
        theBytes = new ArrayList<Byte>();

        theBytes.add((byte) 'I');       //Bytes 1-4: "ILDA"
        theBytes.add((byte) 'L');
        theBytes.add((byte) 'D');
        theBytes.add((byte) 'A');
        theBytes.add((byte) 0);         //Bytes 5-8: Format Code 2
        theBytes.add((byte) 0);
        theBytes.add((byte) 0);
        theBytes.add((byte) 2);


        for (int i = 0; i < 8; i++)    //Bytes 9-16: Name
        {
            char letter;
            if (name.length() < i + 1) letter = ' ';
            else letter = name.charAt(i);
            theBytes.add((byte) letter);
        }


        if (companyName == null)   //Bytes 17-24: Company Name
        {
            theBytes.add((byte) 'I');
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
                if (companyName.length() < i + 1) letter = ' ';
                else letter = companyName.charAt(i);
                theBytes.add((byte) letter);
            }
        }

        int totalSize = colours.size();
        if (totalSize < 1) return null;
        if (totalSize > 255) totalSize = 256;

        theBytes.add((byte) ((totalSize >> 8) & 0xff));              //Bytes 25-26: total colours
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

        return bt;
    }
}
