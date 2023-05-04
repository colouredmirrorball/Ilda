class LineEffect extends Effect {

  float effectSpeed = 0.05f;

  float offset = height/2;
  float angle = 0;

  float oldOffset = offset;
  float oldAngle = angle;

  boolean dots = false;

  public LineEffect() {
    randomize();
  }

  void display(IldaRenderer renderer) {

    oldAngle = oldAngle + (angle - oldAngle)*effectSpeed;
    oldOffset = oldOffset + (offset - oldOffset)*effectSpeed;

    renderer.pushMatrix();
    renderer.translate(width/2, height/2);
    renderer.rotateZ(oldAngle);
    renderer.translate(-width/2, -height/2+offset);
    if (dots) {
      renderer.stroke(secondColor);
      renderer.point(10, 0);
      renderer.point(20, 0);
      renderer.point(30, 0);
    }
    renderer.stroke(firstColor);
    renderer.line(dots?40:0, 0, dots?width-40:width, 0);
    if (dots) {
      renderer.stroke(secondColor);
      renderer.point(width-30, 0);
      renderer.point(width-20, 0);
      renderer.point(width-10, 0);
    }
    renderer.popMatrix();
  }

  void mouse() {
    if (mouseButton == RIGHT) {
      randomize();
    } else if (mouseButton == CENTER) {
      toggleDots();
    }
  }

  void randomize() {
    offset = random(height/2-height/4, height/2+height/4);
    angle = random(0, TWO_PI);
  }

  void toggleDots() {
    dots = !dots;
  }
}
