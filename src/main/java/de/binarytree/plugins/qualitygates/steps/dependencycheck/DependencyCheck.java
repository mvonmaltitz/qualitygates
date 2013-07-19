package de.binarytree.plugins.qualitygates.steps.dependencycheck;

import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import java.io.File;
import java.io.IOException;

import de.binarytree.plugins.qualitygates.GateStep;
import de.binarytree.plugins.qualitygates.result.GateStepReport;
import de.binarytree.plugins.qualitygates.steps.dependencycheck.parser.BuildLogFileParser;

public abstract class DependencyCheck extends GateStep {

    public DependencyCheck() {
        super();
    }


    protected abstract void processBuildLog(AbstractBuild build, GateStepReport checkReport)
            throws IOException;

    @Override
    public void doStep(AbstractBuild build, Launcher launcher, BuildListener listener, GateStepReport checkReport) {
        if (!buildExists(build)) {
            failCheckDueToInexistententBuild(checkReport);
        } else {
            try {
                processBuildLog(build, checkReport);
            } catch (IOException e) {
                failStepWithExceptionAsReason(checkReport, e);
            }
        }
    
    }

    private boolean buildExists(AbstractBuild build) {
        Result result = build.getResult();
        return Result.SUCCESS.equals(result) || Result.UNSTABLE.equals(result);
    }

    private void failCheckDueToInexistententBuild(GateStepReport checkReport) {
        checkReport.setResult(Result.FAILURE,
                "Cannot proceed, build has not been successful");
    }

    protected BuildLogFileParser parseBuildLogFile(AbstractBuild build) throws IOException {
        File logFile = build.getLogFile();
        BuildLogFileParser logFileParser = createLogFileParser();
        logFileParser.parseLogFile(logFile);
        return logFileParser;
    }

    protected BuildLogFileParser createLogFileParser() {
        return new BuildLogFileParser();
    }


}