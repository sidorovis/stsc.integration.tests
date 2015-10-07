package stsc.integration.tests.algorithms.indices.primitive.stock;

import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.Input;
import stsc.algorithms.indices.primitive.stock.SmStDev;
import stsc.algorithms.indices.primitive.stock.Sma;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.integration.tests.helper.TestAlgorithmsHelper;
import stsc.signals.DoubleSignal;

public class SmStDevTest {

	@Test
	public void testSmStDev() throws Exception {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("testIn", "aapl");
		stockInit.getSettings().setString("e", "open");
		final Input inAlgo = new Input(stockInit.getInit());

		final StockAlgoInitHelper smaInit = new StockAlgoInitHelper("testSma", "aapl", stockInit.getStorage());
		smaInit.getSettings().setInteger("N", 7);
		smaInit.getSettings().setInteger("size", 10000);
		smaInit.getSettings().addSubExecutionName("testIn");
		final Sma sma = new Sma(smaInit.getInit());

		final StockAlgoInitHelper smStDevInit = new StockAlgoInitHelper("testStDev", "aapl", stockInit.getStorage());
		smStDevInit.getSettings().setInteger("N", 7);
		smStDevInit.getSettings().setInteger("size", 10000);
		smStDevInit.getSettings().addSubExecutionName("testIn").addSubExecutionName("testSma");
		final SmStDev smStDev = new SmStDev(smStDevInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile(TestAlgorithmsHelper.resourceToPath("aapl"));
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2013, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < aaplIndex + 8; ++i) { // days.size(); ++i) {
			final Day day = days.get(i);
			inAlgo.process(day);
			sma.process(day);
			smStDev.process(day);
		}

		Double sqrSum = 0.0;
		for (int i = aaplIndex; i < aaplIndex + 8; ++i) {
			final double openValue = days.get(i).getPrices().getOpen();
			final double smaValue = smaInit.getStorage().getStockSignal("aapl", "testSma", days.get(i).getDate()).getContent(DoubleSignal.class).getValue();
			final double sqr = Math.pow(openValue - smaValue, 2);
			sqrSum += sqr / 7;
		}

		final double sqrt = Math.sqrt(sqrSum);
		final double stDevValue = smStDevInit.getStorage().getStockSignal("aapl", "testStDev", days.get(aaplIndex + 7).getDate()).getContent(DoubleSignal.class).getValue();

		Assert.assertEquals(sqrt, stDevValue, Settings.doubleEpsilon);
	}
}
