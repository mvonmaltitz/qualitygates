package de.binarytree.plugins.qualitygates.steps.dependencycheck;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import de.binarytree.plugins.qualitygates.GateStep;
import de.binarytree.plugins.qualitygates.GateStepDescriptor;
import de.binarytree.plugins.qualitygates.result.GateStepReport;
import de.binarytree.plugins.qualitygates.steps.dependencycheck.parser.BuildLogFileParser;
import de.binarytree.plugins.qualitygates.steps.dependencycheck.parser.DependencyAnalysisParser;
import de.binarytree.plugins.qualitygates.steps.dependencycheck.result.AnalysisResult;

public class DependencyCheck extends GateStep {

    public static final Logger LOGGER = Logger.getLogger(DependencyCheck.class
            .toString());

    @DataBoundConstructor
    public DependencyCheck() {
    }

    @Override
    public void doStep(AbstractBuild build, Launcher launcher,
            BuildListener listener, GateStepReport checkReport) {
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

    private void processBuildLog(AbstractBuild build, GateStepReport checkReport)
            throws IOException {
        String dependencySection = obtainDependencySection(build);
        if (!dependencySectionWasFound(dependencySection)) {
            setCheckReportToUnstableDueToMissingDependencySection(checkReport);
        } else {
            AnalysisResult dependencyProblems = analyseDependencySection(dependencySection);
            gatherViolationsAndSetCheckReport(checkReport,
                    dependencyProblems);
        }
    }

    private String obtainDependencySection(AbstractBuild build)
            throws IOException {
        BuildLogFileParser logFileParser = parseBuildLogFile(build);
        String dependencySection = logFileParser
                .getDependencyAnalyseBlock();
        return dependencySection;
    }


    private BuildLogFileParser parseBuildLogFile(AbstractBuild build)
            throws IOException {
        File logFile = build.getLogFile();
        BuildLogFileParser logFileParser = createLogFileParser();
        logFileParser.parseLogFile(logFile);
        return logFileParser;
    }

    protected BuildLogFileParser createLogFileParser() {
        return new BuildLogFileParser();
    }

    private boolean dependencySectionWasFound(String dependencySection) {
        return !StringUtils.isBlank(dependencySection);
    }

    /**
     * @param checkReport
     */
    private void setCheckReportToUnstableDueToMissingDependencySection(
            GateStepReport checkReport) {
        LOGGER.info("No dependency section found. Add dependency:analyze on your job configuration.");
        checkReport
                .setResult(
                        Result.UNSTABLE,
                        "No dependency section found. Add dependency:analyze on your job configuration.");
    }

    protected AnalysisResult analyseDependencySection(String dependencySection)
            throws IOException {
        return DependencyAnalysisParser
                .parseDependencyAnalyzeSection(dependencySection);
    }

    private void gatherViolationsAndSetCheckReport(GateStepReport checkReport,
            AnalysisResult analysis) {
        int numberOfUndeclaredDependencies = analysis
                .getNumberOfUndeclaredDependencies();
        int numberOfUnusedDependencies = analysis
                .getNumberOfUnusedDependencies();
        setCheckResultDependingOnNumberOfViolations(checkReport,
                numberOfUndeclaredDependencies, numberOfUnusedDependencies);
    }

    private void setCheckResultDependingOnNumberOfViolations(
            GateStepReport checkReport, int numberOfUndeclaredDependencies,
            int numberOfUnusedDependencies) {
        if ((numberOfUndeclaredDependencies + numberOfUnusedDependencies) > 0) {
            checkReport.setResult(Result.UNSTABLE,
                    numberOfUndeclaredDependencies + " undeclared and "
                            + numberOfUnusedDependencies
                            + " unused dependencies found.");
        } else {
            checkReport.setResult(Result.SUCCESS,
                    "No dependency violations found");
        }
    }

    @Extension
    public static class DescriptorImpl extends GateStepDescriptor {

        @Override
        public String getDisplayName() {
            return "Maven Dependency Check";
        }

    }

    @Override
    public String getDescription() {
        return "Maven Dependency Check";
    }

}
