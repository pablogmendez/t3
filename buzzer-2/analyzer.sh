#!/bin/bash

LIBS=$(echo ../libs/* | sed 's/ /:/g')
CONF=$(echo conf/* | sed 's/ /:/g')
SCRIPTS=$(echo scripts/* | sed 's/ /:/g')
cd analyzer
java -cp "target/analyzer-0.0.1-SNAPSHOT.jar:$LIBS:$CONF:$SCRIPTS" ar.fiuba.taller.analyzer.App
