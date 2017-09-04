#!/bin/bash

LIBS=$(echo ../libs/* | sed 's/ /:/g')
CONF=$(echo conf/* | sed 's/ /:/g')
cd dispatcher
java -cp "target/dispatcher-0.0.1-SNAPSHOT.jar:$LIBS:../common/target/common-0.0.1-SNAPSHOT.jar:conf/" ar.fiuba.taller.dispatcher.MainDispatcher
