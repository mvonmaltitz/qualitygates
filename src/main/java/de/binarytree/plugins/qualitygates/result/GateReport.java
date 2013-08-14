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

    /**
     * Creates a new gate report for the given gate.
     * 
     * @param gate
     *            the gate for which a report shall be created
     */
    public GateReport(Gate gate) {
        this.gateName = gate.getName();
        this.gate = gate;
    }

    private List<GateStepReport> steps() {
        return this.getItems();
    }

    /**
     * Returns the name of the corresponding gate.
     * 
     * @return the name of the corresponding gate
     */
    public String getGateName() {
        return this.gateName;
    }

    /**
     * Returns the result of this report.
     * 
     * @return the reusult of this report
     */
    public Result getResult() {
        return this.result;
    }

    /**
     * Returns the number of {@link GateStepReport}s in this gate report.
     * 
     * @return the number of gate step reports
     */
    public int getNumberOfSteps() {
        return this.steps().size();
    }

    public void setResult(Result result) {
        this.result = result;
    }

    /**
     * Adds the given step report to this report. If there is already a step
     * report referencing the same gate, it is replaced.
     * 
     * @param stepReport
     *            the stepReport to be added
     */
    public void addStepReport(GateStepReport stepReport) {
        this.addOrReplaceItem(stepReport);

    }

    /**
     * Returns a list of the {@link GateStepReport} of this gate report.
     * 
     * @return a list of the contained gate step reports.
     */
    public List<GateStepReport> getStepReports() {
        return new LinkedList<GateStepReport>(this.steps());
    }
/**
 * Whether or not this report belongs to the given gate. 
 * @param gate the gate to be tested
 * @return whether of 
 */
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
