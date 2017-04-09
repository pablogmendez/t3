package ar.fiuba.taller.common;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.*;

public class RemoteQueue {
	private String queueName;
	private String host;
	private ConnectionFactory factory;
	private Connection connection;
	private Channel channel;	
	
	public Channel getChannel() {
		return channel;
	}

	public RemoteQueue(String queueName, String host) {
		this.queueName = queueName;
		this.host = host;
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void init() throws IOException, TimeoutException {
		factory = new ConnectionFactory();
	    factory.setHost(host);
	    connection = factory.newConnection();
	    channel = connection.createChannel();
	    channel.queueDeclareNoWait(queueName, false, false, false, null);
	}
	
	public void close() throws IOException, TimeoutException {
	    channel.close();
	    connection.close();
	}
	
	public void put(ISerialize message) throws IOException {
	    channel.basicPublish("", queueName, null, message.serialize());
	}
	
}
