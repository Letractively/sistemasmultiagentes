package practicas;

import java.net.MalformedURLException;
import java.net.URL;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
			//System.out.print("Hola Grupo.");//Hola chicos ya est� probado e implementado el repo, solo queda empezar a programar =).
			
			
			// [Jacinto]: Vagos
			
			// Creamos agentes de prueba, que hagan lo que tengan que hacer de momento...
			
			
			String[] kw = {"universidad"};
			
			try {
				
				
				AgenteBusqueda agent = new AgenteBusqueda("1", new URL("http://www.uclm.es"), kw);
				
				agent.start();
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}

	}
}
