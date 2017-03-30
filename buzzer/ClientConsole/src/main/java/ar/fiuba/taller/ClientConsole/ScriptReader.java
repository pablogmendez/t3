package ar.fiuba.taller.ClientConsole;

import java.util.concurrent.BlockingQueue;
import org.json.*;

public class ScriptReader implements Runnable {

	BlockingQueue<Command> commandQueue;
	
	public ScriptReader(BlockingQueue<Command> commandQueue) {
		this.commandQueue = commandQueue;
	}

	public void run() {
		try {
			JSONObject obj = new JSONObject(Constants.COMMAND_SCRIPT);
			JSONArray arr = obj.getJSONArray(Constants.COMMAND_ARRAY);
	
			for (int i = 0; i < arr.length(); i++) {
					commandQueue.put(new Command(arr.getJSONObject(i).getString(Constants.COMMAND_KEY), 
							arr.getJSONObject(i).getString(Constants.USER_KEY), 
							arr.getJSONObject(i).getString(Constants.MESSAGE_KEY)));
			}
		
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
