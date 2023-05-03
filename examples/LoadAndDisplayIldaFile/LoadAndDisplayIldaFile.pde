import be.cmbsoft.ilda.*;
import java.util.*;

List<IldaFrame> theFrames = new ArrayList<IldaFrame>();


void setup()
{
  size(600, 600, P3D);


  theFrames = IldaReader.readFile(this, "lines.ild");
  smooth();
}

void draw()
{
  background(00);
  theFrames.get(0).renderFrame(this);

/*
  //This code will manually display the points
  List<IldaPoint> points = theFrames.get(0).getPoints();
  boolean firstPoint = true;
  float oldpositionx = 0;
  float oldpositiony = 0;
  float oldpositionz = 0;
  //println(points.size());
  for (IldaPoint point : points)
  {
    PVector position = point.getPosition(width, height, 0);//(width+height)*0.5);
    if ( !point.isBlanked())
    {
      strokeWeight(3);
      stroke(red(point.getColour()), green(point.getColour()), blue(point.getColour()));
      //stroke(random(255));


      point(position.x, position.y);
      //point(random(width), random(height));
      //println(red(point.getColour()), point.isBlanked(), position.x, position.y, position.z, oldpositionx, oldpositiony, oldpositionz);
    }

    if (!firstPoint)
    {
      strokeWeight(1);
      if (point.isBlanked()) stroke(0);
      else
      {
        line(position.x, position.y, oldpositionx, oldpositiony);
      }
      oldpositionx = position.x;
      oldpositiony = position.y;
      oldpositionz = position.z;
    } else
    {
      firstPoint = false;
      oldpositionx = position.x;
      oldpositiony = position.y;
      oldpositionz = position.z;
    }
  }
  */
}