package client_chat;

import java.io.FileOutputStream;
import java.io.IOException;

public class DownloadFile {
	private FileOutputStream fileStream;
	private int size = 0;

	public DownloadFile(FileOutputStream fileStream) {
		this.fileStream = fileStream;
	}

	public void write(byte[] buffer, int start, int len) throws IOException {
		fileStream.write(buffer, start, len);
		fileStream.flush();
	}

	public void close() throws IOException {
		fileStream.close();
	}

	public void incrementSize(int increment) {
		size += increment;
	}

	public int getSize() {
		return size;
	}

}
