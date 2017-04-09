#!/bin/bash

if [ -z $JAVA_HOME ]; then
	echo "Error: No se ha seteado la variable JAVA_HOME"
fi
DISPLAY_HELP=false

while getopts ":i :c :d" opt; do
  case $opt in
    i)
      echo "Instalando el programa"
      cd crea-deploy
      mvn clean package
      [ $? -ne 0 ] && exit 1
      cd ..
      cp common/target/common-0.0.1-SNAPSHOT.jar libs
      ;;
    c)
      echo "Ejecutando clientConsole"
      gnome-terminal -e "bash -c \"./clientConsole.sh;exec bash\""
      [ $? -ne 0 ] && exit 1
      ;;
    d)
      echo "Ejecutando dispatcher"
      gnome-terminal -e "bash -c \"./dispatcher.sh;exec bash\""
      [ $? -ne 0 ] && exit 1
      ;;
  esac
done

if [ $DISPLAY_HELP == true ]; then
	echo "buzzer - Help"
	echo "----------------------"
	echo "Uso:"
	echo "	./buzzer PARAM"
	echo ""
	echo "Parametros:"
	echo "	Accion al ingresar PARAM"
	echo "	-i	Compila e instala el programa"
	echo "	-c	Ejecuta el cliente"
	echo "	-d	Ejecuta el dispatcher"
	echo ""
fi
