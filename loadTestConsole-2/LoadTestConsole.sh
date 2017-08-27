#!/bin/bash

if [ -z $JAVA_HOME ]; then
	echo "Error: No se ha seteado la variable JAVA_HOME"
fi

DISPLAY_HELP=false
LIBS=$(echo lib/* | sed 's/ /:/g')
CONF=$(echo conf/* | sed 's/ /:/g')

while getopts ":i :r" opt; do
  case $opt in
    i)
      echo "Instalando el programa"
      mvn clean
      [ $? -ne 0 ] && exit 1
      mvn package
      [ $? -ne 0 ] && exit 1
      ;;
    r)
      echo "Ejecutando el programa"
      gnome-terminal -e "bash -c \"watch -n 1 cat log/Report.txt; exec bash\"" &
      java -cp "target/loadTestConsole-0.0.1.jar:$LIBS:$CONF" ar.fiuba.taller.loadTestConsole.Main
      [ $? -ne 0 ] && exit 1
      ;;
    *)
      echo "Invalid option: -$OPTARG"
      DISPLAY_HELP=true
      ;;
  esac
done

if [ $DISPLAY_HELP == true ]; then
	echo "LoadTestConsole - Help"
	echo "----------------------"
	echo "Uso:"
	echo "	./LoadTestConsole [param]"
	echo ""
	echo "[param]:"
	echo "	-i	Compila e instala el programa"
	echo "	-r	Inicia el programa"
	echo ""
fi
