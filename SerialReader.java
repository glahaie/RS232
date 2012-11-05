import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;

	/* Classe qui permet de lire sur le port en série
	 * 
	 */
	public class SerialReader implements SerialPortEventListener 
	{
		private InputStream in;
		private byte[] buffer = new byte[1024];
		private GUI gui;

		public SerialReader ( InputStream in, GUI gui )
		{
			this.in = in;
			this.gui = gui;
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
				gui.ecrireSurTexte(new String(buffer,0,len));
			}
			catch ( IOException e ) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}