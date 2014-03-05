package client_chat;

import java.io.Serializable;

public class ManagementFiles implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6442953142693862910L;
	private String fileName;
	private int fileSize;
	private int id;

	public ManagementFiles(String fileName, int id, int fileSize) {

		this.fileName = fileName;
		this.id = id;
		this.fileSize = fileSize;
	}

	public String getFileName() {
		return fileName;
	}

	public int getFileSize() {
		return fileSize;
	}

	public int getIdFile() {
		return id;
	}

}
