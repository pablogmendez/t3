package ar.fiuba.taller.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {
	
	private static ConfigLoader instance = null;
	private String responseQueueHost;
	private String responseQueueName;
	private String dispatcherQueueHost;
	private String dispatcherQueueName;
	private String auditLoggerQueueHost;
	private String auditLoggerQueueName;
	private String storageResquestQueueHost;
	private String storageRequestQueueName;
	private String storageResponseQueueHost;
	private String storageResponseQueueName;
	private String analyzerQueueHost;
	private String analyzerQueueName;
	private int queryCountShowPosts;
	private int ttCountShowPosts;
	private int shardingFactor;
			
	public int getShardingFactor() {
		return shardingFactor;
	}

	public void setShardingFactor(int shardingFactor) {
		this.shardingFactor = shardingFactor;
	}

	protected ConfigLoader() {
		// TODO Auto-generated constructor stub
	}

	public static ConfigLoader getInstance() {
		if (instance == null) {
			instance = new ConfigLoader();
		}
		return instance;
	}

	public void init(String configFile) throws IOException {
			Properties properties = new Properties();
			FileInputStream input = new FileInputStream(configFile);

			// cargamos el archivo de propiedades
			properties.load(input);

			// obtenemos las propiedades
			responseQueueHost 			= properties.getProperty(Constants.RESPONSE_QUEUE_HOST);
			responseQueueName			= properties.getProperty(Constants.RESPONSE_QUEUE_NAME);
			dispatcherQueueHost			= properties.getProperty(Constants.DISPATCHER_QUEUE_HOST);
			dispatcherQueueName			= properties.getProperty(Constants.DISPATCHER_QUEUE_NAME);
			auditLoggerQueueHost		= properties.getProperty(Constants.AUDIT_LOGGER_QUEUE_HOST);
			auditLoggerQueueName		= properties.getProperty(Constants.AUDIT_LOGGER_QUEUE_NAME);
			storageResquestQueueHost	= properties.getProperty(Constants.STORAGE_REQUEST_QUEUE_HOST);
			storageRequestQueueName		= properties.getProperty(Constants.STORAGE_REQUEST_QUEUE_NAME);
			storageResponseQueueHost	= properties.getProperty(Constants.STORAGE_RESPONSE_QUEUE_HOST);
			storageResponseQueueName	= properties.getProperty(Constants.STORAGE_RESPONSE_QUEUE_NAME);
			analyzerQueueHost			= properties.getProperty(Constants.ANALYZER_QUEUE_HOST);
			analyzerQueueName			= properties.getProperty(Constants.ANALYZER_QUEUE_NAME);
			shardingFactor				= Integer.parseInt(properties.getProperty(Constants.SHARDING_FACTOR));
			queryCountShowPosts			= Integer.parseInt(properties.getProperty(Constants.QUERY_COUNT_SHOW_POSTS));
			ttCountShowPosts			= Integer.parseInt(properties.getProperty(Constants.TT_COUNT_SHOW_POST));
	}

	public String getResponseQueueHost() {
		return responseQueueHost;
	}

	public String getResponseQueueName() {
		return responseQueueName;
	}

	public String getDispatcherQueueHost() {
		return dispatcherQueueHost;
	}

	public String getDispatcherQueueName() {
		return dispatcherQueueName;
	}

	public String getAuditLoggerQueueHost() {
		return auditLoggerQueueHost;
	}

	public String getAuditLoggerQueueName() {
		return auditLoggerQueueName;
	}

	public String getStorageResquestQueueHost() {
		return storageResquestQueueHost;
	}

	public String getStorageRequestQueueName() {
		return storageRequestQueueName;
	}

	public String getStorageResponseQueueHost() {
		return storageResponseQueueHost;
	}

	public String getStorageResponseQueueName() {
		return storageResponseQueueName;
	}

	public String getAnalyzerQueueHost() {
		return analyzerQueueHost;
	}

	public String getAnalyzerQueueName() {
		return analyzerQueueName;
	}

	public int getQueryCountShowPosts() {
		return queryCountShowPosts;
	}

	public void setQueryCountShowPosts(int queryCountShowPosts) {
		this.queryCountShowPosts = queryCountShowPosts;
	}

	public int getTtCountShowPosts() {
		return ttCountShowPosts;
	}

	public void setTtCountShowPosts(int ttCountShowPosts) {
		this.ttCountShowPosts = ttCountShowPosts;
	}
}