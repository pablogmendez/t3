package ar.fiuba.taller.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;

public class ReadingRemoteQueue extends RemoteQueue {
	private KafkaConsumer<byte[], byte[]> consumer;

	public class ReadingRemoteQueueException extends WakeupException {
	}
	
	public ReadingRemoteQueue(String queueName,
			String propertiesFile) throws IOException {
		Properties consumerConfig = new Properties();
		InputStream input = null;
		input = new FileInputStream(propertiesFile);
		consumerConfig.load(input);
		consumer = new KafkaConsumer<byte[], byte[]>(consumerConfig);
		consumer.subscribe(Collections.singletonList(queueName));
		input.close();
	}

	@Override
	public void close() throws IOException, TimeoutException {
		consumer.close();
	}

	public void shutDown() {
		consumer.wakeup();
	}

	public List<byte[]> pop() throws ReadingRemoteQueueException {
		List<byte[]> msgList = null;

		try {
			while (msgList == null) {
				ConsumerRecords<byte[], byte[]> records = consumer
						.poll(Long.MAX_VALUE);
				if (!records.isEmpty()) {
					msgList = new ArrayList<byte[]>();
					for (ConsumerRecord<byte[], byte[]> record : records) {
						msgList.add(record.value());
					}
					consumer.commitSync();
				}
			}
		} catch (WakeupException e) {
			throw new ReadingRemoteQueueException();
		}
		return msgList;
	}

}
