package ar.fiuba.taller.ClientConsole;

import java.util.HashMap;
import java.util.Map;

public class Constants {

	public static final int    COMMAND_QUEUE_SIZE 	= 100;
	public static final String COMMAND_SCRIPT 		= "scripts/script.json";
	public static final String COMMAND_ARRAY 		= "command";
	public static final String COMMAND_KEY 			= "command";
	public static final String USER_KEY 			= "user";
	public static final String MESSAGE_KEY 			= "message";
	
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
