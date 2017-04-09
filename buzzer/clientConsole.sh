#!/bin/bash

LIBS=$(echo ../libs/* | sed 's/ /:/g')
CONF=$(echo conf/* | sed 's/ /:/g')
SCRIPTS=$(echo scripts/* | sed 's/ /:/g')
cd ClientConsole
java -cp "target/ClientConsole-0.0.1-SNAPSHOT.jar:$LIBS:$CONF:$SCRIPTS" ar.fiuba.taller.ClientConsole.App
