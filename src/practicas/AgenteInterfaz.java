package practicas;

import java.awt.*;
import java.awt.event.*;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.Timer;
import java.util.TimerTask;

import javax.swing.*;

import comunicacion.*;

/**
 * Agente de interfaz del sistema. 
 * Muestra una interfaz e interactúa con el usuario.
 * 
 * Permite inciializar la búsqueda activando al agente de búsqueda padre y queda a la escucha de posibles soluciones encontradas.
 * Muestra información relevante al usuario durante la ejecución del resto de agentes.
 * 
 * @author jacinto
 *
 */
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

	private double TEMP_INICIAL = 1;
	
	/**
	 * Se inicializa la interfaz, la mayoría del código del constructor es código de interfaz gráfica de java swing.
	 * La funcionalidad se encuentra en las acciones de los botones y en otros métodos.
	 * 
	 * 
	 * @param log - El fichero de log con el que vamos a monitorizar a los agentes.
	 * @param pizarra - La pizarra compartida mediante la que se comunicarán los agentes.
	 * 
	 */
	public AgenteInterfaz(LogFile log, Pizarra pizarra) {
		
		// CODIGO DE INTERFAZ:
		super("Practicas Multiagente");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(450, 300);
		// setResizable(false);// Para fijar la pantalla.
		definirVentana();
		setVisible(true);

		
		// INICIALIZACIÓN DE PARÁMETROS:
		this.log = log;
		this.pizarra = pizarra;

		
		// Timer que periodicamente actualizará información referente a la ejecución:
		// 
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

	/*
	 * CODIGO DE INTERFAZ GRÁFICA.
	 */
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
	 * CÓDIGO DE INTERFAZ GRÁFICA:
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
	
	/**
	 * Permite añadir texto al área de texto.
	 * @param texto
	 */
	public static void escribirTextArea(String texto){
		area.append(texto);
	}

	
	/**
	 * Código para controlar los eventos de la interfaz:
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		// BOTONES:
		if (e.getSource() == empezarButtom) {
			
			// Comenzamos la búsqueda:
			
			if (!actividad) {
				actividad = true;
				log.reiniciar();
				
				
				AgenteBusqueda agent;

				
				try {
				
					// Leemos la URL:
					String pruebaURL = campoUrlText.getText();

					if (pruebaURL.startsWith("www"))
						pruebaURL = "http://" + pruebaURL;

					// Instanciamos al primero de los agentes:
					agent = new AgenteBusqueda(this, log, pizarra, "p", new URL(pruebaURL), campoKeywordText.getText().split(","), TEMP_INICIAL);
					
					// Iniciamos y lanzamos al agente, a partir de ahora es independiente.
					agent.start();

					// Comenzamos a mostrar la actividad:
					actividadLabel.setText(String.format("Agentes en activo x %s", agentesActivos));

				} catch (MalformedURLException e1) {
					area.append("Esa URL está mal.");
				}
			}

		// Cerramos el sistema.
		} else if (e.getSource() == salirButtom) {
			log.cerrar();
			System.exit(0);
		}

	}

	/**
	 * MÉTODO PARA OBTENER MENSAJES DE OTROS AGENTES Y PROCESARLOS:
	 * 
	 */
	@Override
	public void mensaje(Mensaje msg) {
		
		// comprobamos que el código del mensaje nos sea reconocido, en caso contrario lo ignoramos:
		if (msg.codigo.equals("fin")) {
			System.out.println("Fiiiiiiiin");
			actividad = false;
			actividadLabel.setText("Actividad finalizada");
			log.cerrar();	
			
		} else if (msg.codigo.equals("sol")) {
			escribirTextArea(String.format("%s encontrada %s veces en la URL %s\n", msg.parametros[0], msg.parametros[1], msg.parametros[2]));
		}

		
	}
}
