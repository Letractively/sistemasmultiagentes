package comunicacion;

import java.util.HashMap;

public class Pizarra {

	
	private HashMap<String, Integer> URLVisitadas;
	
	public Pizarra() {
		
		URLVisitadas = new HashMap<String, Integer>();
	}
	
	public synchronized boolean quieroVisitar(String url) {
		
		if (URLVisitadas.containsKey(url)) {
			return false;
			
		} else {
			URLVisitadas.put(url, 0);
			return true;			
		}		
	}
}
