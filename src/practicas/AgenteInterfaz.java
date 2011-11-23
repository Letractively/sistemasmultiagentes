package practicas;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class AgenteInterfaz {
	
	JFrame frame;
	
	public AgenteInterfaz() {
		
		
		frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Practicas Multiagente");
        frame.setBounds(275,150,450,300);
        
        Principal panel = new Principal();
        
        
        frame.add(panel);
        frame.setVisible(true);
	}
	
	
}
