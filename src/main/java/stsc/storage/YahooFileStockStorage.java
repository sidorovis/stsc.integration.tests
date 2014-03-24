package stsc.storage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

import stsc.common.MarketDataContext;
import stsc.common.Stock;

public class YahooFileStockStorage extends ThreadSafeStockStorage {

	private class StockReadThread implements Runnable {

		private MarketDataContext marketDataContext;

		public StockReadThread(MarketDataContext marketDataContext) {
			this.marketDataContext = marketDataContext;
		}

		@Override
		public void run() {
			String task = marketDataContext.getTask();
			while (task != null) {
				Stock s = marketDataContext.getStockFromFileSystem(task);
				if (s != null)
					datafeed.put(s.getName(), new StockLock(s));
				task = marketDataContext.getTask();
			}
		}
	};

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/log4j2.xml");
	}

	private static Logger logger = LogManager.getLogger("YahooFileStorage");

	private MarketDataContext marketDataContext;
	private int readStockThreadSize = 4;

	public YahooFileStockStorage(MarketDataContext marketDataContext) throws ClassNotFoundException, IOException,
			InterruptedException {
		super();
		this.marketDataContext = marketDataContext;
		loadStocksFromFileSystem();
	}

	public YahooFileStockStorage() throws ClassNotFoundException, IOException, InterruptedException {
		super();
		this.marketDataContext = new MarketDataContext();
		loadStocksFromFileSystem();
	}

	private void loadStocksFromFileSystem() throws ClassNotFoundException, IOException, InterruptedException {
		logger.trace("created");
		loadFilteredDatafeed();
		logger.info("filtered datafeed header readed: {} stocks", marketDataContext.taskQueueSize());
		loadStocks();
		logger.info("stocks were loaded");
	}

	private void loadFilteredDatafeed() {
		File folder = new File(marketDataContext.filteredDataFolder);
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			String filename = file.getName();
			if (file.isFile() && filename.endsWith(".uf"))
				marketDataContext.addTask(filename.substring(0, filename.length() - 3));
		}
	}

	private void loadStocks() throws ClassNotFoundException, IOException, InterruptedException {
		StockReadThread stockReadThread = new StockReadThread(marketDataContext);
		List<Thread> threads = new ArrayList<Thread>();

		for (int i = 0; i < readStockThreadSize; ++i) {
			Thread newThread = new Thread(stockReadThread);
			threads.add(newThread);
			newThread.start();
		}

		for (Thread thread : threads) {
			thread.join();
		}
	}
}
