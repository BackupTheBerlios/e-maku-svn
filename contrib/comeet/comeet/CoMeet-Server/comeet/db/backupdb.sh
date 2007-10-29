#!/bin/bash
# Script para sacar backup de la base de datos de CoMeet

DB="comeet"
DATE=`date +%d-%m-%Y_%H:%M`
FILE=$HOME/backups/comeet_$DATE.sql

/usr/bin/pg_dump $DB > $FILE

/usr/bin/bzip2 $FILE

