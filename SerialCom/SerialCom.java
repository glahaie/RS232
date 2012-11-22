package SerialCom;
/* SerialCom.java
 * Par Guillaume Lahaie, Charles-Emmanuel Joyal et Karl Brodeur
 * 
 * 
 * Ce programme permet de se connecter sur un port s�riel et d'envoyer et recevoir
 * des caract�res. Il est aussi possible d'effacer des caract�res.
 * 
 * Pour utiliser le programme, il suffit de choisir un port s�riel de la liste
 * fournie dans la barre de menu. Ensuite, Un message apparaitra pour confirmer
 * la connection. Si la connection �choue, un message apparait pour avertir
 * l'utilisateur.
 * 
 * Une fois que la connection est �tablie, il est possible d'�crire des messages
 * dans la fen�tre et d'en recevoir, si un autre h�te en envoie.
 * 
 * Le programme fonctionne avec rxtx et javacomm, il faut toutefois importer
 * la bonne librairie.
 * 
 * Am�liorations possibles: Le programme devrait d�tecter les erreurs de parit�,
 * toutefois il semble ne pas le faire pour le moment.
 * 
 * 
 * 
 ******************************************************************************
 * La classe SerialCom permet de d�crouvrir les ports disponibles sur l'ordinateur, 
 * et de s'y connecter. Il est ensuite possible d'�crire dans la zone de texte
 * et tout ce qui est �crit est envoy� sur le port.
 * 
 * De la m�me fa�on, si des informations sont envoy�es sur le port, elles seront
 * affich�es dans la zone de texte.
 * 
 * Pour le moment, la classe utilise la package gnu.io, donc rxtx, mais il devrait
 * �tre possible de la changer pour java comm.
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
	
	//Obtenir la liste des ports d�tect�s
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
                
        		gui.infos.setText("Connect� au port " + nomPort + "\n");
        		gui.infos.setEditable(true);
        		gui.infos.requestFocus();
        		
        		connected = true;               	                
			}
		} catch (NoSuchPortException e1) {
			gui.infos.setText("Le port choisi n'existe pas.\n");
		} catch (PortInUseException e2) {
			gui.infos.setText("Probl�me lors de la connexion. Le port choisi est d�j� utilis�.\n");
		} catch (IOException e3) {
			gui.infos.setText("Probl�me lors de la connexion.\n");
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

	
	
	//Event pour la r�ception d'informations sur le port choisi.
	public void serialEvent(SerialPortEvent e) {
		
		try {
			
			if(e.getEventType() == SerialPortEvent.FE) {
				gui.infos.append("Probl�me de framing.\n");
			} else if(e.getEventType() == SerialPortEvent.PE) {
				gui.infos.append("Probl�me de parit�.\n");
			} else if (e.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
				byte singleData = (byte)in.read();
				if((char)singleData == (char)KeyEvent.VK_BACK_SPACE) {
					//On efface un caract�re si possible.
					String chaine = gui.infos.getText();
					if (chaine.length() > 0) {
						gui.infos.setText(chaine.substring(0, chaine.length() -1));
					}
				} else if(!(Character.isIdentifierIgnorable((char)singleData))) {
					//On ajoute le caract�re si ce n'est pas un caract�re de contr�le.
					gui.infos.append(new String(new byte[] {singleData}));
				}
			}			
		} catch (IOException e1) {
			gui.infos.setText("Probl�me lors de la r�ception d'informations.\n");
		}
		
	}

}
