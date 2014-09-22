package Ilda;

import java.util.ArrayList;

/**
 * Created by florian on 20/09/2014.
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
}
