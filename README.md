# Goodspeed's CAT Tool
Android app for controlling the Kenwood TH-D74, TH-D72, TM-D710 and other radios.

## Status

The app is minimally functional, opening a TCP socket to the radio's serial port
from a real phone or within the emulator.

## Usage

Open the app, then poke around.  I'll update these instructions when things are more stable.

## Building

This code depends upon [CodePlugTool.jar](https://github.com/travisgoodspeed/codeplugtool), which must
be built using JDK8 or the Android SDK's `javac`, then placed in `app/libs/`.  For your convenience, a
rarely updated build of that library is included in the repo.

To build the main application, use Android Studio 4 and a matching build of Gradle.

## License

If you use this code, you owe Travis Goodspeed (KK4VCZ) one India Pale Ale.
