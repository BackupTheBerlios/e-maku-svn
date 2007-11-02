#!/bin/bash
export CLASSPATH=$PWD/libs/jdom.jar:$PWD/libs/jcalendar.jar:$PWD/libs/syntheticaBlueIce.jar:$PWD/libs/synthetica.jar:$PWD/libs/icons.jar:$PWD/libs/comeetadmin.jar:$PWD/libs/comeetlib.jar
java com.kazak.comeet.admin.Run
