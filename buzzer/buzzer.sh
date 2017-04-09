#!/bin/bash

# ----------- Funciones auxiliares ----------------
function execClientConsole() {
      echo "Ejecutando clientConsole"
      gnome-terminal -e "bash -c \"./clientConsole.sh;exec bash\""
      [ $? -ne 0 ] && exit 1
}

function execDispatcher() {
      echo "Ejecutando dispatcher"
      gnome-terminal -e "bash -c \"./dispatcher.sh;exec bash\""
      [ $? -ne 0 ] && exit 1
}

function execAuditLogger() {
      echo "Ejecutando audit logger"
      gnome-terminal -e "bash -c \"./auditLogger.sh;exec bash\""
      [ $? -ne 0 ] && exit 1
}

function execAll() {
      echo "Ejecutando todos los programas"
      execClientConsole
      execDispatcher
      execAuditLogger
}
# -------------------------------------------------

# Validacion de java
if [ -z $JAVA_HOME ]; then
	echo "Error: No se ha seteado la variable JAVA_HOME"
fi

# Variables globales
DISPLAY_HELP=false
bold=$(tput bold)
normal=$(tput sgr0)

while getopts ":i :c :d :u :a" opt; do
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
      execClientConsole
      ;;
    d)
      execDispatcher
      ;;
    u)
      execAuditLogger
      ;;
    a)
      execAll
      ;;
   *)
      echo "Invalid option: -$OPTARG"
      DISPLAY_HELP=true
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
	echo "	-i	${bold}I${normal}nstala el programa"
	echo "	-c	Ejecuta el ${bold}c${normal}liente"
	echo "	-d	Ejecuta el ${bold}d${normal}ispatcher"
	echo "	-u	Ejecuta el a${bold}u${normal}dit logger"
	echo "	-a	Ejecuta todos los programas"
	echo ""
fi
