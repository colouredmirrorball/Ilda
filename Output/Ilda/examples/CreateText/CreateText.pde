import Ilda.*;

Ilda ilda;
int s = 200;  //size of the window

void settings()
{
  size(s, s, P3D);
}

void setup()
{

  ilda = new Ilda(this);

  PFont someFont = createFont("Arial", s);  //Specify font and size

  IldaRenderer r = new IldaRenderer(ilda);  //Set up the renderer
  r.beginDraw();
  r.stroke(255);    //Colour
  r.textFont(someFont);
  //r.setTextDetail(0.1);   //gives mixed results - WIP - use at your own risk!
  r.text("C", 0, height-10);  //the string to be printed
  r.endDraw();

  IldaFrame frame = r.getCurrentFrame();    
  println(frame.getPointCount());

  background(0);
  if (frame != null) frame.renderFrame(this, true);   //Draws the frame to the window

  noLoop();

  ilda.writeFile(r.getFrames(), sketchPath() + "\\text.ild");   //Write the frame to an Ilda file
}
