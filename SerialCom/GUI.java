package SerialCom;
/* GUI.java
 * Par Guillaume Lahaie, Charles-Emmanuel Joyal et Karl Brodeur
 * 
 * Interface graphique pour SerialCom, permet d'écrire et de lire
 * les données envoyées sur le port sélectionné.
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class GUI extends JPanel implements KeyListener {

	protected JFrame frame;
	final public static int LARGEUR = 640; //grandeur du frame
	final public static int HAUTEUR = 480;
	protected JTextArea infos;
	private JScrollPane js;
	private BarreMenu bar;
	private JPanel panel;
	private SerialCom serialCom;
		
	GUI(SerialCom serialCom) {
		this.serialCom = serialCom;
		infos = new JTextArea("Veuillez choisir un port.\n");
		infos.setSize(LARGEUR, 50);
		infos.setEditable(false);
		infos.addKeyListener(this);

		js = new JScrollPane(infos);
		js.setPreferredSize(new Dimension(LARGEUR, 50));
		bar = new BarreMenu(serialCom);
		panel = new JPanel(new BorderLayout());
		
		//Vient de l'ancien main
		this.frame = new JFrame("SerialCom");
		this.frame.setSize(LARGEUR, HAUTEUR);
		this.frame.setJMenuBar(this.bar);
		this.panel.add(this.js, BorderLayout.CENTER);
		this.frame.getContentPane().add(this.panel);
		this.frame.setVisible(true);
		this.frame.setLocation(100, 100);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
		
	//Méthodes pour BarreMenu.
	protected void scanPorts() {
		serialCom.decouvrirPorts();
		bar.creerPorts();
		repaint();
	}

	
	
	//KeyListener - Seulement keyTyped est utilisé
	public void keyPressed(KeyEvent e) {}

	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void keyTyped(KeyEvent e) {
		
		if(serialCom.isConnected()) {
			try {
				serialCom.out.write(e.getKeyChar());
			} catch (IOException e1) {
				infos.setText("Problème lors de l'envoi des données.\n");
			}
		}
		
	}


	
}
