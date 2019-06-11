ilda
====

A Processing library that allows the import, creation, editing and export of ilda-compliant files for laser show use.

Download: https://github.com/colouredmirrorball/Ilda/raw/master/Ilda.zip

Manual install: unzip the above file in the Processing libraries folder

Javadoc: http://colouredmirrorball.github.io/Ilda/


First time usage:

 * Download Processing from processing.org if you haven't already (latest version should work, if not file an issue)
 * Unzip and run it at least once
 * Find the sketch path: File >> Settings >> Sketchpath
 * Use a file browser and open this path
 * Create or open the folder "libraries"
 * Unzip ilda.zip in this directory
 * Should now look like this: *Sketchpath*\libraries\ilda\library\ilda.jar
 * Restart Processing
 * Open an example or get coding! Good luck!
 
 
Functionality:
 
  * Load ilda file
  * Write ilda file
  * Load LSX PIC file
  * Get ilda file properties as Processing datatypes (PVector, color)
  * Display ilda file on screen
  * Render ilda frame as if it were a PGraphics, supported operations (tested):
      * line()
      * point()
      * ellipse()
      * rect()
      * stroke()
      * text() (wonky)
      * shape() (includes SVG files - not all SVG tags supported or tested)
      * vertex() 
      
Todo:

   * Finish optimisation: 
      * blank transition dwell points
      * angle dwell
   * Clip points outside of canvas
   * Make library official
  
Compatibility:

   * Tested with Processing 2.2.1 and 3.3.4
   * Tested on Windows 10 64 bit but should be 100 % cross-platform
   
No real time laser output is supported and is out of scope for this library. However, some examples include a way to send 
IldaFrames to LSX using the OSC protocol in real time. 


**DISCLAIMER**

This library is capable of producing laser art files that when scanned by an actual laser projector, might cause permanent
 eye damage when exposed. Always observe proper safety guidelines and local regulations. The authors of this software can 
 in no regards be held responsible for damage caused by improper use of this software. Always avoid scanning into an audience 
 and if you can't help yourself, then make sure the beam is always fast moving. Multiple points at the same location will 
 cause hot beams that pop eyes or cause fires.

It is possible using this software to create laser art files with a relatively long distance between consecutive points. 
These jumps might be hazardous to the projector scan system (galvo's, scanners, etc.). This can be avoided by optimising 
files before exporting. The authors of this software can in no way be held responsible for damage to laser projector systems 
caused by the use of this library.

By using this library it is assumed you are aware of the possible consequences of improper use and accept the sole responsibility.