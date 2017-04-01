package ar.fiuba.taller.common;

import java.util.HashMap;
import java.util.Map;

public class Constants {

	public static final int    COMMAND_QUEUE_SIZE 		= 100;
	public static final String COMMAND_SCRIPT_FOLDER	= "scripts";
	public static final String COMMAND_SCRIPT_EXTENSION	= ".json";
	public static final String COMMAND_ARRAY 			= "commands";
	public static final String COMMAND_KEY 				= "command";
	public static final String USER_KEY 				= "user";
	public static final String USERS_KEY 				= "users";
	public static final String MESSAGE_KEY 				= "message";
	public static final String USERS_FILE 				= "conf/users.json";
	
	public static enum COMMAND {
		PUBLISH, QUERY, DELETE, FOLLOW
	};
	
	public static Map<String, COMMAND> COMMAND_MAP;
    {
        COMMAND_MAP = new HashMap<String, Constants.COMMAND>();
        COMMAND_MAP.put("PUBLISH", COMMAND.PUBLISH);
        COMMAND_MAP.put("QUERY",   COMMAND.QUERY  );
        COMMAND_MAP.put("DELETE",  COMMAND.DELETE );
        COMMAND_MAP.put("FOLLOW",  COMMAND.FOLLOW );
    }
}
