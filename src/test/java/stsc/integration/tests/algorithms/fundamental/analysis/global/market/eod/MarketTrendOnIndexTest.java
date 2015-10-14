package stsc.integration.tests.algorithms.fundamental.analysis.global.market.eod;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.fundamental.analysis.global.market.eod.MarketTrendOnIndex;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.general.trading.BrokerImpl;
import stsc.integration.tests.helper.EodAlgoInitHelper;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.integration.tests.helper.TestAlgorithmsHelper;
import stsc.signals.DoubleSignal;
import stsc.storage.ThreadSafeStockStorage;

public class MarketTrendOnIndexTest {

	@Test
	public void marketTrendOnIndex() throws ParseException, IOException, BadAlgorithmException, BadSignalException, URISyntaxException {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "spy");

		final Stock spy = UnitedFormatStock.readFromUniteFormatFile(TestAlgorithmsHelper.resourceToPath("spy"));
		final ThreadSafeStockStorage stockStorage = new ThreadSafeStockStorage();
		stockStorage.updateStock(spy);
		final BrokerImpl broker = new BrokerImpl(stockStorage);
		final EodAlgoInitHelper eodInit = new EodAlgoInitHelper("mt", stockInit.getStorage(), broker);
		eodInit.getSettings().setInteger("size", 100000);
		final MarketTrendOnIndex mt = new MarketTrendOnIndex(eodInit.getInit());

		final int spyIndex = spy.findDayIndex(new LocalDate(1980, 9, 4).toDate());
		final ArrayList<Day> days = spy.getDays();

		for (int i = spyIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			broker.setToday(day.getDate());
			HashMap<String, Day> datafeed = new HashMap<>();
			datafeed.put("spy", day);
			mt.process(day.getDate(), datafeed);
		}

		double sum = 0;
		for (int i = spyIndex; i < days.size(); ++i) {
			final double value = stockInit.getStorage().getEodSignal("mt", i - spyIndex).getSignal(DoubleSignal.class).get().getValue();
			sum += value;
		}
		Assert.assertTrue(sum != 0.0);
	}
}
