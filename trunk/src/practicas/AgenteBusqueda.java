package practicas;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.*;
import java.util.ArrayList;

public class AgenteBusqueda extends Thread {

	public  String id;
	private URL url;
	private String[] keywords;
	private String codigo;
	private ArrayList<String> vinculos;

	// Expresiones Regulares:
	
	private static String REGEX_TAG_VINCULOS 	= "<a href[^>]*>";
	private static String REGEX_VINCULOS 		= "\"https://[^\"]*\"";
	
	
	/**
	 * 
	 * @param i 	- Identificador del agente.
	 * @param u 	- URL a analizar.
	 * @param kw	- Keywords.
	 */
	public AgenteBusqueda(String i, URL u, String[] kw) {
		
		// Parametros:
		id			= i;
		url 		= u;
		keywords 	= kw;
		
		// Solo incialización:
		codigo 		= "";
		vinculos	= new ArrayList<String>();
	}
	
	
	/**
	 * Extrae el código de la URL dada y lo guarda en un String.
	 */
	private void cogeCodigo() {
		try {
			
			BufferedReader in;
			in = new BufferedReader(new InputStreamReader(url.openStream()));
				
			while (in.ready()) {
				
				codigo += in.readLine();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Utiliza expresiones regulares para extraer los vínculos del código de la página.
	 * 
	 * 
	 */
	private void buscaVinculos() {
		
		// Primero extraemos las tags que se corresponden con vínculos:
		Pattern patTags = Pattern.compile(REGEX_TAG_VINCULOS);
		Matcher matTags = patTags.matcher(codigo);
		
		// Las concatenamos en esta cadena:
		String tags = "";
		
		while (matTags.find()) {
			tags += matTags.group();
		}
		
		// Ahora extraemos el vínculo en sí de cada tag.
		Pattern patVinculos = Pattern.compile(REGEX_VINCULOS);
		Matcher matVinculos = patVinculos.matcher(tags);
				
		while (matVinculos.find()) {
			vinculos.add(matVinculos.group());
		}
	}
	
	
	/**
	 * Lanza el hilo...
	 */
	@Override
	public void run() {
		
		System.out.println(String.format("Agente %s: He sido creado.", id));
		
		
		System.out.println(String.format("Agente %s: Adquiriendo código de %s.", id, url.toExternalForm()));
		cogeCodigo();
		System.out.println(codigo);
		
		
		System.out.println(String.format("Agente %s: Buscando los vinculos.", id));
		buscaVinculos();
		System.out.println(String.format("Agente %s: He encontrado los vínculos:", id));
		for (String s:vinculos) {
			System.out.println(s);
		}
		
		
		System.out.println(String.format("Agente %s: He terminado, me autodestruyo.", id));
	}

}
