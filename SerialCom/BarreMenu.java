package SerialCom;
/* BarreMenu.java
 * Par Guillaume Lahaie, Charles-Emmanuel Joyal et Karl Brodeur
 * 
 * Interface graphique pour SerialCom, permet d'écrire et de lire
 * les données envoyées sur le port sélectionné.
 * 
 * Création de la barre de menu.
 * 
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

public class BarreMenu extends JMenuBar implements ItemListener {

	private JMenu fichier, port;
	private JMenuItem quitter;
	private SerialCom serialCom;
	private JRadioButtonMenuItem[] menuRadio;
	private String portSelected;
	private ButtonGroup buttonGroup;

	//Constructeur
	public BarreMenu (SerialCom serialCom) {
		this.serialCom = serialCom;
		creerMenu();
	}
	
	//Initialise les menus.
	private void creerMenu() {
		fichier = new JMenu("Fichier");	
		quitter = new JMenuItem("Quitter");
		quitter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				serialCom.fermerConn();
				System.exit(0);
			}
		});				
		fichier.add(quitter);
				
		add(fichier);
		creerPorts();
		
	} //Fin creerMenu
	
	
	//Crée des boutons radios pour le choix du port. Si aucun port est détecté,
	//affiche alors dans le menu un message indiquant qu'aucun port n'est disponible.
	protected void creerPorts() {
		
		port = new JMenu("Ports");
		buttonGroup = new ButtonGroup();
		
		if(serialCom.getListe().size() == 0) {
			port.add(new JMenuItem("Aucun Port disponible"));
		} else {
			menuRadio = new JRadioButtonMenuItem[serialCom.getListe().size()];
			for(int i = 0; i < serialCom.getListe().size(); i++) {
				menuRadio[i] = new JRadioButtonMenuItem(serialCom.getListe().get(i));
				menuRadio[i].addItemListener(this);	
				buttonGroup.add(menuRadio[i]);
				port.add(menuRadio[i]);
			}
		}
		
		add(port);
		revalidate();
	}

	
	//Sélection d'un des ports
	public void itemStateChanged(ItemEvent e) {

		
		for(int i = 0; i < menuRadio.length; i++) {
			if(e.getSource() == menuRadio[i]) {
				portSelected = serialCom.getListe().get(i);
			}
			menuRadio[i].setEnabled(false);
		}
		serialCom.openPort(portSelected);
				
	}

} //Fin BarreMenu
