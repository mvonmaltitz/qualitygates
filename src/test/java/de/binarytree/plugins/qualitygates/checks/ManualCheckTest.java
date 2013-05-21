package de.binarytree.plugins.qualitygates.checks;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import org.junit.Before;
import org.junit.Test;

import de.binarytree.plugins.qualitygates.result.CheckResult;

public class ManualCheckTest {
	class MockManualCheck extends ManualCheck {
		public DescriptorImpl getDescriptor() {
			return new ManualCheck.DescriptorImpl();
		}
	}

	private ManualCheck check;
	private AbstractBuild build;
	private BuildListener listener;
	private Launcher launcher;

	@Before
	public void setUp() throws Exception {
		check = new MockManualCheck();
		build = mock(AbstractBuild.class);
		listener = mock(BuildListener.class);
		launcher = mock(Launcher.class);
	}

	@Test
	public void testCheckResult() {
		CheckResult result = new CheckResult(check);
		check.doCheck(build, listener, launcher, result);
		assertEquals(Result.NOT_BUILT, result.getResult());
		assertTrue(result.getReason().startsWith(ManualCheck.AWAITING_MANUAL_APPROVAL));
	}
}
