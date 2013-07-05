package de.binarytree.plugins.qualitygates.result;

import hudson.model.Result;
import de.binarytree.plugins.qualitygates.GateStep;

public class GateStepReport {

    private String stepName;
    private String description;
    private Result result = Result.NOT_BUILT;
    private String reason;
    private GateStep step;

    public GateStepReport(GateStep check) {
        this.step = check;
        this.stepName = check.getDisplayName();
        this.description = check.getDescription();
    }

    public String getDescription() {
        return this.description;
    }

    public String getStepName() {
        return this.stepName;
    }

    /**
     * Documents the result of the corresponding step.
     * 
     * Note: Negative results also need a supplied reason of failure.
     * 
     * @param result
     *            the result for the corresponding step
     */
    public void setResult(Result result) {
        if (isNegative(result)) {
            throwExceptionDueToMissingReason();
        }
        this.result = result;
    }

    private boolean isNegative(Result result) {
        return Result.FAILURE.equals(result) || Result.UNSTABLE.equals(result);
    }

    private void throwExceptionDueToMissingReason() {
        throw new IllegalArgumentException("Negative results need a reason");
    }

    /**
     * Documents the result of the corresponding step. Note: Negative results
     * also need a supplied reason of failure.
     * 
     * @param result
     *            the result for the corresponding step
     */
    public void setResult(Result result, String reason) {
        if (isNegative(result) && isEmpty(reason)) {
            throwExceptionDueToMissingReason();
        }
        // It's okay to have positive results and a reason, even if not
        // necessary
        this.result = result;
        this.reason = reason;
    }

    private boolean isEmpty(String reason) {
        return reason == null || reason.isEmpty();
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
