package de.binarytree.plugins.qualitygates.result;

import hudson.model.Result;

import java.util.LinkedList;
import java.util.List;

import de.binarytree.plugins.qualitygates.Gate;
import de.binarytree.plugins.qualitygates.GateStep;

/**
 * This class is a report about the execution of a gate. It holds the reports of
 * the executed steps and the result of the gate itself.
 */
public class GateReport extends ListContainer<GateStepReport> {

    private String gateName;
    private Result result = Result.NOT_BUILT;
    private transient Gate gate;

    public GateReport(Gate gate) {
        this.gateName = gate.getName();
        this.gate = gate;
    }

    private List<GateStepReport> steps() {
        return this.getItems();
    }

    public String getGateName() {
        return this.gateName;
    }

    public Result getResult() {
        return this.result;
    }

    public int getNumberOfSteps() {
        return this.steps().size();
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public void addStepReport(GateStepReport stepReport) {
        this.addOrReplaceItem(stepReport);

    }

    public List<GateStepReport> getStepReports() {
        return new LinkedList<GateStepReport>(this.steps());
    }

    public boolean belongsTo(Gate gate) {
        return this.gate == gate;
    }

    public boolean referencesSameGateAs(GateReport gateReport) {
        return this.gate == gateReport.gate;
    }

    @Override
    protected boolean isSameItem(GateStepReport a, GateStepReport b) {
        return a.referencesSameStepAs(b);
    }

    public List<String> getReasonOfFailure() {
        LinkedList<String> reasons = new LinkedList<String>();
        for (GateStepReport report : this.steps()) {
            if (report.getResult().isWorseOrEqualTo(Result.FAILURE)) {
                reasons.add(report.getReason());
            }
        }
        return reasons;
    }

    public GateStepReport getReportFor(GateStep step) {
        for (GateStepReport report : this.getStepReports()) {
            if (report.references(step)) {
                return report;
            }
        }
        return null;
    }
}
