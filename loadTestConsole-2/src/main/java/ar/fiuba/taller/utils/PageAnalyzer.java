package ar.fiuba.taller.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ar.fiuba.taller.loadTestConsole.Constants;
import ar.fiuba.taller.loadTestConsole.User;

public class PageAnalyzer {
	final static Logger logger = Logger.getLogger(User.class);
	
	public PageAnalyzer() {
		MDC.put("PID", String.valueOf(Thread.currentThread().getId()));
	}

	public Map<String, String> getResources(String response, String url)
			throws URISyntaxException, IOException, BadLocationException {
		Map<String, String> tmpMap = new HashMap<String, String>();
		Iterator<Element> it = null;
		Document doc = null;
		Elements resource = null;
		String tmpUrl = null;
		doc = Jsoup.parse(response);
		
		for (Map.Entry<String, String> entry : Constants.RESOURCE_MAP.entrySet())
		{
			resource = doc.select(entry.getKey());
			it = resource.iterator();
			while (it.hasNext()) {
				tmpUrl = it.next().attr(entry.getValue());
				if(tmpUrl.indexOf("http") == -1) {
					tmpUrl = normalizeUrl(url, "last") + "/" + normalizeUrl(tmpUrl, "first");
				}
				tmpMap.put(entry.getKey(), tmpUrl);
			}
		}
		return tmpMap;
	}

	private String normalizeUrl(String url, String place) {
		String tmpUrl = url.trim();

		if(("").equals(tmpUrl)) {
			return tmpUrl;
		}
		if(place.equals("first")) {
			while (tmpUrl.substring(0, 1).equals("/")) {
				tmpUrl = tmpUrl.substring(1, tmpUrl.length());
			}			
		} else { // last			
			while (tmpUrl.substring(tmpUrl.length() - 1).equals("/")) {
				tmpUrl = tmpUrl.substring(0, tmpUrl.length() - 1);
			}
		}
		return tmpUrl;
	}	
}
