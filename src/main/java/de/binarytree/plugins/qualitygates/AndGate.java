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

import de.binarytree.plugins.qualitygates.result.GateReport;
import de.binarytree.plugins.qualitygates.result.GateStepReport;

/**
 * This class represents a gate consisting of multiple steps, which can be set
 * at instantiation. Every step is performed and evaluated. Evaluation returns a
 * gate report which holds all performed checks and the result of the gate
 * itself. The gate always has the worst result of all of its checks.
 * 
 * @author mvm
 * 
 */
public class AndGate extends Gate {

    private List<GateStep> steps = new LinkedList<GateStep>();

    @DataBoundConstructor
    public AndGate(String name, Collection<GateStep> steps) {
        super(name);
        if (steps != null) {
            this.steps.addAll(steps);
        }

    }

    public List<GateStep> getSteps() {
        return this.steps;
    }

    protected void addStep(GateStep step) {
        this.steps.add(step);
    }

    public int getNumberOfSteps() {
        return this.steps.size();
    }

    @Override
    public GateReport createEmptyGateReport() {
        GateReport gateReport = super.createEmptyGateReport();
        for (GateStep step : this.steps) {
            gateReport.addStepReport(step.createEmptyGateStepReport());
        }
        return gateReport;
    }

    @Override
    public void doEvaluation(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener, GateReport gateReport) {
        listener.getLogger().println("Processing gate " + this.getName());
        if (stepsAreAvailable()) {
            initializeReportWithSuccessResult(gateReport);
            evaluateSteps(build, launcher, listener, gateReport);
        } else {
            reportGateAsEmpty(gateReport);
        }
    }

    private void initializeReportWithSuccessResult(GateReport gateReport) {
        gateReport.setResult(Result.SUCCESS);
    }

    private void reportGateAsEmpty(GateReport gateReport) {
        gateReport.setResult(resultOfEmptyGate());
    }

    private void evaluateSteps(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener, GateReport gateReport) {
        for (GateStep step : this.steps) {
            GateStepReport stepReport = processStep(build, launcher, listener, step);
            addStepReportToGateReport(gateReport, stepReport);
        }
    }

    private void addStepReportToGateReport(GateReport gateReport, GateStepReport stepReport) {
        gateReport.addStepReport(stepReport);
        Result gateResult = mergeStepResultIntoGateResult(gateReport.getResult(), stepReport.getResult());
        gateReport.setResult(gateResult);
    }

    /**
     * Defines the function how a new step result shall be merged into the current result state of the gate.
     * 
     * @param gateResult
     *            the current result of the gate based on the evaluation of all former checks
     * @param stepResult
     *            the result of the currently evaluated check to be merged with the current result
     * @return the merged result of both parameters
     */
    protected Result mergeStepResultIntoGateResult(Result gateResult, Result stepResult) {
        return gateResult.combine(stepResult);
    }

    private GateStepReport processStep(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener, GateStep step) {
        return step.step(build, listener, launcher);
    }

    /**
     * The result returned when the gate is empty.
     * 
     * @return the result an empty gate shall have.
     */
    protected Result resultOfEmptyGate() {
        return Result.UNSTABLE;
    }

    private boolean stepsAreAvailable() {
        return this.getNumberOfSteps() != 0;
    }

    @Extension
    public static class DescriptorImpl extends QualityGateDescriptor {
        @Override
        public String getDisplayName() {
            return "Standard Gate (AND-Gate)";
        }
    }

}
