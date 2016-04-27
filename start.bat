@echo off

	set MAIN_CLASS_FILE="it.csttech.etltools.UseETL"
	set CLASS_PATH=".;lib\*;bin"
	set LOG_CONFIG="config\log4j2.xml"


	echo "---------------------------------"
	echo "*** Launching %MAIN_CLASS_FILE% %1 %2 %3 %4 %5 %6 %7 %8 %9 ***"
	echo "---------------------------------"
	echo " "


	java -Dlog4j.configurationFile=%LOG_CONFIG% -cp %CLASS_PATH% %MAIN_CLASS_FILE% %1 %2 %3 %4 %5 %6 %7 %8 %9
	
	echo " "
	echo "---------------------------------"
