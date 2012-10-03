Mumble-Positional-Audio-Framework
(MPAF)

MPAF is a framework for working with the positional audio data exposed via the Ice API.

Running MPAF
=================================

Install the latest Java JRE and download MPAF.

Make a copy of mpaf.properties.default.xml and name it mpaf.properties.user.xml.

Make changes to mpaf.properties.user.xml

To run mpaf use: java -jar mpaf.jar via the command line

Access the web interface on port 10000

Building
=================================

Get the Zeroc Ice Eclipse plugin here:
http://www.zeroc.com/eclipse.html

This should generate the Ice classes for java, then just export MPAF as a runnable jar.