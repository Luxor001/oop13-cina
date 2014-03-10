package client_chat;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Writes on a file stream and keeps track of the size of the file written
 * 
 * @author Francesco Cozzolino
 * 
 */
public class DownloadFile {

	private FileOutputStream fileStream;
	private int size = 0;

	/**
	 * 
	 * @param fileStream
	 */
	public DownloadFile(FileOutputStream fileStream) {
		this.fileStream = fileStream;
	}

	/**
	 * Writes a chunk of bytes using a file stream
	 * 
	 * @param buffer
	 *            bytes to write
	 * @param start
	 *            the start offset in the data
	 * @param len
	 *            the number of bytes to write
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public void write(byte[] buffer, int start, int len) throws IOException {
		fileStream.write(buffer, start, len);
		fileStream.flush();

	}

	/**
	 * Closes this file output stream and releases any system resources
	 * associated with this stream.
	 * 
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public void close() throws IOException {
		fileStream.close();

	}

	/**
	 * Increases the size of the file
	 * 
	 * @param step
	 */
	public void incrementSize(int step) {
		size += step;
	}

	public int getSize() {
		return size;
	}

}
