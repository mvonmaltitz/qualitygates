package de.binarytree.plugins.qualitygates;

import hudson.Extension;

import org.kohsuke.stapler.DataBoundConstructor;

import de.binarytree.plugins.qualitygates.steps.manualcheck.ManualCheck;

/**
 * This class represents a manual gate. That is it has to be (dis)approved
 * manually. Therefore, no checks can be added, but it is preinitialized using a
 * {@link de.binarytree.plugins.qualitygates.steps.manualcheck.ManualCheck}.
 * 
 * @author Marcel von Maltitz
 * 
 */
public class ManualGate extends AndGate {
    /**
     * Creates a new ManualGate with the given name. No steps have to be added
     * as the ManualGate consists of only one {@link ManualCheck}. 
     * 
     * @param name the name of the gate
     * 
     */
    @DataBoundConstructor
    public ManualGate(String name) {
        super(name, null);
        this.setUpSingleManualCheck();
    }

    private void setUpSingleManualCheck() {
        ManualCheck check = new ManualCheck();
        this.addStep(check);
    }

    @Extension
    public static class DescriptorImpl extends QualityGateDescriptor {
        @Override
        public String getDisplayName() {
            return "Manual Gate";
        }
    }
}
