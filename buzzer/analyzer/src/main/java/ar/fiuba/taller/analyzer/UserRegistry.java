package ar.fiuba.taller.analyzer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ar.fiuba.taller.common.Constants;

public class UserRegistry {

	public UserRegistry() {
		// TODO Auto-generated constructor stub
	}
	
	public synchronized void update(String follower, String followed) throws IOException, ParseException {
		String updateFile;
		String updateKey;
		JSONParser parser;
        Object obj;
        JSONObject jsonObject;
        JSONArray jsonArray;
        FileWriter file; 
        
		if(followed.substring(0, 0).equals("#")) {
			// Si sigo un hastag => actualizo la base de seguidores del hashtag
			updateFile = Constants.DB_DIR + "/" + Constants.DB_HASHTAG_INDEX;
			updateKey = followed.substring(0, followed.length() - 1);
		} else {
			// Si no, sumo que es un usuario => actualizo la base de seguidores del usuario
			updateFile = Constants.DB_DIR + "/" + Constants.DB_USER_INDEX;
			updateKey = followed;
		}
		
		parser = new JSONParser();
        obj = parser.parse(new FileReader(updateFile));
        jsonObject = (JSONObject) obj;
        jsonArray = (JSONArray) jsonObject.get(followed);
        
        if(jsonArray == null) {
        	// Si no existe el usuario o hashtag, lo creo
        	jsonArray = new JSONArray();
        }
        
        jsonArray.add(follower);
        jsonObject.put(followed, jsonArray);
        
        file = new FileWriter(updateFile);
        file.write(jsonObject.toJSONString());
        file.flush();
	}

	public List<String> getUserFollowers(String followed) throws FileNotFoundException, IOException, ParseException {
		String usersFile = Constants.DB_DIR + "/" + Constants.DB_USER_INDEX;
		
		JSONParser parser = new JSONParser();

        Object obj = parser.parse(new FileReader(usersFile));

        JSONObject jsonObject = (JSONObject) obj;

        // loop array
        return (JSONArray) jsonObject.get(followed);
	}
	
	public List<String> getHashtagFollowers(String followed) throws FileNotFoundException, IOException, ParseException {
		String hashtagFile = Constants.DB_DIR + "/" + Constants.DB_HASHTAG_INDEX;
		List<String> followersList = new ArrayList<String>();
		JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader(hashtagFile));
        JSONObject jsonObject = (JSONObject) obj;
        JSONArray jsonArray;
        Iterator<String> it;
        
        String regexPattern = "(#\\w+)";
		Pattern p = Pattern.compile(regexPattern);
		Matcher m = p.matcher(followed);
		while (m.find()) {
			jsonArray = (JSONArray) jsonObject.get(m.group(1));
			it = jsonArray.iterator();
			while(it.hasNext()) {
				followersList.add(it.next());
			}
		}
        return followersList;
	}
}
