package practicas;

import java.awt.*;
import java.awt.event.*;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.Timer;
import java.util.TimerTask;

import javax.swing.*;

import comunicacion.*;

public class AgenteInterfaz extends JFrame implements Agente, ActionListener {

	private JLabel urlLabel;
	private JTextField campoUrlText;

	private JLabel keywordLabel;
	private JTextField campoKeywordText;

	private JLabel actividadLabel;

	private JButton empezarButtom;
	private JButton salirButtom;

	private static JTextArea area;
	private JScrollPane scroll;

	// Variables:
	private boolean actividad = false;
	private int agentesActivos = 0;
	private int maxAgentes = 0;

	// temporal:
	private LogFile log;
	private Pizarra pizarra;

	public AgenteInterfaz(LogFile log, Pizarra pizarra) {
		super("Practicas Multiagente");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(450, 300);
		// setResizable(false);// Para fijar la pantalla.
		definirVentana();
		setVisible(true);

		this.log = log;
		this.pizarra = pizarra;

		// PAra actualizar el número de agentes:
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (actividad) {
					agentesActivos = Thread.activeCount() - 5;
					actividadLabel.setText(String.format("Agentes en activo x %s", agentesActivos));
				}
			}
		}, 0, 60 * 10);
	}

	public void definirVentana() {

		setLayout(new GridBagLayout());
		urlLabel = new JLabel("URL:");
		generarRestriccion(urlLabel, 0, 0, 1, 1, 0, 0);

		campoUrlText = new JTextField();
		generarRestriccion(campoUrlText, 1, 0, 2, 3, 0, 0);

		keywordLabel = new JLabel("Keyword:");
		generarRestriccion(keywordLabel, 0, 2, 1, 2, 0, 0);

		campoKeywordText = new JTextField();
		generarRestriccion(campoKeywordText, 1, 2, 2, 3, 0, 0);

		empezarButtom = new JButton("Comenzar");
		generarRestriccion(empezarButtom, 1, 5, 1, 1, 0, 0);

		salirButtom = new JButton("Salir");
		generarRestriccion(salirButtom, 2, 5, 1, 1, 0, 0);
		
        actividadLabel = new JLabel("Actividad nula");
        generarRestriccion(actividadLabel, 0, 4, 1, 5, 0, 0);

		area = new JTextArea();
		
		scroll = new JScrollPane(area);
		generarRestriccion(scroll, 0, 8, 5, 8, 1, 1);
		

		empezarButtom.addActionListener(this);
		salirButtom.addActionListener(this);

	}

	/**
	 * M�todo para generar una restricci�n para el layout.
	 * 
	 * @param comp
	 * @param gridx
	 * @param gridy
	 * @param gridwidth
	 * @param gridheight
	 * @param weightx
	 * @param weighty
	 * @return
	 */
	public GridBagConstraints generarRestriccion(Component comp, int gridx, int gridy, int gridwidth, int gridheight, int weightx, int weighty) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.gridwidth = gridwidth;
		gbc.gridheight = gridheight;
		gbc.weightx = weightx;
		gbc.weighty = weighty;
		gbc.fill = GridBagConstraints.BOTH;
		add(comp, gbc);
		return gbc;
	}
	
	public static void escribirTextArea(String texto){
		area.append(texto);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == empezarButtom) {
			if (!actividad) {
				actividad = true;
				log.reiniciar();

				AgenteBusqueda agent;

				try {

					String pruebaURL = campoUrlText.getText();

					if (pruebaURL.startsWith("www"))
						pruebaURL = "http://" + pruebaURL;

					agent = new AgenteBusqueda(this, log, pizarra, "p", new URL(pruebaURL), campoKeywordText.getText().split(","));
					agent.start();

					actividadLabel.setText(String.format("Agentes en activo x %s", agentesActivos));

				} catch (MalformedURLException e1) {
					// TODO: mostrar error
				}
			}

		} else if (e.getSource() == salirButtom) {
			log.cerrar();
			System.exit(0);
		}

	}

	@Override
	public void mensaje(String msg) {
		System.out.println("Fiiiiiiiin");
		actividad = false;
		actividadLabel.setText("Actividad finalizada");
		log.cerrar();
	}
}
