package de.binarytree.plugins.qualitygates.result;

import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.ProminentProjectAction;
import hudson.model.Result;
import hudson.model.StreamBuildListener;
import hudson.model.AbstractBuild;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import jenkins.model.Jenkins;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import de.binarytree.plugins.qualitygates.QualityLineEvaluator;
import de.binarytree.plugins.qualitygates.checks.GateStep;
import de.binarytree.plugins.qualitygates.checks.ManualCheck;

public class BuildResultAction implements ProminentProjectAction {

	private static final String ICONS_PREFIX = "/plugin/qualitygates/images/24x24/";

	private QualityLineEvaluator gateEvaluator;

	public BuildResultAction(QualityLineEvaluator gateEvaluator) {
		this.gateEvaluator = gateEvaluator;
	}

	public QualityLineReport getQualityLineReport() {
		return this.gateEvaluator.getLatestResults();
	}

	public String getIconFileName() {
		return ICONS_PREFIX + "qualitygate_icon.png";
	}

	public String getDisplayName() {
		return "Execution Results of Quality Gates";
	}

	public String getUrlName() {
		return "qualitygates";
	}

	public void doApprove(StaplerRequest req, StaplerResponse res)
			throws IOException {
		if (req.hasParameter("id")) {
			String hashIdOfCheck = req.getParameter("id");
			QualityLineReport qualityLineReport = this.getQualityLineReport();
			GateReport unbuiltGate = this.getNextUnbuiltGate(qualityLineReport);
			if (unbuiltGate != null) {
				findAndApproveNextManualUnbuiltCheckIfExists(hashIdOfCheck,
						unbuiltGate);
				AbstractBuild build = getFormerBuild(req);
				BuildListener listener = new StreamBuildListener(
						getLogfileAppender(build));
				Launcher launcher = this.getLauncher(listener);
				this.gateEvaluator.evaluate(build, launcher, listener);
				build.save();
			}
		}
		res.sendRedirect(".");

	}

	private void findAndApproveNextManualUnbuiltCheckIfExists(
			String hashIdOfCheck, GateReport unbuiltGate) {
		GateStepReport reportOfNextUnbuiltStep = this
				.getNextUnbuiltStep(unbuiltGate);
		GateStep step = reportOfNextUnbuiltStep.getStep();
		if (step instanceof ManualCheck) {
			approveCheckIfHashMatches(hashIdOfCheck, (ManualCheck) step);
		} else {
			throw new IllegalStateException(
					"Next unbuilt step is no check which can be approved.");
		}
	}

	private void approveCheckIfHashMatches(String hashIdOfCheck,
			ManualCheck manualCheck) {
		if (manualCheck.hasHash(hashIdOfCheck)) {
			manualCheck.approve();
		}
	}

	private AbstractBuild getFormerBuild(StaplerRequest req) {
		return req.findAncestorObject(AbstractBuild.class);
	}

	private FileOutputStream getLogfileAppender(AbstractBuild build)
			throws FileNotFoundException {
		return new FileOutputStream(build.getLogFile(), true);
	}

	protected Launcher getLauncher(BuildListener listener) {
		return Jenkins.getInstance().createLauncher(listener);
	}

	public GateReport getNextUnbuiltGate(QualityLineReport qualityLineReport) {
		for (GateReport gateReport : qualityLineReport.getGateReports()) {
			if (isPassedGate(gateReport)) {
				continue;
			} else if (isNotBuilt(gateReport)) {
				return gateReport;
			} else {
				return null;
			}
		}
		return null;
	}

	private boolean isNotBuilt(GateReport gateReport) {
		return gateReport.getResult().equals(Result.NOT_BUILT);
	}

	private boolean isPassedGate(GateReport gateReport) {
		return gateReport.getResult().isBetterOrEqualTo(Result.UNSTABLE);
	}

	public GateStepReport getNextUnbuiltStep(GateReport gateReport) {
		for (GateStepReport stepReport : gateReport.getStepReports()) {
			if (isNotBuilt(stepReport)) {
				return stepReport;
			}
		}
		return null;
	}

	private boolean isNotBuilt(GateStepReport stepReport) {
		return stepReport.getResult().equals(Result.NOT_BUILT);
	}
}
