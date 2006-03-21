#!/bin/sh

classpath="$CLASSPATH":.:lib/Language.jar:lib/kunststoff.jar:lib/jdom.jar:lib/icons.jar:lib/postgresql.jar/:lib/looks.jar
cd `dirname $0`
java -classpath $classpath ws.kazak.xpg.main.XPg
