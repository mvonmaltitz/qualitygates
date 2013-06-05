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

    private static final String MANUALLY_APPROVED = "Manually approved";

    private final String ICONS_PREFIX = "/plugin/qualitygates/images/24x24/";

    private GateEvaluator gateEvaluator;

    public BuildResultAction(GateEvaluator gateEvaluator) {
        this.gateEvaluator = gateEvaluator;
    }

    public GatesResult getGatesResult() {
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
            GatesResult gatesResult = this.getGatesResult();
            GateResult unbuiltGate = this.getNextUnbuiltGate(gatesResult);
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

    private void findAndApproveNextManualUnbuiltCheckIfExists(String hashIdOfCheck, GateResult unbuiltGate) {
        CheckResult unbuiltCheck = this.getNextUnbuiltCheck(unbuiltGate);
        Check check = unbuiltCheck.getCheck();
        if (check instanceof ManualCheck) {
            approveCheckIfHashMatches(hashIdOfCheck, check);
        } else {
            throw new IllegalArgumentException("Next unbuilt check is no check which can be approved.");
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

    public GateResult getNextUnbuiltGate(GatesResult gatesResult) {
        for (GateResult gateResult : gatesResult.getGateResults()) {
            if (isPassedGate(gateResult)) {
                continue;
            } else if (isNotBuilt(gateResult)) {
                return gateResult;
            } else {
                return null;
            }
        }
        return null;
    }

    private boolean isNotBuilt(GateResult gateResult) {
        return gateResult.getResult().equals(Result.NOT_BUILT);
    }

    private boolean isPassedGate(GateResult gateResult) {
        return gateResult.getResult().isBetterOrEqualTo(Result.UNSTABLE);
    }

    public CheckResult getNextUnbuiltCheck(GateResult gateResult) {
        for (CheckResult checkResult : gateResult.getCheckResults()) {
            if (isNotBuilt(checkResult)) {
                return checkResult;
            }
        }
        return null;
    }

    private boolean isNotBuilt(CheckResult checkResult) {
        return checkResult.getResult().equals(Result.NOT_BUILT);
    }
}
