/**
 * Created by florian on 20/09/2014.
 */

package Ilda;

import processing.core.*;

import java.util.ArrayList;

/**
 * This package allows you to render Ilda files straight from Processing!
 */

public class Ilda {
    PApplet parent;
    boolean beta = true;
    String version = "0.0.1";
    public ArrayList<String> status = new ArrayList<String>();

    public Ilda(PApplet parent) {

        this.parent = parent;
        if (beta) parent.println("Ilda for Processing version " + version + " started up successfully.");
    }

    /**
     * Any error, quirk, remark, success and such is stored within the class.
     * Use this method to retrieve it for eg a console-like text box.
     * This is for testing purposes and will get eliminated when stuff gets more mature.
     *
     * @return The status
     */

    public ArrayList<String> getStatus() {
        return status;
    }

    public ArrayList<IldaFrame> readFile(String location) {
        IldaReader reader = new IldaReader(this, location);
        ArrayList<IldaFrame> frames = reader.getFramesFromBytes();
        return frames;
    }

    public void writeFile(ArrayList<IldaFrame> frames, String location) {
        IldaWriter writer = new IldaWriter(this);
        writer.writeFile(location, frames);

    }
}
