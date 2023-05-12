import be.cmbsoft.ilda.*;
import be.cmbsoft.laseroutput.*;
import java.util.*;

List<IldaFrame> theFrames = new ArrayList<IldaFrame>();
LsxOscOutput output;

void setup()
{
  size(600, 600, P3D);
  output = new LsxOscOutput(1, 10, "127.0.0.1", 10000);

  theFrames = IldaReader.readFile(this, "lines.ild");
  smooth();
}

void draw()
{
  background(00);
  IldaFrame currentFrame = theFrames.get(0);
  currentFrame.renderFrame(this);
  output.project(currentFrame);
}