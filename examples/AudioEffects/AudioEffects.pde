import processing.sound.*;

import be.cmbsoft.ilda.*;
import be.cmbsoft.laseroutput.*;


// Use graphic calls on the IldaRenderer object to create laser art
IldaRenderer r;

// The output can receive laser art from the renderer and send it to a laser
LaserOutput output;

boolean showPointCount = true;

AudioIn input;
Amplitude amplitude;
Waveform waveform;
FFT fft;

AudioIn inputRight;
Amplitude amplitudeRight;

int samples = 200;
int bands = 256;
int maxBand = 16;

int effectIndex = 0;
int amountOfEffects = 4;

Effect currentEffect;

void setup() {
  // Since the projection surface of the laser effect is a square, use a square canvas in Processing
  size(800, 800, P3D);

  input = new AudioIn(this, 0);
  input.start();

  inputRight = new AudioIn(this, 1);
  inputRight.start();

  amplitude = new Amplitude(this);
  amplitude.input(input);

  amplitudeRight = new Amplitude(this);
  amplitudeRight.input(inputRight);

  waveform = new Waveform(this, samples);
  waveform.input(input);

  fft = new FFT(this, bands);
  fft.input(input);

  // The renderer requires just a reference to the sketch
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

  nextEffect();
}

void draw() {


  // Clear the Processing window
  background(0);

  // It is required to call beginDraw() and endDraw() on the laser renderer
  r.beginDraw();

  // Reset the frame
  r.background();

  currentEffect.display(r);

  // Calling beginDraw() requires calling endDraw()
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

void keyPressed() {
  // Toggle the point count display
  if (key == 'P' || key == 'p') {
    showPointCount = !showPointCount;
  }
  if (key == CODED) {
    if (keyCode == LEFT) {
      previousEffect();
    } else if (keyCode == RIGHT) {
      nextEffect();
    }
  }
}

void exit() {
  // It is a good idea to clear output to the projector when exiting
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
    currentEffect = new WaveformEffect();
    break;
  case 1:
    currentEffect = new SpectrumBarsEffect();
    break;
  case 2:
    currentEffect = new VUEffect();
    break;
  case 3:
    currentEffect = new CircleWaveEffect();
    break;
  }
}
