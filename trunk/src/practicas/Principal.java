package practicas;


import java.awt.event.*;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.*;


public class Principal extends JPanel implements MouseListener {

	 JLabel url;
	 JTextField campoUrl;
	 
	 JLabel keyword;
	 JTextField campoKeyword;
	 
	 JButton empezar;
	 JButton salir;
	 
	public Principal() {
		
		 setLayout(null);
		
		 url = new JLabel("URL:");
		 url.setBounds(20, 20, 130, 25);
		 add(url);
		 
		 keyword = new JLabel("Keyword:");
		 keyword.setBounds(20, 70, 130, 25);
		 add(keyword);
		 
		 
		 campoUrl = new JTextField("");
		 campoUrl.setBounds(100, 20, 288, 19);
		 add(campoUrl);
		 
		 campoKeyword = new JTextField("");
		 campoKeyword.setBounds(100, 70, 288, 19);
		 add(campoKeyword);
		 
		 
		 empezar = new JButton("Comenzar");
		 empezar.setBounds(140, 164, 120, 25);
		 add(empezar);
		 empezar.addMouseListener(this);
		 
		 salir = new JButton("Salir");
		 salir.setBounds(300, 164, 100, 25);
		 add(salir);
		 salir.addMouseListener(this);
	}
	
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
		if (e.getSource() == empezar){
            
			// Creamos el agente:
			AgenteBusqueda agent;
			
			
			try {
				String pruebaURL = campoUrl.getText();
				
				if (pruebaURL.startsWith("www")) {
					pruebaURL = "http://" + pruebaURL;
				}
				
				agent = new AgenteBusqueda("1", new URL(pruebaURL), campoKeyword.getText().split(","));
				
				agent.start();
				
			} catch (MalformedURLException e1) {
				// TODO: mostrar error
			}
			
        } else if (e.getSource() == salir) {
        	System.exit(0);
        }
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
