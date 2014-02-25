package client_chat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class KeyStoreServer extends Thread {

	private ServerSocket serverSocket;

	public KeyStoreServer() {

		try {
			serverSocket = new ServerSocket(9998);
		} catch (IOException e) {
			e.printStackTrace();
		}
		new Thread(this).start();
	}

	public void run() {
		while (true) {
			try {
				new TransferKeyStore(serverSocket.accept()).start();
			} catch (IOException e) {
			}
		}
	}

	private static class TransferKeyStore extends Thread {
		private Socket socket;
		private ObjectInputStream ois = null;
		private ObjectOutputStream oos = null;

		public TransferKeyStore(Socket socket) {
			this.socket = socket;

		}

		public void run() {

			File file = new File(System.getProperty("user.dir") + "/"
					+ WebsocketHandler.DEBUG_NICKNAME + "ServerKey.jks");
			try {

				ois = new ObjectInputStream(socket.getInputStream());
				oos = new ObjectOutputStream(socket.getOutputStream());
				oos.writeUTF(WebsocketHandler.DEBUG_NICKNAME);
				oos.flush();
				System.out.println("Invio file");
				String name = ois.readUTF();
				FileInputStream fileStream = new FileInputStream(file);
				byte[] buffer = new byte[10240];
				fileStream.read(buffer);
				oos.write(buffer);

				File receivedFile = new File(System.getProperty("user.dir")
						+ "/" + name + "ServerKey.jks");

				receivedFile.createNewFile();
				FileOutputStream outStream = new FileOutputStream(receivedFile);

				byte[] bufferReader = new byte[10240];
				ois.readFully(buffer);
				outStream.write(bufferReader);
				oos.close();
				ois.close();
				socket.close();
				fileStream.close();
				outStream.close();

				System.out.println("File inviato");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
