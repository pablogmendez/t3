#sudo rabbitmqctl list_queues | grep -v Listing | python delete_queues.py
for topic in $(/opt/kafka/kafka_2.12-0.11.0.0/bin/kafka-topics.sh --list --zookeeper localhost:2181 | grep -v __consumer_offsets); do
	/opt/kafka/kafka_2.12-0.11.0.0/bin/kafka-topics.sh --delete --zookeeper localhost:2181 --topic $topic > /dev/null 2>&1
	if [ $? -eq 0 ]; then	
		echo "$topic --> Borrado"
	else
		echo "No se pudo borra el topic: $topic"
	fi
done

