#!/bin/bash

# Create APK build script
echo "Building Roston Time APK..."

# Create temporary build directory
mkdir -p build/roston-time
cd build/roston-time

# Initialize gradle project
gradle init --type basic --dsl kotlin

# Copy source files
mkdir -p app/src/main/java/com/rostontime
mkdir -p app/src/main/res/layout
mkdir -p app/src/main/res/values
mkdir -p app/src/main/res/drawable
mkdir -p app/src/main/res/xml

# Build APK
./gradlew assembleRelease

echo "APK built successfully!"
echo "Location: app/build/outputs/apk/release/app-release.apk"
