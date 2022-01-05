package ilda;

import processing.core.PVector;

import java.util.Objects;

/**
 * A point of an ilda frame. Position is stored in a PVector with bounds [-1..1]. Colour is stored in an integer which
 * is a 32-bit number: the first eight bits are not used, the second eight bits are red (0-255), the next eight
 * represent green and the last eight bits are blue. This is the "official" colour representation: points also store a
 * palIndex but this is only used to set the colour of a palette, never to render it to a screen. A point also has a
 * blanked flag which determines if the point is off or on.
 */
public class IldaPoint
{
    protected PVector position;
    protected int colour;
    protected boolean blanked;
    protected byte palIndex;

    /**
     * Constructor for an IldaPoint.
     *
     * @param position a Processing PVector with the position of the newly created point: rescale the coordinates, so
     *                 they're in [-1,1]! (0 = center)
     * @param red      Integer between 0-255
     * @param green
     * @param blue
     * @param blanked  True if the point should not be on or displayed
     */
    public IldaPoint(PVector position, int red, int green, int blue, boolean blanked)
    {
        floatsToXYZ(position.x, position.y, position.z);
        setColour(red, green, blue);

        this.blanked = blanked;
    }

    public IldaPoint(float x, float y, float z, int red, int green, int blue, boolean blanked)
    {
        floatsToXYZ(x, y, z);
        setColour(red, green, blue);
        this.blanked = blanked;
    }

    /**
     * @param paletteIndex A number corresponding to a colour in a palette, should be 0-255.
     */

    public IldaPoint(float x, float y, float z, int paletteIndex, boolean blanked)
    {
        floatsToXYZ(x, y, z);
        palIndex = (byte) paletteIndex;
        this.blanked = blanked;

    }

    public IldaPoint(IldaPoint point)
    {
        position = new PVector(point.position.x, point.position.y, point.position.z);
        colour = point.colour;
        blanked = point.blanked;
        palIndex = point.palIndex;
    }

    private void floatsToXYZ(float x, float y, float z)
    {
        this.position = new PVector(x, y, z);
    }

    /**
     * Change this point's colour using RGB values
     */

    public void setColour(int red, int green, int blue)
    {
        if (red > 255)
            red = 255;
        if (green > 255)
            green = 255;
        if (blue > 255)
            blue = 255;
        if (red < 0)
            red = 0;
        if (green < 0)
            green = 0;
        if (blue < 0)
            blue = 0;

        colour = ((red & 0xFF) << 16) + ((green & 0xFF) << 8) + (blue & 0xFF);
    }

    /**
     * This method picks the best fitting palette colour that matches this point's RGB value. The palette index of this
     * point is not set by this method, this needs to be done separately if required.
     *
     * @param palette the palette
     * @return the index in the palette this point's colour matches best
     */

    public int getBestFittingPaletteColourIndex(IldaPalette palette)
    {
        int index = 0;
        double distance = 1000;
        byte red = getRed();
        byte green = getGreen();
        byte blue = getBlue();

        int i = 0;
        for (int c : palette.colours)
        {
            byte cred = getRed(c);
            byte cgreen = getGreen(c);
            byte cblue = getBlue(c);
            double d = Math.pow(cred - red, 2) + Math.pow(cgreen - green, 2) + Math
                    .pow(cblue - blue, 2);
            if (d < distance)
            {
                distance = d;
                index = i;
            }
            i++;

        }
        return index;
    }

    public byte getRed()
    {
        return getRed(colour);
    }

    public byte getGreen()
    {
        return getGreen(colour);
    }

    public byte getBlue()
    {
        return getBlue(colour);
    }

    private byte getRed(int c)
    {
        return (byte) ((c >> 16) & 0xFF);
    }

    private byte getGreen(int colour)
    {
        return (byte) ((colour >> 8) & 0xFF);
    }

    private byte getBlue(int colour)
    {
        return (byte) (colour & 0xFF);
    }

    /**
     * Change this point's colour using a palette and a palette index
     *
     * @param paletteIndex The position of the colour in the palette this point should change to
     * @param palette      The palette in which this colour is
     */

    public void setColour(int paletteIndex, IldaPalette palette)
    {
        colour = palette.colours.get(paletteIndex);
    }

    /**
     * Returns the point's position rescaled according to the frameWidth and frameHeight parameters
     *
     * @param frameWidth  the width of the target PGraphics
     * @param frameHeight the height of the target PGraphics
     * @param frameDepth  the depth of the target PGraphics
     * @return a PVector with the position according to the received dimensions.
     */

    public PVector getPosition(float frameWidth, float frameHeight, float frameDepth)
    {
        return new PVector(frameWidth * (position.x * 0.5f + 0.5f),
                frameHeight * (position.y * 0.5f + 0.5f), frameDepth * (position.z * 0.5f + 0.5f));
    }

    public PVector getPosition()
    {
        return position;
    }

    /**
     * The position should be normalised so that the fields x, y and z of the argument PVector are always in the
     * interval -1..1
     *
     * @param position the new position
     */

    public void setPosition(PVector position)
    {
        this.position = position;
    }

    /**
     * The position should be normalised so that x, y and z are between -1 and 1
     *
     * @param x new X position
     * @param y new Y position
     * @param z new Z position
     */

    public void setPosition(float x, float y, float z)
    {
        position.set(x, y, z);
    }

    public float getX()
    {
        return position.x;
    }

    public float getY()
    {
        return position.y;
    }

    public float getZ()
    {
        return position.z;
    }

    public int getColour()
    {
        return colour;
    }

    /**
     * Change this point's colour using a palette and its palette index
     *
     * @param palette The palette the point should use to change its colour
     */

    public void setColour(IldaPalette palette)
    {
        colour = palette.colours.get(palIndex);
    }

    public boolean isBlanked()
    {
        return blanked;
    }

    /**
     * Set the blanked flag of a point Blanked means the lasers will not turn on at this point but the scanners will
     * move to this position
     *
     * @param blanked should the point be blanked?
     */

    public void setBlanked(boolean blanked)
    {
        this.blanked = blanked;
    }

    public byte getPalIndex()
    {
        return palIndex;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IldaPoint point = (IldaPoint) o;
        return colour == point.colour
                && blanked == point.blanked
                && palIndex == point.palIndex
                && position != null && position.equals(point.position);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(position, colour, blanked, palIndex);
    }
}
