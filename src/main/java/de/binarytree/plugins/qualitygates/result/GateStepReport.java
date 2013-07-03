package de.binarytree.plugins.qualitygates.result;

import hudson.model.Result;
import de.binarytree.plugins.qualitygates.checks.GateStep;

public class GateStepReport {

    private String stepName;
    private String description;
    private Result result = Result.NOT_BUILT;
    private String reason;
    private GateStep step;

    public GateStepReport(GateStep check) {
        this.step = check;
        this.stepName = check.getDescriptor().getDisplayName();
        this.description = check.getDescription();
    }

    public String getDescription() {
        return this.description;
    }

    public String getStepName() {
        return this.stepName;
    }

    public void setResult(Result result) {
        if (Result.FAILURE.equals(result) || Result.UNSTABLE.equals(result)) {
            throw new IllegalArgumentException("Negative results need a reason");
        }
        this.result = result;
    }

    public void setResult(Result result, String reason) {
        // It's okay to have positive results and a reason, even if not
        // necessary
        this.result = result;
        this.reason = reason;
    }

    public Result getResult() {
        return this.result;
    }

    public String getReason() {
        return this.reason;
    }

    public boolean referencesSameStepAs(GateStepReport b) {
        return this.step == b.step;
    }

    public boolean references(GateStep check) {
        return this.step.equals(check);
    }

    public GateStep getStep() {
        return this.step;
    }
}
