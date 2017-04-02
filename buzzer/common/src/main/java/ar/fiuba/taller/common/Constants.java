package ar.fiuba.taller.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Constants {

	public static final int    COMMAND_QUEUE_SIZE 				= 100;
	public static final int    RESPONSE_QUEUE_SIZE 				= 100;
	public static final String COMMAND_SCRIPT_FOLDER			= "scripts";
	public static final String COMMAND_SCRIPT_EXTENSION			= ".json";
	public static final String COMMAND_ARRAY 					= "commands";
	public static final String COMMAND_KEY 						= "command";
	public static final String USER_KEY 						= "user";
	public static final String NAME_KEY 						= "name";
	public static final String USERS_KEY 						= "users";
	public static final String MESSAGE_KEY 						= "message";
	public static final String USERS_FILE 						= "conf/users.json";
	public static final String CONF_FILE 						= "conf/configuration.properties";
	public static final String LOGS_DIR 						= "log";
	public static final String EVENT_VIEWER_FILE				= "user_";
	public static final String EVENT_VIEWER_FILE_EXTENSION		= ".events";
	public static final String RESPONSE_QUEUE_HOST				= "responseQueueHost";
	public static final String RESPONSE_QUEUE_NAME				= "responseQueueName";
	public static final String DISPATCHER_QUEUE_HOST			= "dispatcherQueueHost";
	public static final String DISPATCHER_QUEUE_NAME			= "dispatcherQueueName";
	public static final String AUDIT_LOGGER_QUEUE_HOST			= "auditLoggerQueueHost";
	public static final String AUDIT_LOGGER_QUEUE_NAME			= "auditLoggerQueueName";
	public static final String STORAGE_REQUEST_QUEUE_HOST		= "storageResquestQueueHost";
	public static final String STORAGE_REQUEST_QUEUE_NAME		= "storageRequestQueueName";
	public static final String STORAGE_RESPONSE_QUEUE_HOST		= "storageResponseQueueHost";
	public static final String STORAGE_RESPONSE_QUEUE_NAME		= "storageResponseQueueName";
	public static final String ANALYZER_QUEUE_HOST				= "analyzerQueueHost";
	public static final String ANALYZER_QUEUE_NAME				= "analyzerQueueName";
	
	public static enum COMMAND {
		PUBLISH, QUERY, DELETE, FOLLOW
	};
	
	public static Map<String, COMMAND> COMMAND_MAP;
    static {
    	Map<String, COMMAND> tmpMap = new HashMap<String, Constants.COMMAND>();
        tmpMap.put("PUBLISH", COMMAND.PUBLISH);
        tmpMap.put("QUERY",   COMMAND.QUERY  );
        tmpMap.put("DELETE",  COMMAND.DELETE );
        tmpMap.put("FOLLOW",  COMMAND.FOLLOW );
        COMMAND_MAP = Collections.unmodifiableMap(tmpMap);
    }
    
	public static enum RESPONSE_STATUS {
		OK, ERROR
	}
	
	public static Map<String, RESPONSE_STATUS> RESPONSE_STATUS_MAP;
    static {
    	Map<String, RESPONSE_STATUS> tmpMap1 = new HashMap<String, RESPONSE_STATUS>();
    	tmpMap1 = new HashMap<String, Constants.RESPONSE_STATUS>();
    	tmpMap1.put("OK",      RESPONSE_STATUS.OK   );
    	tmpMap1.put("ERROR",   RESPONSE_STATUS.ERROR);
    	RESPONSE_STATUS_MAP = Collections.unmodifiableMap(tmpMap1);
    }
}
