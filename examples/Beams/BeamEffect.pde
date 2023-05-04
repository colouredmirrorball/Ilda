class BeamEffect extends Effect {

  int amount = 10;
  float speed = 0.1f;
  PVector[] positions = new PVector[amount];
  PVector[] oldPositions = new PVector[amount];

  public BeamEffect() {
    assignRandomPositions();
    for (int i = 0; i < positions.length; i++) {
      oldPositions[i] = positions[i];
    }
  }

  @Override
  void display(IldaRenderer renderer) {
    for (int i = 0; i < positions.length; i++) {
      renderer.stroke( i % 2 == 0 ? firstColor : secondColor);
      float x = oldPositions[i].x;
      float y = oldPositions[i].y;
      renderer.point(x, y);
      oldPositions[i].x = x + (positions[i].x-x)*speed;
      oldPositions[i].y = y + (positions[i].y-y)*speed;
    }
  }

  @Override
    void mouse() {
    if (mouseButton == RIGHT) {
      assignRandomPositions();
    }
  }

  void assignRandomPositions() {
    for (int i = 0; i < positions.length; i++) {
      positions[i] = new PVector((int) random(width), (int) random(height));
    }
  }
}
