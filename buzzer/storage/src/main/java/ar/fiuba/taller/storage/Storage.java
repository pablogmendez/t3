package ar.fiuba.taller.storage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
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
	
	int shardingFactor;
	int queryCountShowPosts;
	int ttCountShowPosts;
	final static Logger logger = Logger.getLogger(Storage.class);
	
	public Storage(int shardingFactor, 	int queryCountShowPosts, int ttCountShowPosts) {
		this.shardingFactor = shardingFactor;
		this.queryCountShowPosts = queryCountShowPosts;
		this.ttCountShowPosts = ttCountShowPosts;
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
	}

	public synchronized void create(Command command) throws IOException, ParseException {
		saveMessage(command);
        updateUserIndex(command);
        updateHashTagIndex(command);
        updateTT(command);
	}
	
	private void updateTT(Command command) throws IOException, ParseException {
		String fileName = Constants.DB_INDEX_DIR + "/" + Constants.DB_TT;		
		JSONParser parser = new JSONParser();
		Object obj;
		
		logger.info("Actualizando el inice de hashtags");
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        line = br.readLine();
        obj= parser.parse(line);
        JSONObject jsonObject = (JSONObject) obj;
        int count = 0;
        String regexPattern = "(#\\w+)";
		Pattern p = Pattern.compile(regexPattern);
		Matcher m = p.matcher(command.getMessage());
		String hashtag;
		while (m.find()) {
			hashtag = m.group(1);
			count = (int) jsonObject.get(hashtag);
			count ++;
			jsonObject.put(hashtag, count);
		}
        br.close();
        FileWriter file = new FileWriter(fileName, true);
        try {
            file.write(jsonObject.toJSONString());
        } catch (Exception e) {
			logger.error("Error guardar el indice de hashtags");
			logger.info(e.toString());
            e.printStackTrace();
        } finally {
            file.flush();
            file.close();
        }
	}
	
	private void saveMessage(Command command) throws IOException, ParseException {
		String fileName = Constants.DB_DIR + "/" + 
		command.getUuid().toString().substring(0, shardingFactor - 1) + 
		Constants.COMMAND_SCRIPT_EXTENSION;		
		JSONParser parser = new JSONParser();
		Object obj;
		
		logger.info("Guardando el comando en la base de datos: " + fileName);
		logger.info("Contenido del registro: " + command.toJson());
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        line = br.readLine();
        obj= parser.parse(line);
        JSONObject jsonObject = (JSONObject) obj;
        jsonObject.put(command.getUuid().toString(), command.toJson());
        FileWriter file = new FileWriter(fileName, true);
        try {
            file.write(jsonObject.toJSONString());
        } catch (Exception e) {
			logger.error("Error guardar la base de datos");
			logger.info(e.toString());
            e.printStackTrace();
        } finally {
            file.flush();
            file.close();
        }
	}
	
	private void updateUserIndex(Command command) throws IOException, ParseException {
		String fileName = Constants.DB_INDEX_DIR + "/" + Constants.DB_USER_INDEX;		
		JSONParser parser = new JSONParser();
		Object obj;
		
		logger.info("Actualizando el inice de usuarios");
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        line = br.readLine();
        obj= parser.parse(line);
        JSONObject jsonObject = (JSONObject) obj;
        JSONArray array = (JSONArray) jsonObject.get(command.getUser());
        array.add(command.getUuid().toString());
        jsonObject.put(command.getUser(), array);
        br.close();
        FileWriter file = new FileWriter(fileName, true);
        try {
            file.write(jsonObject.toJSONString());
        } catch (Exception e) {
			logger.error("Error al guardar el user index");
			logger.info(e.toString());
            e.printStackTrace();
        } finally {
            file.flush();
            file.close();
        }
	}
        
	
	private void updateHashTagIndex(Command command) throws IOException, ParseException {
		String fileName = Constants.DB_INDEX_DIR + "/" + Constants.DB_HASHTAG_INDEX;		
		JSONParser parser = new JSONParser();
		Object obj;
		
		logger.info("Actualizando el inice de hashtags");
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        line = br.readLine();
        obj= parser.parse(line);
        JSONObject jsonObject = (JSONObject) obj;
        JSONArray array;
        String regexPattern = "(#\\w+)";
		Pattern p = Pattern.compile(regexPattern);
		Matcher m = p.matcher(command.getMessage());
		String hashtag;
		while (m.find()) {
			hashtag = m.group(1);
			array = (JSONArray) jsonObject.get(hashtag);
			array.add(command.getUuid().toString());
			jsonObject.put(hashtag, array);
		}
        br.close();
        FileWriter file = new FileWriter(fileName, true);
        try {
            file.write(jsonObject.toJSONString());
        } catch (Exception e) {
			logger.error("Error guardar el indice de hashtags");
			logger.info(e.toString());
            e.printStackTrace();
        } finally {
            file.flush();
            file.close();
        }
	}
	
	public String query(Command command) throws IOException, ParseException {
		List<String> resultList;
		String listString = "";
		if(command.getMessage().substring(0).equals("#")) { // Es consulta por hashtag
			resultList = queryHashTag(command.getMessage().substring(1, command.getMessage().length() - 1));
		}
		else if (command.getMessage().equals("TT")){ // Es consulta por TT
			resultList = queryTT(command.getMessage().substring(3, command.getMessage().length() - 1));
		}
		else { // Es consulta por usuario
			resultList = queryUser(command.getMessage());
		}
		for(String element : resultList) {
			listString += "-------------------------------------\n";
			listString += element + "\n";
		}
		return listString;
	}
	
	private List<String> queryHashTag(String hashTag) throws IOException, ParseException {
		String fileName = Constants.DB_INDEX_DIR + "/" + Constants.DB_HASHTAG_INDEX;		
		JSONParser parser = new JSONParser();
		Object obj, obj2;
		List<String> messageList = new ArrayList<String>();
		String file, id;
		
		logger.info("Consultando por hashtag");
		// Obtengo la lista de archivos que contienen el hashtag
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        BufferedReader br2;
        String line;
        String line2;
        line = br.readLine();
        obj= parser.parse(line);
        JSONObject jsonObject = (JSONObject) obj;
        JSONObject jsonObject2;
        JSONArray array = (JSONArray) jsonObject.get(hashTag);
        // Abro archivo por archivo y recupero los mensajes
        Iterator<String> iterator = array.iterator();
        while (iterator.hasNext()) {
            id = iterator.next();
            file = Constants.DB_DIR + "/" + id.substring(0, shardingFactor - 1) + 
            		Constants.COMMAND_SCRIPT_EXTENSION;
            br2 = new BufferedReader(new FileReader(file));
            line2 = br2.readLine();
            obj2= parser.parse(line2);
            jsonObject2 = (JSONObject) obj2;
            messageList.add((String)jsonObject2.get(id));
        }
        // Retorno la lista con los mensajes encontrados
		return messageList;
	}

	private String queryTT(String hashTag) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		// Levantar el json
		// Crear un map
		return sortHashMapByValues(map);
	}
	
	private List<String> queryUser(String user) throws IOException, ParseException {
		String fileName = Constants.DB_INDEX_DIR + "/" + Constants.DB_USER_INDEX;		
		JSONParser parser = new JSONParser();
		Object obj, obj2;
		List<String> messageList = new ArrayList<String>();
		String file, id;
		
		logger.info("Consultando por user");
		// Obtengo la lista de archivos que contienen el user
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        BufferedReader br2;
        String line;
        String line2;
        line = br.readLine();
        obj = parser.parse(line);
        JSONObject jsonObject = (JSONObject) obj;
        JSONObject jsonObject2;
        JSONArray array = (JSONArray) jsonObject.get(user);
        // Abro archivo por archivo y recupero los mensajes
        Iterator<String> iterator = array.iterator();
        while (iterator.hasNext()) {
            id = iterator.next();
            file = Constants.DB_DIR + "/" + id.substring(0, shardingFactor - 1) + 
            		Constants.COMMAND_SCRIPT_EXTENSION;
            br2 = new BufferedReader(new FileReader(file));
            line2 = br2.readLine();
            obj2= parser.parse(line2);
            jsonObject2 = (JSONObject) obj2;
            messageList.add((String)jsonObject2.get(id));
        }
        // Retorno la lista con los mensajes encontrados
		return messageList;
	}
	
	public synchronized void delete(Command command) throws IOException, ParseException {
		String fileName = Constants.DB_DIR + "/" + 
		command.getMessage().substring(0, shardingFactor - 1) + 
		Constants.COMMAND_SCRIPT_EXTENSION;			
		JSONParser parser = new JSONParser();
		Object obj;
		
		logger.info("Eleiminando registro");
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        line = br.readLine();
        obj= parser.parse(line);
        JSONObject jsonObject = (JSONObject) obj;
        JSONArray array = (JSONArray) jsonObject.get(command.getUser());
        array.add(command.getUuid().toString());
        jsonObject.put(command.getUser(), array);
        br.close();
        FileWriter file = new FileWriter(fileName, true);
        try {
            file.write(jsonObject.toJSONString());
        } catch (Exception e) {
			logger.error("Error al guardar el archivo de mensajes");
			logger.info(e.toString());
            e.printStackTrace();
        } finally {
            file.flush();
            file.close();
        }
	}
	
	private List<String> sortHashMapByValues(
	        Map<String, Integer> map) {
	    List<String> mapKeys = new ArrayList<>(map.keySet());
	    List<Integer> mapValues = new ArrayList<>(map.values());
	    Collections.sort(mapValues);
	    Collections.sort(mapKeys);

	    LinkedHashMap<String, Integer> sortedMap =
	        new LinkedHashMap<>();

	    java.util.Iterator<Integer> valueIt = mapValues.iterator();
	    while (valueIt.hasNext()) {
	        Integer val = valueIt.next();
	        java.util.Iterator<String> keyIt = mapKeys.iterator();

	        while (keyIt.hasNext()) {
	            String key = keyIt.next();
	            Integer comp1 = map.get(key);
	            Integer comp2 = val;

	            if (comp1.equals(comp2)) {
	                keyIt.remove();
	                sortedMap.put(key, val);
	                break;
	            }
	        }
	    }
	    Map<String, Integer> map2 = sortedMap;
	    List<String> tt = new ArrayList<String>();
        ArrayList<String> keys = new ArrayList<String>(sortedMap.keySet());
        int i=keys.size()-1;
        int j = ttCountShowPosts;
        while(i >= 0 && j > 0 ) {
        	tt.add(keys.get(i));
        	j--;
        	i--;
        }	    
	    return tt;
	}
	
}
