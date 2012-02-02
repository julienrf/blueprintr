#!/usr/bin/env sh

FOREST=/Users/julienrichard-foy/workspace/forest/compiler/target/scala-2.9.1/forest_2.9.1-0.1-one-jar.jar

java -jar $FOREST app app/views scala forest.backends.Play2Json
java -jar $FOREST app public/javascripts/generated js
