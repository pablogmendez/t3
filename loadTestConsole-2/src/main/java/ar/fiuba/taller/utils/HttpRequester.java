package ar.fiuba.taller.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class HttpRequester {

	public HttpRequester() {
		// TODO Auto-generated constructor stub
	}

	public String doHttpRequest(String method, String url,
			String headers, String data, int timeout) throws Exception {
		String result = null;
		String[] headersArray, tmp;
		Map<String, String> headersMap = new HashMap<String, String>();
		
		if(headers != null) {
			headersArray = headers.split(";");
			for(int i = 0; i < headersArray.length; i++) {
				tmp = headersArray[i].split(":");
				headersMap.put(tmp[0], tmp[1]);
			}
		}
		
		if (method.toLowerCase().equals("get")) {
			result = doGet(url, headersMap, data, timeout);
		} else if (method.toLowerCase().equals("post")) {
				result = doPost(url, headersMap, data, timeout);
		} else if (method.toLowerCase().equals("put")) {
			result = doPut(url, headersMap, data, timeout);
		}
		return result;

	}

	// HTTP GET request
	private String doGet(String url, Map<String, String> headers, String data, int timeout)
			throws Exception {
		// Armo la conexion
		URL obj = new URL(url);
		try {
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");
		con.setConnectTimeout(timeout);

		// add request header
		if (!headers.isEmpty()) {
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				con.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}

		BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		return response.toString();
		} catch (java.net.SocketTimeoutException e) {
		   return "";
		} catch (java.io.IOException e) {
		   return "";
		}
		// print result
	}

	// HTTP POST request
	private String doPost(String url, Map<String, String> headers, String data, int timeout)
			throws Exception {
		URL obj = new URL(url);
		try {
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// add reuqest method
		con.setRequestMethod("POST");
		con.setConnectTimeout(timeout);
		// add reuqest header
		if (!headers.isEmpty()) {
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				con.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(data);
		wr.flush();
		wr.close();

		BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		return response.toString();
		} catch (java.net.SocketTimeoutException e) {
		   return "";
		} catch (java.io.IOException e) {
		   return "";
		}

	}

	// HTTP PUT request
	private String doPut(String url, Map<String, String> headers, String data, int timeout)
			throws Exception {
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		try {
		// add reuqest method
		con.setRequestMethod("PUT");
		con.setConnectTimeout(timeout);

		// add reuqest header
		if (!headers.isEmpty()) {
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				con.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(data);
		wr.flush();
		wr.close();


		BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		return response.toString();
		} catch (java.net.SocketTimeoutException e) {
		   return "";
		} catch (java.io.IOException e) {
		   return "";
		}
	}

}
