package de.binarytree.plugins.qualitygates;

import hudson.DescriptorExtensionList;
import hudson.model.Descriptor;

public abstract class QualityGateDescriptor extends Descriptor<Gate> {

    protected QualityGateDescriptor(Class<? extends Gate> clazz) {
        super(clazz);
    }

    protected QualityGateDescriptor() {
    }

    public DescriptorExtensionList<GateStep, GateStepDescriptor> getDescriptors() {
        return GateStep.all();
    }

}
