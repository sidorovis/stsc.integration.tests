package stsc.integration.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import stsc.common.stocks.UnitedFormatHelper;
import stsc.common.stocks.UnitedFormatStock;
import stsc.common.storage.StockStorage;
import stsc.storage.ThreadSafeStockStorage;

/**
 * This is separated implementation of {@link StockStorage} with big amount of
 * stocks for possibility to develop complicated algorithms. Contain a lot of
 * stocks inside.
 * 
 */
public final class IntegrationStockStorageMock {

	private static ThreadSafeStockStorage stockStorage = null;

	private IntegrationStockStorageMock() {
		// hidden constructor
	}

	final static private File resourceToFile(final String resourcePath) throws URISyntaxException {
		return new File(IntegrationStockStorageMock.class.getResource(resourcePath).toURI());
	}

	public static synchronized StockStorage getStockStorage() {
		if (stockStorage == null) {
			stockStorage = new ThreadSafeStockStorage();
			try {
				final File folder = resourceToFile("./IntegrationStockStorageMockData");
				final File[] listOfFiles = folder.listFiles();
				for (File file : listOfFiles) {
					final String filename = file.getName();
					if (file.isFile() && filename.startsWith(UnitedFormatHelper.getPrefix()) && filename.endsWith(UnitedFormatHelper.getExtension())) {
						try (InputStream is = new FileInputStream(file)) {
							stockStorage.updateStock(UnitedFormatStock.readFromUniteFormatFile(is));
						}
					}
				}
			} catch (URISyntaxException | IOException e) {
				// do nothing, it only for test purposes and we have special
				// junit test that cover errors in here
			}
		}
		return stockStorage;
	}

}
