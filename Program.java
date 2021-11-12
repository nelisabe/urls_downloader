import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Program {

	private static int	parseArgs(String[] args) {
		int			count = 0;
		String[]	argument;

		if (args.length != 1)
			throw new RuntimeException("One argument required");
		try {
			argument = args[0].split("=");
		} catch (Exception ex) {
			throw new RuntimeException("Invalid argument");
		}
		if (!argument[0].equals("--threadsCount") || argument.length != 2) {
			throw new RuntimeException("Invalid argument");
		}
		try {
			count = Integer.parseInt(argument[1]);
		} catch (Exception ex) {
			throw new RuntimeException("Invalid argument");
		}
		return count;
	}

	public static ArrayList<String>	parseUrlsFile() throws Exception {
		ArrayList<String>	urls = new ArrayList<>();
		FileReader			fileReader;
		BufferedReader		bufferedReader;
		String				buffer;

		fileReader = new FileReader("files_urls.txt");
		bufferedReader = new BufferedReader(fileReader);
		do {
			buffer = bufferedReader.readLine();
			if (buffer != null) {
				urls.add(buffer.split(" ")[1]);
			}
		} while (buffer != null);
		fileReader.close();
		bufferedReader.close();
		return urls;
	}

	public static void	main(String[] args) {
		ArrayList<String>	urls;
		int					threadsCount;
		UrlsDownloader		urlsDownloader;

		try {
			threadsCount = parseArgs(args);
		} catch (Exception ex) {
			System.err.println("Error: " + ex.getMessage());
			return;
		}
		try {
			urls = parseUrlsFile();
		} catch (Exception ex) {
			System.err.println("Error: " + ex.getMessage());
			return;
		}
		urlsDownloader = new UrlsDownloader(urls, threadsCount);
		urlsDownloader.downloadFiles();
	}
}
