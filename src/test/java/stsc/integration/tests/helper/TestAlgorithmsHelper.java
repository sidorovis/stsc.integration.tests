package stsc.integration.tests.helper;

import java.io.InputStream;
import java.net.URISyntaxException;

import stsc.common.stocks.united.format.UnitedFormatHelper;

public class TestAlgorithmsHelper {

	final static public InputStream resourceToPath(final String resourcePath) throws URISyntaxException {
		return TestAlgorithmsHelper.class.getResourceAsStream(UnitedFormatHelper.toFilesystem(resourcePath).getFilename());
	}

}
