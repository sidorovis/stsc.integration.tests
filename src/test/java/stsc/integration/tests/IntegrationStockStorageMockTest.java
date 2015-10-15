package stsc.integration.tests;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.storage.StockStorage;

public class IntegrationStockStorageMockTest {

	@Test
	public void testIntegrationStockStorageMock() throws URISyntaxException, IOException {
		final StockStorage stockStorage = IntegrationStockStorageMock.getStockStorage();
		Assert.assertEquals(49, stockStorage.getStockNames().size());
	}

	@Test
	public void testIntegrationStockStorageMockHasSpy() throws URISyntaxException, IOException {
		final StockStorage stockStorage = IntegrationStockStorageMock.getStockStorage();
		Assert.assertTrue(stockStorage.getStockNames().contains("spy"));
	}
}
