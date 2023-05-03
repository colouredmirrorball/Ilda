import be.cmbsoft.ilda.*;
import be.cmbsoft.laseroutput.*;

IldaRenderer r;
LsxOscOutput[] outputs;
Effect currentEffect;

color firstColor;
color secondColor;

void setup() {
  size(800, 800, P3D);
  r = new IldaRenderer(this);
  outputs = new LsxOscOutput[20];
  for (int i = 0; i < 20; i++) {
    //for (int j = 0; j < 20; j++) {
    outputs[i] = new LsxOscOutput(i/20+1, i%20+10, "127.0.0.1", 10000);
    println(i/20, i%20);
    //}
  }
  //output = new LsxOscOutput(1, 10, "127.0.0.1", 10000);
  currentEffect = new BeamEffect();
  colorMode(HSB);
  assignRandomColors();
}

void draw() {
  background(0);
  r.beginDraw();
  currentEffect.display(r);
  r.endDraw();
  IldaFrame currentFrame = r.getCurrentFrame();
  currentFrame.renderFrame(this);
  for (LsxOscOutput output : outputs) {
    output.project(currentFrame);
  }
}

void mousePressed() {
  if (mouseButton == LEFT) {
    assignRandomColors();
  }
  currentEffect.mouse();
}

void assignRandomColors() {
  firstColor = color((int) random(255), (int) random(255), 255);
  secondColor = color((int) random(255), (int) random(255), 255);
}
