package de.binarytree.plugins.qualitygates;

import hudson.DescriptorExtensionList;
import hudson.model.Descriptor;
import de.binarytree.plugins.qualitygates.steps.GateStep;
import de.binarytree.plugins.qualitygates.steps.GateStepDescriptor;

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
