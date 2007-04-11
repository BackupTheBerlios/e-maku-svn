#!/bin/bash

echo "Copiando archivo de configuracion"
cp -v smi_client.conf /etc/

echo "Creando directorio para la aplicacion"
mkdir /usr/local/smiclient

echo "copiando archivos"
cp -rfv libs /usr/local/smiclient
cp -v *.jar /usr/local/smiclient
cp -v smiclient /usr/local/smiclient/
ln -s /usr/local/smiclient/smiclient /home/gamble/.kde/Autostart/smiclient
chmod o+rx /usr/local/smiclient/smiclient

