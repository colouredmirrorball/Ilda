import Ilda.*;

Ilda ilda;
ArrayList<IldaFrame> frames = new ArrayList();

void setup()
{
  size(600, 600);
  ilda = new Ilda(this);
  try
  {
    //Insert a path here
    frames = ilda.readFile("");
  }
  catch(Exception e)
  {
    println("error");
    println(e);
  }
}

void draw()
{
  background(0);
  ArrayList<String> status = ilda.status;
  try
  {
    if (frames.size() > 0) image(frames.get(0).renderFrame(width, height), 0, 0);
  }
  catch(Exception e)
  {
    status.add("Error on drawing");
  }
  fill(255);
  textAlign(LEFT);

  for (int i = 0; i < status.size (); i++)
  {
    fill(255);
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
    //Insert a path here
    ilda.writeFile(frames, "");
  }
}
