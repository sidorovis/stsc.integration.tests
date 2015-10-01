package stsc.integration.tests.algorithms.indices.rsi.stock;

import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.indices.rsi.stock.RsiRsi;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.integration.tests.helper.TestAlgorithmsHelper;
import stsc.signals.DoubleSignal;

public class RsiRsiTest {

	@Test
	public void testRsiRsi() throws Exception {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "aapl");

		final StockAlgoInitHelper rsiInit = new StockAlgoInitHelper("rsi", "aapl", stockInit.getStorage());
		rsiInit.getSettings().setDouble("P", 0.5);
		rsiInit.getSettings().setInteger("size", 10000);
		final RsiRsi rsi = new RsiRsi(rsiInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile(TestAlgorithmsHelper.resourceToPath("aapl.uf"));
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			rsi.process(day);
		}
		final Day lastDay = days.get(days.size() - 1);

		final double u = stockInit.getStorage().getStockSignal("aapl", "rsi_RsiEmaU", lastDay.getDate()).getContent(DoubleSignal.class).getValue();
		final double d = stockInit.getStorage().getStockSignal("aapl", "rsi_RsiEmaD", lastDay.getDate()).getContent(DoubleSignal.class).getValue();

		final double v = stockInit.getStorage().getStockSignal("aapl", "rsi", lastDay.getDate()).getContent(DoubleSignal.class).getValue();

		Assert.assertEquals(100 - 100 / (1 + u / d), v, Settings.doubleEpsilon);
	}
}
