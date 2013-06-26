package de.binarytree.plugins.qualitygates.checks.dependencyanalyzer;

import java.io.IOException;
import java.util.Arrays;

import hudson.Extension;
import hudson.Launcher;
import hudson.maven.MavenModuleSetBuild;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import org.kohsuke.stapler.DataBoundConstructor;

import de.binarytree.plugins.qualitygates.checks.Check;
import de.binarytree.plugins.qualitygates.checks.CheckDescriptor;
import de.binarytree.plugins.qualitygates.checks.dependencyanalyzer.persistence.BuildResultSerializer;
import de.binarytree.plugins.qualitygates.checks.dependencyanalyzer.result.BuildResult;
import de.binarytree.plugins.qualitygates.checks.dependencyanalyzer.result.ModuleResult;
import de.binarytree.plugins.qualitygates.result.CheckReport;

public class DependencyCheck extends Check {

	@DataBoundConstructor
	public DependencyCheck() {
	}

	@Override
	public void doCheck(AbstractBuild build, BuildListener listener,
			Launcher launcher, CheckReport checkReport) {
		if (!buildExists(build)) {
			checkReport.setResult(Result.FAILURE,
					"Cannot proceed, build has not been successful");
		} else {
			// Construct the build result
			BuildResult analysis = null;
			try {
				analysis = DependencyAnalyzerResultBuilder
						.buildResult((MavenModuleSetBuild) build);
				BuildResultSerializer.serialize(build.getRootDir(), analysis);
				gatherViolationsAndSetCheck(checkReport, analysis);
			} catch (IOException e) {
				setCheckResultToStacktrace(checkReport, e);
			}
		}

	}

	private void gatherViolationsAndSetCheck(CheckReport checkReport,
			BuildResult analysis) {
		int numberOfUndeclaredDependencies = analysis
				.getOverallNumberOfUndeclaredDependencies();
		int numberOfUnusedDependencies = analysis
				.getOverallNumberOfUnusedDependencies();
		setCheckResultDependingOnNumberOfViolations(checkReport,
				numberOfUndeclaredDependencies,
				numberOfUnusedDependencies);
	}

	private boolean buildExists(AbstractBuild build) {
		Result result = build.getResult();
		return (Result.SUCCESS.equals(result) || Result.UNSTABLE.equals(result));
	}

	private int getDependencyViolationCount(BuildResult analysis) {
		int undeclared = 0;
		int unused = 0;
		for (ModuleResult module : analysis.getModules()) {
			undeclared += module.getUndeclaredDependenciesCount();
			unused += module.getUnusedDependenciesCount();
		}
		return undeclared + unused;
	}

	private void setCheckResultDependingOnNumberOfViolations(
			CheckReport checkReport, int numberOfUndeclaredDependencies,
			int numberOfUnusedDependencies) {
		if (numberOfUndeclaredDependencies +  numberOfUnusedDependencies > 0) {
			checkReport.setResult(Result.UNSTABLE, numberOfUndeclaredDependencies
					+ " undeclared and " + numberOfUnusedDependencies + " unused dependencies found.");
		} else {
			checkReport.setResult(Result.SUCCESS,
					"No dependency violations found");
		}
	}

	private void setCheckResultToStacktrace(CheckReport checkReport,
			IOException e) {
		checkReport.setResult(Result.FAILURE,
				Arrays.toString(e.getStackTrace()));
	}

	@Extension
	public static class DescriptorImpl extends CheckDescriptor {

		@Override
		public String getDisplayName() {
			return "Maven Dependency Check";
		}

	}

	@Override
	public String getDescription() {
		return "Maven Dependency Check";
	}

}
