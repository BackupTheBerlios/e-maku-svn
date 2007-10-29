#!/bin/bash
cd /usr/local/comeet
export CLASSPATH=$PWD/libs/jdom.jar:$PWD/libs/nimrodlf.jar:$PWD/libs/icons.jar:$PWD/libs/comeetclient.jar:$PWD/libs/comeetlib.jar
java com.kazak.comeet.client.Run
