# MakePom

## Purpose
  Use the local jars in a maven project.

## Guide
* copy jar files to the lib directory located at the root of your project
* run `javac MakePom.java`
* copy `MakePom.class` to your `lib` directory
* run `java MakePom` in your `lib` directory
  * optional: java `-DgroupId=xx -DjarPrefix=xx -DjarVersion=xx` MakePom
* copy the content in `pom.xml.txt` to your pom.xml
