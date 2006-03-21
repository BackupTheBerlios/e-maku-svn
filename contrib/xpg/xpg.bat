@echo off
echo "Loading xpg ....."
set CLASSPATH="%CLASSPATH%;lib\icons.jar;lib\kunstsoff.jar:lib\Language.jar:lib\jdom.jar:lib\postgresql.jar;"
java -classpath %CLASSPATH% ws.kazak.xpg.main.XPg
echo "Closing xpg ....."
 
