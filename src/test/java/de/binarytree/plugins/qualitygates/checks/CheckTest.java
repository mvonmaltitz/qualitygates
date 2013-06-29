package de.binarytree.plugins.qualitygates.checks;

import static org.junit.Assert.*;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.Result;

import org.junit.Before;
import org.junit.Test;

import de.binarytree.plugins.qualitygates.TestHelper;
import de.binarytree.plugins.qualitygates.result.CheckReport;

public class CheckTest {
	String exceptionMessage = "Exception message";

	class MockCheck extends Check {

		@Override
		public void doCheck(AbstractBuild build, BuildListener listener,
				Launcher launcher, CheckReport checkReport) {
			throw new IllegalArgumentException(exceptionMessage);
		}

		@Override
		public String getDescription() {
			return "This check throws an unchecked exception";
		}

		class DescriptorImpl extends CheckDescriptor {

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
		CheckReport report = check.check(build, listener, launcher);
		assertEquals(Result.FAILURE, report.getResult());
		assertTrue(report.getReason().contains(exceptionMessage));

	}
}
