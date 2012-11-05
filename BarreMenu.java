import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

/* MenuBar.java
 * Par Guillaume Lahaie
 * 
 * Cette classe gère la barre de menu de l'interface graphique de Vols. La barre permet surtout
 * de changer de panel pour faire différentes opérations.
 *
 *Dernière modification: 4 décembre 2011.
 * 
 */

public class BarreMenu extends JMenuBar implements ItemListener {

	private JMenu fichier, port;
	private JCheckBoxMenuItem lire, ecrire;
	private JMenuItem quitter, scan;
	private GUI gui;
	private SerialCom serialCom;
	private JRadioButtonMenuItem[] menuRadio;
	private boolean ecriture, lecture;
	private String portSelected;
	private ButtonGroup buttonGroup;

	//Constructeur
	public BarreMenu (SerialCom serialCom, GUI gui) {
		this.serialCom = serialCom;
		this.gui = gui;
		ecriture = false;
		lecture = false;
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
		lire = new JCheckBoxMenuItem("Lire");
		lire.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				AbstractButton button = (AbstractButton) e.getItem();
				if(button.isSelected()) {
					ecriture = true;
					serialCom.ouvrirLecture();
				} else {
					ecriture = false;
					serialCom.fermerLecture();
				}	
			}
		});
		ecrire = new JCheckBoxMenuItem("Ecrire");
		ecrire.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				AbstractButton button = (AbstractButton) e.getItem();
				if(button.isSelected()) {
					ecriture = true;
					serialCom.ouvrirEcriture();
				} else {
					ecriture = false;
					serialCom.fermerEcriture();				
				}
			}
		});

		fichier.add(lire);
		fichier.add(ecrire);
		fichier.addSeparator();
		fichier.add(quitter);
				
		add(fichier);
		creerPorts();
		
	} //Fin créerMenu
	
	protected void creerPorts() {
		
		System.out.println("call creerPorts()");
		
		if(port != null) {
			remove(port);
		}
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
		
		port.addSeparator();
		
		scan = new JMenuItem("Balayer les ports");
		scan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gui.scanPorts();
				//Besoin de mettre � jour la liste des ports, peut-�tre
			}
		});
		port.add(scan);
		add(port);
		revalidate();
	}

	@Override
	public void itemStateChanged(ItemEvent e) {

		
		for(int i = 0; i < menuRadio.length; i++) {
			if(e.getSource() == menuRadio[i]) {
				portSelected = serialCom.getListe().get(i);
			}
		}
		serialCom.openPort(portSelected);
	}
	
	protected boolean getEcriture() {
		return ecriture;
	}
	
	protected boolean getLecture() {
		return lecture;
	}

} //Fin MenuBar
