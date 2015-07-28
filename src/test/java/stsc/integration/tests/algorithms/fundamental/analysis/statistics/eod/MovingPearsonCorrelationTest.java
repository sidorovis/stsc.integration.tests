package stsc.integration.tests.algorithms.fundamental.analysis.statistics.eod;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.fundamental.analysis.statistics.eod.MovingPearsonCorrelation;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.FromToPeriod;
import stsc.common.Settings;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.stocks.MemoryStock;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.common.storage.SignalsStorage;
import stsc.common.storage.StockStorage;
import stsc.general.simulator.Simulator;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.trading.BrokerImpl;
import stsc.general.trading.TradeProcessorInit;
import stsc.integration.tests.helper.EodAlgoInitHelper;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.MapKeyPairToDoubleSignal;
import stsc.storage.ThreadSafeStockStorage;
import stsc.storage.mocks.StockStorageMock;

public class MovingPearsonCorrelationTest {

	// @Test
	public void testCorrelationForStockWithItself() throws IOException, ParseException, BadAlgorithmException, BadSignalException {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "spy");

		final Stock spy = UnitedFormatStock.readFromUniteFormatFile("./test_data/spy.uf");
		final MemoryStock spyCopy = new MemoryStock("spy2");
		spyCopy.getDays().addAll(spy.getDays());
		final StockStorage stockStorage = new ThreadSafeStockStorage();
		stockStorage.updateStock(spy);
		final BrokerImpl broker = new BrokerImpl(stockStorage);
		final EodAlgoInitHelper eodInit = new EodAlgoInitHelper("mt", stockInit.getStorage(), broker);
		eodInit.getSettings().setInteger("size", 100000);
		final MovingPearsonCorrelation c = new MovingPearsonCorrelation(eodInit.getInit());

		final int spyIndex = spy.findDayIndex(new LocalDate(1980, 9, 4).toDate());
		final int spyCopyIndex = spyCopy.findDayIndex(new LocalDate(1980, 9, 4).toDate());
		final ArrayList<Day> spyDays = spy.getDays();
		final ArrayList<Day> spyCopyDays = spyCopy.getDays();

		for (int i = spyIndex, u = spyCopyIndex; i < spyDays.size() && u < spyCopyDays.size();) {
			final Day spyDay = spyDays.get(i);
			final Day spyCopyDay = spyCopyDays.get(u);
			HashMap<String, Day> datafeed = new HashMap<>();
			if (spyDay.getDate().after(spyCopyDay.getDate())) {
				broker.setToday(spyCopyDay.getDate());
				datafeed.put("spyCopy", spyCopyDay);
				c.process(spyCopyDay.getDate(), datafeed);
				++u;
			} else if (spyDay.getDate().before(spyCopyDay.getDate())) {
				broker.setToday(spyDay.getDate());
				datafeed.put("spy", spyDay);
				c.process(spyDay.getDate(), datafeed);
				++i;
			} else {
				broker.setToday(spyDay.getDate());
				datafeed.put("spyCopy", spyCopyDay);
				datafeed.put("spy", spyDay);
				c.process(spyCopyDay.getDate(), datafeed);
				++i;
				++u;
			}
			final MapKeyPairToDoubleSignal signal = stockInit.getStorage().getEodSignal("mt", spyDay.getDate()).getContent(MapKeyPairToDoubleSignal.class);
			final double correlation = signal.getValue("spy", "spyCopy");
			if (i - spyIndex < 11) {
				Assert.assertEquals(0.0, correlation, Settings.doubleEpsilon);
			} else {
				Assert.assertEquals(1.0, correlation, Settings.doubleEpsilon);
			}
		}

	}

	// @Test
	public void testCorrelationForSpyToAapl() throws IOException, ParseException, BadAlgorithmException, BadSignalException {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "spy");

		final Stock spy = UnitedFormatStock.readFromUniteFormatFile("./test_data/spy.uf");
		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final StockStorage stockStorage = new ThreadSafeStockStorage();
		stockStorage.updateStock(spy);
		stockStorage.updateStock(aapl);
		final BrokerImpl broker = new BrokerImpl(stockStorage);
		final EodAlgoInitHelper eodInit = new EodAlgoInitHelper("mt", stockInit.getStorage(), broker);
		eodInit.getSettings().setInteger("size", 100000);
		final MovingPearsonCorrelation c = new MovingPearsonCorrelation(eodInit.getInit());

		final int spyIndex = spy.findDayIndex(new LocalDate(1980, 9, 4).toDate());
		final int aaplIndex = aapl.findDayIndex(new LocalDate(1980, 9, 4).toDate());
		final ArrayList<Day> spyDays = spy.getDays();
		final ArrayList<Day> aaplDays = aapl.getDays();

		for (int i = spyIndex, u = aaplIndex; i < spyDays.size() && u < aaplDays.size();) {
			final Day spyDay = spyDays.get(i);
			final Day aaplDay = aaplDays.get(u);
			HashMap<String, Day> datafeed = new HashMap<>();
			if (spyDay.getDate().after(aaplDay.getDate())) {
				broker.setToday(aaplDay.getDate());
				datafeed.put("aapl", aaplDay);
				c.process(aaplDay.getDate(), datafeed);
				++u;
			} else if (spyDay.getDate().before(aaplDay.getDate())) {
				broker.setToday(spyDay.getDate());
				datafeed.put("spy", spyDay);
				c.process(spyDay.getDate(), datafeed);
				++i;
			} else {
				broker.setToday(spyDay.getDate());
				datafeed.put("aapl", aaplDay);
				datafeed.put("spy", spyDay);
				c.process(aaplDay.getDate(), datafeed);
				++i;
				++u;
			}
			if (((i - spyIndex) > 0) && ((u - aaplIndex) > 0)) {
				final MapKeyPairToDoubleSignal signal = stockInit.getStorage().getEodSignal("mt", spyDay.getDate()).getContent(MapKeyPairToDoubleSignal.class);
				final double correlation = signal.getValue("spy", "aapl");
				if (((i - spyIndex) > 10) && ((u - aaplIndex) > 10)) {
					Assert.assertNotEquals(0.0, correlation, Settings.doubleEpsilon);
				} else {
					Assert.assertEquals(0.0, correlation, Settings.doubleEpsilon);
				}
			} else {
				Assert.assertFalse(stockInit.getStorage().getEodSignal("mt", spyDay.getDate()).getValue().isPresent());
			}
		}
	}

	@Test
	public void testMovingPearsonCorrelationForSeveralStocks() throws BadAlgorithmException, ParseException, BadSignalException {
		final StockStorage stockStorage = StockStorageMock.getStockStorage();
		final String executionName = "correlation";
		final TradeProcessorInit tradeProcessorInit = new TradeProcessorInit(stockStorage, new FromToPeriod("01-01-1900", "01-01-2100"), //
				"EodExecutions = " + executionName + "\n" + //
						executionName + ".loadLine = ." + MovingPearsonCorrelation.class.getSimpleName() + "(size=10000i)\n");
		final SimulatorSettings simulatorSettings = new SimulatorSettings(0, tradeProcessorInit);
		final Simulator simulator = new Simulator(simulatorSettings);
		final SignalsStorage signalsStorage = simulator.getSignalsStorage();

		final int size = signalsStorage.getIndexSize(executionName);
		for (int i = 0; i < size; ++i) {
			Assert.assertTrue(signalsStorage.getEodSignal(executionName, i).getValue().isPresent());
		}
		Assert.assertEquals(3, signalsStorage.getEodSignal(executionName, size - 1).getContent(MapKeyPairToDoubleSignal.class).getValues().size());
	}

}
