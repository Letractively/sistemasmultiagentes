package practicas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.*;
import java.util.ArrayList;

import comunicacion.*;


/**
 * Agente de búsqueda encargado de descargar el código de una web, procesarlo y replicarse.
 * Permanecerá activo mientras su rama lo esté y se comunicará con otros agentes cuando sea necesario.
 * @author jacinto
 *
 */
public class AgenteBusqueda extends Thread implements Agente {

	// Atributos de la clase:
	public  String id;
	private URL url;
	private String[] keywords;
	private int[] ocurrenciasKW;
	private String codigo;
	private String texto;
	private ArrayList<String> vinculos;
	
	private LogFile log;
	private Pizarra pizarra;
	
	private int hijosActivos;
	
	private Agente padre;
	private ArrayList<AgenteBusqueda> hijos;
	
	
	// Expresiones Regulares (utilizadas para procesar el código):
	
	private static String REGEX_TAG_VINCULOS 	= "<a href[^>]*>";
	private static String REGEX_VINCULOS 		= "\"http(s?)://[^\"']*\"";
	private static String REGEX_TEXTO			= "<[^>]*>";
	
	
	/**
	 * Constructor para incialziar una gente:
	 * 
	 * @param p - El padre de dicho agente.
	 * @param m - El fichero de log para escribir sus mensajes.
	 * @param pi - La pziarra compartida de comunicación.
	 * @param i - El identificador de dicho agente.
	 * @param u - La url que se le proporciona para buscar.
	 * @param kw - La lista de keywords.
	 */
	public AgenteBusqueda(Agente p, LogFile m, Pizarra pi, String i, URL u, String[] kw) {
		
		// Parametros:
		id			= i;
		url 		= u;
		keywords 	= kw;
		log			= m;
		pizarra 	= pi;
		padre		= p;
		
		// Solo incialización:
		codigo 			= "";
		texto 			= "";
		ocurrenciasKW	= new int[keywords.length];
		vinculos		= new ArrayList<String>();
		hijos			= new ArrayList<AgenteBusqueda>();
		
	}
	
	
	/**
	 * Extrae el código de la URL dada y lo guarda en un String.
	 * @throws IOException 
	 */
	private void cogeCodigo() throws IOException {

		BufferedReader in;
		in = new BufferedReader(new InputStreamReader(url.openStream()));
			
		while (in.ready()) {
			// Leemos linea linea y concatenamos:
			codigo += in.readLine();
		}

	}
	
	/**
	 * Utiliza expresiones regulares para extraer los vínculos del código de la página.
	 * 
	 * 
	 */
	private void buscaVinculos() {
		
		// Primero extraemos las tags que se corresponden con vínculos:
		Pattern patTags = Pattern.compile(REGEX_TAG_VINCULOS, Pattern.CASE_INSENSITIVE);
		Matcher matTags = patTags.matcher(codigo);
		
		// Las concatenamos en esta cadena:
		String tags = "";
		
		while (matTags.find()) {
			tags += matTags.group();
		}
		
		// Ahora extraemos el vínculo en sí de cada tag.
		Pattern patVinculos = Pattern.compile(REGEX_VINCULOS, Pattern.CASE_INSENSITIVE);
		Matcher matVinculos = patVinculos.matcher(tags);
				
		while (matVinculos.find()) {
			String v = matVinculos.group();
			vinculos.add(v.substring(1,v.length()-1));
		}
	}
	
	
	/**
	 * Extrae el texto a partir del código de la URL
	 */
	private void extraeTexto() {
		
		texto = codigo.replaceAll(REGEX_TEXTO, "");
	}
	
	
	/**
	 * Busca ocurrencias en el texto para cada keyword.
	 * ALmacena el resultado en el vector de ocurrencias.
	 */
	private void buscaKeywords() {
		
		// PAra cada keyword hacemos amtching y guardamos las ocurrencias.
		for (int i = 0; i < keywords.length; i++) {

			Pattern pat = Pattern.compile(keywords[i], Pattern.CASE_INSENSITIVE);

			Matcher mat = pat.matcher(texto);
			
			ocurrenciasKW[i] = 0;
			
			while (mat.find())
				ocurrenciasKW[i]++;
			
		}
	}
	
	
	
	
	
	
	/**
	 * Lanza el hilo...
	 */
	@Override
	public void run() {
			log.escribir(String.format("\n[1](Creacion)\t Agente %s:\t He sido creado.", id));
						
			log.escribir(String.format("\n[2](URL)\t Agente %s:\t Adquiriendo código de %s.", id, url.toExternalForm()));
			
			try {
				
				// Obtenemos el código:
				cogeCodigo();
			
				// Extraemos el texto:
				extraeTexto();
				
				// Buscamos las keywords:
				buscaKeywords();
				
				// Mostrar ocurrencias:
				String ocu = String.format("\n[3](Ocurr)\t Agente %s: \t found: ", id);			
				for (int i = 0; i < keywords.length; i++)
					 ocu += String.format("%s(%d), ", keywords[i], ocurrenciasKW[i]);
				log.escribir(ocu);
				
				// Extraemos los vínculos:
				buscaVinculos();
	
				
				/////////////////////////////////////////////////////////////////////////////////////////////////
				//CLONACION DEL AGENTE:
				
				
				// TEMPORAL: Una profundidad inicial para la búsqeuda:
				if (id.split("-").length < 3) {
					
					
					log.escribir(String.format("\n[4](Clonar)\t Agente %s:\t Me voy a clonar %d veces.", id, vinculos.size()));
					// Clonarse:
					
					int i = 1; // Un contador para los identificadores de los hijos:
					
					// PAra cada vínculo...
					for (String v:vinculos) {
						
						// comprobamos si dicho vínculo no ha sido antes visitado:
						if (pizarra.quieroVisitar(v)) {
							try {
								// En cuyo caso creamos un nuevo agente hijo, concatenando nuestro id al que le toque:
								hijos.add(new AgenteBusqueda(this, log, pizarra, id+"-"+String.valueOf(i), new URL(v), keywords));
								i++;
								
							} catch (MalformedURLException e) {
								e.printStackTrace();
							}
							
						}
					}
					
					// Si no hemos desplegado todos los hijos mostramos el factor de ramificación real:
					if (i-1 != vinculos.size()) {
						log.escribir(String.format("\n[5](Visitados)\t Agente %s:\t Solo me clono %d veces, %d vínculos ya estaban visitados.", id, i, vinculos.size()-i+1));
					}
					
					// Lazamos nuestros hijos:
					for (AgenteBusqueda a:hijos)
						a.start();
							
					// Nos quedaremos activos hasta que nuestros hijos hayan terminado, para poder pasar mensajes:
					hijosActivos = hijos.size();
					// Pasamos el control a un monitor interno para controlar los hijos que nos queden:
					seguir();
				}
					
			
			} catch (IOException e1) {
				// Si no podemos descargar el código mostramos este error:
				log.escribir(String.format("\n[2](URL)\t Agente %s:\t URL no disponible.", id));
			}	
			
			
			// Llegados a este punto el agente muere, lo escribe en el log y antes envia un mensaje a su padre para comunicarselo:
			log.escribir(String.format("\n[6](Muerte)\t Agente %s:\t He terminado, chao.", id));
		    padre.mensaje("Fin");
	}

	
	
	/**
	 * METODOS REFERENTES A COMUNICACIÓN:
	 */
	
	/**
	 * Se espera a que todos sus hijos hayan terminado y mientras queda a la espera:
	 */
	public synchronized void seguir() {
		while (hijosActivos != 0) {
			try {
				wait();	
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * MÉTODO PARA OBTENER MENSAJES DE OTROS AGENTES Y PROCESARLOS:
	 * 
	 */
	@Override
	public synchronized void mensaje(String msg) {
		
		
		// Una forma trivial es mandar un código y procesarlo:
		if (msg.equals("Fin")) {
			hijosActivos--;			
			notify();
		}
		
		//... seguirían más mensajes
	}
}
