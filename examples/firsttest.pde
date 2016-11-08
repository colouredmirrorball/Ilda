import Ilda.*;


Ilda ilda;
ArrayList<IldaFrame> frames = new ArrayList();
boolean showBlanking = true;

//The Ilda frame will be rendered to this PGraphics
PGraphics renderedFrame;


void setup()
{
  size(600, 600, OPENGL);
  ilda = new Ilda(this);

  try
  {
    // Insert file path to Ilda file here
    frames = ilda.readFile("");
  }
  catch(Exception e)
  {
    // what did I screw up now
    println("error upon reading");
    e.printStackTrace();
  }
  try
  {
    if (frames.size() > 0) 
    {
      renderedFrame = createGraphics((int) (width*0.5), (int) (height*0.5), OPENGL);
      //It is now necessary to use beginDraw() and endDraw() on the PGraphics buffer used to draw the Ilda frame to
      renderedFrame.beginDraw();
      renderedFrame = frames.get(0).renderFrame(renderedFrame, showBlanking);
      renderedFrame.endDraw();
    }
  }
  catch(Exception e)
  {
    println("Error on drawing: " + e.toString());
    e.printStackTrace();
  }
}

void draw()
{
  background(0);
  fill(255);
  textAlign(LEFT);
  ArrayList<String> status = ilda.status;


  if (renderedFrame != null) 
  {
    frames.get(0).renderFrame(this, showBlanking, 0f, map(mouseX, 0, height, 0, TWO_PI), map(mouseY, 0, width, 0, TWO_PI));
    text("Rendering frame", width-100, 50);
    text("Pixels: " + renderedFrame.pixels.length, width-100, 75);
  }

  for (int i = 0; i < status.size (); i++)
  {
    if (textWidth(status.get(i)) > width-10) 
    {
      String[] brokenText = splitTokens(status.get(i));
      String str1 = "";
      String str2 = "";
      int k = 0;
      for (int j = 0; j < brokenText.length; j++)
      {      
        if (textWidth(str1 + brokenText[j] + " ") > width-10)
        {
          k = j;
          j = brokenText.length;
        } else str1 += brokenText[j] + " ";
      }
      status.set(i, str1);
      for (int j = k; j < brokenText.length; j++)
      {      
        str2 += brokenText[j] + " ";
      }
      status.add(i+1, str2);
    }
    text(status.get(i), 10, height-20*status.size()+20*i);
  }
}

void keyPressed()
{
  if (key == 's')
  {
    println(frames.size() + " " + (frames == null));
    ilda.writeFile(frames, ""); //Insert a path here where to export the Ilda file to
  }

  if (key == 'i')
  {
    frames.get(0).renderFrame(this, width, height).save(""); //Insert a path here where to export an image
  }

  if (key == 'b') showBlanking = !showBlanking;
}
