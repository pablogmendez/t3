package ar.fiuba.taller.loadTestConsole;

public final class Constants {
	public static final String PROPERTIES_FILE 		= "configuration.properties";
	public static final String MAX_USERS	 		= "max.users";
	public static final String MAX_DOWNLOADERS 		= "max.downloaders";
	public static final String SCRIPT_FILE 			= "script.file";
	public static final String REPORT_FILE			= "report.file";
	public static final String SUMMARY_TIMEOUT 		= "summary.timeout";
	public static final String USERS_PATTERN_FILE	= "users.pattern.file";
	public static final String FILE_WATCHER_TIMEOUT	= "file.watcher.timeout";
	public static final String SUMMARY_QUEUE_SIZE	= "summary.queue.size";
	public static final String REPORT_QUEUE_SIZE	= "report.queue.size";

	public static final int EXIT_SUCCESS			= 0;
	public static final int EXIT_FAILURE			= 1;
	
	
	public static enum REPORT_EVENT {
		URL_ANALYZED, SCRIPT_DOWNLOAD, LINK_DOWNLOADED, IMG_DOWNLOADED,
		SCRIPT_EXECUTING, SOURCE_DOWNLOAD
	};
}
