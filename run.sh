#!/usr/bin/env bash

java -jar --module-path ./lib --add-modules=javafx.controls,javafx.graphics -Dprism.forceGPU=true ./releases/WordRunner2048.jar
 