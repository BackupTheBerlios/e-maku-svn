#!/bin/bash
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

check_emaku_user() {

             E_USER=`egrep "^emaku" /etc/passwd`
             E_GROUP=`egrep "^emaku" /etc/group`

             if [ "$E_USER" = "" ] || [ "$E_GROUP" = "" ] ; then
               echo
               echo "ERROR: El usuario \"emaku\" o el grupo \"emaku\" no existen."
               echo "---"
               echo " Por razones de seguridad, el servidor de transacciones solo puede "
               echo " ser iniciado por el usuario emaku. Por favor, cree la cuenta y el "
               echo " grupo emaku para continuar con la instalacion."
               echo "---"
               echo
               exit 1
             fi
}

check_deps() {
             echo "data: $JAVA_HOME"

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

             LOCALCLASSPATH=${JAVA_HOME}/lib/tools.jar${PS}${JAVA_HOME}/lib/dev.jar${PS}../lib/contrib/ant.jar${PS}../lib/contrib/icons.jar${PS}../lib/contrib/jdom.jar${PS}../lib/contrib/jdbc-pgsql.jar${PS}../lib/emaku/common.jar${PS}../lib/contrib/bsh-core.jar${PS}../lib/contrib/ostermillerutils.jar${PS}../lib/contrib/jpedal.jar${PS}../lib/contrib/jai_codec.jar${PS}../lib/contrib/jai_core.jar${PS}../lib/contrib/itext.jar${PS}../lib/contrib/digester.jar${PS}../lib/contrib/jdt-compiler.jar${PS}../lib/contrib/bcprov.jar${PS}../lib/contrib/jasper.jar${PS}../lib/contrib/jcalendar.jar${PS}../lib/contrib/nimrodlf.jar

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

             cd Client
             $JAVA_HOME/bin/java -Dant.home=$ANT_HOME -classpath $LOCALCLASSPATH${PS}$ADDITIONALCLASSPATH org.apache.tools.ant.Main $*
             cd ..
}

compile_libs() {
             echo
             echo " * Compilando las Librerias..."
             echo

             cd Common/
             $JAVA_HOME/bin/java -Dant.home=$ANT_HOME -classpath $LOCALCLASSPATH${PS}$ADDITIONALCLASSPATH org.apache.tools.ant.Main $*
             cd ..
}

compile_server() {
             
             echo
             echo " * Compilando el Servidor de Transacciones..."
             echo
             cd Server
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
             mkdir -p $EMAKU_HOME
             mkdir -p $EMAKU_HOME/lib
             mkdir -p $EMAKU_HOME/lib/contrib
             mkdir -p $EMAKU_HOME/lib/emaku
             mkdir -p $EMAKU_HOME/bin 
             cp -f $ROOT/lib/contrib/*.jar $EMAKU_HOME/lib/contrib/
             cp -f $ROOT/lib/emaku/*.jar $EMAKU_HOME/lib/emaku/ 
             echo "$EMAKU_HOME" > $ROOT/install.log
}

install_server() {

             cp -rf $ROOT/reports $EMAKU_HOME/

             mkdir -p $EMAKU_HOME/conf

             if [ ! -e $EMAKU_HOME/conf/server.conf ] ; then
               cp -f $ROOT/conf.unix/server.conf $EMAKU_HOME/conf
             fi
             
             cp -f $ROOT/conf.unix/wrapper.conf $EMAKU_HOME/conf

             ARCH=32
             if [ -d "/lib64" ] ; then
               ARCH=64
             fi

             cp -f $ROOT/bin/emaku-server-daemon-$ARCH $EMAKU_HOME/bin/emaku-server-daemon
             cp -f $ROOT/lib/contrib/lib$ARCH/libwrapper.so $EMAKU_HOME/lib/contrib/

             echo "#!/bin/sh" > $EMAKU_HOME/bin/emaku-server
             echo " " >> $EMAKU_HOME/bin/emaku-server
             echo "EMAKU_HOME=$EMAKU_HOME" >> $EMAKU_HOME/bin/emaku-server
             echo "export EMAKU_HOME" >> $EMAKU_HOME/bin/emaku-server
             echo "JAVA_HOME=$JAVA_HOME" >> $EMAKU_HOME/bin/emaku-server
             echo "export JAVA_HOME" >> $EMAKU_HOME/bin/emaku-server
             echo "PATH=$PATH:$EMAKU_HOME/bin" >> $EMAKU_HOME/bin/emaku-server
             echo "export PATH" >> $EMAKU_HOME/bin/emaku-server
             echo " " >> $EMAKU_HOME/bin/emaku-server

             cat $ROOT/bin/emaku-server >> $EMAKU_HOME/bin/emaku-server

             if [ ! -L /usr/bin/emaku-server ] ; then
               ln -s $EMAKU_HOME/bin/emaku-server /usr/bin/emaku-server
             fi

             if [ ! -L /usr/bin/emaku-server-daemon ] ; then
               ln -s $EMAKU_HOME/bin/emaku-server-daemon /usr/bin/emaku-server-daemon
             fi

             echo " Instalando scripts de inicio..."

             if [ -d /etc/init.d ] ; then
               echo "#!/bin/sh" > /etc/init.d/emaku 
               echo " " >> /etc/init.d/emaku 
               echo "EMAKU_USER=emaku" >> /etc/init.d/emaku
               echo "EMAKU_DAEMON=$EMAKU_HOME/bin/emaku-server" >> /etc/init.d/emaku
               echo " " >> /etc/init.d/emaku 

               cat $ROOT/bin/rc.emaku-server >> /etc/init.d/emaku

               chmod 755 /etc/init.d/emaku

               if [ -d /etc/rc3.d ] && [ ! -e /etc/rc3.d/S99emaku ] ; then
                 ln -s /etc/init.d/emaku /etc/rc3.d/S99emaku
               fi
               if [ -d /etc/rc5.d ] && [ ! -e /etc/rc5.d/S99emaku ] ; then
                 ln -s /etc/init.d/emaku /etc/rc5.d/S99emaku
               fi
               if [ -d /etc/rc0.d ] && [ ! -e /etc/rc0.d/K99emaku ] ; then
                 ln -s /etc/init.d/emaku /etc/rc0.d/K99emaku
               fi
               if [ -d /etc/rc6.d ] && [ ! -e /etc/rc6.d/K99emaku ] ; then
                 ln -s /etc/init.d/emaku /etc/rc6.d/K99emaku
               fi
             else
               if [ -e /etc/rc.d/rc.local ] ; then
                  RC_PREV=`egrep emaku-server /etc/rc.d/rc.local`
                  if [ "$RC_PREV" = "" ] ; then
                     echo "" >> /etc/rc.d/rc.local
                     echo "# Iniciando servidor E-Maku..." >> /etc/rc.d/rc.local
                     echo "/usr/sbin/emaku-server start >& /dev/null" >> /etc/rc.d/rc.local
                  fi
               fi
 
             fi
}

install_client() {
             echo "#!/bin/sh" > $EMAKU_HOME/bin/emaku-client
             echo " " >> $EMAKU_HOME/bin/emaku-client
             echo "EMAKU_HOME=$EMAKU_HOME" >> $EMAKU_HOME/bin/emaku-client
             echo "export EMAKU_HOME" >> $EMAKU_HOME/bin/emaku-client
             cat $ROOT/bin/emaku-client >> $EMAKU_HOME/bin/emaku-client

             if [ ! -L /usr/bin/emaku-client ] ; then
               ln -s $EMAKU_HOME/bin/emaku-client /usr/bin/emaku-client
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
             echo "  -i                       : Instala E-Maku en /usr/local/emaku "
             echo "  install                  : Instala E-Maku en /usr/local/emaku "
             echo "  -i -p /ruta/emaku        : Instala E-Maku en /ruta/emaku"
             echo "  -i --prefix=/ruta/emaku  : Instala E-Maku en /ruta/emaku"
             echo "  uninstall                : Elimina la instalacion de E-Maku "
             echo "  -u                       : Elimina la instalacion de E-Maku "
             echo
}

remove(){
             find . -name '*.class' -exec rm {} \;
             \rm -rf build
             \rm -f lib/emaku/common.jar
             \rm -f lib/emaku/client.jar
             \rm -f lib/emaku/server.jar 
}

case "$1" in

  -c|clean)
             echo
             echo " Eliminando archivos de compilacion y archivos temporales..."
             remove
             \rm -f *.log
             echo " Hecho."
             echo
             ;;

  -i|install)

             check_emaku_user

             if [ ! -d "build" ] ; then 
               echo "ALL" > $ROOT/core.log
               compile_all
             fi

             check_root

             if [ -z $2 ] ; then
               echo
               echo " Utilizando ruta de instalacion por defecto (/usr/local/emaku)"
               EMAKU_HOME=/usr/local/emaku
             else
               case "$2" in
                 -p) 
                    if [ "$3" != "" ] ; then
                      EMAKU_HOME=$3
                    else
                      echo 
                      echo " ERROR: La ruta de instalacion de E-Maku no fue ingresada."
                      echo "        Ej: ./build.sh -i -p /usr/local/emaku"
                      echo
                      exit 1
                    fi
                    ;;
                --prefix=*)
                    EMAKU_HOME=`echo $2 | cut -f2 -d=`  
                    if [ "$EMAKU_HOME" = "" ] ; then
                      echo
                      echo " ERROR: La ruta de instalacion de E-Maku no fue ingresada."
                      echo "        Ej: ./build.sh -i --prefix=/usr/local/emaku"
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

             chmod -R 755 $EMAKU_HOME

             chown -R emaku $EMAKU_HOME
             chgrp -R emaku $EMAKU_HOME

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
               EMAKU_HOME=`cat $ROOT/install.log` 
               if [ ! -d "$EMAKU_HOME" ] ; then
                 echo
                 echo " ERROR: No se encuentra el directorio de instalacion de E-Maku" 
                 echo " Aparentemente el directorio \"$EMAKU_HOME\" ya fue eliminado."
                 echo
                 exit 1
               fi
             fi
             
             echo 
             echo " Eliminando directorio de instalacion de E-Maku..."
             rm -rf $EMAKU_HOME

             if [ -L /usr/bin/emaku-client ] ; then
               rm -f /usr/bin/emaku-client
             fi
             if [ -L /usr/sbin/emaku-server ] ; then
               rm -f /usr/sbin/emaku-server
             fi

             echo " Eliminando scripts de inicio..."
             if [ -d /etc/init.d ] && [ -e /etc/init.d/emaku ] ; then
               rm -f /etc/init.d/emaku
               if [ -d /etc/rc3.d ] && [ -e /etc/rc3.d/S99emaku ] ; then
                 rm -f /etc/rc3.d/S99emaku
               fi
               if [ -d /etc/rc5.d ] && [ -e /etc/rc3.d/S99emaku ] ; then
                 rm -f /etc/rc5.d/S99emaku
               fi
              if [ -d /etc/rc0.d ] && [ -e /etc/rc0.d/K99emaku ] ; then
                 rm -f /etc/rc0.d/K99emaku
               fi
              if [ -d /etc/rc6.d ] && [ -e /etc/rc0.d/K99emaku ] ; then
                 rm -f /etc/rc6.d/K99emaku
               fi
             else
               RC_PREV=`egrep emaku-server /etc/rc.d/rc.local`
               if [ "$RC_PREV" != "" ] && [ -e /etc/rc.d/rc.local ] ; then
                 egrep -v emaku-server /etc/rc.d/rc.local | grep -v E-Maku > /tmp/rc.local.tmp
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
