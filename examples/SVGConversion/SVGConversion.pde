import ilda.*; //<>// //<>// //<>// //<>// //<>//

IldaRenderer r;
PShape s;

String error = "";
boolean errorHappened = false;
boolean rerender = false;
void setup()
{
  size(800, 800, P3D);

  r = new IldaRenderer(this);
  r.setOverwrite(true);
  textSize(20);
}

void draw()
{
  background(0);
  if (r.getFramesAmount() > 0) r.getCurrentFrame().renderFrame(this, true, 0, 0);
  fill(255);
  text("Hit O to open an SVG file", 10, 20);
  text("Hit S to save as Ilda file", 10, 45);
  if (errorHappened)
  {
    fill(255, 0, 0);
    text("An error occurred: " + error, 10, 70);
  }
  if (s != null)
  {
    shape(s, 0, 0);
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
  r.clearAllFrames();
  r.beginDraw();
  r.background();


  try
  {
    s = loadShape(file.getAbsolutePath());
    r.shape(s, 0, 0);
  }
  catch(Exception e)
  {
    e.printStackTrace();
    errorHappened = true;
    error = "Something went wrong when trying to render SVG (" + e.getMessage() + ")";
  }

  r.endDraw();

  rerender = true;
}

void ildaSelected(File file)
{
  if (file == null)
  {
    errorHappened = true;
    error = "No valid output file specified, please try again.";
    return;
  }
  IldaWriter.writeFile(file.getAbsolutePath(), r.getFrames(), 4);
}
