/*
See the Help button in the LSX OSC setup window for more explanation
 */

public void ildaframeToLsx(IldaFrame frame, int timeline, int destinationFrame, NetAddress destination)
{
  OscMessage m = new OscMessage("/LSX_0/Frame");

  ArrayList<Byte> b = new ArrayList<Byte>();                    //Contents of the message are stored in a Byte ArrayList 



  // LSX frame OSC message

  //HEADER

  b.add((byte) 2);  //type: 0=XYRGB; 1=XYZRGB; 2=XYZPPrRGB
  b.add((byte) 1);  //store: 0 = buffer, 1 = store in frame
  b.add((byte) timeline);  //scanner/timeline
  b.add((byte) 0);         //future


  b.add((byte) (destinationFrame & 0xff));
  b.add((byte) ((destinationFrame >> 8) & 0xff));




  int pointCount = frame.getPoints().size();
  b.add((byte) (pointCount & 0xff));
  b.add((byte) ((pointCount >> 8) & 0xff));




  int startPoint = 0;
  b.add((byte) (startPoint & 0xff));
  b.add((byte) ((startPoint >> 8) & 0xff));




  int blobPoints = frame.getPoints().size();
  b.add((byte) (blobPoints & 0xff));
  b.add((byte) ((blobPoints >> 8) & 0xff));
  int max = 32767;


  for (IldaPoint p : frame.getPoints())
  {

    int x = (int) constrain(map(p.getPosition().x, -1, 1, -max, max), -max, max);
    b.add((byte) (x & 0xff));
    b.add((byte) ((x >> 8) & 0xff));


    int y = (int) constrain(map(p.getPosition().y, -1, 1, -max, max), -max, max);
    b.add((byte) (y & 0xff));
    b.add((byte) ((y >> 8) & 0xff));



    int z = (int) constrain(map(p.getPosition().z, -1, 1, -max, max), -max, max);
    b.add((byte) (z & 0xff));
    b.add((byte) ((z >> 8) & 0xff));

    // Palette byte: 
    //    First bit: normal vector    1 = regular point    0 = normal vector
    //    Second bit: blanking        1 = blanked          0 = unblanked
    //    Third to eighth bit: palette idx (0-63)
    b.add((byte) (1<<7 |(p.isBlanked() ?  1 << 6 :  0)));

    // Parts-Repeats byte
    //    First to fourth bit: parts (0-15)
    //    Fifth to eighth bit: repeats (0-15)
    b.add((byte)0);


    int red = (p.getColour() >> 16) & 0xFF;  
    int green = (p.getColour() >> 8) & 0xFF;
    int blue =  (p.getColour() & 0xFF);

    if (p.isBlanked())
    {
      red = 0;
      green = 0;
      blue = 0;
    }



    b.add((byte) red);
    b.add((byte) green);
    b.add((byte) blue);
  }


  // Stupid Java does not allow direct conversion from ArrayList to primitive type...
  byte[] bytes = new byte[b.size()];
  for (int i=0; i<b.size (); i++)
  {
    bytes[i] = b.get(i);
  }

  //Add the blob to the OSC message
  m.add(bytes);




  osc.send(m, destination);              // send the OSC message to the remote location defined in setup()
}