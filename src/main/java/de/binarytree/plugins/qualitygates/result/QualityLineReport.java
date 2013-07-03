package de.binarytree.plugins.qualitygates.result;

import hudson.model.Result;

import java.util.LinkedList;
import java.util.List;

import de.binarytree.plugins.qualitygates.Gate;

public class QualityLineReport extends ListContainer<GateReport> {

	private List<GateReport> gates() {
		return this.getItems();
	}

	public int getNumberOfGates() {
		return gates().size();
	}

	public void addGateReport(GateReport gateReport) {
		this.addOrReplaceItem(gateReport);
	}

	public List<GateReport> getGateReports() {
		return new LinkedList<GateReport>(this.gates());
	}

	public Result getResultFor(Gate gate) {
		GateReport report = this.getGateReportFor(gate);
		if (report != null) {
			return report.getResult();
		}
		return Result.NOT_BUILT;
	}

	public GateReport getGateReportFor(Gate gate) {
		for (GateReport report : this.gates()) {
			if (report.belongsTo(gate)) {
				return report;
			}
		}
		return null;
	}

	@Override
	protected boolean isSameItem(GateReport a, GateReport b) {
		return a.referencesSameGateAs(b);
	}

	public int getNumberOfSuccessfulGates() {
		int count = 0;
		for (GateReport result : this.gates()) {
			if (resultCausedTermination(result)) {
				break;
			}
			count++;
		}
		return count;
	}

	public List<String> getReasonsOfTermination() {
		List<String> reasons = new LinkedList<String>();
		for (GateReport report : this.gates()) {
			if (resultCausedTermination(report)) {
				reasons = report.getReasonOfFailure();
				break;
			}
		}
		return reasons;
	}

	private boolean resultCausedTermination(GateReport report) {
		return report.getResult().isWorseOrEqualTo(Result.FAILURE);
	}
}
