import ilda.*;
import be.cmbsoft.laseroutput.*;
//The IldaRenderer can be used as a PGraphics to render Ilda files
IldaRenderer r;
LsxOscOutput output    = new LsxOscOutput(1, 10, "127.0.0.1", 10000);

//Some rectangles that bounce around
ArrayList<Rectangle> rects = new ArrayList<Rectangle>();

boolean hideText = false;

void setup()
{
  size(600, 600, P3D);

  //Default constructor of the IldaRenderer
  r = new IldaRenderer(this);

  for (int i =0; i < 5; i++)
  {
    rects.add(new Rectangle());
  }
}

void draw()
{
  background(0);

  //Just like any PGraphics, beginDraw() needs to be called before any graphical operation begins
  r.beginDraw();

  for (Rectangle rect : rects)
  {
    rect.update();

    //Updates colour
    r.stroke(rect.colour);

    //Draws rectangle
    r.rect(rect.x, rect.y, 20, 20);
  }

  //And endDraw needs to be called as well
  r.endDraw();

  //Displays the current IldaFrame on the Processing canvas
  r.getCurrentFrame().renderFrame(this, false);

  if (!hideText)
  {
    fill(255);
    stroke(255);
    text("Frames: " + r.getFramesAmount(), 40, 40);
    text("C - clear all rectangles", 40, 65);
    text("D - empty frame buffer", 40, 90);
    text("S - save frames to ILDA file", 40, 115);
    text("H - hide help text", 40, 140);
  }
  output.project(r);
}

void mouseClicked()
{
  rects.add(new Rectangle(mouseX, mouseY));
}

void keyPressed()
{
  if (key == 'c') rects.clear();
  if (key == 'd') r.clearAllFrames();
  if (key == 's') IldaWriter.writeFile( sketchPath()+"/Rectangles.ild", r.getFrames());
  if (key == 'h') hideText = !hideText;
}

class Rectangle
{
  float x, y;
  float velx, vely;
  int colour;
  Rectangle()
  {
    this( random(width), random(height));
  }

  Rectangle(float x, float y)
  {
    this.x = x;
    this.y = y;
    velx = random(-5, 5);
    vely = random(-5, 5);
    colour = color(random(255), random(255), random(255));
  }

  void update()
  {
    x += velx;
    y += vely;

    if (x < 0)
    {
      velx = -velx;
      x = 0;
    }
    if (x > width-20)
    {
      velx = - velx;
      x = width-20;
    }
    if (y < 0)
    {
      vely = - vely;
      y = 0;
    }
    if (y > height-20)
    {
      vely = - vely;
      y = height-20;
    }
  }
}