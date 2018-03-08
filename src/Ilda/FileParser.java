package Ilda;

import java.io.File;

import static java.nio.file.Files.readAllBytes;

public class FileParser
{
    protected String location;
    protected byte[] b;

    public FileParser(String location)
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
}
