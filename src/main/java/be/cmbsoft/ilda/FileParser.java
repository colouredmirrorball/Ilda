package be.cmbsoft.ilda;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;

import processing.core.PApplet;

class FileParser
{

    protected byte[] b;
    protected int    position = 0;

    FileParser(PApplet applet, String location)
    {
        b = Optional.ofNullable(applet).map(appl -> appl.loadBytes(location)).orElseThrow();
    }

    public FileParser(File file) throws FileNotFoundException
    {
        b = PApplet.loadBytes(file);
        if (b == null)
        {
            throw new FileNotFoundException("Error: could not read file at " + file);
        }
    }

    byte parseByte()
    {
        return (byte) (b[position++] & 0xff);
    }

    short parseShort()
    {
        return (short) (b[position++] << 8 | (b[position++] & 0xff));
    }

    String parseString(int length) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < length; i++) {
            out.append((char) b[position++]);
        }
        return out.toString();
    }

    void skip(int times) {
        for (int i = 0; i < times; i++) {
            position++;
        }
    }

    void reset() {
        position = 0;
    }

}
