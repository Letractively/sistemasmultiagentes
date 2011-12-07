package comunicacion;

import java.util.ArrayList;


/**
 * Clase que implementa un mensaje estandar, con un código y una serie de parámetros.
 * 
 * @author jacinto
 *
 */
public class Mensaje {

	// Codigo del mensaje, para poder saber de cual se trata:
	public String codigo;
	
	// Parámetros del mensaje para procesarlo:
	public String[] parametros;
	
	public Mensaje(String c, String[] p) {
		
		codigo = c;
		parametros = p;
	}
}
