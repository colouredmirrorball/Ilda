/*
Modified sketch from https://github.com/BarneyWhiteman/CodingChallenges/tree/master/cc5_pulse/pulse
*/


import netP5.*;
import oscP5.*;

import ilda.*;

OscP5 osc;
NetAddress target = new NetAddress("127.0.0.1", 10000);

Ilda ilda;
IldaRenderer r;

ArrayList<Particle> p = new ArrayList<Particle>();

void setup() {
  size(720, 720, P3D);
  osc = new OscP5(this, 12000);
  ilda = new Ilda(this);
  r = new IldaRenderer(ilda);
  r.setOverwrite(true);
  r.colorMode(HSB);
  noStroke();
  //burst(10);
  surface.setLocation(20,20);
}

void draw() {
  background(0);
  r.beginDraw();
  r.background();
  for(int i = p.size() - 1; i >= 0; i --) {
    p.get(i).update();
    if(p.get(i).dead) {
      p.remove(i);
      continue;
    }
    p.get(i).show(r);
  }
  r.endDraw();
  r.getCurrentFrame().renderFrame(this, false);  


  //This line sends the frame to LSX over OSC
  ildaframeToLsx(r.getCurrentFrame(), 1, 10, target);
}

void mousePressed() {
  //if(key == ' ') {
    burst(10);
  //}
}

void burst(int num) {
  int c = int(random(255));
  for(int i = 0; i < num; i ++) {
    float a = int(random(8)) * PI/4;
    p.add(new Particle(mouseX, mouseY, a, c));
  }
}