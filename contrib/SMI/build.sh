#!/bin/bash
#
# Bandera para activar el debug del ant
ANT_FLAG="-q"

clean_lib() {
   echo
   echo "* Eliminando archivos de compilacion en Lib..."
   cd ./smi/SMI-Lib/
   ant $ANT_FLAG clean
   cd -
   find . -iname smilib.jar -exec rm {} \;
}

clean_server() {
   echo
   echo "* Eliminando archivos de compilacion en Servidor..."
   rm -rvf smi/SMI-Server/log/*
   cd ./smi/SMI-Server/
   ant $ANT_FLAG clean
   cd -
   \rm -rf dist/Servidor/*
   find . -iname smiserver.jar -exec rm {} \;
}

clean_client() {
   echo
   echo "* Eliminando archivos de compilacion en Cliente..."
   cd ./smi/SMI-Client/
   ant $ANT_FLAG clean
   cd -
   \rm -rf dist/Cliente/*
   find . -iname smiclient.jar -exec rm {} \;
}

clean_admin() {
   echo
   echo "* Eliminando archivos de compilacion en Admin..."
   cd ./smi/SMI-Admin/
   ant $ANT_FLAG clean
   cd -
   \rm -rf dist/Administrador/*
   find . -iname smiadmin.jar -exec rm {} \;
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
   echo "  compile                  : Compila SMI"
   echo "  -c                       : Elimina archivos .class "
   echo "  clean                    : Elimina archivos .class "
   echo
}

compile_lib(){
   echo "* Compilando fuentes SMI-Lib..."
   echo
   cd ./smi/SMI-Lib/
   ant $ANT_FLAG build
   cd -
}

compile_server(){
   echo "* Compilando fuentes SMI-Server..."
   echo
   cd ./smi/SMI-Server/
   ant $ANT_FLAG build
   cd -
}

compile_client() {
   echo "* Compilando fuentes SMI-Client..."
   echo
   cd ./smi/SMI-Client/
   ant $ANT_FLAG build
   cd -
}

compile_admin() {
   echo "* Compilando fuentes SMI-Admin..."
   echo
   cd ./smi/SMI-Admin/
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
   echo "* Creando jars para SMI-Lib..."
   echo
   cd smi/SMI-Lib/bin
   jar -cf smilib.jar *
   cd -
   mv -v smi/SMI-Lib/bin/smilib.jar smi/SMI-Lib/dist/
}

packaging_server(){
   echo "* Creando jars para SMI-Server..."
   cp -v smi/SMI-Lib/dist/smilib.jar smi/SMI-Server/smiserver/libs
   cd smi/SMI-Server/bin
   cp ../src/*.xml .
   jar -cf smiserver.jar *
   mv smiserver.jar ../smiserver/libs
   cd ..
   find ./smiserver | grep ".svn" > EXCLUDE 
   tar cfX smiserver.tar EXCLUDE smiserver
   gzip smiserver.tar
   rm /tmp/EXCLUDE
   mv smiserver.tar.gz  ../../dist/Servidor
   cd ../../
}

packaging_admin(){
   echo "* Creando jars para SMI-Admin..."
   cp -v smi/SMI-Lib/dist/smilib.jar smi/SMI-Admin/dist/
   cd smi/SMI-Admin/bin
   cp ../src/menu.xml .
   jar -cf smiadmin.jar *
   mv smiadmin.jar ../dist
   cd ../dist
   tar cfz smiadmin.tar.gz *
   mv smiadmin.tar.gz ../../../dist/Administrador
   cd ../../../
}

packaging_client(){
   echo "* Creando jars para SMI-Client..."
   cp -v smi/SMI-Lib/dist/smilib.jar smi/SMI-Client/dist/
   cd smi/SMI-Client/bin
   jar -cf smiclient.jar *
   mv smiclient.jar ../dist
   cd ..
   #jar -cf icons.jar icons
   #mv icons.jar dist/libs
   cd dist
   tar cfz smiclient.tar.gz *
   mv smiclient.tar.gz ../../../dist/Cliente
   zip -qr smiclient.zip *
   mv smiclient.zip ../../../dist/Cliente
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

