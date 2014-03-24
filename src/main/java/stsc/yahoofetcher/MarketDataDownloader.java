package stsc.yahoofetcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

import stsc.common.MarketDataContext;

/**
 * Download Market Data from Yahoo API.
 * 
 */
public final class MarketDataDownloader {

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/log4j2.xml");
	}

	private static Logger logger = LogManager.getLogger("MarketDataDownloader");

	MarketDataContext marketDataContext = new MarketDataContext();
	static int downloadThreadSize = 8;
	static int stockNameMinLength = 5;
	static int stockNameMaxLength = 5;
	static boolean downloadExisted = false;
	static boolean downloadByPattern = false;
	static String startPattern = "a";
	static String endPattern = "zz";

	void generateNextElement(char[] generatedText, int currentIndex, int size) {
		for (char c = 'a'; c <= 'z'; ++c) {
			generatedText[currentIndex] = c;
			if (currentIndex == size - 1) {
				String newTask = new String(generatedText);
				marketDataContext.addTask(newTask);
			} else {
				generateNextElement(generatedText, currentIndex + 1, size);
			}
		}
	}

	void generateTasks(int taskLength) {
		char[] generatedText = new char[taskLength];
		generateNextElement(generatedText, 0, taskLength);
	}

	private void readProperties() throws IOException {
		FileInputStream in = new FileInputStream("config/yahoo_fetcher.ini");

		Properties p = new Properties();
		p.load(in);
		in.close();

		downloadThreadSize = Integer.parseInt(p.getProperty("thread.amount"));
		downloadExisted = Boolean.parseBoolean(p.getProperty("download_existed"));
		if (!downloadExisted) {
			downloadByPattern = Boolean.parseBoolean(p.getProperty("download_by_pattern"));
			if (downloadByPattern) {
				startPattern = p.getProperty("pattern.start");
				endPattern = p.getProperty("pattern.end");
			} else {
				stockNameMinLength = Integer.parseInt(p.getProperty("stock_name_min.size"));
				stockNameMaxLength = Integer.parseInt(p.getProperty("stock_name_max.size"));
			}
		}
	}

	MarketDataDownloader() throws InterruptedException, IOException {

		readProperties();

		DownloadThread downloadThread = new DownloadThread(marketDataContext);

		logger.trace("starting");

		if (downloadExisted) {
			File folder = new File(marketDataContext.dataFolder);
			File[] listOfFiles = folder.listFiles();
			for (File file : listOfFiles) {
				String filename = file.getName();
				if (file.isFile() && filename.endsWith(".uf"))
					marketDataContext.addTask(filename.substring(0, filename.length() - 3));
			}

		} else {
			if (downloadByPattern) {
				String pattern = startPattern;
				while (StringUtils.comparePatterns(pattern, endPattern) <= 0) {
					marketDataContext.addTask(pattern);
					pattern = StringUtils.nextPermutation(pattern);
				}
			} else {
				for (int i = stockNameMinLength; i <= stockNameMaxLength; ++i)
					generateTasks(i);
			}
		}
		logger.trace("tasks size: {}", marketDataContext.taskQueueSize());

		List<Thread> threads = new ArrayList<Thread>();

		for (int i = 0; i < downloadThreadSize; ++i) {
			Thread newThread = new Thread(downloadThread);
			threads.add(newThread);
			newThread.start();
		}

		logger.info("calculating threads started ( {} )", downloadThreadSize);

		for (Thread thread : threads) {
			thread.join();
		}

		logger.trace("finishing");
	}

	public static void main(String[] args) {
		try {
			new MarketDataDownloader();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
