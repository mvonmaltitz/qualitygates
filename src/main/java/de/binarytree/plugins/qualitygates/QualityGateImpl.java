package de.binarytree.plugins.qualitygates;

import hudson.Extension;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import java.util.ArrayList;
import java.util.List;

import org.kohsuke.stapler.DataBoundConstructor;

import de.binarytree.plugins.qualitygates.checks.Check;

public class QualityGateImpl extends QualityGate {
	private List<Check> checks = new ArrayList<Check>();

	@DataBoundConstructor
	public QualityGateImpl(){}
	public void addCheck(Check check) {
		this.checks.add(check);
	}

	public int getNumberOfChecks() {
		return this.checks.size();
	}

	public Result doCheck(AbstractBuild build) {
		Result result = Result.SUCCESS;
		for (Check check : this.checks) {
			result = result.combine(check.doCheck(build));
		}
		return result;
	}

	@Extension
	public static class DescriptorImpl extends QualityGateDescriptor {
		@Override
		public String getDisplayName() {
			return "Quality Gate";
		}
	}
}
