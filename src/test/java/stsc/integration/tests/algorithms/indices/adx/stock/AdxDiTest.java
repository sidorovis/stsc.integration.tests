package stsc.integration.tests.algorithms.indices.adx.stock;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.indices.adx.stock.AdxDi;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.stocks.Stock;
import stsc.common.stocks.united.format.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.integration.tests.helper.TestAlgorithmsHelper;
import stsc.signals.DoubleSignal;
import stsc.signals.ListOfDoubleSignal;

public class AdxDiTest {

	@Test
	public void testAdxDi() throws ParseException, BadAlgorithmException, IOException, BadSignalException, URISyntaxException {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "aapl");

		final StockAlgoInitHelper adiInit = new StockAlgoInitHelper("adi", "aapl", stockInit.getStorage());
		adiInit.getSettings().setInteger("size", 10000);
		final AdxDi adi = new AdxDi(adiInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile(TestAlgorithmsHelper.resourceToPath("aapl"));
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			adi.process(day);
		}

		final double trValue0 = adiInit.getStorage().getStockSignal("aapl", "adi_AdxTrueRange", 0).getContent(DoubleSignal.class).getValue();
		final double trValue1 = adiInit.getStorage().getStockSignal("aapl", "adi_AdxTrueRange", 1).getContent(DoubleSignal.class).getValue();

		final double adxDmValueMinus0 = adiInit.getStorage().getStockSignal("aapl", "adi_AdxDm", 0).getContent(ListOfDoubleSignal.class).getValues().get(0);
		final double adxDmValuePlus0 = adiInit.getStorage().getStockSignal("aapl", "adi_AdxDm", 0).getContent(ListOfDoubleSignal.class).getValues().get(1);

		final double adxDmValueMinus1 = adiInit.getStorage().getStockSignal("aapl", "adi_AdxDm", 1).getContent(ListOfDoubleSignal.class).getValues().get(0);
		final double adxDmValuePlus1 = adiInit.getStorage().getStockSignal("aapl", "adi_AdxDm", 1).getContent(ListOfDoubleSignal.class).getValues().get(1);

		Assert.assertEquals(adxDmValueMinus0 / trValue0, adiInit.getStorage().getStockSignal("aapl", "adi", 0).getContent(ListOfDoubleSignal.class).getValues().get(0),
				Settings.doubleEpsilon);
		Assert.assertEquals(adxDmValuePlus0 / trValue0, adiInit.getStorage().getStockSignal("aapl", "adi", 0).getContent(ListOfDoubleSignal.class).getValues().get(1),
				Settings.doubleEpsilon);

		Assert.assertEquals(adxDmValueMinus1 / trValue1, adiInit.getStorage().getStockSignal("aapl", "adi", 1).getContent(ListOfDoubleSignal.class).getValues().get(0),
				Settings.doubleEpsilon);
		Assert.assertEquals(adxDmValuePlus1 / trValue1, adiInit.getStorage().getStockSignal("aapl", "adi", 1).getContent(ListOfDoubleSignal.class).getValues().get(1),
				Settings.doubleEpsilon);
	}

}
