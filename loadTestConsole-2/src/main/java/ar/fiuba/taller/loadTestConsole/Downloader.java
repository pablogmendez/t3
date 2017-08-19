package ar.fiuba.taller.loadTestConsole;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.log4j.Logger;

import ar.fiuba.taller.utils.HttpRequester;

public class Downloader implements Runnable {

	private ArrayBlockingQueue<DownloaderTask> downloaderTaskPendigQueue;
	private ArrayBlockingQueue<DownloaderTask> downloaderTaskFinishedQueue;
	private ArrayBlockingQueue<SummaryTask> summaryQueue;
	private ArrayBlockingQueue<ReportTask> reportQueue;

	final static Logger logger = Logger.getLogger(App.class);

	public Downloader(
			ArrayBlockingQueue<DownloaderTask> downloaderTaskPendigQueue,
			ArrayBlockingQueue<DownloaderTask> downloaderTaskFinishedQueue,
			ArrayBlockingQueue<SummaryTask> summaryQueue,
			ArrayBlockingQueue<ReportTask> reportQueue) {
		this.downloaderTaskPendigQueue = downloaderTaskPendigQueue;
		this.downloaderTaskFinishedQueue = downloaderTaskFinishedQueue;
		this.summaryQueue = summaryQueue;
		this.reportQueue = reportQueue;
	}

	public void run() {
		DownloaderTask task = null;
		Boolean gracefullQuit = false;
		Integer bytesDownloaded;
		long time_start, time_end, time_elapsed;
		HttpRequester httpRequester = new HttpRequester();

		logger.info("Iniciando un nuevo downloader");
		logger.info("Obteniendo una nueva task");
		try {
			while (!gracefullQuit) {
				task = downloaderTaskPendigQueue.take();
				// Informo al monitor que arranco el downloader
				reportQueue.put(new ReportTask(Constants.DEFAULT_ID,
						Constants.TASK_STATUS.SUBMITTED, false, null));
				logger.info("Task obtenida con los siguientes parametros:\n"
						+ "Id: " + task.getId() + "\nStatus: "
						+ task.getStatus() + "\nMethod: " + task.getMethod()
						+ "\nUri: " + task.getUri());
				if (task.getId() == Constants.DISCONNECT_ID) {
					logger.info(
							"Mensaje de desconexion. Se finaliza el thread.");
					logger.info("Confirmando finalizacion al user");
					downloaderTaskFinishedQueue.put(task);
					logger.info("Downloader finalizado");
					gracefullQuit = true;
				} else {
					logger.info("Nueva tarea recibida");
					logger.info("Descargando recurso...");
					task.setStatus(Constants.TASK_STATUS.EXECUTING);
					try {
						reportQueue.put(new ReportTask(Constants.DEFAULT_ID,
								Constants.TASK_STATUS.EXECUTING, false,
								task.getResourceType()));
						time_start = System.currentTimeMillis();
						bytesDownloaded = httpRequester
								.doHttpRequest(task.getMethod(), task.getUri(),
										new HashMap<String, String>(), "")
								.length();// download(task.getMethod(),
											// task.getUri());
						time_end = System.currentTimeMillis();
						time_elapsed = time_end - time_start;
						logger.info("Bytes descargados: " + bytesDownloaded);
						logger.info("Tiempo inicial: " + time_start
								+ " nanosgundos");
						logger.info(
								"Tiempo final: " + time_end + " nanosgundos");
						logger.info("Tiempo transcurrido: " + time_elapsed
								+ " nanosegundos");
						summaryQueue.put(new SummaryTask(Constants.DEFAULT_ID,
								Constants.TASK_STATUS.SUBMITTED, 0, true,
								time_elapsed));
						// Informo al monitor que arranco el usuario
						reportQueue.put(new ReportTask(Constants.DEFAULT_ID,
								Constants.TASK_STATUS.FINISHED, false, null));
						task.setStatus(Constants.TASK_STATUS.FINISHED);
					} catch (Exception e) {
						task.setStatus(Constants.TASK_STATUS.FAILED);
						reportQueue.put(new ReportTask(Constants.DEFAULT_ID,
								Constants.TASK_STATUS.FAILED, false, null));
						summaryQueue.put(new SummaryTask(Constants.DEFAULT_ID,
								Constants.TASK_STATUS.SUBMITTED, 0, false, 0));
					} finally {
						downloaderTaskFinishedQueue.put(task);
					}
				}
			}
		} catch (InterruptedException e) {
			logger.warn("No se ha podido pushear tareas en las colas");
			e.printStackTrace();
		}
	}

}
