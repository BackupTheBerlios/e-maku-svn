#!/bin/bash

echo "Empaquetado"

rm -rvf smi/SMI-Server/log/*

cd smi/SMI-Lib/bin
jar -cvf smilib.jar *
mv smilib.jar ../dist/libs
cd ../../../

cp -v smi/SMI-Lib/dist/smilib.jar smi/SMI-Client/dist/
cp -v smi/SMI-Lib/dist/smilib.jar smi/SMI-Admin/dist/
cp -v smi/SMI-Lib/dist/smilib.jar smi/SMI-Server/dist/libs

cd smi/SMI-Server/bin
jar -cvf smiserver.jar *
mv smiserver.jar ../dist/libs
cd ../dist
tar cvfz smiserver.tar.gz *
mv smiserver.tar.gz  ../../../dist/Servidor

cd ../../../
cd smi/SMI-Admin/bin
jar -cvf smiadmin.jar *
mv smiadmin.jar ../dist
cd ../dist
tar cvfz smiadmin.tar.gz *
mv smiadmin.tar.gz ../../../dist/Administrador

cd ../../../
cd smi/SMI-Client/bin
jar -cvf smiclient.jar *
mv smiclient.jar ../dist
cd ../dist
tar cvfz smiclient.tar.gz *
mv smiclient.tar.gz ../../../dist/Cliente

