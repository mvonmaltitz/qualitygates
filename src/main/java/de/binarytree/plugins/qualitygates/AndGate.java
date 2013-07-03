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

import de.binarytree.plugins.qualitygates.result.GateStepReport;
import de.binarytree.plugins.qualitygates.result.GateReport;
import de.binarytree.plugins.qualitygates.steps.GateStep;

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

    public GateReport document() {
        GateReport gateReport = super.document();
        for (GateStep step : this.steps) {
            gateReport.addStepReport(step.document());
        }
        return gateReport;
    }

    @Override
    public void doEvaluation(AbstractBuild build, Launcher launcher,
            BuildListener listener, GateReport gateReport) {
        if (stepsAreAvailable()) {
            Result result = Result.SUCCESS;
            for (GateStep step : this.steps) {
                GateStepReport stepReport = step
                        .step(build, listener, launcher);
                result = result.combine(stepReport.getResult());
                gateReport.addStepReport(stepReport);
                gateReport.setResult(result);
            }
        } else {
            gateReport.setResult(this.resultOfEmptyGate());
        }
    }

    private Result resultOfEmptyGate() {
        return Result.UNSTABLE;
    }

    private boolean stepsAreAvailable() {
        return this.steps.size() != 0;
    }

    @Extension
    public static class DescriptorImpl extends QualityGateDescriptor {
        @Override
        public String getDisplayName() {
            return "Standard Gate (AND-Gate)";
        }
    }

}
