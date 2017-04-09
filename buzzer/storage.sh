#!/bin/bash

LIBS=$(echo ../libs/* | sed 's/ /:/g')
CONF=$(echo conf/* | sed 's/ /:/g')
SCRIPTS=$(echo scripts/* | sed 's/ /:/g')
cd storage
java -cp "target/storage-0.0.1-SNAPSHOT.jar:$LIBS:$CONF:$SCRIPTS" ar.fiuba.taller.storage.App
