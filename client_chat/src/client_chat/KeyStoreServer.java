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

public class KeyStoreServer extends Thread {

	private ServerSocket serverSocket;
	private ViewObserver controller;

	public KeyStoreServer(ViewObserver controller) {

		this.controller = controller;

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
				new TransferKeyStore(controller, serverSocket.accept()).start();

			} catch (IOException e) {
			}
		}
	}

	public void close() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static class TransferKeyStore extends Thread {
		private Socket socket;
		private ObjectInputStream ois = null;
		private ObjectOutputStream oos = null;
		private ViewObserver controller;

		public TransferKeyStore(ViewObserver controller, Socket socket) {
			this.socket = socket;
			this.controller = controller;
		}

		public void run() {

			File file = new File(System.getProperty("user.dir") + "/"
					+ WebsocketHandler.NICKNAME + "ServerKey.jks");
			try {

				ois = new ObjectInputStream(socket.getInputStream());
				oos = new ObjectOutputStream(socket.getOutputStream());

				String name = ois.readUTF();
				String who = ois.readUTF();

				if (who.equals("user")) {
					int choice = controller.buildChoiceMessageBox(
							MessageBoxReason.REQUEST_PRIVATE_CHAT, name);
					if (choice == 0) {
						oos.writeUTF(WebsocketHandler.NICKNAME);
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
					oos.writeUTF(WebsocketHandler.NICKNAME);
					oos.flush();
				}

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

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
