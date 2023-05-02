package ilda;

public class Utilities
{

    private Utilities()
    {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void logException(Exception exception)
    {
        // TODO figure out best way to log
        exception.printStackTrace();
    }

}
