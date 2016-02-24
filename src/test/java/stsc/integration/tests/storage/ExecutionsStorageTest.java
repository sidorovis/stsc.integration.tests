package stsc.integration.tests.storage;

import java.text.ParseException;

import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.indices.primitive.stock.Sma;
import stsc.algorithms.primitive.eod.TestingEodAlgorithm;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.EodExecutionInstance;
import stsc.common.algorithms.MutableAlgorithmConfiguration;
import stsc.common.algorithms.StockExecutionInstance;
import stsc.common.storage.StockStorage;
import stsc.general.algorithm.AlgorithmConfigurationImpl;
import stsc.general.trading.BrokerImpl;
import stsc.storage.ExecutionInstanceProcessor;
import stsc.storage.ExecutionInstancesStorage;
import stsc.storage.mocks.StockStorageMock;

public class ExecutionsStorageTest {

	private final StockStorage stockStorage = StockStorageMock.getStockStorage();

	@Test
	public void testExecutionsStorage() throws BadAlgorithmException {
		final MutableAlgorithmConfiguration smaSettings = new AlgorithmConfigurationImpl().addSubExecutionName("asd");

		final ExecutionInstancesStorage eStorage = new ExecutionInstancesStorage();

		eStorage.addStockExecution(new StockExecutionInstance("t2", Sma.class, smaSettings));
		eStorage.addEodExecution(new EodExecutionInstance("t1", TestingEodAlgorithm.class, new AlgorithmConfigurationImpl()));
		ExecutionInstanceProcessor es = eStorage.initialize(new BrokerImpl(stockStorage), stockStorage.getStockNames());

		Assert.assertEquals(1, es.getEodAlgorithmsSize());

		Assert.assertNotNull(es.getEodAlgorithm("t1"));
		Assert.assertNull(es.getEodAlgorithm("t2"));

		Assert.assertNotNull(es.getStockAlgorithm("t2", "aapl"));
		Assert.assertNotNull(es.getStockAlgorithm("t2", "adm"));
		Assert.assertNotNull(es.getStockAlgorithm("t2", "spy"));

		Assert.assertFalse(es.getStockAlgorithm("t1", "aapl").isPresent());
		Assert.assertFalse(es.getStockAlgorithm("t1", "adm").isPresent());
		Assert.assertFalse(es.getStockAlgorithm("t1", "spy").isPresent());

		Assert.assertFalse(es.getStockAlgorithm("t2", "non").isPresent());
	}

	@Test
	public void testExceptionOnInit() throws BadAlgorithmException, ParseException {
		final ExecutionInstancesStorage es = new ExecutionInstancesStorage();
		es.addStockExecution(new StockExecutionInstance("t2", Sma.class, new AlgorithmConfigurationImpl()));

		boolean throwed = false;
		try {
			es.initialize(new BrokerImpl(stockStorage), stockStorage.getStockNames());
		} catch (BadAlgorithmException e) {
			throwed = true;
		}
		Assert.assertEquals(true, throwed);
	}

}
