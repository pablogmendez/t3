package ar.fiuba.taller.common;

import java.io.IOException;
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

	public ReadingRemoteQueue(String queueName, String queueHost,
			Map<String, String> params) {
		Properties consumerConfig = new Properties();
		consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, queueHost);
		consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG,
				params.get(Constants.GROUP_ID_CONFIG));
		consumerConfig.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
				params.get(Constants.AUTO_OFFSET_RESET_CONFIG));
		consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
				params.get(Constants.KEY_DESERIALIZER_CLASS_CONFIG));
		consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
				params.get(Constants.VALUE_DESERIALIZER_CLASS_CONFIG));
		consumer = new KafkaConsumer<byte[], byte[]>(consumerConfig);
		consumer.subscribe(Collections.singletonList(queueName));
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
