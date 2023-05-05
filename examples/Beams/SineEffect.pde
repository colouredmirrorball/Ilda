class SineEffect extends Effect {

  int amount = 200;

  float frequency = 0;
  float amplitude = 0f;
  float speed = 1f;

  float oldFrequency = frequency;
  float oldAmplitude = amplitude;
  float oldSpeed = 0;

  float effectSpeed = 0.1f;
  double previousTime = millis();
  double time = 0;

  boolean dots = false;
  int shapeKind = LINES;

  public SineEffect() {
    randomize();
  }

  void display(IldaRenderer renderer) {
    oldFrequency = oldFrequency + (frequency-oldFrequency)*effectSpeed*0.2f;
    oldAmplitude = oldAmplitude + (amplitude - oldAmplitude)*effectSpeed;
    oldSpeed = oldSpeed + (speed - oldSpeed)*effectSpeed;
    time = time + (millis() - previousTime)*oldSpeed;
    previousTime = millis();
    renderer.beginShape(shapeKind);

    for (int i = 0; i < amount; i++) {

      double phase = TWO_PI* (map(i-amount/2, 0, amount, 0, oldFrequency) + time);
      renderer.stroke( lerpColor(firstColor, secondColor, abs(map(i, 0, amount, 0, 2)-1)));
      renderer.vertex((width*i)/amount, (float)(height*(0.5+0.5*oldAmplitude*Math.sin(phase))));
    }
    renderer.endShape();
  }

  void mouse() {
    if (mouseButton == RIGHT) {
      randomize();
    } else if (mouseButton == CENTER) {
      toggleDots();
    }
  }

  void randomize() {
    frequency = (int)(7*randomGaussian()+1);
    amplitude = random(0.02, 0.5);
    speed = (random(1) < 0.5 ? -1 : 1) * random(0.00005, 0.0003);
  }

  void toggleDots() {
    if (dots = !dots) {
      shapeKind = POINTS;
      amount = 30;
    } else {
      shapeKind = LINES;
      amount = 200;
    }
  }
}