package de.binarytree.plugins.qualitygates.checks.dependencyanalyzer;

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

import de.binarytree.plugins.qualitygates.checks.Check;
import de.binarytree.plugins.qualitygates.checks.CheckDescriptor;
import de.binarytree.plugins.qualitygates.checks.dependencyanalyzer.parser.BuildLogFileParser;
import de.binarytree.plugins.qualitygates.checks.dependencyanalyzer.parser.DependencyAnalysisParser;
import de.binarytree.plugins.qualitygates.checks.dependencyanalyzer.result.AnalysisResult;
import de.binarytree.plugins.qualitygates.result.CheckReport;

public class DependencyCheck extends Check {

    public final static Logger LOGGER = Logger.getLogger(DependencyCheck.class.toString());

    @DataBoundConstructor
    public DependencyCheck() {
    }

    @Override
    public void doCheck(AbstractBuild build, BuildListener listener, Launcher launcher, CheckReport checkReport) {
        if (!buildExists(build)) {
            checkReport.setResult(Result.FAILURE, "Cannot proceed, build has not been successful");
        } else {
            try {
                BuildLogFileParser logFileParser = parseBuildLogFile(build);
                String dependencySection = logFileParser.getDependencyAnalyseBlock();

                if (!dependencySectionWasFound(dependencySection)) {
                    setCheckReportToUnstableDueToMissingDependencySection(checkReport);
                } else {
                    AnalysisResult dependencyProblems = analyseDependencySection(dependencySection);
                    gatherViolationsAndSetCheckReport(checkReport, dependencyProblems);
                }
            } catch (IOException e) {
                failCheckAndlogExceptionInCheckReport(checkReport, e);
            }
        }

    }

	protected AnalysisResult analyseDependencySection(String dependencySection)
			throws IOException {
		return DependencyAnalysisParser
		        .parseDependencyAnalyzeSection(dependencySection);
	}

    /**
     * @param checkReport
     */
    private void setCheckReportToUnstableDueToMissingDependencySection(CheckReport checkReport) {
        LOGGER.info("No dependency section found. Add dependency:analyze on your job configuration.");
        checkReport.setResult(Result.UNSTABLE,
                "No dependency section found. Add dependency:analyze on your job configuration.");
    }

    private boolean dependencySectionWasFound(String dependencySection) {
        return !StringUtils.isBlank(dependencySection);
    }

    private BuildLogFileParser parseBuildLogFile(AbstractBuild build) throws IOException {
        File logFile = build.getLogFile();
        BuildLogFileParser logFileParser = createLogFileParser();
        logFileParser.parseLogFile(logFile);
        return logFileParser;
    }

	protected BuildLogFileParser createLogFileParser() {
		BuildLogFileParser logFileParser = new BuildLogFileParser();
		return logFileParser;
	}

    private void gatherViolationsAndSetCheckReport(CheckReport checkReport, AnalysisResult analysis) {
        int numberOfUndeclaredDependencies = analysis.getNumberOfUndeclaredDependencies();
        int numberOfUnusedDependencies = analysis.getNumberOfUnusedDependencies();
        setCheckResultDependingOnNumberOfViolations(checkReport, numberOfUndeclaredDependencies,
                numberOfUnusedDependencies);
    }

    private boolean buildExists(AbstractBuild build) {
        Result result = build.getResult();
        return Result.SUCCESS.equals(result) || Result.UNSTABLE.equals(result);
    }

    private void setCheckResultDependingOnNumberOfViolations(CheckReport checkReport,
            int numberOfUndeclaredDependencies, int numberOfUnusedDependencies) {
        if ((numberOfUndeclaredDependencies + numberOfUnusedDependencies) > 0) {
            checkReport.setResult(Result.UNSTABLE, numberOfUndeclaredDependencies + " undeclared and "
                    + numberOfUnusedDependencies + " unused dependencies found.");
        } else {
            checkReport.setResult(Result.SUCCESS, "No dependency violations found");
        }
    }

    @Extension
    public static class DescriptorImpl extends CheckDescriptor {

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
