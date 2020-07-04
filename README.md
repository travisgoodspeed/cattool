# Goodspeed's CAT Tool

Android app for controlling the Kenwood TH-D74, TH-D72, TM-D710 and
other radios.  Based on CodePlugTool, which is a pure-Java library for
controlling these same radios.

## Status

The app is minimally functional, connecting to radios and using their
CAT protocols over TCP or Bluetooth.  On-phone editing of the codeplug
is not yet functional.

## Usage

Open the app, then poke around.  I'll update these instructions when
things are more stable.

To share a mobile radio's serial port over your home LAN, run `socat
tcp-l:54321,reuseaddr,fork file:/dev/ttyS1,nonblock,raw,echo=0` with
your own port numbers.

## Building

This code depends upon
[CodePlugTool.jar](https://github.com/travisgoodspeed/codeplugtool),
which must be built using JDK8 or the Android SDK's `javac`, then
placed in `app/libs/`.  For your convenience, a rarely updated build
of that library is included in the repo.

To build the main application, use Android Studio 4 and a matching
build of Gradle.

## Development

At home, I develop the app in the AVD emulator with the app configured
to use my Kenwood TM-D710 over TCP.  At the coffee house and the bar,
I debug the app on my phone and connect it to my Kenwood TH-D74 over
Bluetooth to avoid cables.

Any radio protocol changes are written separately in the
[CodePlugTool](https://github.com/travisgoodspeed/codeplugtool) repo
and tested in Unix before ever seeing the phone.  If a feature doesn't
exist in the command-line JAR file, that's a bug and should be fixed
there rather than debugged here.

## License

If you use this app, you owe Travis Goodspeed (KK4VCZ) one India Pale
Ale.  It would be nice if it were delivered cold at a
[RACK](http://www.w4bbb.org/) meeting.