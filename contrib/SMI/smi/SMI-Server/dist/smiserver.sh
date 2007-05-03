#!/bin/bash
export SMI_HOME=/usr/local/smiserver
cd $SMI_HOME
$SMI_HOME/bin/smi-server.bin $SMI_HOME/conf/wrapper.conf
