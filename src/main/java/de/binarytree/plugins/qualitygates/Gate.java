package de.binarytree.plugins.qualitygates;

import hudson.DescriptorExtensionList;
import hudson.ExtensionPoint;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Describable;
import hudson.model.AbstractBuild;
import hudson.model.Hudson;
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

    public abstract void doEvaluation(AbstractBuild build, Launcher launcher,
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
