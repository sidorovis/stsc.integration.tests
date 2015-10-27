package stsc.integration.tests.algorithms.strategy_e1;

import java.text.ParseException;

import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.fundamental.analysis.statistics.eod.LeftToRightMovingPearsonCorrelation;
import stsc.algorithms.strategy_e1.TradingSanguinaria;
import stsc.common.BadSignalException;
import stsc.common.FromToPeriod;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.storage.StockStorage;
import stsc.general.simulator.Simulator;
import stsc.general.simulator.SimulatorImpl;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.simulator.SimulatorSettingsImpl;
import stsc.general.trading.TradeProcessorInit;
import stsc.integration.tests.IntegrationStockStorageMock;

public class TradingSanguinariaTest {

	private final static StockStorage stockStorage = IntegrationStockStorageMock.getStockStorage();

	@Test
	public void testTradingSanguinaria() throws ParseException, BadAlgorithmException, BadSignalException {
		final String executionsConfig = //
		"EodExecutions = test\n" + //
				"test.loadLine = ." + TradingSanguinaria.class.getSimpleName() + "( " + //
				LeftToRightMovingPearsonCorrelation.class.getSimpleName() + "(N=104i, LE=spy, ALLR=true)" + //
				" )\n";
		final SimulatorSettings simulatorSettings = new SimulatorSettingsImpl(0, new TradeProcessorInit(stockStorage, new FromToPeriod("01-01-2000", "01-01-2020"), executionsConfig));
		final Simulator simulator = new SimulatorImpl();
		simulator.simulateMarketTrading(simulatorSettings);
		Assert.assertNotNull(simulator.getMetrics());
	}

}
