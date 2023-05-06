/*
Modified sketch from https://github.com/BarneyWhiteman/CodingChallenges/tree/master/cc5_pulse/pulse
 */


import be.cmbsoft.ilda.*;
import be.cmbsoft.laseroutput.*;

// Use graphic calls on the IldaRenderer object to create laser art
IldaRenderer r;

// The output can receive laser art from the renderer and send it to a laser
LaserOutput output;

boolean showPointCount = true;

ArrayList<Particle> p = new ArrayList<Particle>();

void setup() {
  size(720, 720, P3D);

  r = new IldaRenderer(this);

  //In the overwrite mode, the renderer will keep on drawing to the same frame instead of storing a frame array.
  //Since we're sending the generated frames straight to LSX there's no need to store them in the renderer
  //so it can keep on reusing the same frame for better memory management.
  //If we were saving the animation to an ILDA file this line should be removed.
  r.setOverwrite(true);

  r.setOptimise(false);

  // The LSX OSC output sends frames over OSC to LSX.
  output = new LsxOscOutput(1, // Timeline (projector number)
    10, // Frame catalog index
    "127.0.0.1", // IP address of computer running LSX
    10000 // Port of the LSX OSC server. This can be changed using Setup >> Remote Control >> OSC Setup >> Listening port.
    );

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
  // Retrieve the freshly created frame from the renderer
  IldaFrame currentFrame = r.getCurrentFrame();

  // This will display the laser frame in the Processing window
  currentFrame.renderFrame(this);

  // Display the point count on the screen, toggle with "P" on the keyboard.
  // It's a good idea to limit point count to 1500 points to reduce flickering.
  if (showPointCount) {
    int pointCount = currentFrame.getPointCount();
    fill(255);
    if (pointCount > 1500) {
      fill(255, 0, 0);
    }
    text(pointCount, 20, 20);
  }
  // This will send the laser frame to the laser
  output.project(currentFrame);
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


void keyPressed() {
  // Toggle the point count display
  if (key == 'P' || key == 'p') {
    showPointCount = !showPointCount;
  }
}

void exit() {
  // It is a good idea to clear output to the projector when exiting
  output.sendEmptyFrame();
  super.exit();
}
