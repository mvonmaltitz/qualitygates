package de.binarytree.plugins.qualitygates;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import java.util.Collection;

import org.kohsuke.stapler.DataBoundConstructor;

import de.binarytree.plugins.qualitygates.checks.Check;
import de.binarytree.plugins.qualitygates.result.CheckReport;
import de.binarytree.plugins.qualitygates.result.GateReport;

public class AndGate extends Gate {

	@DataBoundConstructor
	public AndGate(String name, Collection<Check> checks) {
		super(name, checks);

	}

	@Override
	public void doCheck(AbstractBuild build, Launcher launcher,
			BuildListener listener, GateReport gateReport) {
//		listener.getLogger().println("QG " + this.getName());
		if (checksAreAvailable()) {
			Result result = Result.SUCCESS;
			for (Check check : this.checks) {
				CheckReport checkReport = check.check(build, listener, launcher); 
				result = result.combine(checkReport.getResult());
//				listener.getLogger().println( "Check: " + check.toString() + " Result: " + result.toString());
				gateReport.addCheckResult(checkReport); 
				gateReport.setResult(result); 
			}
		} else {
			gateReport.setResult(this.resultOfEmptyGate());
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
			return "Standard Gate (AND-Gate)";
		}
	}

}
