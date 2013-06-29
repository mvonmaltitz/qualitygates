package de.binarytree.plugins.qualitygates.checks.dependencyanalyzer.parser;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;

/**
 * Base class for parser tests.<br>
 * contains utility methods for file manipulations<br>
 * 
 * 
 * @author Vincent Sellier
 * 
 */
public class AbstractParserTestUtils {

	/**
	 * return a file searching in the classpath
	 * 
	 * @param fileName
	 *            the name of the file relative to the classpath
	 * @return the file
	 * @throws Exception
	 */
	protected File getFile(String fileName) throws Exception {
		URL fileURL = this.getClass().getResource(fileName);
		File file = new File(fileURL.toURI());
		return file;
	}

}
