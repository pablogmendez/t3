package ar.fiuba.taller.storage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

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
	final static Logger logger = Logger.getLogger(App.class);
	
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
		
		logger.info("Actualizando el inice de usuarios");
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        line = br.readLine();
        obj= parser.parse(line);
        JSONObject jsonObject = (JSONObject) obj;
        JSONArray array = (JSONArray) jsonObject.get(command.getUser());
        array.add(command.getUuid().toString());
        jsonObject.remove(command.getMessage());
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
	
	public String query(Command command) {
		List<String> resultList;
		if(command.getMessage().substring(0).equals("#")) { // Es consulta por hashtag
			resultList = queryHashTag(command.getMessage().substring(1, command.getMessage().length() - 1));
		}
		else if (command.getMessage().equals("TT")){ // Es consulta por TT
			resultList = queryTT(command.getMessage().substring(3, command.getMessage().length() - 1));
		}
		else { // Es consulta por usuario
			resultList = queryUser(command.getMessage());
		}
		return null;
		
	}
	
	private List<String> queryHashTag(String hashTag) {
		return null;
	}

	private List<String> queryTT(String hashTag) {
		return null;
	}
	
	private List<String> queryUser(String user) {
		return null;
	}
	
	public void delete(Command command) throws IOException, ParseException {
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
	
}
