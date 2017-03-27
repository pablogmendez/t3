package ar.fiuba.taller.loadTestConsole;

public final class Constants {
	public static final String PROPERTIES_FILE = "src/main/resources/configuration.properties";
	public static final String SCRIPT_FILE = "src/main/resources/script.xml";
	public static final String REPORT_FILE = "log/Report.txt";
	public static final String SIMULATION_TIME_PROPERTY = "simulationTime";
	public static final String USER_LOGIN_TIME_INTERVAL_PROPERTY = "userLoginTimeInterval";
	public static final String FUNCTION_PROPERTY = "function";
	public static final String NUMBER_OF_USERS_PROPERTY = "numberOfUsers";
	public static final String STEP_LENGTH_PROPERTY = "stepLength";
	public static final String USERES_QUEUE_SIZE = "usersQueueSize";
	public static final String MAX_SIZE_USER_POOL_THREAD = "maxSizeUserPoolThread";
	public static final String MAX_SIZE_DOWNLOADERS_POOL_THREAD = "maxSizeDownloadersPoolThread";
	public static final String TASKS_QUEUE_SIZE = "tasksQueueSize";
	public static final String GET_METHOD = "get";
	public static final String PUT_METHOD = "put";
	public static final String POST_METHOD = "post";
	public static final String SCRIPT_TAG = "script";
	public static final String LINK_TAG = "link";
	public static final String IMG_TAG = "img";
	public static final Integer DISCONNECT_ID = -1;
	public static final Integer DEFAULT_ID = 0;

	public static enum TASK_STATUS {
		SUBMITTED, EXECUTING, FINISHED, FAILED
	};
}
