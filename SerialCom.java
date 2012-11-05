import gnu.io.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.*;

public class SerialCom {

	private List<String> listePorts;
	private InputStream in;
	private OutputStream out;
	private CommPort commPort;
	private SerialPort serialPort;
	private CommPortIdentifier portIdentifier;
	private GUI gui;
	private SerialWriter serialWriter;
	private SerialReader serialReader;
	
	//Obtenir une liste des ports disponibles
	protected void decouvrirPorts() {
		
		CommPortIdentifier cp;
		Enumeration portIdentifiers = CommPortIdentifier.getPortIdentifiers();
		
		listePorts = new ArrayList<String>();
		
		while(portIdentifiers.hasMoreElements()) {
			cp = (CommPortIdentifier)portIdentifiers.nextElement();
			listePorts.add(cp.getName());
		}
		
		System.out.println(listePorts);
		
	}
	
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
		try {
			connect(portSelected);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void ouvrirLecture() {
		
	    try {
			serialPort.addEventListener(new SerialReader(in, gui));
	        serialPort.notifyOnDataAvailable(true);
		} catch (TooManyListenersException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void fermerLecture() {
		if(in != null) {
			serialPort.notifyOnDataAvailable(false);
			serialPort.removeEventListener();
		} else {
			System.out.println("Vous devez vous connecter.");
		}

	}
	
	protected void ouvrirEcriture() {
		if (out != null) {
			
			(new Thread(new SerialWriter(out, gui))).start();
		} else {
			System.out.println("Vous devez vous connecter avant.");
		}
	}		  
	
	protected void fermerEcriture() {
		System.out.println("fermer");
	}
	
	//Connect sur le port, si disponible
	void connect (String nomPort) throws Exception {
		
		portIdentifier = CommPortIdentifier.getPortIdentifier(nomPort);
		if(portIdentifier.isCurrentlyOwned()) {
			System.out.println("Le port choisi est déjà  utilisé.");
		} else {
			commPort = portIdentifier.open(this.getClass().getName(), 2000);
			if (commPort instanceof SerialPort) {
				serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(57600,SerialPort.DATABITS_8,
						SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                
                in = serialPort.getInputStream();
                out = serialPort.getOutputStream();
                
			} else {
				System.out.println("Veuillez utiliser un port en série");
			}
		}		
	}
	
	public static void main(String[] args) {
		
		SerialCom s = new SerialCom();
		s.decouvrirPorts();
		System.out.println(s.listePorts);
		GUI gui = new GUI(s);
	}

}
