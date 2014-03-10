package client_chat;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/*N.B. queste sono classi di prova, create per verificare la fattibilita del progetto,
 per questo motivo sono presenti indirizzi ip,porte,percorsi assoluti inseriti in modo manuale
 dal programmatore*/

public class CreateKeyStore {

	public CreateKeyStore() {
		try {

			// creo un file bat
			FileOutputStream output = new FileOutputStream("certificato.bat");
			DataOutputStream stdout = new DataOutputStream(output);

			// codice per la creazione di un certificato
			stdout.write("@echo off\n".getBytes());
			stdout.write("cd ".getBytes());
			stdout.write(System.getProperty("java.home").getBytes());
			stdout.write("\n".getBytes());
			stdout.write(("(echo francesco cozzolino & echo cozzo & echo cozzo & echo misano "
					+ "& echo rn & echo it & echo si) | keytool -genkey -alias serverkey -keyalg RSA"
					+ " -keypass password -storepass password -keystore C:\\chiave.jks\n")
					.getBytes());

			stdout.write(("keytool -export -alias serverkey -storepass password "
					+ "-file C:\\certificato.cer -keystore C:\\chiave.jks\n")
					.getBytes());

			stdout.write("echo on\n".getBytes());

			stdout.close();

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public static void main(String[] Args) {

		new CreateKeyStore();
		try {
			System.out.println(System.getProperty("java.home"));
			// eseguo il file .bat
			Runtime.getRuntime().exec("certificato.bat");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
