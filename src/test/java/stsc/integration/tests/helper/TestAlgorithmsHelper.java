package stsc.integration.tests.helper;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Date;

import stsc.algorithms.AlgorithmSettingsImpl;
import stsc.common.FromToPeriod;
import stsc.common.stocks.UnitedFormatHelper;

public class TestAlgorithmsHelper {

	public static FromToPeriod getPeriod() {
		try {
			return new FromToPeriod("01-01-2000", "31-12-2009");
		} catch (Exception e) {
		}
		return new FromToPeriod(new Date(), new Date());
	}

	public static AlgorithmSettingsImpl getSettings() {
		return new AlgorithmSettingsImpl(getPeriod());
	}

	final static public InputStream resourceToPath(final String resourcePath) throws URISyntaxException {
		return TestAlgorithmsHelper.class.getResourceAsStream(UnitedFormatHelper.toFilesystem(resourcePath).getFilename());
	}

}
