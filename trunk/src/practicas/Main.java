package practicas;

import java.net.MalformedURLException;
import java.net.URL;

import comunicacion.LogFile;
import comunicacion.Pizarra;



public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		
			// Creamos los elementos comunes del sistema:
		
			// Monitores:
		
			LogFile log 		= new LogFile("SMAlog");
			Pizarra pizarra		= new Pizarra();
			
			
			// El agente interfaz instancia de momento al de busqueda
			AgenteInterfaz interfaz = new AgenteInterfaz(log, pizarra);
			

	}
}
