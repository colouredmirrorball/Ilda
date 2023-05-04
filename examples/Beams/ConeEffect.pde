class ConeEffect extends Effect {

  int amount = 6;

  float radius = 50;
  float effectRadius = 0.8f;
  float speed = 1f;

  float oldRadius = radius;
  float oldEffectRadius = effectRadius;
  float oldSpeed = 0;

  float effectSpeed = 0.1f;
  double previousTime = millis();
  double time =0;

  public ConeEffect() {
    randomize();
    randomizeAmount();
  }

  void display(IldaRenderer renderer) {
    renderer.setEllipseDetail(0.3);
    renderer.setEllipseCorrection(1);
    oldRadius = oldRadius + (radius-oldRadius)*effectSpeed;
    oldEffectRadius = oldEffectRadius + (effectRadius - oldEffectRadius)*effectSpeed;
    oldSpeed = oldSpeed + (speed - oldSpeed)*effectSpeed;
    time = time + (millis() - previousTime)*oldSpeed;
    previousTime = millis();

    for (int i = 0; i < amount; i++) {

      double phase = TWO_PI* (map(i, 0, amount, 0, 1) + time);
      renderer.stroke( i % 2 == 0 ? firstColor : secondColor);
      renderer.ellipse((float)(width*(0.5+0.5*oldEffectRadius*Math.sin(phase))), (float)( height*(0.5+0.5*oldEffectRadius*Math.cos(phase))), oldRadius, oldRadius);
    }
  }

  void mouse() {
    if (mouseButton == RIGHT) {
      randomize();
    } else if (mouseButton == CENTER) {
      randomizeAmount();
    }
  }

  void randomize() {
    radius = random(20, 100);
    effectRadius = random(0.2, 0.9);
    speed = (random(1) < 0.5 ? -1 : 1) * random(0.00005, 0.0003);
  }

  void randomizeAmount() {
    amount = (int) random(1, 6)*2;
  };
}
