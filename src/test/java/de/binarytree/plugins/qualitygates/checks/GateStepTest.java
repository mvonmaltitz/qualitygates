package de.binarytree.plugins.qualitygates.checks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import org.junit.Before;
import org.junit.Test;

import de.binarytree.plugins.qualitygates.TestHelper;
import de.binarytree.plugins.qualitygates.result.GateStepReport;

public class GateStepTest {
	String exceptionMessage = "Exception message";

	class MockCheck extends GateStep {

		@Override
		public void doStep(AbstractBuild build, BuildListener listener,
				Launcher launcher, GateStepReport checkReport) {
			throw new IllegalArgumentException(exceptionMessage);
		}

		@Override
		public String getDescription() {
			return "This check throws an unchecked exception";
		}

		class DescriptorImpl extends GateStepDescriptor {

			@Override
			public String getDisplayName() {
				return "MockCheck";
			}
		}

		public DescriptorImpl getDescriptor() {
			return new DescriptorImpl();
		}
	}

	private MockCheck check;
	private AbstractBuild build;
	private BuildListener listener;
	private Launcher launcher;

	@Before
	public void setUp() {
		check = new MockCheck();
		build = TestHelper.getBuildMock();
		listener = TestHelper.getListenerMock();
		launcher = TestHelper.getLauncherMock();
	}

	@Test
	public void testExceptionBecomesFailureReportResult() {
		GateStepReport report = check.step(build, listener, launcher);
		assertEquals(Result.FAILURE, report.getResult());
		assertTrue(report.getReason().contains(exceptionMessage));

	}
}
