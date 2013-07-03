package de.binarytree.plugins.qualitygates.checks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import org.junit.Before;
import org.junit.Test;

import de.binarytree.plugins.qualitygates.result.GateStepReport;

public class ManualCheckTest {
	class MockManualCheck extends ManualCheck {
		public MockManualCheck() {
			super();
		}

		public MockManualCheck(String hash) {
			this.setHash(hash);
		}

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
		GateStepReport result = new GateStepReport(check);
		check.doStep(build, listener, launcher, result);
		assertEquals(Result.NOT_BUILT, result.getResult());
		assertTrue(result.getReason().startsWith(
				ManualCheck.AWAITING_MANUAL_APPROVAL));
	}

	@Test
	public void testEqualsTrue() {
		ManualCheck check1 = new MockManualCheck("hash");
		ManualCheck check2 = new MockManualCheck("hash");
		assertEquals(check1, check2);

	}

	@Test
	public void testEqualsFalse() {
		ManualCheck check1 = new MockManualCheck("hash1");
		ManualCheck check2 = new MockManualCheck("hash2");
		assertNotEquals(check1, check2);

	}

}
