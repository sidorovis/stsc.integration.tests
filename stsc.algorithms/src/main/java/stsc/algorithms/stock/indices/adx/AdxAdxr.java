package stsc.algorithms.stock.indices.adx;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.StockSignal;
import stsc.signals.DoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class AdxAdxr extends StockAlgorithm {

	private int currentIndex = 0;
	private final Integer N;

	private final String adxAdxName;
	private final AdxAdx adxAdx;

	public AdxAdxr(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		N = init.getSettings().getIntegerSetting("N", 14).getValue();

		this.adxAdxName = init.getExecutionName() + "_AdxAdx";
		final StockAlgorithmInit adxAdxInit = new StockAlgorithmInit(adxAdxName, init, init.getSettings());
		this.adxAdx = new AdxAdx(adxAdxInit);
	}

	@Override
	public SignalsSerie<StockSignal> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return new LimitSignalsSerie<>(DoubleSignal.class, size);
	}

	@Override
	public void process(Day day) throws BadSignalException {
		adxAdx.process(day);
		final double current = getSignal(adxAdxName, day.getDate()).getSignal(DoubleSignal.class).getValue();
		double previous = 0.0;
		if (currentIndex <= N) {
			previous = getSignal(adxAdxName, 0).getSignal(DoubleSignal.class).getValue();
		} else {
			previous = getSignal(adxAdxName, currentIndex - N).getSignal(DoubleSignal.class).getValue();
		}
		currentIndex += 1;
		addSignal(day.getDate(), new DoubleSignal((current - previous) / 2.0));
	}
}