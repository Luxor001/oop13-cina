package client_chat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import client_chat.Controller.MessageBoxReason;

/**
 * Creates a socket server.To accept a client you need to have router's port
 * open.With this class it's possible to exchange keystores with the clients
 * 
 * @author Francesco Cozzolino
 * 
 */
public class KeyStoreServer {

	private ServerSocket serverSocket;

	/**
	 * Starts new thread that accepts the clients for exchange keystores
	 * 
	 * @param controller
	 * @throws IOException
	 *             if an I/O error occurs
	 * @see ViewObserver
	 */
	public KeyStoreServer(final ViewObserver controller) throws IOException {

		serverSocket = new ServerSocket(User.getPortKeyStore());

		class Accept implements Runnable {
			public void run() {
				while (true) {
					try {
						new TransferKeyStore(controller, serverSocket.accept())
								.start();

					} catch (IOException e) {
					}
				}
			}
		}
		new Thread(new Accept()).start();
	}

	/**
	 * 
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public void close() throws IOException {
		serverSocket.close();
	}

	/**
	 * This class permits to exchange keystores with the clients
	 * 
	 * @author Francesco
	 * 
	 */
	private static class TransferKeyStore extends Thread {
		private Socket socket;
		private ObjectInputStream ois = null;
		private ObjectOutputStream oos = null;
		private ViewObserver controller;

		/**
		 * 
		 * @param controller
		 * @param socket
		 * 
		 * @see Controller
		 * @see Socket
		 */
		public TransferKeyStore(ViewObserver controller, Socket socket) {
			this.socket = socket;
			this.controller = controller;
		}

		/**
		 * This thread exchange the keystores with the client
		 */
		public void run() {

			File file = new File(System.getProperty("user.dir") + "/"
					+ User.getNickName() + "ServerKey.jks");

			try {
				ois = new ObjectInputStream(socket.getInputStream());

				oos = new ObjectOutputStream(socket.getOutputStream());

				String name = ois.readUTF();
				String who = ois.readUTF();

				if (who.equals("user")) {
					int choice = controller.buildChoiceMessageBox(
							MessageBoxReason.REQUEST_PRIVATE_CHAT, name);
					if (choice == 0) {
						oos.writeUTF(User.getNickName());
						oos.flush();
					} else {
						oos.writeObject(null);
						oos.flush();
						oos.close();
						ois.close();
						socket.close();
						return;
					}

				} else {
					oos.writeUTF(User.getNickName());
					oos.flush();
				}

				oos.writeInt((int) file.length());
				oos.flush();
				int size = ois.readInt();

				FileInputStream fileStream = new FileInputStream(file);
				byte[] buffer = new byte[(int) file.length()];
				fileStream.read(buffer);
				oos.write(buffer);
				oos.flush();

				File receivedFile = new File(System.getProperty("user.dir")
						+ "/" + name + "ServerKey.jks");

				receivedFile.createNewFile();
				FileOutputStream outStream = new FileOutputStream(receivedFile);

				byte[] bufferReader = new byte[size];
				ois.readFully(bufferReader);
				outStream.write(bufferReader);
				oos.close();
				ois.close();
				socket.close();
				fileStream.close();
				outStream.close();
			} catch (IOException e) {
			}

		}
	}
}
