#!/bin/bash

echo "Borrando los topics de kafka"
find  /tmp/kafka-logs/ -type d| grep -e queue -e user -e offsets | xargs rm -fr
echo "ok"

echo borrando los topics de zookeeper
#TOPICS=$(/opt/kafka/kafka_2.12-0.11.0.0/bin/zookeeper-shell.sh localhost:2181 ls /brokers/topics | grep '\[' | sed 's/\[/, /g' | cut -f1 -d] | sed 's/, __consumer_offsets//g' | sed 's/, / \/brokers\/topics\//g')
#IFS=" "
#for t in $TOPICS; do
#	echo "Borrando topic --  $t"
#	/opt/kafka/kafka_2.12-0.11.0.0/bin/zookeeper-shell.sh localhost:2181 rmr $t
#don
/opt/kafka_2.11-0.11.0.1/bin/zookeeper-shell.sh localhost:2181 rmr /brokers/topics

echo "ok"
