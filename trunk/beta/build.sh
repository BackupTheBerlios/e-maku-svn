#!/bin/sh

ROOT=$PWD

check_root() {
             if [ "$USER" != "root" ] ; then
               echo
               echo " ERROR: Para realizar la des-instalacion de E-Maku se requiere de"
               echo "        privilegios especiales."
               echo "        Es necesario ejecutar esta opcion como el usuario \"root\"."
               echo
               exit 1
             fi
}

check_deps() {
             if [ "$JAVA_HOME" = "" ] ; then
               echo
               echo "ERROR: la variable de entorno JAVA_HOME no se encuentra definida."
               echo "---"
               echo " Por favor, defina la variable JAVA_HOME con el valor de la ruta"
               echo " en donde se encuentra la instalacion de Java."
               echo " Ej: export JAVA_HOME=/usr/local/java"
               echo "---"
               exit 1
             fi

             if [ -d "build" ] ; then
               echo
               echo " Removiendo archivos de compilacion previos..."
               remove
             fi

             java -version >& /tmp/java.log;
             JAVAVERSION=`head -1 /tmp/java.log | cut -d'"' -f2`
             rm -f /tmp/java.log

             VER=`echo $JAVAVERSION | cut -d'.' -f2`

             if [ "$VER" -lt "5" ] ; then
               echo "---"
               echo " E-Maku requiere Java 1.5 o superior para ser compilado."
               echo " Por favor, actualice su version de Java."
               echo "---"
               exit 1
             fi

             echo
             echo " Usando Java version: $JAVAVERSION"
             echo
}

set_vars() {
             if [ `uname | grep -n CYGWIN` ]; then
               PS=";"
             elif [ `uname | grep -n Windows` ]; then
               PS=";"
             else
               PS=":"
             fi

             LOCALCLASSPATH=${JAVA_HOME}/lib/tools.jar${PS}${JAVA_HOME}/lib/dev.jar${PS}../lib/contrib/ant.jar${PS}../lib/contrib/icons.jar${PS}../lib/contrib/jdom.jar${PS}../lib/contrib/jdbc-pgsql.jar${PS}../lib/midas/jmlib.jar${PS}../lib/contrib/bsh-core.jar${PS}../lib/contrib/ostermillerutils.jar${PS}../lib/contrib/jpedal.jar${PS}../lib/contrib/jai_codec.jar${PS}../lib/contrib/jai_core.jar${PS}../lib/contrib/itext.jar${PS}../lib/contrib/digester.jar${PS}../lib/contrib/jdt-compiler.jar${PS}../lib/contrib/bcprov.jar${PS}../lib/contrib/jasper.jar

             ANT_HOME=../lib

             echo " * Construyendo el classpath $LOCALCLASSPATH${PS}$ADDITIONALCLASSPATH"
             echo " * Iniciando Ant..."
}

set_previous() {
             check_deps
             set_vars
}


compile_client() {
             echo
             echo " * Compilando el Cliente..."
             echo

             cd JMClient
             $JAVA_HOME/bin/java -Dant.home=$ANT_HOME -classpath $LOCALCLASSPATH${PS}$ADDITIONALCLASSPATH org.apache.tools.ant.Main $*
             cd ..
}

compile_libs() {
             echo
             echo " * Compilando las Librerias..."
             echo

             cd JMLib/
             $JAVA_HOME/bin/java -Dant.home=$ANT_HOME -classpath $LOCALCLASSPATH${PS}$ADDITIONALCLASSPATH org.apache.tools.ant.Main $*
             cd ..
}

compile_server() {
             
             echo
             echo " * Compilando el Servidor de Transacciones..."
             echo
             cd JMServer
             $JAVA_HOME/bin/java -Dant.home=$ANT_HOME -classpath $LOCALCLASSPATH${PS}$ADDITIONALCLASSPATH org.apache.tools.ant.Main $*
             cd ..
}

compile_all() {
             set_previous

             compile_libs

             compile_server

             compile_client
}

install_base() {
             mkdir -p $JMIDAS_HOME
             mkdir -p $JMIDAS_HOME/lib
             mkdir -p $JMIDAS_HOME/bin 
             cp -rf $ROOT/lib $JMIDAS_HOME
             chmod -R 755 $JMIDAS_HOME
             echo "$JMIDAS_HOME" > $ROOT/install.log
}

install_server() {
             mkdir -p $JMIDAS_HOME/conf
             cp -f $ROOT/conf/server.conf $JMIDAS_HOME/conf
             cp -f $ROOT/conf/wrapper.conf $JMIDAS_HOME/conf
             cp -f $ROOT/bin/wrapper $JMIDAS_HOME/bin
             cp -f $ROOT/bin/jmidas_daemon $JMIDAS_HOME/bin

             echo "#!/bin/sh" > $JMIDAS_HOME/bin/jmserver
             echo " " >> $JMIDAS_HOME/bin/jmserver
             echo "JMIDAS_HOME=$JMIDAS_HOME" >> $JMIDAS_HOME/bin/jmserver
             echo "export JMIDAS_HOME" >> $JMIDAS_HOME/bin/jmserver
             cat $ROOT/bin/jmserver >> $JMIDAS_HOME/bin/jmserver

             if [ ! -L /usr/sbin/jmserver ] ; then
               ln -s $JMIDAS_HOME/bin/jmserver /usr/sbin/jmserver
             fi

             if [ ! -L /usr/sbin/jmidas_daemon ] ; then
               ln -s $JMIDAS_HOME/bin/jmidas_daemon /usr/sbin/jmidas_daemon
             fi

             echo " Instalando scripts de inicio..."
             if [ -d /etc/init.d ] ; then
               cp -f $JMIDAS_HOME/bin/jmserver /etc/init.d/jmidas
               if [ -d /etc/rc3.d ] && [ ! -e /etc/rc3.d/S20jmidas ] ; then
                 ln -s /etc/init.d/jmidas /etc/rc3.d/S20jmidas
               fi
               if [ -d /etc/rc5.d ] && [ ! -e /etc/rc3.d/S20jmidas ] ; then
                 ln -s /etc/init.d/jmidas /etc/rc5.d/S20jmidas
               fi
               if [ -d /etc/rc0.d ] && [ ! -e /etc/rc0.d/K20jmidas ] ; then
                 ln -s /etc/init.d/jmidas /etc/rc0.d/K20jmidas
               fi
               if [ -d /etc/rc6.d ] && [ ! -e /etc/rc0.d/K20jmidas ] ; then
                 ln -s /etc/init.d/jmidas /etc/rc6.d/K20jmidas
               fi
             else
               RC_PREV=`egrep jmserver /etc/rc.d/rc.local`
               if [ "$RC_PREV" = "" ] && [ -e /etc/rc.d/rc.local ] ; then
                 echo "" >> /etc/rc.d/rc.local
                 echo "# Iniciando servidor E-Maku..." >> /etc/rc.d/rc.local
                 echo "/usr/sbin/jmserver start >& /dev/null" >> /etc/rc.d/rc.local
               fi
             fi

             chmod -R 600 $JMIDAS_HOME/conf
}

install_client() {
             echo "#!/bin/sh" > $JMIDAS_HOME/bin/jmclient
             echo " " >> $JMIDAS_HOME/bin/jmclient
             echo "JMIDAS_HOME=$JMIDAS_HOME" >> $JMIDAS_HOME/bin/jmclient
             echo "export JMIDAS_HOME" >> $JMIDAS_HOME/bin/jmclient
             cat $ROOT/bin/jmclient >> $JMIDAS_HOME/bin/jmclient

             if [ ! -L /usr/bin/jmclient ] ; then
               ln -s $JMIDAS_HOME/bin/jmclient /usr/bin/jmclient
             fi
}

help(){
             echo
             echo " Uso: ./build.sh [opciones] "
             echo " Donde las opciones pueden ser:"
             echo "  help                     : Despliega este mensaje "
             echo "  compile                  : Compila E-Maku Empresarial "
             echo "  compile server           : Compila el componente servidor E-Maku "
             echo "  compile client           : Compila el componente cliente E-Maku "
             echo "  -c                       : Elimina archivos .class "
             echo "  clean                    : Elimina archivos .class "
             echo "  -i                       : Instala E-Maku en /usr/local/jmidas "
             echo "  install                  : Instala E-Maku en /usr/local/jmidas "
             echo "  -i -p /ruta/jmidas       : Instala E-Maku en /ruta/jmidas"
             echo "  -i --prefix=/ruta/jmidas : Instala E-Maku en /ruta/jmidas"
             echo "  uninstall                : Elimina la instalacion de E-Maku "
             echo "  -u                       : Elimina la instalacion de E-Maku "
             echo
}

remove(){
             find . -name '*.class' -exec rm {} \;
             \rm -rf build
             \rm -f lib/midas/jmlib.jar
             \rm -f lib/midas/jmclient.jar
             \rm -f lib/midas/jmserver2.jar 
             \rm -f *.log
}

case "$1" in

  -c|clean)
             echo
             echo " Eliminando archivos de compilacion y archivos temporales..."
             remove
             echo " Hecho."
             echo
             ;;

  -i|install)
             if [ ! -d "build" ] ; then 
               echo "ALL" > $ROOT/core.log
               compile_all
             fi

             check_root

             if [ -z $2 ] ; then
               echo
               echo " Utilizando ruta de instalacion por defecto (/usr/local/jmidas)"
               JMIDAS_HOME=/usr/local/jmidas
             else
               case "$2" in
                 -p) 
                    if [ "$3" != "" ] ; then
                      JMIDAS_HOME=$3
                    else
                      echo 
                      echo " ERROR: La ruta de instalacion de E-Maku no fue ingresada."
                      echo "        Ej: ./build.sh -i -p /usr/local/jmidas"
                      echo
                      exit 1
                    fi
                    ;;
                --prefix=*)
                    JMIDAS_HOME=`echo $2 | cut -f2 -d=`  
                    if [ "$JMIDAS_HOME" = "" ] ; then
                      echo
                      echo " ERROR: La ruta de instalacion de E-Maku no fue ingresada."
                      echo "        Ej: ./build.sh -i --prefix=/usr/local/jmidas"
                      echo
                      exit 1
                    fi
                    ;;
                *)
                    echo
                    echo " ERROR: $3 es un parametro desconocido." 
                    help
                    exit 1
                    ;;
               esac
             fi

             CORE=`cat $ROOT/core.log`

             install_base

             case "$CORE" in
               CLIENT)
                    echo " Instalando componente cliente de E-Maku..."
                    install_client
                    ;;
               SERVER)
                    echo " Instalando componente servidor de E-Maku..."
                    install_server
                    ;;
               ALL) install_client
                    install_server
                    ;;
             esac
             echo " Hecho."
             echo
             ;;

  compile)
             if [ -z $2 ] ; then
               echo "ALL" > $ROOT/core.log
               compile_all
             else
               set_previous
               case "$2" in
                 server)
                         echo "SERVER" > $ROOT/core.log
                         compile_libs
                         compile_server 
                         ;;
                 client)
                         echo "CLIENT" > $ROOT/core.log
                         compile_libs
                         compile_client
                         ;;
                 *)
                         echo
                         echo " ERROR: $2 es un parametro desconocido."
                         help
                         exit 1
                         ;;
               esac
             fi
             ;;

  "")
             help 
             ;;

  -u|uninstall)

             check_root

             if [ ! -e "install.log" ] ; then
               echo
               echo " ERROR: No se encuentra el directorio de instalacion de E-Maku"
               echo " Aparentemente el proyecto no ha sido instalado en el sistema."
               echo
               exit 1 
             else
               JMIDAS_HOME=`cat $ROOT/install.log` 
               if [ ! -d "$JMIDAS_HOME" ] ; then
                 echo
                 echo " ERROR: No se encuentra el directorio de instalacion de E-Maku" 
                 echo " Aparentemente el directorio \"$JMIDAS_HOME\" ya fue eliminado."
                 echo
                 exit 1
               fi
             fi
             
             echo 
             echo " Eliminando directorio de instalacion de E-Maku..."
             rm -rf $JMIDAS_HOME

             if [ -L /usr/bin/jmclient ] ; then
               rm -f /usr/bin/jmclient
             fi
             if [ -L /usr/sbin/jmserver ] ; then
               rm -f /usr/sbin/jmserver
             fi

             echo " Eliminando scripts de inicio..."
             if [ -d /etc/init.d ] && [ -e /etc/init.d/jmidas ] ; then
               rm -f /etc/init.d/jmidas
               if [ -d /etc/rc3.d ] && [ -e /etc/rc3.d/S20jmidas ] ; then
                 rm -f /etc/rc3.d/S20jmidas
               fi
               if [ -d /etc/rc5.d ] && [ -e /etc/rc3.d/S20jmidas ] ; then
                 rm -f /etc/rc5.d/S20jmidas
               fi
              if [ -d /etc/rc0.d ] && [ -e /etc/rc0.d/K20jmidas ] ; then
                 rm -f /etc/rc0.d/K20jmidas
               fi
              if [ -d /etc/rc6.d ] && [ -e /etc/rc0.d/K20jmidas ] ; then
                 rm -f /etc/rc6.d/K20jmidas
               fi
             else
               RC_PREV=`egrep jmserver /etc/rc.d/rc.local`
               if [ "$RC_PREV" != "" ] && [ -e /etc/rc.d/rc.local ] ; then
                 egrep -v jmserver /etc/rc.d/rc.local | grep -v E-Maku > /tmp/rc.local.tmp
                 cp -f /tmp/rc.local.tmp /etc/rc.d/rc.local
                 rm -f /tmp/rc.local.tmp
               fi
             fi

             echo " Hecho."
             echo
             ;;

   *)
             help
             ;;
esac
