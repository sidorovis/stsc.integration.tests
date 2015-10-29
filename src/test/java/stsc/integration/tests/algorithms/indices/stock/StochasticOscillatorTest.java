package stsc.integration.tests.algorithms.indices.stock;

import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.indices.stock.StochasticOscillator;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.stocks.Stock;
import stsc.common.stocks.united.format.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.integration.tests.helper.TestAlgorithmsHelper;
import stsc.signals.DoubleSignal;

public class StochasticOscillatorTest {

	@Test
	public void testStochasticOscillator() throws Exception {
		final StockAlgoInitHelper init = new StockAlgoInitHelper("in", "aapl");

		final StockAlgoInitHelper soInit = new StockAlgoInitHelper("so", "aapl", init.getStorage());
		final StochasticOscillator so = new StochasticOscillator(soInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile(TestAlgorithmsHelper.resourceToPath("aapl"));
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			so.process(day);

			final double ct = day.getPrices().getClose();
			final double ln = init.getStorage().getStockSignal("aapl", "so_Ln", day.getDate()).getContent(DoubleSignal.class).getValue();
			final double hn = init.getStorage().getStockSignal("aapl", "so_Hn", day.getDate()).getContent(DoubleSignal.class).getValue();
			final double v = init.getStorage().getStockSignal("aapl", "so", day.getDate()).getContent(DoubleSignal.class).getValue();

			Assert.assertEquals(100.0 * (ct - ln) / (hn - ln), v, Settings.doubleEpsilon);
		}
	}
}
