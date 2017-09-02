#!/bin/bash

# ----------- Funciones auxiliares ----------------
function execClientConsole() {
      echo "Ejecutando clientConsole"
      gnome-terminal --title "clientConsole" -e "bash -c \"./clientConsole.sh;exec bash\""
      [ $? -ne 0 ] && exit 1
}

function execDispatcher() {
      echo "Ejecutando dispatcher"
      gnome-terminal --title "dispatcher" -e "bash -c \"./dispatcher.sh;exec bash\""
      [ $? -ne 0 ] && exit 1
}

function execAuditLogger() {
      echo "Ejecutando audit logger"
      gnome-terminal --title "auditLogger" -e "bash -c \"./auditLogger.sh;exec bash\""
      [ $? -ne 0 ] && exit 1
}

function execStorage() {
      echo "Ejecutando el storage"
      gnome-terminal --title "storage" -e "bash -c \"./storage.sh;exec bash\""
      [ $? -ne 0 ] && exit 1
}

function execAnalyzer() {
      echo "Ejecutando el analyzer"
      gnome-terminal --title "analyzer" -e "bash -c \"./analyzer.sh;exec bash\""
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

while getopts ":i :c :d :u :s :n :a" opt; do
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
    s)
      execStorage
      ;;
    n)
      execAnalyzer
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
	echo "	-s	Ejecuta el ${bold}s${normal}torage"
	echo "	-n	Ejecuta el a${bold}n${normal}alyzer"
	echo "	-a	Ejecuta todos los programas"
	echo ""
fi
