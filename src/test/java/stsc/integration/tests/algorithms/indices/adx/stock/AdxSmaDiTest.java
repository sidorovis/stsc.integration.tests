package stsc.integration.tests.algorithms.indices.adx.stock;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.indices.adx.stock.AdxSmaDi;
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

public class AdxSmaDiTest {

	@Test
	public void testAdxSmaDi() throws BadAlgorithmException, ParseException, IOException, BadSignalException, URISyntaxException {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "aapl");

		final StockAlgoInitHelper adiInit = new StockAlgoInitHelper("smadi", "aapl", stockInit.getStorage());
		adiInit.getSettings().setInteger("size", 10000);
		final AdxSmaDi adiSma = new AdxSmaDi(adiInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile(TestAlgorithmsHelper.resourceToPath("aapl.uf"));
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			adiSma.process(day);

			final double adxMinusSma = adiInit.getStorage().getStockSignal("aapl", "smadi_AdxDi_MinusAdapter_Sma", day.getDate()).getContent(DoubleSignal.class).getValue();
			final double adxPlusSma = adiInit.getStorage().getStockSignal("aapl", "smadi_AdxDi_PlusAdapter_Sma", day.getDate()).getContent(DoubleSignal.class).getValue();

			final double aMinus = adiInit.getStorage().getStockSignal("aapl", "smadi", day.getDate()).getContent(ListOfDoubleSignal.class).getValues().get(0);
			final double aPlus = adiInit.getStorage().getStockSignal("aapl", "smadi", day.getDate()).getContent(ListOfDoubleSignal.class).getValues().get(1);

			Assert.assertEquals(adxMinusSma, aMinus, Settings.doubleEpsilon);
			Assert.assertEquals(adxPlusSma, aPlus, Settings.doubleEpsilon);
		}

	}
}
