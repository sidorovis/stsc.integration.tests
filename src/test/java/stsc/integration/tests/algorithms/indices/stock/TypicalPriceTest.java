package stsc.integration.tests.algorithms.indices.stock;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.indices.stock.TypicalPrice;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.integration.tests.helper.TestAlgorithmsHelper;
import stsc.signals.DoubleSignal;

public class TypicalPriceTest {

	@Test
	public void testTypicalPrice() throws ParseException, IOException, BadAlgorithmException, BadSignalException, URISyntaxException {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "aapl");

		final StockAlgoInitHelper tpInit = new StockAlgoInitHelper("tp", "aapl", stockInit.getStorage());
		tpInit.getSettings().setInteger("size", 10000);
		final TypicalPrice tp = new TypicalPrice(tpInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile(TestAlgorithmsHelper.resourceToPath("aapl"));
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			tp.process(day);

			final double typicalPrice = (day.getPrices().getHigh() + day.getPrices().getLow() + day.getPrices().getClose()) / 3;
			Assert.assertEquals(typicalPrice, tpInit.getStorage().getStockSignal("aapl", "tp", day.getDate()).getContent(DoubleSignal.class).getValue(), Settings.doubleEpsilon);
		}
	}
}
