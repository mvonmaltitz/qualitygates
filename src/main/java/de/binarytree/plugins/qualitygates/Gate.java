package de.binarytree.plugins.qualitygates;

import hudson.DescriptorExtensionList;
import hudson.ExtensionPoint;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Describable;
import hudson.model.AbstractBuild;
import hudson.model.Hudson;
import de.binarytree.plugins.qualitygates.result.GateReport;

/**
 * This class represents a single gate of the {@link QualityLine}.
 * 
 * A gate can be added to the {@link QualityLine}. During execution the
 * {@link #evaluate(AbstractBuild, Launcher, BuildListener)} method is invoked.
 * Whether a gate has been successfully executed is determined by the result of
 * the gate available via the {@link GateReport}. The gate being successful
 * leads to the execution of the following gate. A gate having failed causes the
 * {@Link QualityLine} to stop, documenting the remaining gates as
 * {@linkplain hudson.model.Result#NOT_BUILT}. Non-fatal warnings can be issued
 * with {@link hudson.model.Result#UNSTABLE} as result. A gate returning
 * {@link hudson.model.Result#NOT_BUILT} itself leads to a temporary
 * interruption of the line.
 * 
 * 
 * @author Marcel von Maltitz
 * 
 */
public abstract class Gate implements Describable<Gate>, ExtensionPoint {

    private String name;

    public Gate(String name) {
        this.name = name;
    }

    public GateReport evaluate(AbstractBuild build, Launcher launcher,
            BuildListener listener) {
        GateReport gateReport = this.createEmptyGateReport();
        this.doEvaluation(build, launcher, listener, gateReport);
        return gateReport;
    }

    public GateReport createEmptyGateReport() {
        return new GateReport(this);
    }

    /**
     * Performs the actual evaluation of this gate.
     * 
     * The type of evaluation is fully up to the implementer. Whether the
     * evaluation has been successful has to be denoted in the given gateReport
     * object.
     * 
     * - {@link hudson.model.Result#SUCCESS SUCCESS} means, that the result of
     * the gate was positive and the following gates shall be evaluated
     * afterwards.
     * 
     * - {@link hudson.model.Result#UNSTABLE UNSTABLE} means, that the gate has
     * generated warnings, but the following gates shall be evaluated
     * nevertheless.
     * 
     * - {@link hudson.model.Result#FAILURE FAILURE} means, that the result of
     * the gate was negative, therefore the following gates shall NOT be
     * evaluated.
     * 
     * - {@link hudson.model.Result#NOT_BUILT NOT_BUILT} means, that the gate
     * was not evaluated, therefore the following gates shall NOT be evaluated.
     * Reevaluation can be triggered by a GET-Request to the
     * {@link BuildResultAction}.
     * 
     * @param gateReport
     *            the report where the gate has to document, whether its
     *            evaluation has been successful
     */
    abstract void doEvaluation(AbstractBuild build, Launcher launcher,
            BuildListener listener, GateReport gateReport);

    public QualityGateDescriptor getDescriptor() {
        return (QualityGateDescriptor) Hudson.getInstance().getDescriptor(
                getClass());
    }

    public String getName() {
        return this.name;
    }

    public static DescriptorExtensionList<Gate, QualityGateDescriptor> all() {
        return Hudson.getInstance()
                .<Gate, QualityGateDescriptor> getDescriptorList(Gate.class);
    }

}
