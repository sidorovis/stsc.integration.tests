package stsc.integration.tests.patterns.stock;

import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.indices.stock.StockMarketCycle;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.signals.SignalContainer;
import stsc.common.stocks.Stock;
import stsc.common.stocks.united.format.UnitedFormatStock;
import stsc.common.storage.SignalsStorage;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.integration.tests.helper.TestAlgorithmsHelper;
import stsc.signals.DoubleSignal;

public class StockMarketCycleTest {

	private void testHelper(String sn) throws Exception {
		final StockAlgoInitHelper smcInit = new StockAlgoInitHelper("smc", sn);
		final StockMarketCycle smc = new StockMarketCycle(smcInit.getInit());

		final Stock stock = UnitedFormatStock.readFromUniteFormatFile(TestAlgorithmsHelper.resourceToPath(sn));
		final int stockIndex = stock.findDayIndex(new LocalDate(1990, 9, 4).toDate());
		final ArrayList<Day> days = stock.getDays();

		final SignalsStorage ss = smcInit.getStorage();

		for (int i = stockIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			smc.process(day);

			final SignalContainer<?> sc = ss.getStockSignal(sn, "smc", day.getDate());

			if (i - stockIndex < 5) {
				Assert.assertNotNull(sc);
				Assert.assertEquals(0.0, sc.getContent(DoubleSignal.class).getValue(), Settings.doubleEpsilon);
			} else {
				Assert.assertNotNull(sc);
			}
		}
	}

	@Test
	public void testStockMarketCycle() throws Exception {
		testHelper("aa");
		testHelper("aapl");
		testHelper("adm");
		testHelper("apa");
		testHelper("spy");
	}

}
