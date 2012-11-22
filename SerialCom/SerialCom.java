package SerialCom;
/* SerialCom.java
 * Par Guillaume Lahaie, Charles-Emmanuel Joyal et Karl Brodeur
 * 
 * 
 * Ce programme permet de se connecter sur un port sériel et d'envoyer et recevoir
 * des caractères. Il est aussi possible d'effacer des caractères.
 * 
 * Pour utiliser le programme, il suffit de choisir un port sériel de la liste
 * fournie dans la barre de menu. Ensuite, Un message apparaitra pour confirmer
 * la connection. Si la connection échoue, un message apparait pour avertir
 * l'utilisateur.
 * 
 * Une fois que la connection est établie, il est possible d'écrire des messages
 * dans la fenêtre et d'en recevoir, si un autre hôte en envoie.
 * 
 * Le programme fonctionne avec rxtx et javacomm, il faut toutefois importer
 * la bonne librairie.
 * 
 * Améliorations possibles: Le programme devrait détecter les erreurs de parité,
 * toutefois il semble ne pas le faire pour le moment.
 * 
 * 
 * 
 ******************************************************************************
 * La classe SerialCom permet de décrouvrir les ports disponibles sur l'ordinateur, 
 * et de s'y connecter. Il est ensuite possible d'écrire dans la zone de texte
 * et tout ce qui est écrit est envoyé sur le port.
 * 
 * De la même façon, si des informations sont envoyées sur le port, elles seront
 * affichées dans la zone de texte.
 * 
 * Pour le moment, la classe utilise la package gnu.io, donc rxtx, mais il devrait
 * être possible de la changer pour java comm.
 * 
 */


import gnu.io.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.awt.event.KeyEvent;
//import javax.comm.*;


public class SerialCom implements SerialPortEventListener {

	private List<String> listePorts;
	protected InputStream in;
	protected OutputStream out;
	private CommPort commPort;
	private SerialPort serialPort;
	private CommPortIdentifier portIdentifier;
	private static GUI gui;
	private boolean connected = false;
	
	
	//Constructeur
	public SerialCom () {
		decouvrirPorts();
		gui = new GUI(this);
	}
	
	//Obtenir une liste des ports disponibles
	protected void decouvrirPorts() {
		
		CommPortIdentifier cp;
		Enumeration portIdentifiers = CommPortIdentifier.getPortIdentifiers();
		
		listePorts = new ArrayList<String>();
		
		while(portIdentifiers.hasMoreElements()) {
			cp = (CommPortIdentifier)portIdentifiers.nextElement();
			listePorts.add(cp.getName());
		}
		
	}
	
	//Obtenir la liste des ports détectés
	protected List<String> getListe() {
		return listePorts;
	}
	
	protected void fermerConn() {
		if (serialPort != null) {
			commPort.close();
		}
	}
	
	protected void openPort(String portSelected) {
		if(serialPort != null) {
			commPort.close();
		}
		connect(portSelected);

	}	
	//Connecte sur le port nomPort. Si le port n'est pas disponible, affiche
	//un message d'erreur.
	void connect (String nomPort) {
	
		try {
			portIdentifier = CommPortIdentifier.getPortIdentifier(nomPort);

			commPort = portIdentifier.open(this.getClass().getName(), 2000);
			if (commPort instanceof SerialPort) {
				serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(9600,SerialPort.DATABITS_8,
						SerialPort.STOPBITS_1,SerialPort.PARITY_EVEN);
                
                in = serialPort.getInputStream();
                out = serialPort.getOutputStream();
                
                serialPort.addEventListener(this);
                serialPort.notifyOnDataAvailable(true);
                serialPort.notifyOnParityError(true);
                serialPort.notifyOnFramingError(true);
                
        		gui.infos.setText("Connecté au port " + nomPort + "\n");
        		gui.infos.setEditable(true);
        		gui.infos.requestFocus();
        		
        		connected = true;               	                
			}
		} catch (NoSuchPortException e1) {
			gui.infos.setText("Le port choisi n'existe pas.\n");
		} catch (PortInUseException e2) {
			gui.infos.setText("Problème lors de la connexion. Le port choisi est déjà utilisé.\n");
		} catch (IOException e3) {
			gui.infos.setText("Problème lors de la connexion.\n");
		} catch(Exception e4) {
			e4.printStackTrace();
		}
	
	}
	
	public boolean isConnected() {
		return connected;
	}
	
	public static void main(String[] args) {
		
		new SerialCom();
	}

	
	
	//Event pour la réception d'informations sur le port choisi.
	public void serialEvent(SerialPortEvent e) {
		
		try {
			
			if(e.getEventType() == SerialPortEvent.FE) {
				gui.infos.append("Problème de framing.\n");
			} else if(e.getEventType() == SerialPortEvent.PE) {
				gui.infos.append("Problème de parité.\n");
			} else if (e.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
				byte singleData = (byte)in.read();
				if((char)singleData == (char)KeyEvent.VK_BACK_SPACE) {
					//On efface un caractère si possible.
					String chaine = gui.infos.getText();
					if (chaine.length() > 0) {
						gui.infos.setText(chaine.substring(0, chaine.length() -1));
					}
				} else if(!(Character.isIdentifierIgnorable((char)singleData))) {
					//On ajoute le caractère si ce n'est pas un caractère de contrôle.
					gui.infos.append(new String(new byte[] {singleData}));
				}
			}			
		} catch (IOException e1) {
			gui.infos.setText("Problème lors de la réception d'informations.\n");
		}
		
	}

}
