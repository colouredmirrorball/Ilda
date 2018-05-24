ilda
====

A Processing library that allows the import, creation, editing and export of ilda-compliant files for laser show use.

Download: https://github.com/colouredmirrorball/ilda/raw/master/ilda.zip

Manual install: unzip the above file in the Processing libraries folder

Javadoc: http://colouredmirrorball.github.io/ilda/


First time usage:

 * Download Processing from processing.org if you haven't already (latest version should work, if not file an issue)
 * Unzip and run it at least once
 * Find the sketch path: File >> Settings >> Sketchpath
 * Use a file browser and open this path
 * Create or open the folder "libraries"
 * Unzip ilda.zip in this directory
 * Should now look like this: *Sketchpath*\libraries\ilda\library\ilda.jar
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
  
Compatibility:

   * Tested with Processing 2.2.1 and 3.3.4
   * Tested on Windows 10 64 bit but should be 100 % cross-platform
   
No real time laser output is supported and is out of scope for this library. However, some examples include a way to send IldaFrames to LSX using the OSC protocol in real time. 