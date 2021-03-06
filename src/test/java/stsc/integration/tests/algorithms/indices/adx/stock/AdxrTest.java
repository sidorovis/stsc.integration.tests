package stsc.integration.tests.algorithms.indices.adx.stock;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.indices.adx.stock.Adxr;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.stocks.Stock;
import stsc.common.stocks.united.format.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.integration.tests.helper.TestAlgorithmsHelper;
import stsc.signals.DoubleSignal;

public class AdxrTest {

	@Test
	public void testAdxr() throws ParseException, IOException, BadSignalException, BadAlgorithmException, URISyntaxException {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "aapl");

		final StockAlgoInitHelper adxInit = new StockAlgoInitHelper("adxr", "aapl", stockInit.getStorage());
		adxInit.getSettings().setInteger("size", 10000);
		final Adxr adxr = new Adxr(adxInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile(TestAlgorithmsHelper.resourceToPath("aapl"));
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			adxr.process(day);
		}

		final double previous = adxInit.getStorage().getStockSignal("aapl", "adxr_AdxAdx", days.size() - 15 - aaplIndex).getContent(DoubleSignal.class).getValue();
		final double current = adxInit.getStorage().getStockSignal("aapl", "adxr_AdxAdx", days.size() - 1 - aaplIndex).getContent(DoubleSignal.class).getValue();

		final double adxrValue = adxInit.getStorage().getStockSignal("aapl", "adxr", days.size() - 1 - aaplIndex).getContent(DoubleSignal.class).getValue();

		Assert.assertEquals((current - previous) / 2.0, adxrValue, Settings.doubleEpsilon);
	}
}
