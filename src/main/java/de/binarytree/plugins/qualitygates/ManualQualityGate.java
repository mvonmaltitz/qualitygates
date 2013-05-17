package de.binarytree.plugins.qualitygates;

import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import de.binarytree.plugins.qualitygates.result.GateResult;

public class ManualQualityGate extends QualityGate {

	public ManualQualityGate(String name) {
		super(name, null);
	}

	@Override
	public void doCheck(AbstractBuild build, Launcher launcher,
			BuildListener listener, GateResult gateResult) {
		// TODO Auto-generated method stub

	}

}
