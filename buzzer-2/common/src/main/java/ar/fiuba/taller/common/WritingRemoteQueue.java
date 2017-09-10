package ar.fiuba.taller.common;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

public class WritingRemoteQueue extends RemoteQueue {
	private Producer<byte[], byte[]> producer;
	private String queueName;

	public WritingRemoteQueue(String queueName, String queueHost, Map<String, String> params) {
		Properties props = new Properties();
		this.queueName = queueName;
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, queueHost);
		props.put(ProducerConfig.ACKS_CONFIG, params.get(Constants.ACKS_CONFIG));
		props.put(ProducerConfig.RETRIES_CONFIG, Integer.parseInt(params.get(Constants.RETRIES_CONFIG)));
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, params.get(Constants.VALUE_SERIALIZER_CLASS_CONFIG));
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, params.get(Constants.KEY_SERIALIZER_CLASS_CONFIG));
		producer = new KafkaProducer<byte[], byte[]>(props);
	}

	public void close() throws IOException, TimeoutException {
		producer.close();
	}

	public void push(ISerialize message) throws IOException {
			ProducerRecord<byte[], byte[]> data = new ProducerRecord<byte[], byte[]>(
               queueName, message.serialize());
			producer.send(data);
	}

}
