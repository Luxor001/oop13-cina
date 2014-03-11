package client_chat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;

/**
 * You can sends and receives files. This abstract class provides two methods to
 * define for send message (string message, chunk of bytes)
 * 
 * 
 * @author Francesco Cozzolino
 * 
 */
public abstract class SendReceiveFile {

	/**
	 * Sends file to an user and adds the state of upload to a Downlaoded
	 * object. With a Downloaded object it's possible to see the state of
	 * downloads/uploads
	 * 
	 * @param file
	 *            file to send
	 * @param name
	 *            name of receiver
	 * @param managementFile
	 * @param download
	 * 
	 * @see ManagementFiles
	 * @see Downloaded
	 * 
	 * @throws IOException
	 *             if an I/O error occurs
	 */
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
		fileStream.close();
	}

	/**
	 * Sends a textual message
	 * 
	 * @param message
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public abstract void sendMessage(String message) throws IOException;

	/**
	 * Sends an array of bytes
	 * 
	 * @param file
	 * @param message
	 *            array of bytes
	 * @param step
	 *            how many bytes to write
	 * 
	 * @see ManagementFiles
	 * 
	 * @throws IOException
	 *             if an I/O error occurs
	 * 
	 */
	public abstract void sendMessage(ManagementFiles file, byte[] message,
			int step) throws IOException;

	/**
	 * 
	 * Reads chunk of bytes and writes on the file's filestram and uploads the
	 * state of Downlaoded object.With a Downloaded object it's possible to see
	 * the state of downloads/uploads
	 * 
	 * @param o
	 *            object to read
	 * @param name
	 *            name of sender
	 * @param ois
	 *            ObjectInputStream for reads bytes from sender
	 * @param download
	 * @param fileReceive
	 *            Map consists of a integer key and a DownloadFile value. With
	 *            this it's possible to write on file's filestream
	 * 
	 * @see Downloaded
	 * @see DownloadFile
	 * 
	 * @throws IOException
	 *             if an I/O error occurs
	 */
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
			fileReceive.get(managementFile.getIdFile()).close();
			fileReceive.remove(managementFile.getIdFile());
		}

	}

}
