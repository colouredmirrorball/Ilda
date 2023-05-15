class WaveformEffect extends Effect {

  int hue;

  public WaveformEffect() {
    hue = (int) random(255);
  }

  @Override
    void display(IldaRenderer r) {
    waveform.analyze();
    float intensity = amplitude.analyze();
    r.stroke(hue, 255, map(intensity, 0, 0.3, 25, 255));

    r.beginShape(LINE);
    for (int i = 0; i < samples; i++) {
      r.vertex(map(i, 0, samples, 0, width), map(waveform.data[i], -1, 1, 0, height));
    }
    r.endShape();
  }
}
