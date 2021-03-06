package stsc.integration.tests.algorithms;

import java.text.ParseException;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockExecutionInstance;
import stsc.general.algorithm.AlgorithmConfigurationImpl;
import stsc.integration.tests.helper.StockAlgoInitHelper;

public final class StockExecutionTest {

	@Test
	public void testStockAlgorithmExecutionConstructor() {
		boolean exception = false;
		try {
			new StockExecutionInstance("execution1", "algorithm1", new AlgorithmConfigurationImpl());
		} catch (BadAlgorithmException e) {
			exception = true;
		}
		Assert.assertTrue(exception);
	}

	@Test
	public void testExecution() throws BadAlgorithmException, ParseException {
		final StockExecutionInstance e3 = new StockExecutionInstance("e1", TestingStockAlgorithm.class.getName(), new AlgorithmConfigurationImpl());
		Assert.assertEquals(TestingStockAlgorithm.class.getName(), e3.getAlgorithmName());
		Assert.assertEquals("e1", e3.getExecutionName());

		try {
			StockAlgoInitHelper init = new StockAlgoInitHelper("e1", "aapl");
			final StockAlgorithm sai = e3.getInstance("e1", init.getStorage());
			Assert.assertTrue(sai instanceof TestingStockAlgorithm);
		} catch (BadAlgorithmException e) {
			e.printStackTrace();
		}
	}
}
