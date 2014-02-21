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

	public void Run() {
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
			try {
				ois = new ObjectInputStream(socket.getInputStream());
				oos = new ObjectOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {

			File file = new File(System.getProperty("user.dir") + "/"
					+ System.getProperty("user.name") + "ServerKey.jks");
			try {
				oos.writeUTF(System.getProperty("user.name"));
				oos.flush();
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
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
