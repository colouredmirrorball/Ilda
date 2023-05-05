import be.cmbsoft.ilda.*;
import be.cmbsoft.laseroutput.*;

IldaRenderer r;
LsxOscOutput output;

String content;

float offset;
color colour;

boolean showPointCount = true;

void setup() {

  // Since the projection surface of the laser effect is a square, use a square canvas in Processing
  size(800, 800, P3D);

  // The IldaRenderer object can be used to draw on, similar to a PGraphics object
  r = new IldaRenderer(this);

  // Because we are continuously sending to a laser in real time, we don't want to keep everything that's been rendered
  // in memory. That's what this method does: it tells the renderer to keep writing to the same frame instead of making
  // a new frame every time we call r.beginDraw(). If we would want to export the animation to a file, this should be
  // false.
  r.setOverwrite(true);

  // The LSX OSC output sends frames over OSC to LSX.
  output = new LsxOscOutput(1, // Timeline (projector number)
    10, // Frame catalog index
    "127.0.0.1", // IP address of computer running LSX
    10000 // Port of the LSX OSC server. This can be changed using Setup >> Remote Control >> OSC Setup >> Listening port.
    );

  // Fetch some data from Wikipedia
  JSONObject json = loadJSONObject("https://en.wikipedia.org/api/rest_v1/page/random/summary");
  content = json.getString("extract");
  println(json.getString("title"));
  println(content);

  colorMode(HSB);
  colour = color(random(255), 255, 255);

  // Initialise some parameters in the renderer
  r.beginDraw();
  r.stroke(colour);
  r.textSize(height/4);
  r.endDraw();

  colorMode(RGB);

  offset = width;
}

void draw() {
  // Clear the Processing window
  background(0);

  // It is required to call beginDraw() and endDraw() on the laser renderer
  r.beginDraw();

  // Reset the frame
  r.background();

  // Draw some text
  r.text(content, 10+offset, height/2);

  // Calling beginDraw() requires calling endDraw()
  r.endDraw();
  offset-=10;
  IldaFrame currentFrame = r.getCurrentFrame();

  // This will display the laser frame in the Processing window
  currentFrame.renderFrame(this);

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

void keyPressed() {
  if (key == 'P' || key == 'p') {
    showPointCount = !showPointCount;
  }
}

void exit() {
  // It is a good idea to clear output to the projector when exiting
  output.sendEmptyFrame();
  super.exit();
}