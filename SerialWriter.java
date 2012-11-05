import java.io.IOException;
import java.io.OutputStream;

	/* Classe pour écrire les données du port 
	 * 
	 */
	public class SerialWriter implements Runnable 
	{
		OutputStream out;
		GUI gui;

		public SerialWriter ( OutputStream out, GUI gui )
		{
			this.out = out;
			this.gui = gui;
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