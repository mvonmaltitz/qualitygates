package de.binarytree.plugins.qualitygates;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import java.util.Collection;
import java.util.logging.Logger;

import org.kohsuke.stapler.DataBoundConstructor;

import de.binarytree.plugins.qualitygates.checks.Check;

public class QualityGateImpl extends QualityGate {

	@DataBoundConstructor
	public QualityGateImpl(String name, Collection<Check> checks) {
		super(name, checks) ; 

	}

	@Override
	public Result doCheck(AbstractBuild build, Launcher launcher,
			BuildListener listener) {
		listener.getLogger().println("QG " + this.getName()); 
		Result result; 
		if (checksAreAvailable()) {
			result = doChecksAndGetResultFor(build, listener, launcher);
		} else {
			result = resultOfEmptyGate(); 
		}
			return result;
	}

	private Result doChecksAndGetResultFor(AbstractBuild build, BuildListener listener, Launcher launcher) {
		Result result = Result.SUCCESS;
		for (Check check : this.checks) {
			result = result.combine(check.doCheck(build, listener, launcher));
			listener.getLogger().println("Check: " + check.toString() + " Result: " + result.toString());
		}
		return result;
	}

	private Result resultOfEmptyGate() {
		return Result.UNSTABLE;
	}

	private boolean checksAreAvailable() {
		return this.checks.size() != 0;
	}

	@Extension
	public static class DescriptorImpl extends QualityGateDescriptor {
		@Override
		public String getDisplayName() {
			return "Standard Quality Gate (AND-Gate)";
		}
	}

}
