#!/bin/sh
# Compile and run (needs JDK 11 or newer). Run this from the project folder.
mkdir -p out
javac -encoding UTF-8 -d out $(find . -name "*.java") && java -cp out Main
