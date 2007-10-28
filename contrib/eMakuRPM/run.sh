#!/bin/bash

LIBS=lib
echo $LIBS

export CLASSPATH=$LIBS/jdom.jar:$LIBS/commons-beanutils-bean-collections.jar:$LIBS/digester.jar:$LIBS/jdt-compiler.jar:$LIBS/commons-beanutils-core.jar:$LIBS/jasper.jar:$LIBS/logging.jar:$LIBS/commons-beanutils.jar:$LIBS/jdbc-pgsql.jar:$LIBS/reports.jar:$LIBS/commons.jar:reportmanager.jar:$LIBS/syntax.jar:$LIBS/theme.jar
echo $CLASSPATH

export RPM_HOME="."
java -classpath $CLASSPATH net.emaku.tools.Run
