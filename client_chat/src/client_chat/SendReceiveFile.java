package client_chat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;

public abstract class SendReceiveFile {

	protected SendReceiveFile() {

	}

	protected void sendFile(File file, String name,
			ManagementFiles managementFile, Downloaded download)
			throws IOException {

		int step = 150000;
		byte[] buffer = new byte[step];
		long fileSize = file.length();
		FileInputStream fileStream = new FileInputStream(file);

		try {
			download.addFile(name, managementFile.getIdFile(),
					managementFile.getFileName(), (int) fileSize);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("File sending");
		sendMessage("Receiving file");
		while (fileSize > 0) {
			fileSize -= step;

			if (fileSize < 0) {
				fileSize += step;
				step = (int) fileSize;
				fileSize = 0;
			}

			download.updateProgressBar(name, managementFile.getIdFile(), step);

			fileStream.read(buffer);
			sendMessage(managementFile, buffer, step);
		}

		sendMessage("File received");
		System.out.println("File sent");
		fileStream.close();
	}

	public abstract void sendMessage(String message) throws IOException;

	public abstract void sendMessage(ManagementFiles file, byte[] message,
			int step) throws IOException;

	protected void receiveFile(Object o, String name, ObjectInputStream ois,
			Downloaded download, Map<Integer, DownloadFile> fileReceive)
			throws IOException {
		ManagementFiles managementFile = (ManagementFiles) o;

		int step = ois.readInt();
		byte[] bufferReceive = new byte[step];
		ois.readFully(bufferReceive, 0, step);

		DownloadFile value = fileReceive.get(managementFile.getIdFile());

		if (value == null) {

			String nameFile = managementFile.getFileName();

			String newName = nameFile;
			int i = 1;
			String dfaddress = User.getStoredPath();

			while (new File(dfaddress + "/" + newName).exists()) {

				String[] nameExtension = nameFile.split("\\.");

				nameExtension[0] = nameExtension[0] + "(" + i + ")";
				newName = nameExtension[0] + "." + nameExtension[1];
				i++;
			}

			download.addFile(name, managementFile.getIdFile(), newName,
					managementFile.getFileSize());

			value = new DownloadFile(new FileOutputStream(new File(dfaddress
					+ "/" + newName)));
		}

		download.updateProgressBar(name, managementFile.getIdFile(), step);

		value.write(bufferReceive, 0, step);
		value.incrementSize(step);

		fileReceive.put(managementFile.getIdFile(), value);

		if (value.getSize() == managementFile.getFileSize()) {
			System.out.println("File received " + managementFile.getFileName());
			fileReceive.get(managementFile.getIdFile()).close();
			fileReceive.remove(managementFile.getIdFile());
		}

	}

}
