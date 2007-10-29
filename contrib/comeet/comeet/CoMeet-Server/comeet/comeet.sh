#!/bin/bash
export COMEET_HOME=/usr/local/comeet
cd $COMEET_HOME
$COMEET_HOME/bin/comeet-server.bin $COMEET_HOME/conf/wrapper.conf
