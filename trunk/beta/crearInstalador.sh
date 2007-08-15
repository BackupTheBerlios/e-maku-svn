#!/bin/bash

ROOT=/home/emaku/beta.berlios/beta

COMPILE=`which compile`

if [ "$COMPILE" == "" ] ; then 
  echo
  echo " ERROR:"
  echo " El paquete Izpack no se encuentra instalado en este sistema."
  echo " Desgarguelo desde http://www.izforge.com/izpack/"
  echo
  exit
fi

compile $ROOT/instalador.xml -b . -o $ROOT/instalador-emaku.jar -k standard
