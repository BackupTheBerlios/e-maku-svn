#!/bin/bash

COMPILE=`which compile`

if [ "$COMPILE" == "" ] ; then 
  echo
  echo " ERROR:"
  echo " El paquete Izpack no se encuentra instalado en este sistema."
  echo " Desgarguelo desde http://www.izforge.com/izpack/"
  echo
  exit
fi

compile instalador.xml -b . -o instalador-jmidas.jar -k standard
