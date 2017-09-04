#!/bin/bash

LIBS=$(echo ../libs/* | sed 's/ /:/g')
CONF=$(echo conf/* | sed 's/ /:/g')
cd storage
java -cp "target/storage-0.0.1-SNAPSHOT.jar:$LIBS:../common/target/common-0.0.1-SNAPSHOT.jar:conf/" ar.fiuba.taller.storage.MainStorage
