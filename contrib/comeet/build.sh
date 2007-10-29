#!/bin/bash
#
# Bandera para activar el debug del ant
ANT_FLAG="-q"

clean_lib() {
   echo
   echo "* Eliminando archivos de compilacion en Lib..."
   cd ./comeet/CoMeet-Lib/
   ant $ANT_FLAG clean
   cd -
   find . -iname comeetlib.jar -exec rm {} \;
}

clean_server() {
   echo
   echo "* Eliminando archivos de compilacion en Servidor..."
   rm -rvf comeet/CoMeet-Server/log/*
   cd ./comeet/CoMeet-Server/
   ant $ANT_FLAG clean
   cd -
   \rm -rf dist/Servidor/*
   find . -iname comeetserver.jar -exec rm {} \;
}

clean_client() {
   echo
   echo "* Eliminando archivos de compilacion en Cliente..."
   cd ./comeet/CoMeet-Client/
   ant $ANT_FLAG clean
   cd -
   \rm -rf dist/Cliente/*
   find . -iname comeetclient.jar -exec rm {} \;
}

clean_admin() {
   echo
   echo "* Eliminando archivos de compilacion en Admin..."
   cd ./comeet/CoMeet-Admin/
   ant $ANT_FLAG clean
   cd -
   \rm -rf dist/Administrador/*
   find . -iname comeetadmin.jar -exec rm {} \;
}

clean_all() {
   clean_lib
   clean_server
   clean_client
   clean_admin
   echo
   echo "* Hecho."
   echo
}

help(){
   echo
   echo " Uso: ./build.sh [opciones] "
   echo " Donde las opciones pueden ser:"
   echo "  help                     : Despliega este mensaje "
   echo "  compile                  : Compila CoMeet"
   echo "  compile client           : Compila el cliente CoMeet"
   echo "  compile server           : Compila el servidor CoMeet"
   echo "  compile admin            : Compila el admin CoMeet"
   echo "  -c                       : Elimina archivos .class "
   echo "  clean                    : Elimina archivos .class "
   echo
}

compile_lib(){
   echo "* Compilando fuentes CoMeet-Lib..."
   echo
   cd ./comeet/CoMeet-Lib/
   ant $ANT_FLAG build
   cd -
}

compile_server(){
   echo "* Compilando fuentes CoMeet-Server..."
   echo
   cd ./comeet/CoMeet-Server/
   ant $ANT_FLAG build
   cd -
}

compile_client() {
   echo "* Compilando fuentes CoMeet-Client..."
   echo
   cd ./comeet/CoMeet-Client/
   ant $ANT_FLAG build
   cd -
}

compile_admin() {
   echo "* Compilando fuentes CoMeet-Admin..."
   echo
   cd ./comeet/CoMeet-Admin/
   ant $ANT_FLAG build
   cd -
}

compile_all(){
   echo "* Compilando..."
   echo
   compile_lib
   echo
   compile_server
   echo
   compile_client
   echo
   compile_admin
}

packaging_lib(){
   echo "* Creando jars para CoMeet-Lib..."
   echo
   cd comeet/CoMeet-Lib/bin
   jar -cf comeetlib.jar *
   cd -
   mv -v comeet/CoMeet-Lib/bin/comeetlib.jar comeet/CoMeet-Lib/dist/
}

packaging_server(){
   echo "* Creando jars para CoMeet-Server..."
   cp -v comeet/CoMeet-Lib/dist/comeetlib.jar comeet/CoMeet-Server/comeet/libs
   cd comeet/CoMeet-Server/bin
   cp ../src/*.xml .
   jar -cf comeetserver.jar *
   mv comeetserver.jar ../comeet/libs
   cd ..
   cp /dev/null comeet/log/comeet_server.log
   find ./comeet | grep ".svn" > /tmp/EXCLUDE 
   tar cfX comeetserver.tar /tmp/EXCLUDE comeet
   gzip comeetserver.tar
   rm /tmp/EXCLUDE
   mv comeetserver.tar.gz  ../../dist/Servidor
   cd ../../
}

packaging_admin(){
   echo "* Creando jars para CoMeet-Admin..."
   cp -v comeet/CoMeet-Lib/dist/comeetlib.jar comeet/CoMeet-Admin/dist/
   cd comeet/CoMeet-Admin/bin
   cp ../src/menu.xml .
   jar -cf comeetadmin.jar *
   mv comeetadmin.jar ../dist
   cd ..
   jar -cf icons.jar icons
   mv icons.jar dist/libs
   cd dist
   tar cfz comeetadmin.tar.gz *
   mv comeetadmin.tar.gz ../../../dist/Administrador
   zip -qr comeetadmin.zip *
   mv comeetadmin.zip ../../../dist/Administrador
   cd ../../../
}

packaging_client(){
   echo "* Creando jars para CoMeet-Client..."
   cp -v comeet/CoMeet-Lib/dist/comeetlib.jar comeet/CoMeet-Client/dist/libs
   cd comeet/CoMeet-Client/bin
   jar -cf comeetclient.jar *
   mv comeetclient.jar ../dist/libs
   cd ..
   jar -cf icons.jar icons
   mv icons.jar dist/libs
   cd dist
   tar cfz comeetclient.tar.gz *
   mv comeetclient.tar.gz ../../../dist/Cliente
   zip -qr comeetclient.zip *
   mv comeetclient.zip ../../../dist/Cliente
   cd ../../../
}

packaging_all(){
   echo "* Empaquetando..."
   echo
   packaging_lib
   echo
   packaging_server
   echo
   packaging_admin
   echo
   packaging_client
   echo
   echo "* Instaladores Listos"
}

case "$1" in

  -c|clean)
             clean_all
             ;;
   compile)
             if [ -z $2 ] ; then
                clean_all
                compile_all
                packaging_all
             else
               case "$2" in
                 server)
                        clean_lib
                        echo
                        compile_lib
                        echo
			packaging_lib
                        echo
                        clean_server
                        echo
                        compile_server
                        echo
                        packaging_server
                        ;;
                 client)
                        clean_lib
                        echo
                        compile_lib
                        echo
                        packaging_lib
                        echo
                        clean_client
                        echo
                        compile_client
                        echo
                        packaging_client
                        ;;
                 admin)
                        clean_lib
                        echo
                        compile_lib
                        echo
                        packaging_lib
                        echo
                        clean_admin
                        echo
                        compile_admin
                        echo
                        packaging_admin
                        ;;
               esac
             fi
             ;;
   *)
             help
             ;;
esac

