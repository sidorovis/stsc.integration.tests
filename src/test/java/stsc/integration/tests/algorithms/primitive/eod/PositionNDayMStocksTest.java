package stsc.integration.tests.algorithms.primitive.eod;

import java.io.File;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.Input;
import stsc.algorithms.primitive.eod.PositionNDayMStocks;
import stsc.common.FromToPeriod;
import stsc.common.Settings;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.EodExecution;
import stsc.common.algorithms.StockExecution;
import stsc.common.stocks.united.format.UnitedFormatStock;
import stsc.general.algorithm.AlgorithmConfigurationImpl;
import stsc.general.simulator.Simulator;
import stsc.general.simulator.SimulatorImpl;
import stsc.general.simulator.SimulatorConfigurationImpl;
import stsc.general.statistic.MetricType;
import stsc.general.statistic.Metrics;
import stsc.general.trading.TradeProcessorInit;
import stsc.integration.tests.helper.EodAlgoInitHelper;
import stsc.integration.tests.helper.TestAlgorithmsHelper;
import stsc.storage.ThreadSafeStockStorage;

public class PositionNDayMStocksTest {

	final private File resourceToPath(final String resourcePath) throws URISyntaxException {
		return new File(PositionNDayMStocksTest.class.getResource(resourcePath).toURI());
	}

	@Test
	public void testPositionNDayMStocksException() {
		try {
			EodAlgoInitHelper init = new EodAlgoInitHelper("eName");
			new PositionNDayMStocks(init.getInit());
			Assert.fail("PositionNDayMStocks algo ");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof BadAlgorithmException);
		}
	}

	@Test
	public void testPositionNDayMStocks() throws Exception {
		Metrics s = SimulatorImpl.fromFile(resourceToPath("simulator_tests/ndays.ini")).getMetrics();
		Assert.assertNotNull(s);
		Assert.assertEquals(550.0, s.getMetric(MetricType.period), Settings.doubleEpsilon);
		Assert.assertEquals(-20.773388, s.getMetric(MetricType.avGain), Settings.doubleEpsilon);
	}

	private void testHelper(String side) throws Exception {
		final FromToPeriod period = new FromToPeriod("01-01-2000", "31-12-2013");
		final ThreadSafeStockStorage stockStorage = new ThreadSafeStockStorage();
		stockStorage.updateStock(UnitedFormatStock.readFromUniteFormatFile(TestAlgorithmsHelper.resourceToPath("adm")));
		stockStorage.updateStock(UnitedFormatStock.readFromUniteFormatFile(TestAlgorithmsHelper.resourceToPath("aapl")));
		stockStorage.updateStock(UnitedFormatStock.readFromUniteFormatFile(TestAlgorithmsHelper.resourceToPath("spy")));
		stockStorage.updateStock(UnitedFormatStock.readFromUniteFormatFile(TestAlgorithmsHelper.resourceToPath("apa")));
		final TradeProcessorInit init = new TradeProcessorInit(stockStorage, period);

		final AlgorithmConfigurationImpl in = new AlgorithmConfigurationImpl();
		in.setString("e", "open");
		init.getExecutionsStorage().addStockExecution(new StockExecution("in", Input.class, in));

		final AlgorithmConfigurationImpl positionNDayMStocks = new AlgorithmConfigurationImpl();
		positionNDayMStocks.setInteger("n", 22);
		positionNDayMStocks.setInteger("m", 2);
		positionNDayMStocks.setString("side", side);
		positionNDayMStocks.addSubExecutionName("in");
		init.getExecutionsStorage().addEodExecution(new EodExecution("positionNDayMStocks", PositionNDayMStocks.class, positionNDayMStocks));

		final Simulator simulator = new SimulatorImpl();
		simulator.simulateMarketTrading(new SimulatorConfigurationImpl(0, init));
		final Metrics s = simulator.getMetrics();
		Assert.assertEquals(0.247656, s.getMetric(MetricType.freq), Settings.doubleEpsilon);
	}

	@Test
	public void testStaticPositionNDayMStocks() throws Exception {
		testHelper("long");
		testHelper("short");
	}
}
