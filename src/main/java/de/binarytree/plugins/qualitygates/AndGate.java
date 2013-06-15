package de.binarytree.plugins.qualitygates;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.kohsuke.stapler.DataBoundConstructor;

import de.binarytree.plugins.qualitygates.checks.Check;
import de.binarytree.plugins.qualitygates.result.CheckReport;
import de.binarytree.plugins.qualitygates.result.GateReport;

public class AndGate extends Gate {

    protected List<Check> checks = new LinkedList<Check>();
	@DataBoundConstructor
	public AndGate(String name, Collection<Check> checks) {
		super(name);
        if (checks != null) {
            this.checks.addAll(checks);
        }

	}

    public List<Check> getChecks() {
        return this.checks;
    }

    public int getNumberOfChecks() {
        return this.checks.size();
    }

    public GateReport document() {
    	GateReport gateReport = super.document(); 
        for (Check check : this.checks) {
            gateReport.addCheckResult(check.document());
        }
        return gateReport; 
    }
	
	@Override
	public void doCheck(AbstractBuild build, Launcher launcher,
			BuildListener listener, GateReport gateReport) {
		if (checksAreAvailable()) {
			Result result = Result.SUCCESS;
			for (Check check : this.checks) {
				CheckReport checkReport = check.check(build, listener, launcher); 
				result = result.combine(checkReport.getResult());
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
