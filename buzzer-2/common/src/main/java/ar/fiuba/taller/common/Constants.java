package ar.fiuba.taller.common;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Constants {

	// Constantes globales
	public static final int COMMAND_QUEUE_SIZE = 1000;
	public static final int RESPONSE_QUEUE_SIZE = 1000;
	public static final String LOGGER_CONF = "conf/log4j.properties";
	
	public static final String COMMAND_SCRIPT = "scripts/script.json";
	public static final String COMMAND_ARRAY = "commands";
	public static final String COMMAND_KEY = "command";
	public static final String USER_KEY = "user";
	public static final String NAME_KEY = "name";
	public static final String USERS_KEY = "users";
	public static final String MESSAGE_KEY = "message";
	public static final String USERS_FILE = "conf/users.json";
	public static final String CONF_FILE = "configuration.properties";
	public static final String LOGS_DIR = "log";
	public static final String EVENT_VIEWER_FILE = "user_";
	public static final String EVENT_VIEWER_FILE_EXTENSION = ".events";
	public static final String COMMANDS_FILE_EXTENSION = ".commands";

	// Constantes para el usuario
	public static final String INTERACTIVE_MODE = "i";
	public static final String BATCH_MODE = "b";
	public static final String MAX_LENGTH_MSG = "max.length.msg";
	public static final String COMMAND_AMOUNT = "command.amount";
	public static final String BATCH_DELAY_TIME = "batch.delay.time";
	public static final long USER_THREAD_WAIT_TIME = 5000;
	
	// Constantes para el storage
	public static final String STORAGE_QUEUE_NAME = "storage.queue.name";
	public static final String STORAGE_QUERY_RESULT_QUEUE_NAME = "storage.query.result.queue.name";
	public static final String STORAGE_QUEUE_HOST = "storage.queue.host";
	public static final String STORAGE_QUERY_RESULT_QUEUE_HOST = "storage.query.result.queue.host";
	public static final long   STORAGE_THREAD_WAIT_TIME = 5000;
	public static final String SHARDING_FACTOR = "sharding.factor";
	public static final String QUERY_COUNT_SHOW_POSTS = "query.count.show.posts";
	public static final String TT_COUNT_SHOW = "tt.count.show";
	public static final String COMMAND_SCRIPT_EXTENSION = ".json";
	
	// Constantes para el audit logger
	public static final String AUDIT_LOGGER_QUEUE_HOST = "audit.logger.queue.host";
	public static final String AUDIT_LOGGER_QUEUE_NAME = "audit.logger.queue.name";
	public static final long   AUDIT_LOGGER_THREAD_WAIT_TIME = 5000;
	public static final String AUDIT_LOG_FILE = "audit.log.file";

	// Constantes para el dispatcher
	public static final String DISPATCHER_QUEUE_NAME = "dispatcher.queue.name";
	public static final String DISPATCHER_QUEUE_HOST = "dispatcher.queue.host";
	public static final long   DISPATCHER_THREAD_WAIT_TIME = 5000;
	
	// Constantes para el analyzer
	public static final String ANALYZER_QUEUE_HOST = "analyzer.queue.host";
	public static final String ANALYZER_QUEUE_NAME = "analyzer.queue.name";
	public static final long   ANALYZER_THREAD_WAIT_TIME = 5000;
	
	public static final String DB_DIR = "db";
	public static final String DB_INDEX_DIR = "idx";
	public static final String DB_USER_INDEX = "user.json";
	public static final String DB_HASHTAG_INDEX = "hashtag.json";
	public static final String DB_TT = "tt.json";
	public static final SimpleDateFormat SDF = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public static final String USER_READ_MODE = "r";
	public static final String USER_WRITE_MODE = "w";

	public static final String ACKS_CONFIG = "acks.config";
	public static final String RETRIES_CONFIG = "retries.config";
	public static final String KEY_SERIALIZER_CLASS_CONFIG = "key.serializer.class.config";
	public static final String VALUE_SERIALIZER_CLASS_CONFIG = "value.serializer.class.config";
	public static final String KEY_DESERIALIZER_CLASS_CONFIG = "key.deserializer.class.config";
	public static final String VALUE_DESERIALIZER_CLASS_CONFIG = "value.deserializer.class.config";
	public static final String GROUP_ID_CONFIG = "group.id.config";
	public static final String AUTO_OFFSET_RESET_CONFIG = "auto.offset.reset.config";
	
	public static enum COMMAND {
		PUBLISH, QUERY, DELETE, FOLLOW
	};

	public static Map<String, COMMAND> COMMAND_MAP;
	static {
		Map<String, COMMAND> tmpMap = new HashMap<String, Constants.COMMAND>();
		tmpMap.put("PUBLISH", COMMAND.PUBLISH);
		tmpMap.put("QUERY", COMMAND.QUERY);
		tmpMap.put("DELETE", COMMAND.DELETE);
		tmpMap.put("FOLLOW", COMMAND.FOLLOW);
		COMMAND_MAP = Collections.unmodifiableMap(tmpMap);
	}

	public static enum RESPONSE_STATUS {
		OK, ERROR, REGISTERED
	}

	public static Map<String, RESPONSE_STATUS> RESPONSE_STATUS_MAP;
	static {
		Map<String, RESPONSE_STATUS> tmpMap1 = new HashMap<String, RESPONSE_STATUS>();
		tmpMap1 = new HashMap<String, Constants.RESPONSE_STATUS>();
		tmpMap1.put("OK", RESPONSE_STATUS.OK);
		tmpMap1.put("ERROR", RESPONSE_STATUS.ERROR);
		tmpMap1.put("REGISTERED", RESPONSE_STATUS.REGISTERED);
		RESPONSE_STATUS_MAP = Collections.unmodifiableMap(tmpMap1);
	}
	
	public static final int EXIT_SUCCESS = 0;
	public static final int EXIT_FAILURE = 1;
}
