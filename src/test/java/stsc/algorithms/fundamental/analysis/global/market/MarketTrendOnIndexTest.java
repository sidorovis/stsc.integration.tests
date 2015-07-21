package stsc.algorithms.fundamental.analysis.global.market;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import org.joda.time.LocalDate;
import org.junit.Test;

import stsc.common.Day;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.common.storage.StockStorage;
import stsc.general.trading.BrokerImpl;
import stsc.integration.tests.helper.EodAlgoInitHelper;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.storage.ThreadSafeStockStorage;

public class MarketTrendOnIndexTest {

	@Test
	public void marketTrendOnIndex() throws ParseException, IOException {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "spy");

		final Stock spy = UnitedFormatStock.readFromUniteFormatFile("./test_data/spy.uf");
		final StockStorage stockStorage = new ThreadSafeStockStorage();
		stockStorage.updateStock(spy);
		final BrokerImpl broker = new BrokerImpl(stockStorage);
		final EodAlgoInitHelper eodInit = new EodAlgoInitHelper("mt", stockInit.getStorage(), broker);
//		final MarketTre
		
		final int spyIndex = spy.findDayIndex(new LocalDate(1980, 9, 4).toDate());
		final ArrayList<Day> days = spy.getDays();

		for (int i = spyIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			broker.setToday(day.getDate());
			HashMap<String, Day> datafeed = new HashMap<>();
			datafeed.put("spy", day);
//			eodOwsa.process(day.getDate(), datafeed);
		}

	}
}
