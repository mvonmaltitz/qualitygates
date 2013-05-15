package de.binarytree.plugins.qualitygates;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import java.util.Collection;

import org.kohsuke.stapler.DataBoundConstructor;

import de.binarytree.plugins.qualitygates.checks.Check;
import de.binarytree.plugins.qualitygates.result.CheckResult;
import de.binarytree.plugins.qualitygates.result.GateResult;

public class QualityGateImpl extends QualityGate {

	@DataBoundConstructor
	public QualityGateImpl(String name, Collection<Check> checks) {
		super(name, checks);

	}

	@Override
	public void doCheck(AbstractBuild build, Launcher launcher,
			BuildListener listener, GateResult gateResult) {
		listener.getLogger().println("QG " + this.getName());
		if (checksAreAvailable()) {
			Result result = Result.SUCCESS;
			for (Check check : this.checks) {
				CheckResult checkResult = check.check(build, listener, launcher); 
				result = result.combine(checkResult.getResult());
				listener.getLogger().println( "Check: " + check.toString() + " Result: " + result.toString());
				gateResult.addCheckResult(checkResult); 
				gateResult.setResult(result); 
			}
		} else {
			gateResult.setResult(this.resultOfEmptyGate());
		}
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
