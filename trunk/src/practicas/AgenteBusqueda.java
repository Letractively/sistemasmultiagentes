package practicas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.*;
import java.util.ArrayList;

import comunicacion.LogFile;


public class AgenteBusqueda extends Thread implements Agente {

	public  String id;
	private URL url;
	private String[] keywords;
	private int[] ocurrenciasKW;
	private String codigo;
	private String texto;
	private ArrayList<String> vinculos;
	private LogFile out;
	
	private int hijosActivos;
	
	private Agente padre;
	private ArrayList<AgenteBusqueda> hijos;
	
	// Expresiones Regulares:
	
	private static String REGEX_TAG_VINCULOS 	= "<a href[^>]*>";
	private static String REGEX_VINCULOS 		= "\"http(s?)://[^\"']*\"";
	private static String REGEX_TEXTO			= "<[^>]*>";
	
	// Log:
	
	private static String log_file				= "logSMA";
	
	
	/**
	 * 
	 * @param i 	- Identificador del agente.
	 * @param u 	- URL a analizar.
	 * @param kw	- Keywords.
	 */
	public AgenteBusqueda(Agente p, LogFile m, String i, URL u, String[] kw) {
		
		// Parametros:
		id			= i;
		url 		= u;
		keywords 	= kw;
		out			= m;
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
			out.escribir(String.format("\n[1](Creacion)\t Agente %s:\t He sido creado.", id));
						
			out.escribir(String.format("\n[2](URL)\t Agente %s:\t Adquiriendo código de %s.", id, url.toExternalForm()));
			
			try {
				cogeCodigo();
			
			
			extraeTexto();
			buscaKeywords();
			
			// Mostrar ocurrencias:
			String ocu = String.format("\n[3](Ocurr)\t Agente %s: \t found: ", id);			
			for (int i = 0; i < keywords.length; i++)
				 ocu += String.format("%s(%d), ", keywords[i], ocurrenciasKW[i]);
			out.escribir(ocu);
			
			
			buscaVinculos();

			
			/////////////////////////////////////////////////////////////////////////////////////////////////
			//CLONACION DEL AGENTE:
			
			
			// TEMPORAL: Una profundidad inicial:
			if (id.split("-").length < 3) {
				
				
				out.escribir(String.format("\n[4](Clonar)\t Agente %s:\t Me voy a clonar %d veces.", id, vinculos.size()));
				// Clonarse:
				int i = 1;
				for (String v:vinculos) {
					
					try {
						
						hijos.add(new AgenteBusqueda(this, out, id+"-"+String.valueOf(i), new URL(v), keywords));
						i++;
						
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
				
				for (AgenteBusqueda a:hijos)
					a.start();
						
				// Nos quedaremos activos hasta que nuestros hijos hayan terminado, para poder pasar mensajes:
				hijosActivos = hijos.size();
				// Padamos el control a un monitor interno:
				seguir();
			}
					
			
			} catch (IOException e1) {
				out.escribir(String.format("\n[2](URL)\t Agente %s:\t URL no disponible.", id));
			}	
			
			
			// Terminamos:
			out.escribir(String.format("\n[5](Muerte)\t Agente %s:\t He terminado, chao.", id));
		    padre.mensaje("Fin");
	}

	
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
	
	
	// Con este método recibimos los mensajes:
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
