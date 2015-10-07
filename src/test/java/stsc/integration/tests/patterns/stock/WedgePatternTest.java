package stsc.integration.tests.patterns.stock;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.Input;
import stsc.algorithms.patterns.stock.WedgePattern;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.signals.SignalContainer;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.common.storage.SignalsStorage;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.integration.tests.helper.TestAlgorithmsHelper;
import stsc.signals.DoubleSignal;
import stsc.signals.ListOfDoubleSignal;

public class WedgePatternTest {

	private void testHelper(String sn) throws Exception {
		final StockAlgoInitHelper iniHigh = new StockAlgoInitHelper("inh", sn);
		iniHigh.getSettings().setString("e", "high");
		final Input inHigh = new Input(iniHigh.getInit());

		final StockAlgoInitHelper iniLow = new StockAlgoInitHelper("inl", sn, iniHigh.getStorage());
		iniHigh.getSettings().setString("e", "low");
		final Input inLow = new Input(iniLow.getInit());

		final StockAlgoInitHelper wpInit = new StockAlgoInitHelper("wp", sn, iniLow.getStorage());
		wpInit.getSettings().addSubExecutionName("inh");
		wpInit.getSettings().addSubExecutionName("inl");
		final WedgePattern wp = new WedgePattern(wpInit.getInit());

		final Stock stock = UnitedFormatStock.readFromUniteFormatFile(TestAlgorithmsHelper.resourceToPath(sn));
		final int stockIndex = stock.findDayIndex(new LocalDate(1990, 9, 4).toDate());
		final ArrayList<Day> days = stock.getDays();

		final SignalsStorage ss = iniHigh.getStorage();

		for (int i = stockIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			inHigh.process(day);
			inLow.process(day);
			wp.process(day);

			final SignalContainer<?> sc = ss.getStockSignal(sn, "wp", day.getDate());
			if (!sc.isPresent()) {
				continue;
			}
			final List<Double> v = sc.getContent(ListOfDoubleSignal.class).getValues();

			final double x = v.get(1);
			final double y = v.get(2);

			Assert.assertTrue(x >= (i - stockIndex + 2));
			Assert.assertTrue(x <= (i - stockIndex + 3));

			final double maxLineStdDev = ss.getStockSignal(sn, "wp_Max", day.getDate()).getContent(DoubleSignal.class).getValue();
			final double minLineStdDev = ss.getStockSignal(sn, "wp_Min", day.getDate()).getContent(DoubleSignal.class).getValue();

			final List<Double> lcMax = ss.getStockSignal(sn, "wp_Max_Lss", day.getDate()).getContent(ListOfDoubleSignal.class).getValues();
			final double eY = lcMax.get(0) + lcMax.get(1) * x;

			final List<Double> lcMin = ss.getStockSignal(sn, "wp_Min_Lss", day.getDate()).getContent(ListOfDoubleSignal.class).getValues();

			Assert.assertEquals(eY, y, Settings.doubleEpsilon);

			Assert.assertTrue(0.5 > maxLineStdDev);
			Assert.assertTrue(0.5 > minLineStdDev);

			if (v.get(0) < 0.0) {
				Assert.assertTrue(lcMax.get(1) < -0.05);
				Assert.assertTrue(lcMin.get(1) < -0.05);
			} else {
				Assert.assertTrue(lcMax.get(1) > 0.05);
				Assert.assertTrue(lcMin.get(1) > 0.05);
			}
		}
	}

	@Test
	public void testWedgePattern() throws Exception {
		testHelper("aa");
		testHelper("aapl");
		testHelper("adm");
		testHelper("apa");
		testHelper("spy");
	}
}
