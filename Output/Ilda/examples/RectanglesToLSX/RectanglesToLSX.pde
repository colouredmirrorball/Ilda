/*
  Example sketch to send laser frames generated by the ilda library to LSX using the OSC protocol.
 For this example to work, LSX has to run on the same computer with an Animation event on timeline 1 set to frame 10.
 The frames generated in this sketch will appear in the Animation event.
 */


import netP5.*;
import oscP5.*;

import ilda.*;

//ilda element
ilda ilda;

//The canvas for laser art, treat this as a PGraphics
IldaRenderer r;

OscP5 osc;
//Change this to the IP and port of LSX
NetAddress lsxLocation = new NetAddress("127.0.0.1", 10000);

//Some bouncy bois
ArrayList<Rectangle> rects = new ArrayList<Rectangle>();

void setup()
{

  size(600, 600, P3D);
  ilda = new ilda(this);
  
  //Create an OSC client, listening to port 12000 (but received messages will be ignored)
  osc = new OscP5(this, 12000);


  r = new IldaRenderer(ilda);
  
  //This line causes the renderer to keep on reusing its current frame, otherwise it would create a new frame in each draw() loop
  r.setOverwrite(true);

  //Add the bouncy bois!
  for (int i =0; i < 5; i++)
  {
    rects.add(new Rectangle());
  }
}

void draw()
{
  background(0);
  
  //To begin creating laser frames, use beginDraw() on the IldaRenderer
  r.beginDraw();
  r.background();    //this line removes the points from the previous frame
  for (Rectangle rect : rects)
  {
    rect.update();    //update the rectangle's position
    r.stroke(rect.colour);  
    r.rect(rect.x, rect.y, 20, 20);    //draw the rectangle on the IldaRenderer, previous line specifies colour
  }
  
  //Don't forget to call endDraw()!
  r.endDraw();

  //The next line displays the laser frame on the Processing canvas
  r.getCurrentFrame().renderFrame(this, false);  


  //This line sends the frame to LSX over OSC
  ildaframeToLsx(r.getCurrentFrame(), 1, 10, lsxLocation);
}

void mouseClicked()
{
  rects.add(new Rectangle(mouseX, mouseY));
}

void keyPressed()
{
  if (key == 'c') rects.clear();
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