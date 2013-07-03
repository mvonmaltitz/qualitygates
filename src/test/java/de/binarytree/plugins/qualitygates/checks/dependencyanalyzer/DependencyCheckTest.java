package de.binarytree.plugins.qualitygates.checks.dependencyanalyzer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import de.binarytree.plugins.qualitygates.TestHelper;
import de.binarytree.plugins.qualitygates.checks.dependencyanalyzer.parser.BuildLogFileParser;
import de.binarytree.plugins.qualitygates.checks.dependencyanalyzer.result.AnalysisResult;
import de.binarytree.plugins.qualitygates.result.GateStepReport;

public class DependencyCheckTest {

	private static final String DEPENDENCY_SECTION = ""
			+ "[WARNING] Used undeclared dependencies found:"
			+ "[WARNING]    org.apache.maven:maven-model:jar:2.0.2:compile"
			+ "[WARNING]    org.codehaus.plexus:plexus-utils:jar:1.1:compile"
			+ "[WARNING] Unused declared dependencies found:"
			+ "[WARNING]    org.apache.maven:maven-artifact-manager:jar:2.0:compile"
			+ "[WARNING]    org.apache.maven:maven-artifact:jar:2.0:compile";

	class MockDependencyCheck extends DependencyCheck {
		private BuildLogFileParser logFileParser;
		private AnalysisResult analysis;

		public MockDependencyCheck() {
			logFileParser = mock(BuildLogFileParser.class);
			analysis = mock(AnalysisResult.class);
		}

		@Override
		protected BuildLogFileParser createLogFileParser() {
			return this.logFileParser;
		}

		public DescriptorImpl getDescriptor() {
			return new DependencyCheck.DescriptorImpl();
		}

		@Override
		protected AnalysisResult analyseDependencySection(String depencySection) {
			return analysis;
		}

		public AnalysisResult getMockAnalysis() {
			return analysis;
		}
	}

	private AbstractBuild build;
	private BuildListener listener;
	private Launcher launcher;
	private MockDependencyCheck check;
	private GateStepReport report;

	@Before
	public void setUp() throws Exception {
		build = TestHelper.getBuildMock();
		listener = TestHelper.getListenerMock();
		launcher = TestHelper.getLauncherMock();
		check = new MockDependencyCheck();
		report = new GateStepReport(check);
	}

	@Test
	public void testResultIsFailureIfBuildFailed() {
		when(build.getResult()).thenReturn(Result.FAILURE);
		check.doStep(build, listener, launcher, report);
		assertEquals(Result.FAILURE, report.getResult());
	}

	@Test
	public void testResultIsFailureIfBuildNotBuilt() {
		when(build.getResult()).thenReturn(Result.NOT_BUILT);
		check.doStep(build, listener, launcher, report);
		assertEquals(Result.FAILURE, report.getResult());
	}

	@Test
	public void testResultIsFailureIfBuildAborted() {
		when(build.getResult()).thenReturn(Result.ABORTED);
		check.doStep(build, listener, launcher, report);
		assertEquals(Result.FAILURE, report.getResult());
	}

	@Test
	public void testResultIsFailureWhenExceptionHappens() throws IOException {
		setBuildResultToSuccess();
		BuildLogFileParser parser = check.createLogFileParser();
		doThrow(new IOException("xxxmessagexxx")).when(parser).parseLogFile(
				any(File.class));
		check.doStep(build, listener, launcher, report);
		assertEquals(Result.FAILURE, report.getResult());
		assertTrue(report.getReason().toLowerCase().contains("xxxmessagexxx"));
	}

	@Test
	public void testResultIsUnstableIfDependencySectionIsEmpty() {
		setBuildResultToSuccess();
		BuildLogFileParser parser = check.createLogFileParser();
		letParserReturn(parser, "");
		check.doStep(build, listener, launcher, report);
		assertEquals(Result.UNSTABLE, report.getResult());
		assertTrue(report.getReason().toLowerCase()
				.contains("dependency:analyze"));
	}

	@Test
	public void testResultIsUnstableIfViolationsAreFound() {
		setBuildResultToSuccess();
		BuildLogFileParser parser = check.createLogFileParser();
		letParserReturn(parser, DEPENDENCY_SECTION);
		letAnalysisReturn(check, 5, 10);
		check.doStep(build, listener, launcher, report);
		assertEquals(Result.UNSTABLE, report.getResult());
		assertTrue(report.getReason().contains("5 undeclared"));
		assertTrue(report.getReason().contains("10 unused"));
	}

	@Test
	public void testResultIsUnstableIfOnlyUnusedDepencendiesAreFound() {
		setBuildResultToSuccess();
		BuildLogFileParser parser = check.createLogFileParser();
		letParserReturn(parser, DEPENDENCY_SECTION);
		letAnalysisReturn(check, 0, 2);
		check.doStep(build, listener, launcher, report);
		assertEquals(Result.UNSTABLE, report.getResult());
		assertTrue(report.getReason().contains("2 unused"));
	}

	@Test
	public void testResultIsSuccessIfNoViolationsAreFound() {
		setBuildResultToSuccess();
		BuildLogFileParser parser = check.createLogFileParser();
		letParserReturn(parser, DEPENDENCY_SECTION);
		letAnalysisReturn(check, 0, 0);
		check.doStep(build, listener, launcher, report);
		assertEquals(Result.SUCCESS, report.getResult());
	}

	@Test
	public void testResultIsSuccessIfNoViolationsAreFoundButBuildIsUnstable() {
		when(build.getResult()).thenReturn(Result.UNSTABLE);
		BuildLogFileParser parser = check.createLogFileParser();
		letParserReturn(parser, DEPENDENCY_SECTION);
		letAnalysisReturn(check, 0, 0);
		check.doStep(build, listener, launcher, report);
		assertEquals(Result.SUCCESS, report.getResult());
	}

	private void letAnalysisReturn(MockDependencyCheck check,
			int numberOfUndeclaredDependencies, int numberOfUnusedDependencies) {
		AnalysisResult analysis = check.getMockAnalysis();
		when(analysis.getNumberOfUndeclaredDependencies()).thenReturn(
				numberOfUndeclaredDependencies);
		when(analysis.getNumberOfUnusedDependencies()).thenReturn(
				numberOfUnusedDependencies);
	}

	private void letParserReturn(BuildLogFileParser parser, String returnValue) {
		when(parser.getDependencyAnalyseBlock()).thenReturn(returnValue);
	}

	private void setBuildResultToSuccess() {
		when(build.getResult()).thenReturn(Result.SUCCESS);
	}
}
