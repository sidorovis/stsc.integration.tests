package stsc.trading;

import java.io.IOException;
import java.text.ParseException;

import org.joda.time.LocalDate;

import stsc.algorithms.EodAlgorithmExecution;
import stsc.algorithms.EodSignal;
import stsc.algorithms.primitive.TestingEodAlgorithm;
import stsc.algorithms.primitive.TestingEodAlgorithmSignal;
import stsc.common.UnitedFormatStock;
import stsc.storage.ThreadSafeStockStorage;
import stsc.storage.SignalsStorage;
import stsc.storage.StockStorage;
import junit.framework.TestCase;

public class MarketSimulatorTest extends TestCase {
	private void csvReaderHelper(StockStorage ss, String stockName) throws IOException, ParseException {
		final String stocksFilePath = "./test_data/market_simulator_tests/";
		ss.updateStock(UnitedFormatStock.readFromCsvFile(stockName, stocksFilePath + stockName + ".csv"));
	}

	public void atestMarketSimulator() throws Exception {

		StockStorage ss = new ThreadSafeStockStorage();

		csvReaderHelper(ss, "aapl");
		csvReaderHelper(ss, "gfi");
		csvReaderHelper(ss, "oldstock");
		csvReaderHelper(ss, "no30");

		MarketSimulatorSettings settings = new MarketSimulatorSettings();
		settings.setStockStorage(ss);
		settings.setBroker(new Broker(ss));
		settings.setFrom("30-10-2013");
		settings.setTo("06-11-2013");
		settings.getEodExecutionsList().add(new EodAlgorithmExecution("e1", TestingEodAlgorithm.class.getName()));
		settings.getStockList().add("aapl");
		settings.getStockList().add("gfi");
		settings.getStockList().add("no30");
		settings.getStockList().add("unexisted_stock");
		settings.getStockList().add("oldstock");

		MarketSimulator marketSimulator = new MarketSimulator(settings);
		marketSimulator.simulate();
		assertEquals(1, marketSimulator.getTradeAlgorithms().size());

		TestingEodAlgorithm ta = (TestingEodAlgorithm) marketSimulator.getTradeAlgorithms().get("e1");
		assertEquals(ta.datafeeds.size(), 7);

		int[] expectedDatafeedSizes = { 1, 1, 2, 2, 3, 2, 0 };

		for (int i = 0; i < expectedDatafeedSizes.length; ++i)
			assertEquals(expectedDatafeedSizes[i], ta.datafeeds.get(i).size());

		SignalsStorage signalsStorage = marketSimulator.getSignalsStorage();
		EodSignal e1s1 = signalsStorage.getEodSignal("e1", new LocalDate(2013, 10, 30).toDate());
		assertEquals(true, e1s1.getClass() == TestingEodAlgorithmSignal.class);
		assertEquals("2013-10-30", ((TestingEodAlgorithmSignal) e1s1).dateRepresentation);

		EodSignal e1s2 = signalsStorage.getEodSignal("e1", new LocalDate(2013, 10, 31).toDate());
		assertEquals(true, e1s2.getClass() == TestingEodAlgorithmSignal.class);
		assertEquals("2013-10-31", ((TestingEodAlgorithmSignal) e1s2).dateRepresentation);

		EodSignal e1s3 = signalsStorage.getEodSignal("e1", new LocalDate(2013, 11, 01).toDate());
		assertEquals(true, e1s3.getClass() == TestingEodAlgorithmSignal.class);
		assertEquals("2013-11-01", ((TestingEodAlgorithmSignal) e1s3).dateRepresentation);

		EodSignal e1s6 = signalsStorage.getEodSignal("e1", new LocalDate(2013, 11, 05).toDate());
		assertEquals(true, e1s6.getClass() == TestingEodAlgorithmSignal.class);
		assertEquals("2013-11-05", ((TestingEodAlgorithmSignal) e1s6).dateRepresentation);

		assertNull(signalsStorage.getEodSignal("e1", new LocalDate(2013, 11, 6).toDate()));
		assertNull(signalsStorage.getEodSignal("e2", new LocalDate(2013, 11, 3).toDate()));
		assertNull(signalsStorage.getEodSignal("e1", new LocalDate(2013, 11, 29).toDate()));
	}

	public void testMarketSimulatorWithStatistics() throws Exception {
		StockStorage ss = new ThreadSafeStockStorage();

		ss.updateStock(UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf"));
		ss.updateStock(UnitedFormatStock.readFromUniteFormatFile("./test_data/adm.uf"));
		ss.updateStock(UnitedFormatStock.readFromUniteFormatFile("./test_data/spy.uf"));

		MarketSimulatorSettings settings = new MarketSimulatorSettings();
		settings.setStockStorage(ss);
		settings.setBroker(new Broker(ss));
		settings.setFrom("02-09-2013");
		settings.setTo("06-11-2013");
		settings.getEodExecutionsList().add(new EodAlgorithmExecution("e1", TestingEodAlgorithm.class.getName()));
		settings.getStockList().add("aapl");
		settings.getStockList().add("adm");
		settings.getStockList().add("spy");

		MarketSimulator marketSimulator = new MarketSimulator(settings);
		marketSimulator.simulate();

	}
}
