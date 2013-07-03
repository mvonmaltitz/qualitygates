package de.binarytree.plugins.qualitygates.checks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import org.junit.Before;
import org.junit.Test;

import de.binarytree.plugins.qualitygates.checks.FailingCheck.DescriptorImpl;

public class FailingCheckTest {

	private FailingCheck check;
	private DescriptorImpl descriptor;

	@Before
	public void setUp() throws Exception {
		descriptor = new FailingCheck.DescriptorImpl();
		check = new FailingCheck() {
			public DescriptorImpl getDescriptor() {
				return descriptor;
			}
		};

	}

	@Test
	public void testDisplayName() {
		assertTrue(descriptor.getDisplayName().contains("fail"));
	}

	@Test
	public void testDescription() {
		assertTrue(check.toString().contains("FAIL"));
	}

	@Test
	public void testFailingWithValidBuild() {
		AbstractBuild build = mock(AbstractBuild.class);
		assertEquals(Result.FAILURE, check.step(build, null, null).getResult());
	}

	@Test
	public void testFailingWithNullBuild() {
		AbstractBuild build = null;
		assertEquals(Result.FAILURE, check.step(build, null, null).getResult());
	}

}
