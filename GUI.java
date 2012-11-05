import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/* GUI.java
 * Par Guillaume Lahaie
 * 
 * Gestion de l'ensemble des panels de l'interface pour la gestion des vols d'avion et des passager.
 * Les panels utilisés seront dans d'autres classes.
 *  
 * Dernière modification: 4 décembre 2011
 *  
 *  Utilisation pour TP1 de INF3270 - 2 novembre 2011
 */
public class GUI extends JPanel implements KeyListener {

	protected JFrame frame;
	final public static int LARGEUR = 640; //grandeur du frame
	final public static int HAUTEUR = 480;
	protected JTextArea infos;
	private JScrollPane js;
	private BarreMenu bar;
	private JPanel panel;
	private SerialCom serialCom;
	private char c = 'a';
		
	GUI(SerialCom serialCom) {
		this.serialCom = serialCom;
		infos = new JTextArea("Ouverture de la gestion des vols.\n");
		infos.setSize(LARGEUR, 50);
		infos.setEditable(true);
		infos.addKeyListener(this);

		js = new JScrollPane(infos);
		js.setPreferredSize(new Dimension(LARGEUR, 50));
		bar = new BarreMenu(serialCom, this);
		panel = new JPanel(new BorderLayout());
		
		//Vient de l'ancien main
		this.frame = new JFrame("Gestion de vols");
		this.frame.setSize(LARGEUR, HAUTEUR);
		this.frame.setJMenuBar(this.bar);
		this.panel.add(this.js, BorderLayout.CENTER);
		this.frame.getContentPane().add(this.panel);
		this.frame.setVisible(true);
		this.frame.setLocation(100, 100);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
		
	//M�thodes pour BarreMenu.
	protected void scanPorts() {
		System.out.println("Call scanPorst");
		serialCom.decouvrirPorts();
		bar.creerPorts();
		repaint();
	}
	
	protected void ecrireSurTexte(String s) {
		infos.append(s);
	}
	
	protected int envoyerTexte() {
		return (int)c;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (bar.getEcriture()) {
			c = e.getKeyChar();
		}
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void keyTyped(KeyEvent arg0) {}
	
}
