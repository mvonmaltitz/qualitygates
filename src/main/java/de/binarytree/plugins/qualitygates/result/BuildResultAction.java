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

import de.binarytree.plugins.qualitygates.GateEvaluator;
import de.binarytree.plugins.qualitygates.checks.Check;
import de.binarytree.plugins.qualitygates.checks.ManualCheck;

public class BuildResultAction implements ProminentProjectAction {


    private final static String ICONS_PREFIX = "/plugin/qualitygates/images/24x24/";

    private GateEvaluator gateEvaluator;

    public BuildResultAction(GateEvaluator gateEvaluator) {
        this.gateEvaluator = gateEvaluator;
    }

    public QualityLineReport getGatesResult() {
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

    public void doApprove(StaplerRequest req, StaplerResponse res) throws IOException {
        if (req.hasParameter("id")) {
            String hashIdOfCheck = req.getParameter("id");
            QualityLineReport qualityLineReport = this.getGatesResult();
            GateReport unbuiltGate = this.getNextUnbuiltGate(qualityLineReport);
            if (unbuiltGate != null) {
                findAndApproveNextManualUnbuiltCheckIfExists(hashIdOfCheck, unbuiltGate);
                AbstractBuild build = getFormerBuild(req);
                BuildListener listener = new StreamBuildListener(getLogfileAppender(build));
                Launcher launcher = this.getLauncher(listener);
                this.gateEvaluator.evaluate(build, launcher, listener);
                build.save();
            }
        }
        res.sendRedirect(".");

    }

    private void findAndApproveNextManualUnbuiltCheckIfExists(String hashIdOfCheck, GateReport unbuiltGate) {
        CheckReport unbuiltCheck = this.getNextUnbuiltCheck(unbuiltGate);
        Check check = unbuiltCheck.getCheck();
        if (check instanceof ManualCheck) {
            approveCheckIfHashMatches(hashIdOfCheck, check);
        } else {
            throw new IllegalStateException("Next unbuilt check is no check which can be approved.");
        }
    }

    private void approveCheckIfHashMatches(String hashIdOfCheck, Check check) {
        ManualCheck manualCheck = (ManualCheck) check;
        if (manualCheck.hasHash(hashIdOfCheck)) {
            manualCheck.approve();
        }
    }

    private AbstractBuild getFormerBuild(StaplerRequest req) {
        return req.findAncestorObject(AbstractBuild.class);
    }

    private FileOutputStream getLogfileAppender(AbstractBuild build) throws FileNotFoundException {
        return new FileOutputStream(build.getLogFile(), true);
    }

    protected Launcher getLauncher(BuildListener listener) {
        return Jenkins.getInstance().createLauncher(listener);
    }

    public GateReport getNextUnbuiltGate(QualityLineReport qualityLineReport) {
        for (GateReport gateReport : qualityLineReport.getGateResults()) {
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

    public CheckReport getNextUnbuiltCheck(GateReport gateReport) {
        for (CheckReport checkReport : gateReport.getCheckResults()) {
            if (isNotBuilt(checkReport)) {
                return checkReport;
            }
        }
        return null;
    }

    private boolean isNotBuilt(CheckReport checkReport) {
        return checkReport.getResult().equals(Result.NOT_BUILT);
    }
}
