package ar.fiuba.taller.storage;

import java.io.FileReader;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import ar.fiuba.taller.common.Command;
import ar.fiuba.taller.common.Constants;

public class Storage {

	public Storage() {
		// TODO Auto-generated constructor stub
	}

	public int insert(Command command) {
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(new FileReader(commandScript));
		JSONObject jsonObject = (JSONObject) obj;
		JSONArray commandArray = (JSONArray) jsonObject.get(Constants.COMMAND_ARRAY);
        Iterator<JSONObject> iterator = commandArray.iterator();
        JSONObject commandObject;
		return 0;
		
	}
	
	public String query() {
		return null;
		
	}
	
	public int delete() {
		return 0;
		
	}
	
}
