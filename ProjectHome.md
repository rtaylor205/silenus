# What is Silenus? #

Starting from Adobe Flash CS5, flash animations are saved in a new data format that can be easily read and interpreted, called the XFL format. Even if you save as FLA in CS5, your fill will simply be a zipped XFL directory. Silenus will read these FLA files or XFL directories and allow you to animate them without the use of Flash of Flash player.

The following flow chart describes the Silenus approach:

![http://www.castlequest.be/cq3/Schema-small.png](http://www.castlequest.be/cq3/Schema-small.png)

A demo can be found on [this page](http://silenus.silenistudios.com).

Silenus is java library, so any java application can now easily read Flash files and render the entire animation in their native graphics system. Additionally, Silenus allows you to export the animation data to JSON, which can be read and used in your browser so that HTML5 games can render flash animations in all their detail. Silenus even comes with a pre-made javascript renderer that will read the produced JSON and render the animation to canvas.

Silenus can be used in a number of ways:
  * As a [stand-alone java application](http://code.google.com/p/silenus/downloads/list) with two modes: render to screen using Java Graphics, or export to file as JSON.
  * As a [stand-alone jar file](http://code.google.com/p/silenus/downloads/list) that contains the Silenus API, for you to include in your java application. This might be useful to integrate the exporter in a Java backend (for example: Tomcat), or to use flash animations in a Java application.
  * As [source code](http://code.google.com/p/silenus/source/checkout). Silenus is not yet entirely bug-free and does not support every flash feature yet, so contributions are very welcome!

Silenus was developed by [Sileni Studios](http://www.silenistudios.com/) in order to export complex animations from Flash and use them in a HTML5-only game. An [in-depth blog post](http://www.castlequest.be/silenistudios/?p=54) is available on the website that explains the reasoning behind Silenus. Documentation is available in the GettingStarted section.

If you are going to use Silenus, please let us know! We'd like to hear about the projects that Silenus will be used for.

# Why use Silenus? #

Some of the things Silenus can be used for:
  * Use flash animations in a java-based game. Seamlessly integrate complex, layered animations made with the excellent flash tool in a java project without loss of detail or features.
  * Render flash animations in real-time in your browser using javascript, by reading the JSON produced by Silenus. This offers an open-source and better-documented alternative for [Google Swiffy](http://www.google.com/doubleclick/studio/swiffy/) or [Adobe Wallaby](http://labs.adobe.com/technologies/wallaby/) that doesn't force SVG on the user.


# Supported Flash features #

Silenus does not support every feature in flash (yet), but it does support a fair amount of basic and more advanced features, including:
  * Multiple scenes
  * Layer hierarchies
  * Translation, scaling, rotation
  * Classic tweening with custom eases
  * Vector graphics: straight lines, curves, line caps, joints, ...
  * Masks (clipping)
  * Animation with inverse kinematics (IK)
  * Color effects: alpha, brightness, tint, advanced, linear gradients
  * Automatic reconstruction of PNG and JPEG images from the internal Flash binary format (files in the XFL format still keep images in an Adobe-specific binary format)


The following features are **not yet** supported:
  * Filters (for example: blur)
  * More image formats for conversion
  * Actionscript (Silenus could be linked with something like http://www.jangaroo.net/home/ to support in-browser actionscript without flash player)
  * Text rendering


# Contribute #

We're going to need help filling out the blanks, so that Silenus ends up being a complete emulator that supports every feature flash supports. If you want to contribute, you can always send me an [e-mail](mailto:Karel.Crombecq@gmail.com).