import gnu.io.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.*;

public class SerialCom {

	/**
	 * @param args
	 */
	
	//Obtenir une liste des ports disponibles
	private static List<String> decouvrirPorts () {
		
		CommPortIdentifier cp;
		Enumeration portIdentifiers = CommPortIdentifier.getPortIdentifiers();
		
		List<String> liste = new ArrayList<String>();
		
		liste = null;
		while(portIdentifiers.hasMoreElements()) {
			cp = (CommPortIdentifier)portIdentifiers.nextElement();
			liste.add(cp.getName());
		}
		
		return liste;
		
	}
	
	
	//Connect sur le port, si disponible
	void connect (String nomPort) throws Exception {
		
		CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(nomPort);
		if(portIdentifier.isCurrentlyOwned()) {
			System.out.println("Le port choisi est déjà utilisé.");
		} else {
			CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);
			if (commPort instanceof SerialPort) {
				SerialPort serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(57600,SerialPort.DATABITS_8,
						SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                
                InputStream in = serialPort.getInputStream();
                OutputStream out = serialPort.getOutputStream();
                
                (new Thread(new SerialWriter(out))).start();
                
                serialPort.addEventListener(new SerialReader(in));
                serialPort.notifyOnDataAvailable(true);
			} else {
				System.out.println("Veuillez utiliser un port en série");
			}
		}
		
		
		
	}
	
	public static void main(String[] args) {
		
		InputStream in = System.in;
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		int choix = Integer.MIN_VALUE;
		
		System.out.println("Veuillez choisir un port:");
		List<String> l = decouvrirPorts();
		for(int i = 0; i < l.size(); i++) {
			System.out.println(i + ": " + l.get(i));
		}
		
		try {
			while(choix <0 || choix >= l.size()) {
				System.out.println("Entrer le numéro du port:");
				choix = Integer.parseInt(br.readLine());
				
				if(choix < 0 || choix >= l.size()) {
					System.out.println("Mauvaise valeur, veuillez choisir de nouveau.");
				}
			}

			(new SerialCom()).connect(l.get(choix));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/* Classe qui permet de lire sur le port en série
	 * 
	 */
	public static class SerialReader implements SerialPortEventListener 
	{
		private InputStream in;
		private byte[] buffer = new byte[1024];

		public SerialReader ( InputStream in )
		{
			this.in = in;
		}

		public void serialEvent(SerialPortEvent arg0) {
			int data;

			try
			{
				int len = 0;
				while ( ( data = in.read()) > -1 )
				{
					if ( data == '\n' ) {
						break;
					}
					buffer[len++] = (byte) data;
				}
				System.out.print(new String(buffer,0,len));
			}
			catch ( IOException e ) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}
	
	/* Classe pour écrire les données du port 
	 * 
	 */
	public static class SerialWriter implements Runnable 
	{
		OutputStream out;

		public SerialWriter ( OutputStream out )
		{
			this.out = out;
		}

		public void run ()
		{
			try
			{
				int c = 0;
				while ( ( c = System.in.read()) > -1 )
				{
					this.out.write(c);
				}
			}
			catch ( IOException e )
			{
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}
}
