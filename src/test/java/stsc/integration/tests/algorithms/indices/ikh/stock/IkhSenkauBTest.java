package stsc.integration.tests.algorithms.indices.ikh.stock;

import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.indices.ikh.stock.IkhSenkauB;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.integration.tests.helper.TestAlgorithmsHelper;
import stsc.signals.DoubleSignal;

public class IkhSenkauBTest {

	@Test
	public void testIkhSenkauB() throws Exception {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "aapl");

		final int tm = 18;
		final int tl = 47;

		final StockAlgoInitHelper sBInit = new StockAlgoInitHelper("senkauB", "aapl", stockInit.getStorage());
		sBInit.getSettings().setInteger("TM", tm);
		sBInit.getSettings().setInteger("TL", tl);
		final IkhSenkauB senkauB = new IkhSenkauB(sBInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile(TestAlgorithmsHelper.resourceToPath("aapl.uf"));
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			senkauB.process(day);

			if (i - tm - tl + 1 >= aaplIndex) {
				double highMax = -Double.MIN_VALUE;
				double lowMin = Double.MAX_VALUE;
				for (int u = i - tm - tl + 1; u < i - tm + 1; ++u) {
					highMax = Math.max(highMax, days.get(u).getPrices().getHigh());
					lowMin = Math.min(lowMin, days.get(u).getPrices().getLow());
				}
				final double v = stockInit.getStorage().getStockSignal("aapl", "senkauB", day.getDate()).getContent(DoubleSignal.class).getValue();
				Assert.assertEquals((highMax + lowMin) / 2.0, v, Settings.doubleEpsilon);
			}
		}
	}
}
