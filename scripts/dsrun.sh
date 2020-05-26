#!/bin/bash

DSPACE_HOME=/dspace
DSPACE_JARS=`echo /usr/local/src/dspace-5.3-src-release/dspace/target/dspace-installer/lib/*jar | sed 's/ /\:/g'`
JARS="$DSPACE_JARS:/usr/local/src/dspace-5.3-src-release/dspace-example-modules/target/dspace-example-module-5.3.jar"
CLASS=edu.princeton.library.dspace.ExampleTask

/usr/bin/env java  -classpath "$JARS:$DSPACE_HOME/config"   org.dspace.app.launcher.ScriptLauncher dsrun $CLASS "$@"
