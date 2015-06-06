package stsc.integration.tests.algorithms.stock.indices.ikh;

import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.indices.ikh.stock.IkhTenkan;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.DoubleSignal;

public class IkhTenkanTest {

	@Test
	public void testIkhTenkan() throws Exception {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "aapl");

		final int ts = 5;

		final StockAlgoInitHelper tenkahInit = new StockAlgoInitHelper("tenkah", "aapl", stockInit.getStorage());
		tenkahInit.getSettings().setInteger("TS", ts);
		tenkahInit.getSettings().setInteger("size", 10000);
		final IkhTenkan tenkah = new IkhTenkan(tenkahInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			tenkah.process(day);

			double highMax = -Double.MIN_VALUE;
			double lowMin = Double.MAX_VALUE;
			int startIndex = aaplIndex;
			if (i - aaplIndex >= ts) {
				startIndex = i - ts + 1;
			}
			for (int u = startIndex; u <= i; ++u) {
				highMax = Math.max(highMax, days.get(u).getPrices().getHigh());
				lowMin = Math.min(lowMin, days.get(u).getPrices().getLow());
			}
			final double v = stockInit.getStorage().getStockSignal("aapl", "tenkah", day.getDate()).getContent(DoubleSignal.class)
					.getValue();
			Assert.assertEquals((highMax + lowMin) / 2.0, v, Settings.doubleEpsilon);
		}

	}
}
