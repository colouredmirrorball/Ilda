import be.cmbsoft.ilda.*;
import be.cmbsoft.laseroutput.*;

/*
 * A selection of beam effects. These look great with a bit of fog.
 * Controls:
 *   Left/right arrow keys: select effect
 *   Mouse buttons: modify effect (dependent on effect)
 *   Left mouse button: pick new random colours
 *   P: toggle displaying of point count on canvas
 */

IldaRenderer r;
LsxOscOutput output;
Effect currentEffect;

color firstColor;
color secondColor;

color newFirstColor;
color newSecondColor;
float colorSpeed = 0.075f;

int effectIndex = 0;
int amountOfEffects = 4;

boolean showPointCount = false;

void setup() {

  // Since the projection surface of the laser effect is a square, use a square canvas in Processing
  size(800, 800, P3D);

  // The IldaRenderer object can be used to draw on, similar to a PGraphics object
  r = new IldaRenderer(this);

  r.setOverwrite(true);

  // The LSX OSC output sends frames over OSC to LSX.
  output = new LsxOscOutput(1, // Timeline (projector number)
    10, // Frame catalog index
    "127.0.0.1", // IP address of computer running LSX
    10000 // Port of the LSX OSC server. This can be changed using Setup >> Remote Control >> OSC Setup >> Listening port.
    );

  // Use the arrow keys to switch between effects.
  currentEffect = new BeamEffect();
  colorMode(HSB);
  assignRandomColors();
}

void draw() {
  background(0);

  lerpColors();

  r.beginDraw();
  r.background();

  currentEffect.display(r);
  r.endDraw();
  IldaFrame currentFrame = r.getCurrentFrame();
  currentFrame.renderFrame(this);

  if (showPointCount) {
    int pointCount = currentFrame.getPointCount();
    fill(255);
    text(pointCount, 20, 20);
  }

  output.project(currentFrame);
}

void mousePressed() {
  if (mouseButton == LEFT) {
    assignRandomColors();
  }
  currentEffect.mouse();
}

void keyPressed() {
  if (key == CODED) {
    if (keyCode == LEFT) {
      previousEffect();
    } else if (keyCode == RIGHT) {
      nextEffect();
    }
  }
  if (key == 'P' || key == 'p') {
    showPointCount = !showPointCount;
  }
}

void exit() {
  // For safety, disable laser output when exiting
  output.sendEmptyFrame();
  super.exit();
}

void nextEffect() {
  setEffect(++effectIndex >= amountOfEffects ? effectIndex = 0 : effectIndex);
}

void previousEffect() {
  setEffect(--effectIndex < 0 ? effectIndex = amountOfEffects-1 : effectIndex);
}

void setEffect(int index) {
  switch(index) {
  case 0:
  default:
    currentEffect = new BeamEffect();
    break;
  case 1 :
    currentEffect = new ConeEffect();
    break;
  case 2:
    currentEffect = new SineEffect();
    break;
  case 3:
    currentEffect = new LineEffect();
    break;
  }
}

void assignRandomColors() {
  newFirstColor = randomColor();
  newSecondColor = randomColor();
}

void lerpColors() {
  // Fade nicely between the new color and the old one
  firstColor = lerpColor(firstColor, newFirstColor, colorSpeed);
  secondColor = lerpColor(secondColor, newSecondColor, colorSpeed);
}

color randomColor() {
  // Use Gaussian distribution to favor saturated colours
  return color((int) random(255), (int) 255-255*randomGaussian(), 255);
}
