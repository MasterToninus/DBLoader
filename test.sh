#!/bin/bash

	MAIN_CLASS_FILE="it.csttech.dbloader.test.ReflectionDbLoader"
	CLASS_PATH=".;lib/*;bin" #; su windows?
	LOG_CONFIG="config/log4j2.xml"
	PROPERTIES_FILE="config/dbloader.properties"


	echo "---------------------------------"
	echo "*** Launching $MAIN_CLASS_FILE $@ ***"
	echo "---------------------------------"
	echo " "


	java -Dlog4j.configurationFile=$LOG_CONFIG -Dprop.File=$PROPERTIES_FILE -cp $CLASS_PATH $MAIN_CLASS_FILE $@

	echo " "
	echo "---------------------------------"
