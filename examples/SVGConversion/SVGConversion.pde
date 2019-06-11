import ilda.*;

IldaRenderer r;
PShape s;

String error = "";
boolean errorHappened = false;



void setup()
{
  size(800, 800, P3D);

  //r is the IldaRenderer object where the SVG will be drawn onto
  r = new IldaRenderer(this);

  textSize(20);
}

void draw()
{
  background(0);

  //Draw the frame on the canvas
  if (r.getFramesAmount() > 0) r.getCurrentFrame().renderFrame(this, true, 0, 0);

  //Display text
  fill(255);
  text("Hit O to open an SVG file", 10, 20);
  text("Hit S to save as Ilda file", 10, 45);
  if (errorHappened)
  {
    fill(255, 0, 0);
    text("An error occurred: " + error, 10, 70);
  }
}

void keyPressed()
{
  errorHappened = false;
  if (key == 'o' || key == 'O')
  {
    selectInput("Select an SVG file", "svgSelected");
  }
  if (key == 's' || key == 'S')
  {
    if (s ==  null)
    {
      errorHappened = true;
      error = "No SVG file found to convert!";
    } else selectOutput("Save to ILDA file", "ildaSelected");
  }
}

void svgSelected(File file)
{
  if (file == null)
  {
    errorHappened = true;
    error = "No file found.";
    return;
  }

  //Reset the IldaRenderer object
  r.clearAllFrames();
  r.beginDraw();
  r.background();

  try
  {
    //Retrieve the SVG from disk
    s = loadShape(file.getAbsolutePath());

    //Render the SVG to the IldaRenderer (which acts as a PGraphics)
    r.shape(s, 0, 0);
  }
  catch(Exception e)
  {
    e.printStackTrace();
    errorHappened = true;
    error = "Something went wrong when trying to render SVG (" + e.getMessage() + ")";
  }

  //Don't forget
  r.endDraw();
  println(r.getCurrentFrame().getPoints().size());
  println(r.getFramesAmount());
}

void ildaSelected(File file)
{
  if (file == null)
  {
    errorHappened = true;
    error = "No valid output file specified, please try again.";
    return;
  }

  println(r.getFrames().size());

  //Writing an Ilda file is as simple as that
  IldaWriter.writeFile(file.getAbsolutePath(), r.getFrames(), 4);
}
