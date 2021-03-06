package stsc.integration.tests.algorithms.indices.macd.stock;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.Input;
import stsc.algorithms.indices.macd.stock.MacdSignal;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.stocks.Stock;
import stsc.common.stocks.united.format.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.integration.tests.helper.TestAlgorithmsHelper;
import stsc.signals.DoubleSignal;

public class MacdSignalTest {

	@Test
	public void testMacdMacd() throws ParseException, BadAlgorithmException, IOException, BadSignalException, URISyntaxException {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "aapl");
		final Input in = new Input(stockInit.getInit());

		final StockAlgoInitHelper macdInit = new StockAlgoInitHelper("macd", "aapl", stockInit.getStorage());
		macdInit.getSettings().addSubExecutionName("in");
		final MacdSignal macd = new MacdSignal(macdInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile(TestAlgorithmsHelper.resourceToPath("aapl"));
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			in.process(day);
			macd.process(day);

			final double sma = stockInit.getStorage().getStockSignal("aapl", "macd_Sma", day.getDate()).getContent(DoubleSignal.class).getValue();
			final double v = stockInit.getStorage().getStockSignal("aapl", "macd", day.getDate()).getContent(DoubleSignal.class).getValue();

			Assert.assertEquals(sma, v, Settings.doubleEpsilon);
		}
	}
}
