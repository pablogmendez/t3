#!/bin/bash

LIBS=$(echo ../libs/* | sed 's/ /:/g')
CONF=$(echo conf/* | sed 's/ /:/g')
cd ClientConsole
java -cp "target/ClientConsole-0.0.1-SNAPSHOT.jar:$LIBS:../common/target/common-0.0.1-SNAPSHOT.jar:conf/" ar.fiuba.taller.ClientConsole.MainClientConsole $1 $2 $3
