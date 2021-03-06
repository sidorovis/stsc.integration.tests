package stsc.integration.tests.algorithms.indices.ikh.stock;

import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.indices.ikh.stock.IkhSenkauA;
import stsc.common.Day;
import stsc.common.stocks.Stock;
import stsc.common.stocks.united.format.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.integration.tests.helper.TestAlgorithmsHelper;

public class IkhSenkauATest {

	@Test
	public void testIkhTenkan() throws Exception {
		testHelperIkhTenkan(6, 12);
		testHelperIkhTenkan(12, 6);
		testHelperIkhTenkan(9, 26);
	}

	private void testHelperIkhTenkan(final int ts, final int tm) throws Exception {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "aapl");
		final StockAlgoInitHelper sAInit = new StockAlgoInitHelper("senkauA", "aapl", stockInit.getStorage());
		sAInit.getSettings().setInteger("TS", ts);
		sAInit.getSettings().setInteger("TM", tm);
		final IkhSenkauA senkauA = new IkhSenkauA(sAInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile(TestAlgorithmsHelper.resourceToPath("aapl"));
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			senkauA.process(day);
			Assert.assertNotNull(stockInit.getStorage().getStockSignal("aapl", "senkauA", day.getDate()));
		}
	}
}
