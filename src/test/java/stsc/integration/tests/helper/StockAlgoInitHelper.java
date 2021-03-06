package stsc.integration.tests.helper;

import java.text.ParseException;

import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.storage.SignalsStorage;
import stsc.general.algorithm.AlgorithmConfigurationImpl;
import stsc.storage.SignalsStorageImpl;

public class StockAlgoInitHelper {

	private final SignalsStorage signalsStorage;

	private final AlgorithmConfigurationImpl settings;

	private final StockAlgorithmInit init;

	public StockAlgoInitHelper(String executionName, String stockName, SignalsStorage stockStorage) throws ParseException {
		this.signalsStorage = stockStorage;
		this.settings = new AlgorithmConfigurationImpl();
		this.init = new StockAlgorithmInit(executionName, stockStorage, stockName, settings);
	}

	public StockAlgoInitHelper(String executionName, String stockName) throws ParseException {
		this(executionName, stockName, new SignalsStorageImpl());
	}

	public SignalsStorage getStorage() {
		return signalsStorage;
	}

	public AlgorithmConfigurationImpl getSettings() {
		return settings;
	}

	public StockAlgorithmInit getInit() {
		return init;
	}
}
