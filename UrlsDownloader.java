import java.util.ArrayList;

public class UrlsDownloader {
	UrlsDownloader(ArrayList<String> urls, int threadsCount) {
		_urls = urls;
		_urlsArraySize = _urls.size();
		_threadsCount = threadsCount;
	}

	private UrlDownloaderThread[]	startThreads() {
		UrlDownloaderThread[]	threads = new UrlDownloaderThread[_threadsCount];
		PrintThreadStatus		printer = new PrintThreadStatus();
		int						indexBegin = 0;
		int						indexEnd = 0;
		int						numberOfParts = _threadsCount;
		int						elementsCount;

		for (int i = 0; numberOfParts != 0; ++i, --numberOfParts) {
			elementsCount = (_urlsArraySize - indexBegin) / numberOfParts;
			indexEnd = indexBegin + elementsCount;
			threads[i] = new UrlDownloaderThread(_urls, indexBegin,
					indexEnd, i + 1, printer);
			threads[i].start();
			indexBegin += elementsCount;
		}
		return threads;
	}

	public void downloadFiles() {
		UrlDownloaderThread[]	threads;

		threads = startThreads();
		for (int i = 0; i < _threadsCount; ++i) {
			try {
				threads[i].join();
			} catch (Exception ex) {
				System.err.println("Error thread: " + ex.getMessage());
				ex.printStackTrace();
			}
		}
	}

	private final int				_threadsCount;
	private final ArrayList<String>	_urls;
	private final int				_urlsArraySize;
}
