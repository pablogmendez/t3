package ar.fiuba.taller.analyzer;

import java.util.List;

public class UserRegistry {

	public UserRegistry() {
		// TODO Auto-generated constructor stub
	}
	

	public synchronized boolean validUser(String user) {
		return true;
	}
	
	public synchronized void reloadDataBase() {
		
	}
	
	public synchronized boolean update(String follower, String followed) {
		return true;
	}

	public List<String> getUserFollowers(String followed) {
		return null;
		
	}
	
	public List<String> getHashtagFollowers(String followed) {
		return null;
		
	}
	
}
