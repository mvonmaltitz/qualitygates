package de.binarytree.plugins.qualitygates;

import hudson.DescriptorExtensionList;
import hudson.ExtensionPoint;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Describable;
import hudson.model.AbstractBuild;
import hudson.model.Hudson;
import de.binarytree.plugins.qualitygates.result.BuildResultAction;
import de.binarytree.plugins.qualitygates.result.GateReport;

public abstract class Gate implements Describable<Gate>, ExtensionPoint {

    private String name;

    public Gate(String name) {
        this.name = name;
    }

    public GateReport evaluate(AbstractBuild build, Launcher launcher,
            BuildListener listener) {
        GateReport gateReport = new GateReport(this);
        this.doEvaluation(build, launcher, listener, gateReport);
        return gateReport;
    }

    public GateReport document() {
        return new GateReport(this);
    }

    /**
     * Performs the actual evaluation of this gate.
     * 
     * The type of evaluation is fully up to the implementer. Whether the
     * evaluation has been successful has to be denoted in the given gateReport
     * object.
     * 
     * - SUCCESS means, that the result of the gate was positive and the
     * following gates shall be evaluated afterwards. - UNSTALBE means, that the
     * gate has generated warnings, but the following gates shall be evaluated
     * nevertheless. - FAILURE means, that the result of the gate was negative,
     * therefore the following gates shall NOT be evaluated. - NOT_BUILT means,
     * that the gate was not evaluated, therefore the following gates shall NOT
     * be evaluated. Reevaluation can be triggered by a GET-Request to the
     * {@link BuildResultAction}.
     * 
     * @param gateReport
     *            the report where the gate has to document, whether its
     *            evaluation has been successfull
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
