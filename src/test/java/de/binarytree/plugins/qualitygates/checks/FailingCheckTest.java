package de.binarytree.plugins.qualitygates.checks;

import static org.junit.Assert.*;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*; 
public class FailingCheckTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testFailingWithValidBuild() {
		AbstractBuild build = mock(AbstractBuild.class); 
		FailingCheck check = new FailingCheck(); 
		assertEquals(Result.FAILURE, check.doCheck(build, null, null)); 
	}
	@Test
	public void testFailingWithNullBuild() {
		AbstractBuild build = null; 
		FailingCheck check = new FailingCheck(); 
		assertEquals(Result.FAILURE, check.doCheck(build, null, null)); 
	}

}

