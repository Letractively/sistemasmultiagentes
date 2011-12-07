package comunicacion;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import practicas.AgenteInterfaz;


/**
 * Esta clase implementa un monitor que contiene un fichero de log con los métodos necesarios para abrirlo y escribir en él de forma concurrente.
 * 
 * @author jacinto
 *
 */
public class LogFile {

	// El objeto en el que se escribe desde java.
	private BufferedWriter out;
	
	// La ruta absoluta del fichero donde se almacenará lo escrito.
	private String path;
	
	
	/**
	 * Este constructor inicializa el fichero de log a partir de su ruta. Puede no haber sido creado previamente.
	 * 
	 * @param p - Ruta del fichero de log.
	 */
	public LogFile(String p){
		 path = p;
		 
		 try {
			 
			out = new BufferedWriter(new FileWriter(path));
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}	 
	}

	
	
	/**
	 * Método del monitor que permite escribir de forma concurrente en dicho fichero de log.
	 * Se trata de un método sincronizado por lo que creará colas deespera en el monitor.
	 * 
	 * @param s - El mensaje que se quiere escribir en el fichero. 
	 */
	public synchronized void escribir(String s) {
		
		try {
			
			out.write(s);
			AgenteInterfaz.escribirTextArea(s);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Permite volver a empezar el fichero de log. Borra todo lo anterior.
	 */
	public void reiniciar() {
		
		cerrar();
		try {
			 
			out = new BufferedWriter(new FileWriter(path));
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	
	/**
	 * Cierra el fichero para evitar pérdidas de información.
	 */
	public void cerrar() {
		try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
