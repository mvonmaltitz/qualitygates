package de.binarytree.plugins.qualitygates.checks.dependencyanalyzer.result;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DependencyProblemTypeTest {

	@Test
	public void testMatchAnyMatchesUndeclared() throws Exception {
		String logLine = "Used undeclared dependencies"; 
		assertEquals(DependencyProblemType.UNDECLARED, DependencyProblemType.matchAny(logLine)); 
	}
	@Test
	public void testMatchAnyMatchesUnused() throws Exception {
		String logLine = "Unused declared dependencies"; 
		assertEquals(DependencyProblemType.UNUSED, DependencyProblemType.matchAny(logLine)); 
	}

	@Test
	public void testMatchAnyDoesNotMatch() throws Exception {
		String logLine = "Something else"; 
		assertEquals(null, DependencyProblemType.matchAny(logLine)); 
	}

}
