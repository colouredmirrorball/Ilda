import ilda.*;

ArrayList<IldaFrame> frames;



void setup()
{
  size(800, 800, P3D);
  try
  {
    //Read the Ilda file and retrieve the frames it contains
    frames = IldaReader.readFile(sketchPath()+"\\Circles.ild");
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
  frames.get(frameCount%frames.size()).renderFrame(this);
}