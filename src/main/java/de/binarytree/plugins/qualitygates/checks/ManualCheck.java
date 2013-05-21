package de.binarytree.plugins.qualitygates.checks;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import java.util.Random;

import org.kohsuke.stapler.DataBoundConstructor;

import de.binarytree.plugins.qualitygates.result.CheckResult;

public class ManualCheck extends Check {

	public static final String AWAITING_MANUAL_APPROVAL = "Awaiting manual approval.";

	public final String hash;

	@DataBoundConstructor
	public ManualCheck() {
		hash = Long.toString(System.currentTimeMillis())
				+ Integer.toString((new Random()).nextInt());
	}

	@Override
	public void doCheck(AbstractBuild build, BuildListener listener,
			Launcher launcher, CheckResult checkResult) {
		if (checkResult.getResult().equals(Result.NOT_BUILT)) {
			System.out.println(this.hash + " Setting manual wait.");
			checkResult.setResult(Result.NOT_BUILT, AWAITING_MANUAL_APPROVAL
					+ " <a href='approve'>Approve</a>");
		} else {
			System.out.println(this.hash
					+ " Skipping manual as it is approved.");
		}
	}

	@Override
	public String getDescription() {
		return "Wait for manual approval (" + this.hash + ")";
	}

	public boolean equals(Object o) {
		return o instanceof ManualCheck && ((ManualCheck) o).hash.equals(this.hash); 
	}

	@Extension
	public static class DescriptorImpl extends CheckDescriptor {
		@Override
		public String getDisplayName() {
			return "Manual Check";
		}
	}
}
