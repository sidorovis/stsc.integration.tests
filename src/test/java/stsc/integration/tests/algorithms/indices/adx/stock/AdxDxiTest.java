package stsc.integration.tests.algorithms.indices.adx.stock;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Optional;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.indices.adx.stock.AdxDxi;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.integration.tests.helper.TestAlgorithmsHelper;
import stsc.signals.DoubleSignal;
import stsc.signals.ListOfDoubleSignal;

public class AdxDxiTest {

	@Test
	public void testAdxDxi() throws ParseException, BadAlgorithmException, IOException, BadSignalException, URISyntaxException {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "aapl");

		final StockAlgoInitHelper adxInit = new StockAlgoInitHelper("adx", "aapl", stockInit.getStorage());
		adxInit.getSettings().setInteger("size", 10000);
		final AdxDxi adxDxi = new AdxDxi(adxInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile(TestAlgorithmsHelper.resourceToPath("aapl.uf"));
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			adxDxi.process(day);

			final Optional<ListOfDoubleSignal> s = adxInit.getStorage().getStockSignal("aapl", "adx_adxSmaDiName", day.getDate()).getSignal(ListOfDoubleSignal.class);

			if (!s.isPresent()) {
				return;
			}

			final double m = s.get().getValues().get(0);
			final double p = s.get().getValues().get(1);

			final double r = adxInit.getStorage().getStockSignal("aapl", "adx", day.getDate()).getContent(DoubleSignal.class).getValue();

			if (Double.compare(p + m, 0.0) == 0) {
				Assert.assertEquals(100.0, r, Settings.doubleEpsilon);
			} else {
				Assert.assertEquals(100.0 * Math.abs(p - m) / (p + m), r, Settings.doubleEpsilon);
			}
		}
	}
}
