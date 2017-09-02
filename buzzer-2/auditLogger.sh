#!/bin/bash

LIBS=$(echo ../libs/* | sed 's/ /:/g')
CONF=$(echo conf/* | sed 's/ /:/g')
SCRIPTS=$(echo scripts/* | sed 's/ /:/g')
cd auditLogger
java -cp "target/auditLogger-0.0.1-SNAPSHOT.jar:$LIBS:$CONF:$SCRIPTS" ar.fiuba.taller.auditLogger.App
