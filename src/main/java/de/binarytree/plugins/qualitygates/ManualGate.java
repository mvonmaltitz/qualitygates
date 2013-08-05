package de.binarytree.plugins.qualitygates;

import hudson.Extension;

import org.kohsuke.stapler.DataBoundConstructor;

import de.binarytree.plugins.qualitygates.steps.manualcheck.ManualCheck;

/**
 * This class represents a manual gate. That is it has to be (dis)approved
 * manually. Therefore, no checks can be added, but it is preinitialized using a
 * {@link de.binarytree.plugins.qualitygates.steps.manualcheck.ManualCheck}.
 * 
 * @author mvm
 * 
 */
public class ManualGate extends AndGate {

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
