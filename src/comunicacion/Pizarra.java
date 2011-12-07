package comunicacion;

import java.util.HashMap;

/**
 * Esta clase implementa una pizarra compartida a modo de momitor para su escritura y consulta concurrente
 * por parte de los agentes.
 * @author jacinto
 *
 */
public class Pizarra {

	
	// En este hash almacenaremos las URL visitadas por los agentes.
	private HashMap<String, Integer> URLVisitadas;
	
	/**
	 * Inicializa la pizarra. Sin más.
	 */
	public Pizarra() {
		
		URLVisitadas = new HashMap<String, Integer>();
	}
	
	
	/**
	 * Permite al gente comprobar si la url está o no visitada, y en caso negativo la apunta a la pizarra.
	 * @param url - La URL a consultar.
	 * @return false si la URL ha sido visitada anteriormente, true si no lo está.
	 * 
	 */
	public synchronized boolean quieroVisitar(String url) {
		
		if (URLVisitadas.containsKey(url)) {
			return false;
			
		} else {
			URLVisitadas.put(url, 0);
			return true;			
		}		
	}
}
