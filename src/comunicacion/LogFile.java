package comunicacion;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class LogFile {

	private BufferedWriter out;
	private String path;
	
	public LogFile(String p){
		 path = p;
		 
		 try {
			 
			out = new BufferedWriter(new FileWriter(path));
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}	 
	}

	
	public synchronized void escribir(String s) {
		
		try {
			
			out.write(s);
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void reiniciar() {
		
		cerrar();
		try {
			 
			out = new BufferedWriter(new FileWriter(path));
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	
	public void cerrar() {
		try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
