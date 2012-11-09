/* SerialCom.java
 * Par Guillaume Lahaie, Charles-Emmanuel Joyal et Karl Brodeur
 * 
 * La classe permet de d�crouvrir les ports disponibles sur l'ordinateur, et
 * de s'y connecter. Il est ensuite possible d'�crire dans la zone de texte
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
				serialPort.setSerialPortParams(57600,SerialPort.DATABITS_8,
						SerialPort.STOPBITS_1,SerialPort.PARITY_EVEN);
                
                in = serialPort.getInputStream();
                out = serialPort.getOutputStream();
                
                serialPort.addEventListener(this);
                serialPort.notifyOnDataAvailable(true);
                
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
			
			switch(e.getEventType()) {
				case SerialPortEvent.PE: gui.infos.append("Probl�me de parit�.\n");
										 break;
				case SerialPortEvent.DATA_AVAILABLE:	byte singleData = (byte)in.read();
									gui.infos.append(new String(new byte[] {singleData}));
									break;
			}
			
		} catch (IOException e1) {
			gui.infos.setText("Probl�me lors de la r�ception d'informations.\n");
		}
		
	}

}
