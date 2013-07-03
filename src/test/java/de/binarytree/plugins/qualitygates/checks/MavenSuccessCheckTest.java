package de.binarytree.plugins.qualitygates.checks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import org.junit.Before;
import org.junit.Test;

import de.binarytree.plugins.qualitygates.checks.MavenSuccessCheck.MavenSuccessCheckDescriptor;
import de.binarytree.plugins.qualitygates.result.GateStepReport;

public class MavenSuccessCheckTest {

	private MavenSuccessCheck check;
	private AbstractBuild build;
	private MavenSuccessCheckDescriptor descriptor;

	@Before
	public void setUp() {
		descriptor = new MavenSuccessCheck.MavenSuccessCheckDescriptor();
		build = mock(AbstractBuild.class, RETURNS_DEEP_STUBS);
		check = new MavenSuccessCheck() {
			public MavenSuccessCheckDescriptor getDescriptor() {
				return descriptor;
			}
		};
	}

	@Test
	public void testDisplayName() {
		assertTrue(descriptor.getDisplayName().contains("Maven"));
	}

	@Test
	public void testBuildSuccess() {
		this.testBuildResult(Result.SUCCESS);
	}

	@Test
	public void testBuildIsNull() {
		build = null;
		GateStepReport result = check.step(build, null, null);
		assertEquals(Result.FAILURE, result.getResult());
	}

	@Test
	public void testBuildUnstable() {
		this.testBuildResult(Result.UNSTABLE);
	}

	@Test
	public void testBuildFailed() {
		this.testBuildResult(Result.FAILURE);
	}

	public void testBuildResult(Result desiredResult) {
		when(build.getResult()).thenReturn(desiredResult);
		GateStepReport result = check.step(build, null, null);
		assertEquals(desiredResult, result.getResult());
	}

}
