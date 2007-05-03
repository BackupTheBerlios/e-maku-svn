#!/bin/bash

clean() {
   echo
   echo "* Eliminando archivos de compilacion y archivos temporales..."
   echo
   rm -rvf smi/SMI-Server/log/*
   cd ./smi/SMI-Lib/
   ant clean
   cd -
   cd ./smi/SMI-Server/
   ant clean
   cd -
   cd ./smi/SMI-Client/
   ant clean
   cd -
   cd ./smi/SMI-Admin/
   ant clean
   cd -
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

compile(){
   echo "* Compilando..."
   echo

   echo "* Compilando fuentes SMI-Lib..."
   echo
   cd ./smi/SMI-Lib/
   ant build
   cd -

   echo
   echo "* Compilando fuentes SMI-Server..."
   echo
   cd ./smi/SMI-Server/
   ant build
   cd -

   echo
   echo "* Compilando fuentes SMI-Client..."
   echo
   cd ./smi/SMI-Client/
   ant build
   cd -

   echo
   echo "* Compilando fuentes SMI-Admin..."
   echo
   cd ./smi/SMI-Admin/
   ant build
   cd -
   echo
   echo "* Empaquetando..."

   echo
   echo "* Creando jars para SMI-Lib..."
   echo
   cd smi/SMI-Lib/bin
   jar -cf smilib.jar *
   cd -
   mv -v smi/SMI-Lib/bin/smilib.jar smi/SMI-Lib/dist/

   cp -v smi/SMI-Lib/dist/smilib.jar smi/SMI-Client/dist/
   cp -v smi/SMI-Lib/dist/smilib.jar smi/SMI-Admin/dist/
   cp -v smi/SMI-Lib/dist/smilib.jar smi/SMI-Server/dist/libs

   echo
   echo "* Creando jars para SMI-Server..."
   cd smi/SMI-Server/bin
   cp ../src/*.xml .
   jar -cf smiserver.jar *
   mv smiserver.jar ../dist/libs
   cd ../dist
   tar cfz smiserver.tar.gz *
   mv smiserver.tar.gz  ../../../dist/Servidor

   echo "* Creando jars para SMI-Admin..."
   cd ../../../ 
   cd smi/SMI-Admin/bin
   cp ../src/resources/menu.xml .
   jar -cf smiadmin.jar *
   mv smiadmin.jar ../dist
   cd ../dist
   tar cfz smiadmin.tar.gz *
   mv smiadmin.tar.gz ../../../dist/Administrador

   echo "* Creando jars para SMI-Client..."

   cd ../../../ 
   cd smi/SMI-Client/bin
   jar -cf smiclient.jar *
   mv smiclient.jar ../dist
   cd ..
   jar -cf icons.jar icons 
   mv icons.jar dist/libs
   cd dist
   tar cfz smiclient.tar.gz *
   mv smiclient.tar.gz ../../../dist/Cliente
   zip -qr smiclient.zip *
   mv smiclient.zip ../../../dist/Cliente

   echo "* Instaladores Listos"
}

case "$1" in

  -c|clean)
             clean
             ;;
   compile)
             clean
             compile
             ;;
   *)
             help
             ;;
esac

