#!/bin/bash
# Script para sacar backup de la base de datos del SMI

DB="smi"
DATE=`date +%d-%m-%Y_%H:%M`
FILE=$HOME/backups/smi_$DATE.sql

/usr/bin/pg_dump $DB > $FILE

/usr/bin/bzip2 $FILE

