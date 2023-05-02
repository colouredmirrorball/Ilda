import ilda.*;
import be.cmbsoft.laseroutput.*;
import java.util.*;

List<IldaFrame> frames;
LsxOscOutput output;


void setup()
{
  size(800, 800, P3D);
  output = new LsxOscOutput(1, 10, "127.0.0.1", 10000);
  try
  {
    //Read the Ilda file and retrieve the frames it contains
    frames = IldaReader.readFile(this, "Circles.ild");
  }
  catch(Exception e)
  {
    //Invalid ILDA file!
    e.printStackTrace();
  }
  println("There are", frames.size(), "frames in the ILDA file");

  frameRate(20);
}

void draw()
{
  background(0);

  //Display the current frame on the Processing canvas
  IldaFrame currentFrame = frames.get(frameCount%frames.size());
  currentFrame.renderFrame(this);
  output.project(currentFrame);
}