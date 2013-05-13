package de.binarytree.plugins.qualitygates;

import hudson.Extension;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.kohsuke.stapler.DataBoundConstructor;

import de.binarytree.plugins.qualitygates.checks.Check;

public class QualityGateImpl extends QualityGate {

	@DataBoundConstructor
	public QualityGateImpl(String name, Collection<Check> checks) {
		super(name, checks) ; 

	}

	public Result doCheck(AbstractBuild build) {
		Result result; 
		if (checksAreAvailable()) {
			result = doChecksAndGetResultFor(build);
		} else {
			result = resultOfEmptyGate(); 
		}
			return result;
	}

	private Result doChecksAndGetResultFor(AbstractBuild build) {
		Result result = Result.SUCCESS;
		for (Check check : this.checks) {
			result = result.combine(check.doCheck(build));
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
