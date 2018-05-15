Ilda
====

A Processing library which renders Ilda files.
Work in progress. As of right now you can do some simple things.

Alpha release (largely works but has some bugs): https://github.com/colouredmirrorball/Ilda/releases/tag/v0.0.3-alpha

Javadoc: http://colouredmirrorball.github.io/Ilda/


How to install:

 * Download Processing from processing.org if you haven't already (latest version should work, if not file an issue)
 * Unzip and run it at least once
 * Find the sketch path: File >> Settings >> Sketchpath
 * Use a file browser and open this path
 * Create or open the folder "libraries"
 * Unzip Ilda.jar in this directory
 * Should now look like this: *Sketchpath*\libraries\Ilda\library\Ilda.jar
 * Open an example or get coding! Good luck!
 
 
 Functionality:
 
  * Load Ilda file
  * Write Ilda file
  * Load LSX PIC file
  * Get Ilda file properties as Processing datatypes (PVector, color)
  * Display Ilda file on screen
  * Render Ilda frame as if it were a PGraphics, supported operations (tested):
      * line()
      * point()
      * ellipse()
      * rect()
      * fill()
      * text() (wonky)
      * shape() (includes SVG files)
      * vertex() 
  
