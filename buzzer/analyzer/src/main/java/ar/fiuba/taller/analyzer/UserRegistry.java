package ar.fiuba.taller.analyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ar.fiuba.taller.common.Constants;

public class UserRegistry {

	final static Logger logger = Logger.getLogger(UserRegistry.class);
	
	
	public UserRegistry() {
		// TODO Auto-generated constructor stub
	}
	
	public synchronized void update(String follower, String followed) throws IOException, ParseException {
		String updateFile;
		String updateKey;
		JSONParser parser = new JSONParser();;
        Object obj;
        JSONObject jsonObject;
        JSONArray jsonArray;
        FileWriter file; 
        
		if(String.valueOf(followed.charAt(0)).equals("#")) {
			// Si sigo un hastag => actualizo la base de seguidores del hashtag
			updateFile = Constants.DB_DIR + "/" + Constants.DB_HASHTAG_INDEX;
			updateKey = followed.substring(1, followed.length());
		} else {
			// Si no, asumo que es un usuario => actualizo la base de seguidores del usuario
			updateFile = Constants.DB_DIR + "/" + Constants.DB_USER_INDEX;
			updateKey = followed;
		}
		
		logger.info("Actualizando el inice: " + updateFile + " con " + updateKey);
		File tmpFile = new File(updateFile);
		if(tmpFile.createNewFile()) {
			FileOutputStream oFile = new FileOutputStream(tmpFile, false);
			oFile.write("{}".getBytes());
		}
		
        obj= parser.parse(new FileReader(tmpFile));
        jsonObject = (JSONObject) obj;
        JSONArray array = (JSONArray) jsonObject.get(updateKey);
        if(array == null) {
        	// Hay que crear la entrada en el indice
        	JSONArray ar2 = new JSONArray();
        	ar2.add(follower);
        	jsonObject.put(updateKey, ar2);
        } else {
	        array.add(follower);
	        jsonObject.put(updateKey, array);
        }
        file = new FileWriter(tmpFile);
        try {
            file.write(jsonObject.toJSONString());
        } catch (Exception e) {
			logger.error("Error al guardar el index");
			logger.info(e.toString());
            e.printStackTrace();
        } finally {
            file.flush();
            file.close();
        }
	}

	public List<String> getUserFollowers(String followed) throws FileNotFoundException, IOException, ParseException {
		String usersFile = Constants.DB_DIR + "/" + Constants.DB_USER_INDEX;
		JSONParser parser = new JSONParser();
		Object obj;
		JSONObject jsonObject;
		
		logger.info("Buscando followers del usuario");
		
		File tmpFile = new File(usersFile);
		if(tmpFile.createNewFile()) {
			FileOutputStream oFile = new FileOutputStream(tmpFile, false);
			oFile.write("{}".getBytes());
		}
        obj= parser.parse(new FileReader(usersFile));
        jsonObject = (JSONObject) obj;
        JSONArray array = (JSONArray) jsonObject.get(followed);

        System.out.println(array.toJSONString());
		
        return array;
	}
	
	public List<String> getHashtagFollowers(String followed) throws FileNotFoundException, IOException, ParseException {
		String hashtagFile = Constants.DB_DIR + "/" + Constants.DB_HASHTAG_INDEX;
		List<String> followersList = new ArrayList<String>();
		JSONParser parser = new JSONParser();
        Object obj;
        JSONObject jsonObject;
        JSONArray jsonArray;
        Iterator<String> it;
        String word;
        
        logger.info("Buscando followers del hashtag");
        
        File tmpFile = new File(hashtagFile);
		if(tmpFile.createNewFile()) {
			FileOutputStream oFile = new FileOutputStream(tmpFile, false);
			oFile.write("{}".getBytes());
		}
		logger.info("Obteniendo hashtags de " + followed);
		obj= parser.parse(new FileReader(hashtagFile));
		jsonObject = (JSONObject) obj;
        String regexPattern = "(#\\w+)";
		Pattern p = Pattern.compile(regexPattern);
		Matcher m = p.matcher(followed);
		while (m.find()) {
			word = m.group(1).substring(1, m.group(1).length());
			logger.info("Hashtag: " + m.group(1));
			logger.info("Topic sin #: " + word);	
			jsonArray = (JSONArray) jsonObject.get(word);
			logger.info("arr: " + jsonArray);
			it = jsonArray.iterator();
			while(it.hasNext()) {
				followersList.add(it.next());
			}
		}
        return followersList;
	}
}
