package de.binarytree.plugins.qualitygates;

import hudson.DescriptorExtensionList;
import hudson.ExtensionPoint;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Describable;
import hudson.model.AbstractBuild;
import hudson.model.Hudson;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.binarytree.plugins.qualitygates.checks.Check;
import de.binarytree.plugins.qualitygates.result.GateReport;

public abstract class Gate implements Describable<Gate>, ExtensionPoint {

    protected String name;

    protected List<Check> checks = new LinkedList<Check>();

    public Gate(String name, Collection<Check> checks) {
        this.name = name;
        if (checks != null) {
            this.checks.addAll(checks);
        }
    }

    public List<Check> getChecks() {
        return this.checks;
    }

    public int getNumberOfChecks() {
        return this.checks.size();
    }

    public GateReport check(AbstractBuild build, Launcher launcher, BuildListener listener) {
        GateReport gateReport = new GateReport(this);
        this.doCheck(build, launcher, listener, gateReport);
        return gateReport;
    }

    public GateReport document() {
        GateReport gateReport = new GateReport(this);
        for (Check check : this.checks) {
            gateReport.addCheckResult(check.document());
        }
        return gateReport;
    }

    public abstract void doCheck(AbstractBuild build, Launcher launcher, BuildListener listener, GateReport gateReport);

    public QualityGateDescriptor getDescriptor() {
        return (QualityGateDescriptor) Hudson.getInstance().getDescriptor(getClass());
    }

    public String getName() {
        return this.name;
    }

    public static DescriptorExtensionList<Gate, QualityGateDescriptor> all() {
        return Hudson.getInstance().<Gate, QualityGateDescriptor> getDescriptorList(Gate.class);
    }

}
