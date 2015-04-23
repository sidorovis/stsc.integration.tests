package stsc.integration.tests.algorithms.stock.indices.adi;

import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.stock.indices.adi.AdiClv;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.stocks.Prices;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.DoubleSignal;

public class AdiClvTest {

	@Test
	public void testAdiClv() throws Exception {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "aapl");

		final StockAlgoInitHelper clvInit = new StockAlgoInitHelper("clv", "aapl", stockInit.getStorage());
		final AdiClv clv = new AdiClv(clvInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			clv.process(day);
			final Prices p = day.getPrices();
			final double denominator = p.getHigh() - p.getLow();
			final double expected = ((p.getClose() - p.getLow() - (p.getHigh() - p.getClose()))) / (denominator);
			Assert.assertEquals(expected, stockInit.getStorage().getStockSignal("aapl", "clv", day.getDate()).getContent(DoubleSignal.class)
					.getValue(), Settings.doubleEpsilon);
		}
	}

}