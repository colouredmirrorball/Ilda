class SpectrumBarsEffect extends Effect {

  int barWidth;

  public SpectrumBarsEffect() {
    barWidth = width/maxBand;
  }

  @Override
    void display(IldaRenderer r) {
    r.colorMode(HSB);
    fft.analyze();
    for (int i = 0; i < maxBand; i++) {
      float signal = fft.spectrum[i];
      r.stroke(map(signal, 0, 0.5, 90, 0), 255, 255);
      // Draw the rectangles, adjust their height using the scale factor
      r.rect(i*barWidth, height, barWidth, -signal*height*2);
    }
  }
}
