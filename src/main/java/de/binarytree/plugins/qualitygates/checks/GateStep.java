package de.binarytree.plugins.qualitygates.checks;

import hudson.DescriptorExtensionList;
import hudson.ExtensionPoint;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Describable;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.Hudson;

import java.util.Arrays;

import de.binarytree.plugins.qualitygates.result.GateStepReport;

public abstract class GateStep implements Describable<GateStep>, ExtensionPoint {

	public GateStepReport step(AbstractBuild build, BuildListener listener,
			Launcher launcher) {
		GateStepReport stepReport = this.document();
		try {
			this.doStep(build, listener, launcher, stepReport);
		} catch (Exception e) {
			failStepAndlogExceptionInCheckReport(stepReport, e);
		}
		return stepReport;
	}

	public GateStepReport document() {
		return new GateStepReport(this);
	}

	protected void failStepAndlogExceptionInCheckReport(
			GateStepReport stepReport, Exception e) {
		stepReport.setResult(Result.FAILURE,
				e.getMessage() + Arrays.toString(e.getStackTrace()));
	}

	public abstract void doStep(AbstractBuild build, BuildListener listener,
			Launcher launcher, GateStepReport checkReport);

	public abstract String getDescription();

	@Override
	public String toString() {
		return "GateStep [" + this.getDescriptor().getDisplayName() + "]";
	}

	public GateStepDescriptor getDescriptor() {
		return (GateStepDescriptor) Hudson.getInstance().getDescriptor(
				getClass());
	}

	public static DescriptorExtensionList<GateStep, GateStepDescriptor> all() {
		return Hudson.getInstance()
				.<GateStep, GateStepDescriptor> getDescriptorList(
						GateStep.class);
	}

}
