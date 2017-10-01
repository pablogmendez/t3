package ar.fiuba.taller.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class WritingRemoteQueue extends RemoteQueue {
	private Producer<byte[], byte[]> producer;
	private String queueName;

	public WritingRemoteQueue(String queueName,
			String propertiesFile) throws IOException {
		Properties props = new Properties();
		this.queueName = queueName;
		
		InputStream input = null;
		input = new FileInputStream(propertiesFile);
		props.load(input);
		producer = new KafkaProducer<byte[], byte[]>(props);
		input.close();
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
