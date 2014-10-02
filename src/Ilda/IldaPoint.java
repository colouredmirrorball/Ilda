package Ilda;

import processing.core.PVector;

/**
 * A point of an ilda frame. Location is stored in three shorts (xyz) ranging from -32767 to 32768.
 * Colour is stored in an integer which is a 32 bit number: the first eight bits are not used,
 * the second eight bits are red (0-255), the next eight represent green and the last eight bits are blue.
 * This is the "official" colour representation: points also store a palIndex but this is only used to set
 * the colour of a palette, never to render it to a screen.
 * A point also has a blanked flag which determines if the point is off or on.
 */
public class IldaPoint {
    protected short x, y, z;
    protected int colour;
    protected boolean blanked;
    protected byte palIndex;

    /**
     * Constructor for an IldaPoint.
     *
     * @param position a Processing PVector with the position of the newly created point
     * @param red      Integer between 0-255
     * @param green
     * @param blue
     * @param blanked  True if the point should not be on or displayed
     */
    public IldaPoint(PVector position, int red, int green, int blue, boolean blanked) {
        floatsToXYZ(position.x, position.y, position.z);
        setColour(red, green, blue);

        this.blanked = blanked;
    }

    public IldaPoint(float x, float y, float z, int red, int green, int blue, boolean blanked) {
        floatsToXYZ(x, y, z);
        setColour(red, green, blue);
        this.blanked = blanked;
    }

    public IldaPoint(int x, int y, int z, int red, int green, int blue, boolean blanked) {
        floatsToXYZ((float) x, (float) y, (float) z);
        setColour(red, green, blue);
        this.blanked = blanked;
    }

    /**
     * @param paletteIndex A number corresponding to a colour in a palette, should be 0-255.
     */

    public IldaPoint(float x, float y, float z, int paletteIndex, boolean blanked) {
        floatsToXYZ(x, y, z);
        palIndex = (byte) paletteIndex;
        this.blanked = blanked;

    }

    public IldaPoint(int x, int y, int z, int paletteIndex, boolean blanked) {
        floatsToXYZ((float) x, (float) y, (float) z);
        palIndex = (byte) paletteIndex;
        this.blanked = blanked;
    }

    public IldaPoint(IldaPoint point) {
        x = point.x;
        y = point.y;
        z = point.z;
        colour = point.colour;
        blanked = point.blanked;
        palIndex = point.palIndex;
    }

    @Override
    public IldaPoint clone() {
        IldaPoint point = new IldaPoint(this);
        return point;
    }

    private void floatsToXYZ(float x, float y, float z) {
        this.x = (short) x;
        this.y = (short) y;
        this.z = (short) z;
    }

    /**
     * Change this point's colour using RGB values
     */

    public void setColour(int red, int green, int blue) {
        if (red > 255) red = 255;
        if (green > 255) green = 255;
        if (blue > 255) blue = 255;
        if (red < 0) red = 0;
        if (green < 0) green = 0;
        if (blue < 0) blue = 0;

        colour = ((red & 0xFF) << 16) + ((green & 0xFF) << 8) + ((blue & 0xFF));
    }

    /**
     * Change this point's colour using a palette and its palette index
     *
     * @param palette The palette the point should use to change its colour
     */

    public void setColour(IldaPalette palette) {
        colour = palette.colours.get(palIndex);
    }

    /**
     * Change this point's colour using a palette and a palette index
     *
     * @param paletteIndex The position of the colour in the palette this point should change to
     * @param palette      The palette in which this colour is
     */

    public void setColour(int paletteIndex, IldaPalette palette) {

    }

    /**
     * This method picks the best fitting palette colour that matches this point's RGB value.
     * The palette index of this point is not set by this method, this needs to be done separately if required.
     *
     * @param palette the palette
     * @return the index in the palette this point's colour matches best
     */

    public int getBestFittingPaletteColourIndex(IldaPalette palette) {
        int index = 0;
        double distance = 1000;
        byte red = (byte) ((colour >> 16) & 0xFF);
        byte green = (byte) ((colour >> 8) & 0xFF);
        byte blue = (byte) (colour & 0xFF);

        int i = 0;
        for (int c : palette.colours) {
            byte cred = (byte) ((c >> 16) & 0xFF);
            byte cgreen = (byte) ((c >> 8) & 0xFF);
            byte cblue = (byte) (c & 0xFF);
            double d = Math.pow(cred - red, 2) + Math.pow(cgreen - green, 2) + Math.pow(cblue - blue, 2);
            if (d < distance) {
                distance = d;
                index = i;
            }
            i++;

        }
        return index;
    }

}
