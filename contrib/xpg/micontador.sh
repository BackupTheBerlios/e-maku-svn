fecha=`date | awk '{ print $3"-" $2"-" $6 }'`
namelog=contalineas-$fecha.log
log=/tmp/$namelog
total=0;

datos(){
    echo "de que directorio de archivos desea que se cuenten las lineas de codigo "
    echo "ejm:"
    echo "/usr/src/linux"
    read dir
    echo "que tipo de archivos desea buscar en $dir  ( * para todos)"
    echo "ejm:"
    echo "*.c"
    read tarch
    echo "fecha de ejecucion $fecha"
    find $dir -name "$tarch" > /tmp/lista.contalineas 
}

ciclo(){
    echo "Procesando numero de lineas"
    echo " " > $log
    echo $fecha >> $log
    echo $dir  >> $log
    echo $tarch >> $log

    for art in `cat /tmp/lista.contalineas` 
      do
      suma=`gawk '/\x2A|\x2F\x2F/ {i++}  END{print NR-i}' $art`
      total=`echo $(($total+$suma))`
      echo "*****************************" >> $log 
      echo $art >> $log
      echo $suma >> $log
      echo "subtotal = $total" >> $log
      echo "*****************************" >> $log
    done
    echo "****************************"
    echo Total de lineas $total >> $log
    echo Total de lineas $total
    echo "****************************"
    echo "en el archivo $log encontrara un log detallado "
    echo "*****"
    echo "EL ARCHIVO LOG SERA REESCRITO CADA VEZ QUE EJECUTE EL SCRIPT"
    echo "Si tiene algun mensaje de error podria ser que no tiene permisos en algunos de los archivo a contar"
    echo "y esto podria ocasionar cambios en los resultados"
}

if datos 
    then
    ciclo
    else
    echo "erro de ejecucion"
fi

#echo "cuantas lineas por dia?"
#read tdia 
#echo dias $(($total/$tdia))
#echo años $(($total/$tdia/365)) 
#echo y $(($total/$tdia%365)) dias

