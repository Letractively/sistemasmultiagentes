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
	private ArrayList<String> vinculosNuevos;
	
	private LogFile log;
	private Pizarra pizarra;
	
	private int hijosActivos;
	
	private Agente padre;
	private ArrayList<AgenteBusqueda> hijos;
	
	
	// Expresiones Regulares (utilizadas para procesar el código):
	
	private static String REGEX_TAG_VINCULOS 	= "<a href[^>]*>";
	private static String REGEX_VINCULOS 		= "\"http(s?)://[^\"']*\"";
	private static String REGEX_TEXTO			= "<[^>]*>";
	
	// Solo temporal. Para controlar la profundidad:
	private static int    PROFUNDIDAD_MAXIMA = 2;
	private static int    MINIMO_OCURRENCIAS = 2;
	
	
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
		vinculosNuevos	= new ArrayList<String>();
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
	 * Comprobamos mediante la pizarra que los vínculos no estaban visitados:
	 */
	private void compruebaVinculos() {
		
		// Para cada vínculo...
		for (String v:vinculos) {
			
			
			// comprobamos si dicho vínculo no ha sido antes visitado:
			if (pizarra.quieroVisitar(v)) {
				
				vinculosNuevos.add(v);
				
			}
		}
	}
	
	
	
	/**
	 * Lanza el hilo y ejecuta la función del agente:
	 */
	@Override
	public void run() {
		
			// LOG: Mensajes de Log iniciales:
			log.escribir(String.format("\n[1](Creacion)\t Agente %s:\t He sido creado.", id));						
			log.escribir(String.format("\n[2](URL)\t Agente %s:\t Adquiriendo código de %s.", id, url.toExternalForm()));
			
			try {
				
				// Obtenemos el código:
				cogeCodigo();
			
				// Extraemos el texto:
				extraeTexto();
				
				// Buscamos las keywords:
				buscaKeywords();
				
				
				// Analizamos los resultados de la busqueda:
				
				// LOG: Vamos generando un mensaje Log para mostrarlas:
				String ocu = String.format("\n[3](Ocurr)\t Agente %s: \t found: ", id);		
				
				// Para cada keyword componemos el mensaje en sí:
				for (int i = 0; i < keywords.length; i++) {		
					
					// LOG:
					 ocu += String.format("%s(%d), ", keywords[i], ocurrenciasKW[i]);
				
					 
					 // También si las ocurrencias  para esta keyword exceden el mínimo, debemos comunicarselo a nuestro padre para que se lo vayamos enviando a la interfaz:
					 if (ocurrenciasKW[i] >= MINIMO_OCURRENCIAS)
						 padre.mensaje(new Mensaje("sol", new String[]{String.valueOf(keywords[i]), String.valueOf(ocurrenciasKW[i]), url.toExternalForm()}));
				}
				
				// LOG: finalmente imprimimos el mensjae de log:
				log.escribir(ocu);
				
				
				
				// Extraemos los vínculos:
				buscaVinculos();
					
				
				// Comprobamos qué vinculos son nuevos:
				compruebaVinculos();
				
				
				// LOG: Mensaje de log para mosrar el número de vínculos y cuales son nuevos.
				log.escribir(String.format("\n[4](Vinculos)\t Agente %s:\t Tengo %d vinculos de los cuales %d ya estan visitados.", id, vinculos.size(), vinculos.size()-vinculosNuevos.size()));

				
				
				
				///////////////////////////////////////////////
				//
				// A continuación activar el método para generar la descendencia deseado:
				
				generarDescendenciaTrivial();
				
				// generarDescendencia1();
				
				
				
				
				// LOG: Mensaje de log para indicar el volumen de la replicacion de este agente:
				if (hijos.isEmpty()) {
					log.escribir(String.format("\n[5](Clonar)\t Agente %s:\t No me voy a clonar.", id));
				} else {
					log.escribir(String.format("\n[5](Clonar)\t Agente %s:\t Me voy a clonar %d veces.", id, hijos.size()));
				}
				
				
				// Lanzamos nuestros hijos:
				for (AgenteBusqueda a:hijos)
					a.start();
						
	
				// Final del agente:
						
				// Nos quedaremos activos hasta que nuestros hijos hayan terminado, para poder escuchar y pasar mensajes:
				hijosActivos = hijos.size();
													
				// Pasamos el control a un monitor interno para controlar los hijos que nos queden:
				seguir();
				
			
			// Control de excepciones para el caso de un mal uso del fichero:
			} catch (IOException e1) {
				// LOG: Si no podemos descargar el código mostramos este error:
				log.escribir(String.format("\n[2](URL)\t Agente %s:\t URL no disponible.", id));
			}	
			
			// Llegados a este punto el agente muere:
			
			// LOG: lo escribe en el log y 
			log.escribir(String.format("\n[6](Muerte)\t Agente %s:\t He terminado, chao.", id));
			
			// Antes envia un mensaje a su padre para comunicarselo:
		    padre.mensaje(new Mensaje("fin", null));
	}

	
	
	/**
	 * METODOS REFERENTES A COMUNICACIÓN:
	 */
	
	/**
	 * Este método se utiliza para que el agente no muera hasta que todos sus hijos hayan terminado.
	 * Se espera a que todos sus hijos hayan terminado y mientras queda a la espera.
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
	public synchronized void mensaje(Mensaje msg) {
		
		
		// Comprobamos que el código del mensaje nos sea reconocido, en caso contrario lo ignoramos:
		
		// Mensajes de fin para comunicarle a tu padre que has terminado y no tienes descendencia:
		if (msg.codigo.equals("fin")) {
			hijosActivos--;			
			notify();
			
			
		// Mensaje para pasar una solución encontrada (o redirigida por tu hijo) hacia arriba:
		} else if (msg.codigo.equals("sol")) {
			padre.mensaje(msg);
		}

	}




		//////////////////////////////////////////////////////////////////////////////////////////////
		//
		//		AQUÍ PODEMOS PONER LOS MÉTODOS DE GENERAR LA DESCENDENCIA QUE VAYAMOS HACIENDO:
		//
		//
		//
		
	/*
	 * Los metodos para generar descendencia deben tomar la lista de vinculosNuevos y a partir de los mismos y del resto de atributos del agente
	 * que se consideren relevantes introducir nuevos agentes en la lista "hijos", luego el programa se encargará de lanzar dichos hijos.
	 * 
	 * Se ruega que se comente que hace cada metodo y que si indica si lleva a cabo una búsqueda infinita.
	 * 
	 */
	
	
	/**
	 * Genera la descendencia incondicionalmente.
	 * 
	 * 
	 */
	private void generarDescendenciaTrivial() {

		// Un contador para ir construyendo los identificadores de los hijos a partir del nuestro.
		int i = 1; 
		
		// Para cada vínculo nuevo en la lista creamos un hijo:
		for (String v:vinculosNuevos) {

			try {
				// Creamos un nuevo hijo:
				hijos.add(new AgenteBusqueda(this, log, pizarra, id+"-"+String.valueOf(i), new URL(v), keywords));
				i++;
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}	
		}
	}
	
	
	/**
	 * Genera descendencia acotando la profundidad del árbol a la constante PROFUNDIDAD_MAXIMA
	 * Cuando un agente se encuentr en el nivel d = PROFUNDIDAD_MAXIMA no generará ninguna descendencia.
	 * 
	 */
	private void generarDescendencia1() {
		
	
		if (id.split("-").length < PROFUNDIDAD_MAXIMA) {
	
			int i = 1; // Un contador para los identificadores de los hijos:
			
			// Para cada vínculo nuevo creamos un hijo:
			for (String v:vinculosNuevos) {

				try {
					// En cuyo caso creamos un nuevo agente hijo, concatenando nuestro id al que le toque:
					hijos.add(new AgenteBusqueda(this, log, pizarra, id+"-"+String.valueOf(i), new URL(v), keywords));
					i++;
					
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}	
			}
		}
	}

}

