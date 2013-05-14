package de.binarytree.plugins.qualitygates.checks;

import static org.junit.Assert.*;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import org.junit.Before;
import org.junit.Test;

import de.binarytree.plugins.qualitygates.checks.FailingCheck.DescriptorImpl;
import static org.mockito.Mockito.*; 
public class FailingCheckTest {

	private FailingCheck check;
	@Before
	public void setUp() throws Exception {
		check = new FailingCheck(); 
	}

	@Test
	public void testDisplayName(){
		DescriptorImpl descriptor = new FailingCheck.DescriptorImpl(); 
		assertTrue(descriptor.getDisplayName().contains("fail")); 
	}
	@Test
	public void testDescription(){
		assertTrue(check.toString().contains("FAIL")); 
	}
	@Test
	public void testFailingWithValidBuild() {
		AbstractBuild build = mock(AbstractBuild.class); 
		assertEquals(Result.FAILURE, check.doCheck(build, null, null)); 
	}
	@Test
	public void testFailingWithNullBuild() {
		AbstractBuild build = null; 
		assertEquals(Result.FAILURE, check.doCheck(build, null, null)); 
	}

}

