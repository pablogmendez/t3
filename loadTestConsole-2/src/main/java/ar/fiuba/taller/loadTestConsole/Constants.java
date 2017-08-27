package ar.fiuba.taller.loadTestConsole;

import java.util.HashMap;
import java.util.Map;

public final class Constants {
	public static final String PROPERTIES_FILE 		= "configuration.properties";
	public static final String MAX_USERS	 		= "max.users";
	public static final String MAX_DOWNLOADERS 		= "max.downloaders";
	public static final String SCRIPT_FILE 			= "script.file";
	public static final String REPORT_FILE			= "report.file";
	public static final String SUMMARY_TIMEOUT 		= "summary.timeout";
	public static final String HTTP_TIMEOUT			= "http.timeout";
	public static final String FILE_WATCHER_TIMEOUT	= "file.watcher.timeout";
	public static final String USERS_PATTERN_FILE	= "users.pattern.file";
	public static final String SUMMARY_QUEUE_SIZE	= "summary.queue.size";
	public static final String REPORT_QUEUE_SIZE	= "report.queue.size";

	public static final int EXIT_SUCCESS			= 0;
	public static final int EXIT_FAILURE			= 1;
	public static final int SLEEP_UNIT				= 1000;
	
    public static final Map<String, String> RESOURCE_MAP;
    static
    {
    	RESOURCE_MAP = new HashMap<String, String>();
    	RESOURCE_MAP.put("LINK", "href");
    	RESOURCE_MAP.put("SCRIPT", "src");
    	RESOURCE_MAP.put("IMG", "src");
    }
	
	public static enum REPORT_EVENT {
		URL_ANALYZED, SCRIPT_DOWNLOADED, LINK_DOWNLOADED, IMG_DOWNLOADED,
		SCRIPT_EXECUTING, SCRIPT_EXECUTED, RESOURCE_DOWNLOAD, RESOURCE_DOWNLOADED
	};
	
    public static final Map<String, REPORT_EVENT> TYPE_RESOURCE_MAP;
    static
    {
    	TYPE_RESOURCE_MAP = new HashMap<String, REPORT_EVENT>();
    	TYPE_RESOURCE_MAP.put("LINK", REPORT_EVENT.LINK_DOWNLOADED);
    	TYPE_RESOURCE_MAP.put("SCRIPT", REPORT_EVENT.SCRIPT_DOWNLOADED);
    	TYPE_RESOURCE_MAP.put("IMG", REPORT_EVENT.IMG_DOWNLOADED);
    }
}
