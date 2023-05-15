class CircleWaveEffect extends Effect {

  int hue;
  float radius;

  public CircleWaveEffect() {
    hue = (int) random(255);
    radius = random(width/2)+10;
  }

  @Override
    void display(IldaRenderer r) {
    waveform.analyze();
    float intensity = amplitude.analyze();
    float angle = mousePressed ? HALF_PI : TAU/samples;
    r.colorMode(HSB);


    r.resetMatrix();
    r.translate(width/2, height/2);
    r.beginShape(LINE);
    for (int i = 0; i < samples; i++) {
      float data = abs(waveform.data[i]);
      float offset = data*radius+radius/4;
      r.stroke(hue+intensity*50, 255, map(data, 0, 0.5, 25, 255));
      r.vertex(offset, offset);
      r.rotate(angle);
    }
    r.endShape();
  }
}
