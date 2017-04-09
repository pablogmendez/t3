#!/bin/bash

LIBS=$(echo ../libs/* | sed 's/ /:/g')
CONF=$(echo conf/* | sed 's/ /:/g')
SCRIPTS=$(echo scripts/* | sed 's/ /:/g')
cd dispatcher
java -cp "target/dispatcher-0.0.1-SNAPSHOT.jar:$LIBS:$CONF:$SCRIPTS" ar.fiuba.taller.dispatcher.App
