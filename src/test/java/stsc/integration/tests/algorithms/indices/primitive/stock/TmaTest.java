package stsc.integration.tests.algorithms.indices.primitive.stock;

import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.Input;
import stsc.algorithms.indices.primitive.stock.Tma;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.integration.tests.helper.TestAlgorithmsHelper;
import stsc.signals.DoubleSignal;

public class TmaTest {

	@Test
	public void testTma() throws Exception {
		final StockAlgoInitHelper inInit = new StockAlgoInitHelper("testIn", "aapl");
		inInit.getSettings().setString("e", "open");
		final Input inAlgo = new Input(inInit.getInit());

		final StockAlgoInitHelper tmaInit = new StockAlgoInitHelper("testTma", "aapl", inInit.getStorage());
		tmaInit.getSettings().setDouble("P", 0.3);
		tmaInit.getSettings().addSubExecutionName("testIn");
		final Tma tma = new Tma(tmaInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile(TestAlgorithmsHelper.resourceToPath("aapl.uf"));
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			inAlgo.process(day);
			tma.process(day);

			if (i == aaplIndex) {
				final double open = inInit.getStorage().getStockSignal("aapl", "testIn", day.getDate()).getContent(DoubleSignal.class).getValue();
				final double emaV = inInit.getStorage().getStockSignal("aapl", "testTma_Dma_Ema", day.getDate()).getContent(DoubleSignal.class).getValue();
				final double dmaV = inInit.getStorage().getStockSignal("aapl", "testTma_Dma", day.getDate()).getContent(DoubleSignal.class).getValue();
				final double tmaV = inInit.getStorage().getStockSignal("aapl", "testTma", day.getDate()).getContent(DoubleSignal.class).getValue();

				Assert.assertEquals(open, emaV, Settings.doubleEpsilon);
				Assert.assertEquals(open, dmaV, Settings.doubleEpsilon);
				Assert.assertEquals(open, tmaV, Settings.doubleEpsilon);

			} else {
				final double open = inInit.getStorage().getStockSignal("aapl", "testIn", day.getDate()).getContent(DoubleSignal.class).getValue();

				final double pEmaV = inInit.getStorage().getStockSignal("aapl", "testTma_Dma_Ema", days.get(i - 1).getDate()).getContent(DoubleSignal.class).getValue();
				final double pDmaV = inInit.getStorage().getStockSignal("aapl", "testTma_Dma", days.get(i - 1).getDate()).getContent(DoubleSignal.class).getValue();
				final double pTmaV = inInit.getStorage().getStockSignal("aapl", "testTma", days.get(i - 1).getDate()).getContent(DoubleSignal.class).getValue();

				final double emaV = inInit.getStorage().getStockSignal("aapl", "testTma_Dma_Ema", day.getDate()).getContent(DoubleSignal.class).getValue();
				final double dmaV = inInit.getStorage().getStockSignal("aapl", "testTma_Dma", day.getDate()).getContent(DoubleSignal.class).getValue();
				final double tmaV = inInit.getStorage().getStockSignal("aapl", "testTma", day.getDate()).getContent(DoubleSignal.class).getValue();

				Assert.assertEquals(pEmaV * 0.7 + 0.3 * open, emaV, Settings.doubleEpsilon);
				Assert.assertEquals(pDmaV * 0.7 + 0.3 * emaV, dmaV, Settings.doubleEpsilon);
				Assert.assertEquals(pTmaV * 0.7 + 0.3 * dmaV, tmaV, Settings.doubleEpsilon);
			}

		}
	}
}
