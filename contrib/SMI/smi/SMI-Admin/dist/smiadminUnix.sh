#!/bin/bash
export CLASSPATH=$PWD/libs/jdom.jar:$PWD/libs/jcalendar.jar:$PWD/libs/syntheticaBlueIce.jar:$PWD/libs/synthetica.jar:$PWD/libs/icons.jar:$PWD/smiadmin.jar:$PWD/smilib.jar
java com.kazak.smi.admin.Run
