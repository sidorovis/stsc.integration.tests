package stsc.general.statistic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import stsc.general.strategy.TradingStrategy;

class SimpleSortedStrategies implements SortedStrategies {
	private final Map<String, TradingStrategy> storageByHashCode;
	private final SortedMap<Double, List<TradingStrategy>> storageByRating;

	SimpleSortedStrategies() {
		this.storageByHashCode = new HashMap<String, TradingStrategy>();
		this.storageByRating = new TreeMap<Double, List<TradingStrategy>>();
	}

	@Override
	public boolean add(Double rating, TradingStrategy value) {
		final String hashCode = value.stringHashCode();
		if (storageByHashCode.containsKey(hashCode)) {
			return false;
		}
		if (storageByRating.containsKey(rating)) {
			final List<TradingStrategy> ratingSet = storageByRating.get(rating);
			ratingSet.add(value);
			storageByHashCode.put(hashCode, value);
		} else {
			final List<TradingStrategy> newValue = new ArrayList<>();
			newValue.add(value);
			storageByRating.put(rating, newValue);
		}
		return true;
	}

	@Override
	public TradingStrategy deleteLast() {
		if (storageByRating.isEmpty()) {
			return null;
		}
		final List<TradingStrategy> strategies = storageByRating.get(storageByRating.firstKey());
		final TradingStrategy result = strategies.remove(strategies.size() - 1);
		storageByHashCode.remove(result.stringHashCode());
		if (strategies.isEmpty()) {
			storageByRating.remove(storageByRating.firstKey());
		}
		return result;
	}

	@Override
	public int size() {
		int sum = 0;
		for (Map.Entry<Double, List<TradingStrategy>> i : storageByRating.entrySet()) {
			sum += i.getValue().size();
		}
		return sum;
	}

	@Override
	public SortedMap<Double, List<TradingStrategy>> getValues() {
		return storageByRating;
	}

}