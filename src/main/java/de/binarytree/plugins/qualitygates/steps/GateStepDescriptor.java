package de.binarytree.plugins.qualitygates.steps;

import hudson.ExtensionPoint;
import hudson.model.Descriptor;

public abstract class GateStepDescriptor extends Descriptor<GateStep> implements
        ExtensionPoint {

    protected GateStepDescriptor(Class<? extends GateStep> clazz) {
        super(clazz);
    }

    protected GateStepDescriptor() {
    }
}
