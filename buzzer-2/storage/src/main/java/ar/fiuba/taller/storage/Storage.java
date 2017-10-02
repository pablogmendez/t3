package ar.fiuba.taller.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ar.fiuba.taller.common.Command;
import ar.fiuba.taller.common.Constants;

public class Storage {

	private int shardingFactor;
	private int queryCountShowPosts;
	private int ttCountShowPosts;
	final static Logger logger = Logger.getLogger(Storage.class);

	public Storage(int shardingFactor, int queryCountShowPosts,
			int ttCountShowPosts) {
		this.shardingFactor = shardingFactor;
		this.queryCountShowPosts = queryCountShowPosts;
		this.ttCountShowPosts = ttCountShowPosts;
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
	}

	private void updateTT(Command command) throws IOException, ParseException {
		String fileName = Constants.DB_INDEX_DIR + "/" + Constants.DB_TT;
		JSONParser parser = new JSONParser();
		Object obj;

		logger.info("Actualizando los TT");
		File tmpFile = new File(fileName);
		if (tmpFile.createNewFile()) {
			FileOutputStream oFile = new FileOutputStream(tmpFile, false);
			oFile.write("{}".getBytes());
		}
		Path path = Paths.get(fileName);
		FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ);
		FileLock lock = fileChannel.lock(0, Long.MAX_VALUE, true);
		try {
			ByteBuffer buffer = ByteBuffer.allocate(((int) fileChannel.size()));
			fileChannel.read(buffer);
			buffer.position(0);
			StringBuilder sb = new StringBuilder();
	        while (buffer.hasRemaining()) {	
	            sb.append((char) buffer.get());                
	        }
	        String tmp = sb.toString();
	        if((tmp.split("}", -1).length - 1) > 1) {
	        	tmp = tmp.substring(0, tmp.indexOf("}")+1);
	        }
			obj = parser.parse(new StringReader(tmp));
			JSONObject jsonObject = (JSONObject) obj;
			int count = 0;
			String regexPattern = "(#\\w+)";
			Pattern p = Pattern.compile(regexPattern);
			Matcher m = p.matcher(command.getMessage());
			String hashtag;
			while (m.find()) {
				hashtag = m.group(1);
				hashtag = hashtag.substring(1, hashtag.length());
				Long obj2 = (Long) jsonObject.get(hashtag);
				if (obj2 == null) {
					// La entrada no existe y hay que crearla
					jsonObject.put(hashtag, 1);
				} else {
					obj2++;
					jsonObject.put(hashtag, obj2);
				}
			}
			lock.release();
			fileChannel.close();
			logger.debug("sssssss" + jsonObject.toJSONString());
			fileChannel = FileChannel.open(path, StandardOpenOption.WRITE,
		            StandardOpenOption.TRUNCATE_EXISTING);
			lock = fileChannel.lock(); // gets an exclusive lock
				buffer = ByteBuffer.wrap(jsonObject.toJSONString().getBytes());
				fileChannel.write(buffer);
		} catch (Exception e) {
			logger.error("Error guardar el indice de TT: " + e);
		} finally {
			lock.release();
			fileChannel.close();
		}
	}

	public void saveMessage(Command command)
			throws IOException, ParseException {
		String fileName = Constants.DB_DIR + "/"
				+ command.getUuid().toString().substring(0, shardingFactor)
				+ Constants.COMMAND_SCRIPT_EXTENSION;
		JSONParser parser = new JSONParser();
		Object obj;

		logger.info("Guardando el comando en la base de datos: " + fileName);
		logger.info("Contenido del registro: " + command.toJson());
		File tmpFile = new File(fileName);
		if (tmpFile.createNewFile()) {
			FileOutputStream oFile = new FileOutputStream(tmpFile, false);
		}
		JSONObject obj2 = new JSONObject();
		obj2.put("command", command.getCommand().toString());
		obj2.put("user", command.getUser());
		obj2.put("message", command.getMessage());
		obj2.put("timestamp", command.getTimestamp());
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(command.getUuid().toString(), obj2);

        Path path = Paths.get(fileName);
        FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.WRITE,
	            StandardOpenOption.APPEND);
        FileLock lock = fileChannel.lock(); // gets an exclusive lock
		try {
			ByteBuffer buffer = ByteBuffer.wrap((jsonObject.toJSONString() + String.format("%n")).getBytes());
			fileChannel.write(buffer);
		} catch (Exception e) {
			logger.error("Error guardar la base de datos: " + e);
		} finally {
			lock.release();
			fileChannel.close();
		}
		// Una vez que persisto el mensaje, actualizo los indices y el TT
		updateUserIndex(command);
		updateHashTagIndex(command);
		updateTT(command);
	}

	private void updateUserIndex(Command command)
			throws IOException, ParseException {
		String fileName = Constants.DB_INDEX_DIR + "/"
				+ Constants.DB_USER_INDEX;
		JSONParser parser = new JSONParser();
		Object obj;

		logger.info("Actualizando el inice de usuarios");
		File tmpFile = new File(fileName);
		if (tmpFile.createNewFile()) {
			FileOutputStream oFile = new FileOutputStream(tmpFile, false);
			oFile.write("{}".getBytes());
		}

        Path path = Paths.get(fileName);
        FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ);
        FileLock lock = fileChannel.lock(0, Long.MAX_VALUE, true);
        try {
		ByteBuffer buffer = ByteBuffer.allocate(((int) fileChannel.size()));
		fileChannel.read(buffer);
		buffer.position(0);
		StringBuilder sb = new StringBuilder();
        while (buffer.hasRemaining()) {	
            sb.append((char) buffer.get());                
        }
        String tmp = sb.toString();
        if((tmp.split("}", -1).length - 1) > 1) {
        	tmp = tmp.substring(0, tmp.indexOf("}")+1);
        }		
		obj = parser.parse(new StringReader(tmp));
		JSONObject jsonObject = (JSONObject) obj;
		JSONArray array = (JSONArray) jsonObject.get(command.getUser());
		if (array == null) {
			// Hay que crear la entrada en el indice
			JSONArray ar2 = new JSONArray();
			ar2.add(command.getUuid().toString());
			jsonObject.put(command.getUser(), ar2);
		} else {
			array.add(command.getUuid().toString());
			jsonObject.put(command.getUser(), array);
		}
		lock.release();
		fileChannel.close();
		fileChannel = FileChannel.open(path, StandardOpenOption.WRITE,
	            StandardOpenOption.TRUNCATE_EXISTING);
		lock = fileChannel.lock(); // gets an exclusive lock
			buffer = ByteBuffer.wrap(jsonObject.toJSONString().getBytes());
			fileChannel.write(buffer);
		} catch (Exception e) {
			logger.error("Error guardar el indice de u: " + e);
		} finally {
			lock.release();
			fileChannel.close();
		}
	}

	private void updateHashTagIndex(Command command)
			throws IOException, ParseException {
		String fileName = Constants.DB_INDEX_DIR + "/"
				+ Constants.DB_HASHTAG_INDEX;
		JSONParser parser = new JSONParser();
		Object obj;

		logger.info("Actualizando el inice de hashtags");
		File tmpFile = new File(fileName);
		if (tmpFile.createNewFile()) {
			FileOutputStream oFile = new FileOutputStream(tmpFile, false);
			oFile.write("{}".getBytes());
		}
		
        Path path = Paths.get(fileName);
        FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ);
        FileLock lock = fileChannel.lock(0, Long.MAX_VALUE, true);
        try {
		ByteBuffer buffer = ByteBuffer.allocate(((int) fileChannel.size()));
		fileChannel.read(buffer);
		buffer.position(0);
		StringBuilder sb = new StringBuilder();
        while (buffer.hasRemaining()) {	
            sb.append((char) buffer.get());                
        }
        String tmp = sb.toString();
        if((tmp.split("}", -1).length - 1) > 1) {
        	tmp = tmp.substring(0, tmp.indexOf("}")+1);
        }
		obj = parser.parse(new StringReader(tmp));
		JSONObject jsonObject = (JSONObject) obj;
		JSONArray array;
		String regexPattern = "(#\\w+)";
		Pattern p = Pattern.compile(regexPattern);
		Matcher m = p.matcher(command.getMessage());
		String hashtag;
		JSONArray ar2;
		while (m.find()) {
			hashtag = m.group(1);
			hashtag = hashtag.substring(1, hashtag.length());
			array = (JSONArray) jsonObject.get(hashtag);
			if (array == null) {
				// Hay que crear la entrada en el indice
				ar2 = new JSONArray();
				ar2.add(command.getUuid().toString());
				jsonObject.put(hashtag, ar2);
			} else {
				array.add(command.getUuid().toString());
				jsonObject.put(hashtag, array);
			}
		}
		lock.release();
		fileChannel.close();
		fileChannel = FileChannel.open(path, StandardOpenOption.WRITE,
	            StandardOpenOption.TRUNCATE_EXISTING);
		lock = fileChannel.lock(); // gets an exclusive lock
			buffer = ByteBuffer.wrap(jsonObject.toJSONString().getBytes());
			fileChannel.write(buffer);
		} catch (Exception e) {
			logger.error("Error guardar el indice de hashtags: " + e);
		} finally {
			lock.release();
			fileChannel.close();
		}
	}

	public String query(Command command) throws IOException, ParseException {
		List<String> resultList = new ArrayList<String>();
		String listString = "";
		if (String.valueOf(command.getMessage().charAt(0)).equals("#")) { // #
			resultList = queryBy(command.getMessage().substring(1,
					command.getMessage().length()), "HASHTAG");
		} else if (command.getMessage().equals("TT")) { // Es consulta por TT
			resultList = queryTT(command.getMessage());
		} else { // Es consulta por usuario
			resultList = queryBy(command.getMessage(), "USER");
		}
		if(!resultList.isEmpty()) {
			for (String element : resultList) {
				listString += element + "\n";
			}
		}
		return listString;
	}

	private List<String> queryTT(String hashTag)
			throws FileNotFoundException, IOException, ParseException {
		Map<String, Long> map = new HashMap<String, Long>();
		String fileName = Constants.DB_INDEX_DIR + "/" + Constants.DB_TT;
		List<String> returnList = new ArrayList<String>();

		// Levantar el json
		JSONParser parser = new JSONParser();

        Path path = Paths.get(fileName);
        FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ);
        FileLock lock = fileChannel.lock(0, Long.MAX_VALUE, true);
        try {
        ByteBuffer buffer = ByteBuffer.allocate(((int) fileChannel.size()));
		fileChannel.read(buffer);
		buffer.position(0);
		StringBuilder sb = new StringBuilder();
        while (buffer.hasRemaining()) {	
            sb.append((char) buffer.get());                
        }
		
		Object obj = parser.parse(new StringReader(sb.toString()));

		JSONObject jsonObject = (JSONObject) obj;

		// Crear un map
		for (Iterator iterator = jsonObject.keySet().iterator(); iterator
				.hasNext();) {
			String key = (String) iterator.next();
			map.put(key, (Long) jsonObject.get(key));
		}

		returnList = sortHashMapByValues(map);
		returnList
				.add("Total de topics: " + String.valueOf(map.keySet().size()));
        } catch(Exception e) {
        	// Do nothing
        } finally {
		lock.release();
		fileChannel.close();
        }
		return returnList;
	}

	private List<String> queryBy(String key, String type)
			throws IOException, ParseException {
		String fileName;
		JSONParser parser = new JSONParser();
		Object obj, obj2;
		List<String> messageList = new ArrayList<String>();
		String file, id;

		if (type.equals("USER")) {
			logger.info("Consultando por user");
			fileName = Constants.DB_INDEX_DIR + "/" + Constants.DB_USER_INDEX;
		} else if (type.equals("HASHTAG")) {
			logger.info("Consultando por hashtag");
			fileName = Constants.DB_INDEX_DIR + "/"
					+ Constants.DB_HASHTAG_INDEX;
		} else {
			return messageList;
		}

		// Obtengo la lista de archivos que contienen el user

		File tmpFile = new File(fileName);
		if (tmpFile.createNewFile()) {
			FileOutputStream oFile = new FileOutputStream(tmpFile, false);
			oFile.write("{}".getBytes());
		}
		
        Path path = Paths.get(fileName);
        FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ);
        FileLock lock = fileChannel.lock(0, Long.MAX_VALUE, true);
        try {
		ByteBuffer buffer = ByteBuffer.allocate(((int) fileChannel.size()));
		fileChannel.read(buffer);
		buffer.position(0);
		StringBuilder sb = new StringBuilder();
        while (buffer.hasRemaining()) {	
            sb.append((char) buffer.get());                
        }
		
		obj = parser.parse(new StringReader(sb.toString()));
		JSONObject jsonObject = (JSONObject) obj;
		JSONArray array = (JSONArray) jsonObject.get(key);

		String line, reg;
		JSONObject jsonObject2;
		int remainingPost = queryCountShowPosts;
		// Abro archivo por archivo y recupero los mensajes
		if (array != null) {
			ListIterator<String> iterator = array.listIterator(array.size());
			while (iterator.hasPrevious() && remainingPost > 0) {
				id = iterator.previous();
				file = Constants.DB_DIR + "/" + id.substring(0, shardingFactor)
						+ Constants.COMMAND_SCRIPT_EXTENSION;
				Path path2 = Paths.get(file);
				FileChannel fileChannel2 = FileChannel.open(path2, StandardOpenOption.READ);
				FileLock lock2 = fileChannel2.lock(0, Long.MAX_VALUE, true);
				ByteBuffer buffer2 = ByteBuffer.allocate(((int) fileChannel2.size()));
				fileChannel2.read(buffer2);
				buffer2.position(0);
				StringBuilder sb2 = new StringBuilder();
				while (buffer2.hasRemaining()) {	
					sb2.append((char) buffer2.get());                
				}
				try (
					BufferedReader br = new BufferedReader(
					new StringReader(sb2.toString()))
				) {
					while ((line = br.readLine()) != null && remainingPost > 0
							&& !("").equals(line.trim())) {
						System.out.println("line: " + line);
						obj2 = parser.parse(line);
						jsonObject2 = (JSONObject) obj2;
						if (jsonObject2.get(id) != null) {
							messageList.add(jsonObject2.get(id).toString());
						}
						remainingPost--;
					}
				}
				lock2.release();
				fileChannel2.close();
			}
		}
        }catch(Exception e) {
        	// Do nothing
        } finally {
		lock.release();
		fileChannel.close();
        }
		// Retorno la lista con los mensajes encontrados
		return messageList;
	}

	public void delete(Command command)
			throws IOException, ParseException {
		String file = Constants.DB_DIR + "/"
				+ command.getMessage().substring(0, shardingFactor)
				+ Constants.COMMAND_SCRIPT_EXTENSION;
		String fileTmp = file + ".tmp";
		JSONParser parser = new JSONParser();
		Object obj2;
		String line, key;
		JSONObject jsonObject2;

		// Creo un archivo temporal
		PrintWriter pw = new PrintWriter(
				new BufferedWriter(new FileWriter(fileTmp)));

		logger.info("Eleiminando registro");

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			while ((line = br.readLine()) != null) {
				obj2 = parser.parse(line);
				jsonObject2 = (JSONObject) obj2;
				key = (String) jsonObject2.keySet().iterator().next();
				if (!(key.equals(command.getMessage()))) {
					// Si no es la clave a borrar, guardo el registro en un
					// archivo temporal
					pw.println(jsonObject2);
				}
			}
		}
		pw.close();
		// Borro el archvio original y renombro el tmp
		File fileToDelete = new File(file);
		File newFile = new File(fileTmp);
		if (fileToDelete.delete()) {
			logger.info("Archivo original borrado");
			logger.info("Renombrado el archivo temporal al original");
			if (newFile.renameTo(fileToDelete)) {
				logger.info("Archivo renombrado con exito");
			} else {
				logger.error("No se ha podido renombrar el archivo");
				throw new IOException();
			}
		} else {
			logger.error(
					"No se ha podido borrar el registro. Se aborta la operacion");
			throw new IOException();
		}
	}

	private List<String> sortHashMapByValues(Map<String, Long> map) {
		List<String> mapKeys = new ArrayList<String>(map.keySet());
		List<Long> mapValues = new ArrayList<Long>(map.values());
		Collections.sort(mapValues);
		Collections.sort(mapKeys);

		LinkedHashMap<String, Long> sortedMap = new LinkedHashMap<String, Long>();

		java.util.Iterator<Long> valueIt = mapValues.iterator();
		while (valueIt.hasNext()) {
			Long val = valueIt.next();
			java.util.Iterator<String> keyIt = mapKeys.iterator();

			while (keyIt.hasNext()) {
				String key = keyIt.next();
				Long comp1 = map.get(key);
				Long comp2 = val;

				if (comp1.equals(comp2)) {
					keyIt.remove();
					sortedMap.put(key, val);
					break;
				}
			}
		}
		List<String> tt = new ArrayList<String>();
		ArrayList<String> keys = new ArrayList<String>(sortedMap.keySet());
		int i = keys.size() - 1;
		int j = ttCountShowPosts;
		while (i >= 0 && j > 0) {
			tt.add(keys.get(i));
			j--;
			i--;
		}
		return tt;
	}

}
