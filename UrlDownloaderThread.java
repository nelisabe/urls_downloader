import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;

public class UrlDownloaderThread extends Thread {
	UrlDownloaderThread(ArrayList<String> urls, int indexBegin,
						int indexEnd, int threadId, PrintThreadStatus printer) {
		_urls = urls;
		_indexBegin = indexBegin;
		_indexEnd = indexEnd;
		_printer = printer;
		_threadId = threadId;
	}

	private URL	openUrl(String urlString) {
		URL	url = null;

		try {
			url = new URL(urlString);
		} catch (Exception ex) {
			System.err.println("Error: " + ex.getMessage());
		}
		return url;
	}

	private URLConnection		openUrlConnection(URL url) {
		URLConnection	urlConnection = null;

		try {
			urlConnection = url.openConnection();
			urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
		} catch (Exception ex) {
			System.err.println("Error: " + ex.getMessage());
		}
		return urlConnection;
	}

	private InputStream			openUrlStream(URLConnection urlConnection) {
		InputStream	stream = null;

		try {
			stream = urlConnection.getInputStream();
		} catch (Exception ex) {
			System.err.println("Error: " + ex.getMessage());
		}
		return stream;
	}

	private void				closeInputStream(InputStream stream) {
		try {
			stream.close();
		} catch (Exception ex) {
			System.err.println("Error: " + ex.getMessage());
		}
	}

	private ReadableByteChannel	initRBC(InputStream stream) {
		ReadableByteChannel	readableByteChannel = null;

		try {
			readableByteChannel = Channels.newChannel(stream);
		} catch (Exception ex) {
			System.err.println("Error: " + ex.getMessage());
		}
		return readableByteChannel;
	}

	private void				closeRBC(ReadableByteChannel readableByteChannel) {
		try {
			readableByteChannel.close();
		} catch (Exception ex) {
			System.err.println("Error: " + ex.getMessage());
		}
	}

	private FileOutputStream	openFileOutputStream(String fileName) {
		FileOutputStream	fileOutputStream = null;

		try {
			fileOutputStream = new FileOutputStream(fileName);
		} catch (Exception ex) {
			System.err.println("Error: " + ex.getMessage());
		}
		return fileOutputStream;
	}

	private void			closeFileOutputStream(FileOutputStream stream) {
		try {
			stream.close();
		} catch (Exception ex) {
			System.err.println("Error: " + ex.getMessage());
		}
	}

	private String				parseFilename(String url) {
		String	fileName;

		fileName = url.substring(url.lastIndexOf('/') + 1);
		return fileName;
	}

	public void		run() {
		URL					url;
		URLConnection		urlConnection;
		InputStream			urlStream;
		ReadableByteChannel	readableByteChannel;
		FileOutputStream	fileOutputStream;

		for (int i = _indexBegin; i < _indexEnd; ++i) {
			_printer.printThreadStatus(_threadId, i + 1);
			if ((url = openUrl(_urls.get(i))) == null) {
				continue;
			}
			if ((urlConnection = openUrlConnection(url)) == null) {
				continue;
			}
			if ((urlStream = openUrlStream(urlConnection)) == null) {
				continue;
			}
			if ((readableByteChannel = initRBC(urlStream)) == null) {
				closeInputStream(urlStream);
				continue;
			}
			if ((fileOutputStream = openFileOutputStream(parseFilename(_urls.get(i))))
					== null) {
				closeInputStream(urlStream);
				closeRBC(readableByteChannel);
				continue;
			}
			try {
				fileOutputStream.getChannel().transferFrom(readableByteChannel,
						0, Long.MAX_VALUE);
			} catch (Exception ex) {
				System.err.println("Error: " + ex.getMessage());
			} finally {
				closeInputStream(urlStream);
				closeRBC(readableByteChannel);
				closeFileOutputStream(fileOutputStream);
			}
		}
	}

	private final ArrayList<String>	_urls;
	private final int				_indexBegin;
	private final int				_indexEnd;
	private final int				_threadId;
	private final PrintThreadStatus	_printer;
}
