package practicas;

import java.awt.*;
import java.awt.event.*;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.*;

import comunicacion.LogFile;

public class AgenteInterfaz extends JFrame implements Agente, ActionListener {

	private JLabel urlLabel;
	private JTextField campoUrlText;

	private JLabel keywordLabel;
	private JTextField campoKeywordText;

	private JLabel actividadLabel;
	
	private JButton empezarButtom;
	private JButton salirButtom;

	
	// temporal: 
	private LogFile log;
	
	public AgenteInterfaz(LogFile log) {
		super("Practicas Multiagente");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(450, 300);
		//setResizable(false);// Para fijar la pantalla.
		definirVentana();
		setVisible(true);
		
		this.log = log;
	}

	public void definirVentana() {
		
		setLayout(new GridBagLayout());
		urlLabel = new JLabel("URL:");
		generarRestriccion(urlLabel, 0, 0, 1, 1, 0, 0);
		
		campoUrlText = new JTextField();
		generarRestriccion(campoUrlText, 1, 0, 2, 3, 0,0);	
		
		keywordLabel = new JLabel("Keyword:");
		generarRestriccion(keywordLabel, 0, 2, 1, 2, 0, 0);
		
		campoKeywordText = new JTextField();
		generarRestriccion(campoKeywordText, 1, 2, 2, 3, 0, 0);	

		actividadLabel = new JLabel("Actividad nula");
		generarRestriccion(actividadLabel, 0, 6, 1, 5, 0, 0);
		
		empezarButtom = new JButton("Comenzar");
		generarRestriccion(empezarButtom, 1, 5, 1, 1, 0, 0);			
		
		salirButtom = new JButton("Salir");
		generarRestriccion(salirButtom, 2, 5, 1, 1, 0, 0);

		empezarButtom.addActionListener(this);
		salirButtom.addActionListener(this);
	}
	
	/**
	 * M�todo para generar una restricci�n para el layout.
	 * @param comp
	 * @param gridx
	 * @param gridy
	 * @param gridwidth
	 * @param gridheight
	 * @param weightx
	 * @param weighty
	 * @return
	 */
	public GridBagConstraints generarRestriccion(Component comp,int gridx, int gridy, int gridwidth, int gridheight, int weightx, int weighty ){
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx=gridx;
		gbc.gridy=gridy;
		gbc.gridwidth=gridwidth;
		gbc.gridheight=gridheight;
		gbc.weightx=weightx;
		gbc.weighty=weighty;
		gbc.fill= GridBagConstraints.BOTH;
		add(comp,gbc);
		return gbc;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == empezarButtom) {
			AgenteBusqueda agent;
			try {
				
				String pruebaURL = campoUrlText.getText();
				
				if (pruebaURL.startsWith("www"))
					pruebaURL = "http://" + pruebaURL;
				
				agent = new AgenteBusqueda(this, log, "1", new URL(pruebaURL), campoKeywordText.getText().split(","));
				agent.start();
				
				actividadLabel.setText("Agentes en activo");
			
				
			} catch (MalformedURLException e1) {
				// TODO: mostrar error
			}

		} else if (e.getSource() == salirButtom) {
			System.exit(0);
		}

	}
	

	

	@Override
	public void mensaje(String msg) {
		actividadLabel.setText("Actividad finalizada");
		log.cerrar();
	}
}
