package stsc.integration.tests.algorithms.indices.rsi.stock;

import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.indices.rsi.stock.RsiD;
import stsc.algorithms.indices.rsi.stock.RsiU;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.integration.tests.helper.TestAlgorithmsHelper;
import stsc.signals.DoubleSignal;

public class RsiURsiDTest {

	@Test
	public void testRsiURsiD() throws Exception {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "aapl");

		final StockAlgoInitHelper rsiDinit = new StockAlgoInitHelper("rsiD", "aapl", stockInit.getStorage());
		rsiDinit.getSettings().setInteger("size", 10000);
		final RsiD rsid = new RsiD(rsiDinit.getInit());

		final StockAlgoInitHelper rsiUinit = new StockAlgoInitHelper("rsiU", "aapl", stockInit.getStorage());
		rsiUinit.getSettings().setInteger("size", 10000);
		final RsiU rsiu = new RsiU(rsiUinit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile(TestAlgorithmsHelper.resourceToPath("aapl"));
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			rsid.process(day);
			rsiu.process(day);

			if (i > aaplIndex) {
				final double rsidValue = stockInit.getStorage().getStockSignal("aapl", "rsiD", i - aaplIndex).getContent(DoubleSignal.class).getValue();
				final double rsiuValue = stockInit.getStorage().getStockSignal("aapl", "rsiU", i - aaplIndex).getContent(DoubleSignal.class).getValue();

				final double closeNow = day.getPrices().getClose();
				final double closePrevious = days.get(i - 1).getPrices().getClose();

				final double v = closeNow - closePrevious;

				if (v >= 0.0) {
					Assert.assertEquals(v, rsiuValue, Settings.doubleEpsilon);
					Assert.assertEquals(0.0, rsidValue, Settings.doubleEpsilon);
				} else {
					Assert.assertEquals(0.0, rsiuValue, Settings.doubleEpsilon);
					Assert.assertEquals(-v, rsidValue, Settings.doubleEpsilon);
				}
			}
		}
	}
}
