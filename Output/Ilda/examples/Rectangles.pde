import ilda.*;

ilda ilda;
IldaRenderer r;

ArrayList<Rectangle> rects = new ArrayList<Rectangle>();

void setup()
{
  size(600, 600, P3D);
  ilda = new ilda(this);
  r = new IldaRenderer(ilda);

  for (int i =0; i < 5; i++)
  {
    rects.add(new Rectangle());
  }
}

void draw()
{
  background(0);
  r.beginDraw();
  for (Rectangle rect : rects)
  {
    rect.update();
    r.stroke(rect.colour);
    r.rect(rect.x, rect.y, 20, 20);
  }
  r.endDraw();
  
  
  r.getCurrentFrame().renderFrame(this, false);  
  
  fill(255);
  stroke(255);
  text("Frames: " + r.getFramesAmount(), 40, 40);
}

void mouseClicked()
{
  rects.add(new Rectangle(mouseX, mouseY));
}

void keyPressed()
{
  if(key == 'c') rects.clear();
  if(key == 'd') r.clearAllFrames();
  if(key == 's') ilda.writeFile(r.getFrames(),  ""); //Set output path here
}

class Rectangle
{
  float x, y;
  float velx, vely;
  int colour;
  Rectangle()
  {
    this( random(width),  random(height));
  }

  Rectangle(float x, float y)
  {
    this.x = x;
    this.y = y;
    velx = random(-5,5);
    vely = random(-5,5);
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
