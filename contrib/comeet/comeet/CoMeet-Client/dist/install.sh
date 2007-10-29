	#!/bin/bash

echo "Copiando archivo de configuracion"
cp -v conf/comeet.conf /etc/

echo "Creando directorio para la aplicacion"
mkdir /usr/local/comeet

echo "copiando archivos"
cp -rfv libs /usr/local/comeet
cp -v *.jar /usr/local/comeet
cp -v comeetclient /usr/local/comeet/
ln -s /usr/local/comeet/comeetclient.sh /home/usuario/.kde/Autostart/comeetclient.sh
chmod o+rx /usr/local/comeet/comeetclient
