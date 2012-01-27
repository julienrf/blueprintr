#!/usr/bin/env sh

FOREST=/Users/julienrichard-foy/workspace/forest/target/scala-2.9.1/forest_2.9.1-0.1-one-jar.jar

java -jar $FOREST app app/views
java -jar $FOREST app app/assets/javascripts js
