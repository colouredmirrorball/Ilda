class VUEffect extends Effect {

  int bars = 10;
  int barWidth = height/bars-10;

  public VUEffect() {
  }

  @Override
    void display(IldaRenderer r) {
    r.colorMode(HSB);
    float signalLeft=map(amplitude.analyze(), 0, 0.5, 0, bars);
    float signalRight=map(amplitudeRight.analyze(), 0, 0.5, 0, bars);

    for (int i = 0; i < bars; i++) {
      if (signalLeft >= i) {
        r.stroke(map(i, 0, bars, 90, 0), 255, 255);
        r.rect(width/2-barWidth-10, height-(i+1)*(barWidth+10), barWidth, barWidth);
      }
      if (signalRight >= i) {
        r.stroke(map(i, 0, bars, 90, 0), 255, 255);
        r.rect(width/2+barWidth+10, height-(i+1)*(barWidth+10), barWidth, barWidth);
      }
    }
  }
}
