package client_chat;

import java.io.Serializable;

/**
 * Encapsulates some information about a file like as name of file,size and id.
 * This class implements Serializable interface, so you can use an object of
 * this class with I/O operations
 * 
 * @author Francesco
 * 
 */
public class ManagementFiles implements Serializable {

	private static final long serialVersionUID = -6442953142693862910L;
	private String fileName;
	private int fileSize;
	private int id;

	/**
	 * 
	 * @param fileName
	 *            file's name
	 * @param id
	 * @param fileSize
	 *            file's size
	 */
	public ManagementFiles(String fileName, int id, int fileSize) {

		this.fileName = fileName;
		this.id = id;
		this.fileSize = fileSize;
	}

	/**
	 * 
	 * @return name of file
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * 
	 * @return size of file
	 */
	public int getFileSize() {
		return fileSize;
	}

	/**
	 * 
	 * @return file's id
	 */
	public int getIdFile() {
		return id;
	}

}
