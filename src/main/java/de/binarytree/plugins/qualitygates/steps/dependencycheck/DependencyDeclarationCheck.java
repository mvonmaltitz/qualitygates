package de.binarytree.plugins.qualitygates.steps.dependencycheck;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.Result;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import de.binarytree.plugins.qualitygates.GateStepDescriptor;
import de.binarytree.plugins.qualitygates.result.GateStepReport;
import de.binarytree.plugins.qualitygates.steps.dependencycheck.parser.BuildLogFileParser;
import de.binarytree.plugins.qualitygates.steps.dependencycheck.parser.BuildLogFileParser.Goal;
import de.binarytree.plugins.qualitygates.steps.dependencycheck.parser.DependencyAnalysisParser;
import de.binarytree.plugins.qualitygates.steps.dependencycheck.result.MavenDependencyAnalysisResult;

public class DependencyDeclarationCheck extends DependencyCheck {

    public static final Logger LOGGER = Logger.getLogger(DependencyDeclarationCheck.class
            .toString());

    @DataBoundConstructor
    public DependencyDeclarationCheck() {
    }

    @Override
    protected void processBuildLog(AbstractBuild build, GateStepReport checkReport)
            throws IOException {
        String dependencySection = obtainDependencySection(build);
        if (!dependencySectionWasFound(dependencySection)) {
            setCheckReportToUnstableDueToMissingDependencySection(checkReport);
        } else {
            MavenDependencyAnalysisResult dependencyProblems = analyseDependencySection(dependencySection);
            gatherViolationsAndSetCheckReport(checkReport,
                    dependencyProblems);
        }
    }

    private String obtainDependencySection(AbstractBuild build)
            throws IOException {
        BuildLogFileParser logFileParser = parseBuildLogFile(build);
        String dependencySection = logFileParser.getContentOfSectionFor(Goal.DEPENDENCY_ANALYSE); 
        return dependencySection;
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

    protected MavenDependencyAnalysisResult analyseDependencySection(String dependencySection)
            throws IOException {
        return DependencyAnalysisParser
                .parseDependencyAnalyzeSection(dependencySection);
    }

    private void gatherViolationsAndSetCheckReport(GateStepReport checkReport,
            MavenDependencyAnalysisResult analysis) {
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
        return "Dependency Declaration Check"; 
    }

}