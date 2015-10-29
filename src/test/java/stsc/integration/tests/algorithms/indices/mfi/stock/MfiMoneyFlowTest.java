package stsc.integration.tests.algorithms.indices.mfi.stock;

import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.indices.mfi.stock.MfiMoneyFlow;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.stocks.Stock;
import stsc.common.stocks.united.format.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.integration.tests.helper.TestAlgorithmsHelper;
import stsc.signals.DoubleSignal;

public class MfiMoneyFlowTest {

	@Test
	public void testMfiMoneyFlow() throws Exception {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "aapl");

		final StockAlgoInitHelper mfiMfInit = new StockAlgoInitHelper("mfiMf", "aapl", stockInit.getStorage());
		mfiMfInit.getSettings().setInteger("size", 10000);
		final MfiMoneyFlow mfiMf = new MfiMoneyFlow(mfiMfInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile(TestAlgorithmsHelper.resourceToPath("aapl"));
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			mfiMf.process(day);

			final double tp = stockInit.getStorage().getStockSignal("aapl", "mfiMf_mfiTp", day.getDate()).getContent(DoubleSignal.class).getValue();

			Assert.assertEquals(tp * day.getVolume(), mfiMfInit.getStorage().getStockSignal("aapl", "mfiMf", day.getDate()).getContent(DoubleSignal.class).getValue(),
					Settings.doubleEpsilon);
		}
	}

}
