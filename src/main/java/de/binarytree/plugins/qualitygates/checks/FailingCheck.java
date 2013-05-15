package de.binarytree.plugins.qualitygates.checks;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import org.kohsuke.stapler.DataBoundConstructor;

import de.binarytree.plugins.qualitygates.result.CheckResult;

public class FailingCheck extends Check {

	@DataBoundConstructor
	public FailingCheck() {
	}

	@Override
	public void doCheck(AbstractBuild build, BuildListener listener,
			Launcher launcher, CheckResult checkResult) {
		checkResult.setResult(Result.FAILURE, "This check always fails.");
	}

	@Override
	public String toString() {
		return super.toString() + "[Will FAIL]";
	}

	@Extension
	public static class DescriptorImpl extends CheckDescriptor {

		@Override
		public String getDisplayName() {
			return "Always failing check";
		}

	}

}
