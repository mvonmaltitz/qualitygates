package de.binarytree.plugins.qualitygates;

import hudson.DescriptorExtensionList;
import hudson.ExtensionPoint;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Describable;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.Hudson;

import java.util.Arrays;

import de.binarytree.plugins.qualitygates.result.GateStepReport;

/**
 * This class represents a single step of a {@link Gate Gate}.
 * 
 * Two main types exist:
 * 
 * A check examines a specific characteristic of the current build. The
 * expectations about this characteristics being met, should lead to a
 * successful result of the check. Otherwise, the check should fail. If the
 * check finds objections, which are not fatal, the check may issue a warning.
 * 
 * An action does a manipulation or transformation on the build. The
 * manipulation being successful, should lead to a successful result of the
 * action. Errors during the execution should lead to failure. Like the check,
 * an action may issue warnings.
 * 
 * @author Marcel von Maltitz
 * 
 */
public abstract class GateStep implements Describable<GateStep>, ExtensionPoint {

    public GateStepReport step(AbstractBuild build, BuildListener listener,
            Launcher launcher) {
        GateStepReport stepReport = this.createEmptyGateStepReport();
        try {
            this.doStep(build, launcher, listener, stepReport);
        } catch (Exception e) {
            failStepWithExceptionAsReason(stepReport, e);
        }
        return stepReport;
    }

    public GateStepReport createEmptyGateStepReport() {
        return new GateStepReport(this);
    }

    protected void failStepWithExceptionAsReason(GateStepReport stepReport,
            Exception e) {
        stepReport.setResult(Result.FAILURE,
                e.getMessage() + Arrays.toString(e.getStackTrace()));
    }

    /**
     * Performs the actual evaluation of this step.
     * 
     * The type of evaluation is fully up to the implementer. Whether the
     * evaluation has been successful has to be denoted in the given gateReport
     * object.
     * 
     * 
     * - {@link hudson.model.Result#SUCCESS SUCCESS} means, that the result of
     * the step was positive.
     * 
     * - {@link hudson.model.Result#UNSTABLE UNSTABLE} means, that the step has
     * generated warnings.
     * 
     * - {@link hudson.model.Result#FAILURE FAILURE} means, that the result of
     * the step was negative.
     * 
     * - {@link hudson.model.Result#NOT_BUILT NOT_BUILT} means, that the step
     * was not evaluated.
     * 
     * @param stepReport
     *            the report where the step has to document, whether its
     *            evaluation has been successful
     */
    public abstract void doStep(AbstractBuild build, Launcher launcher,
            BuildListener listener, GateStepReport stepReport);

    /**
     * Returns a short textual description of this step.
     * 
     * @return a short textual description of this step
     */
    public abstract String getDescription();

    public String getDisplayName() {
        return this.getDescriptor().getDisplayName();
    }

    @Override
    public String toString() {
        return "GateStep [" + this.getDescriptor().getDisplayName() + "]";
    }

    public GateStepDescriptor getDescriptor() {
        return (GateStepDescriptor) Hudson.getInstance().getDescriptor(
                getClass());
    }

    public static DescriptorExtensionList<GateStep, GateStepDescriptor> all() {
        return Hudson.getInstance()
                .<GateStep, GateStepDescriptor> getDescriptorList(
                        GateStep.class);
    }

}
