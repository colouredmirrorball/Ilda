/*
Modified sketch from https://github.com/BarneyWhiteman/CodingChallenges/tree/master/cc5_pulse/pulse
 */


import netP5.*;
import oscP5.*;

import ilda.*;

OscP5 osc;
NetAddress target = new NetAddress("127.0.0.1", 10000);


IldaRenderer r;

ArrayList<Particle> p = new ArrayList<Particle>();

void setup() {
  size(720, 720, P3D);
  osc = new OscP5(this, 12000);

  r = new IldaRenderer(this);
  
  //In the overwrite mode, the renderer will keep on drawing to the same frame instead of storing a frame array.
  //Since we're sending the generated frames straight to LSX there's no need to store them in the renderer
  //so it can keep on reusing the same frame for better memory management.
  //If we were saving the animation to an ILDA file this line should be removed.
  r.setOverwrite(true);
  
  //By default, optimisation is turned on
  //r.setOptimise(false);
  r.colorMode(HSB);

  surface.setLocation(20, 20);
}

void draw() {
  background(0);
  
  //Always call beginDraw() on the renderer
  r.beginDraw();
  
  //Remove the contents of the frame to start with a blank canvas
  r.background();
  
  //Loops over all particles
  for (int i = p.size() - 1; i >= 0; i --) 
  {
    p.get(i).update();
    if (p.get(i).dead) 
    {
      p.remove(i);
      continue;
    }
    //Displays the particle on the IldaRenderer object
    p.get(i).show(r);
  }
  r.endDraw();
  r.getCurrentFrame().renderFrame(this, true);  


  //This line sends the frame to LSX over OSC
  ildaframeToLsx(r.getCurrentFrame(), 1, 10, target);
}

void mousePressed() 
{
  burst(10);
}

void burst(int num) {
  int c = int(random(255));
  for (int i = 0; i < num; i ++) {
    float a = int(random(8)) * PI/4;
    p.add(new Particle(mouseX, mouseY, a, c));
  }
}