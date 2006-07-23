@echo off

set MIDAS_HOME=D:\xtingray\midas

set CLASSPATH=.;%MIDAS_HOME%\lib\emaku\server.jar;%MIDAS_HOME%\lib\contrib\jdom.jar;%MIDAS_HOME%\lib\emaku\common.jar;%MIDAS_HOME%\lib\contrib\jdbc-pgsql.jar;%MIDAS_HOME%\lib\emaku\reports.jar;%MIDAS_HOME%\lib\contrib\jasper.jar;%MIDAS_HOME%\lib\contrib\commons.jar;%MIDAS_HOME%\lib\contrib\commons-beanutils.jar;%MIDAS_HOME%\lib\contrib\commons-beanutils-bean-collections.jar;%MIDAS_HOME%\lib\contrib\commons-beanutils-core.jar;%MIDAS_HOME%\lib\contrib\bsh-core.jar;%MIDAS_HOME%\lib\contrib\ostermillerutils.jar;%MIDAS_HOME%\lib\contrib\jpedal.jar;%MIDAS_HOME%\lib\contrib\jai_codec.jar;%MIDAS_HOME%\lib\contrib\jai_core.jar;%MIDAS_HOME%\lib\contrib\itext.jar;%MIDAS_HOME%\lib\contrib\digester.jar;%MIDAS_HOME%\lib\contrib\jdt-compiler.jar;%MIDAS_HOME%\lib\contrib\bcprov.jar;%MIDAS_HOME%\lib\contrib\mysql.jar;%MIDAS_HOME%\lib\contrib\logging.jar

java server.Run
