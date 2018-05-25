package ilda;

import java.io.File;

import static java.nio.file.Files.readAllBytes;

class FileParser
{
    protected String location;
    protected byte[] b;

    int position = 0;




    FileParser(String location)
    {
        this.location = location;
        try {
            b = readAllBytes(new File(location).toPath());
        } catch (Exception e) {
            b = null;

        }
    }

    public FileParser(File file)
    {
        this(file.getAbsolutePath());
    }

    byte parseByte()
    {
        return (byte) (b[position++] & 0xff);
    }

    short parseShort()
    {
        return (short) (b[position++] << 8 | (b[position++] & 0xff));
    }

    String parseString(int length)
    {
        StringBuilder out = new StringBuilder();
        for(int i = 0; i < length; i++)
        {
            out.append((char) b[position++]);
        }
        return out.toString();
    }

    void skip(int times)
    {
        for(int i = 0;i <times;i++)
        {
            position++;
        }
    }

    void reset()
    {
        position = 0;
    }
}
