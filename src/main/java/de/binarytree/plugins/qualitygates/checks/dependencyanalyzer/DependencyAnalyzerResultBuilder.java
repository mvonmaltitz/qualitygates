package de.binarytree.plugins.qualitygates.checks.dependencyanalyzer;

import hudson.maven.MavenBuild;
import hudson.maven.MavenModule;
import hudson.maven.MavenModuleSetBuild;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import de.binarytree.plugins.qualitygates.checks.dependencyanalyzer.parser.BuildLogFileParser;
import de.binarytree.plugins.qualitygates.checks.dependencyanalyzer.parser.DependencyAnalysisParser;
import de.binarytree.plugins.qualitygates.checks.dependencyanalyzer.result.BuildResult;
import de.binarytree.plugins.qualitygates.checks.dependencyanalyzer.result.DependencyProblemType;
import de.binarytree.plugins.qualitygates.checks.dependencyanalyzer.result.ModuleResult;

public class DependencyAnalyzerResultBuilder {
    public final static Logger LOGGER = Logger.getLogger(DependencyAnalyzerResultBuilder.class.toString());

    public static BuildResult buildResult(MavenModuleSetBuild build) throws IOException {
        Map<MavenModule, List<MavenBuild>> moduleBuilds = build.getModuleBuilds();

        Iterator<MavenModule> iterator = moduleBuilds.keySet().iterator();

        BuildResult analysisResult = new BuildResult();

        while (iterator.hasNext()) {
            List<MavenBuild> builds = moduleBuilds.get(iterator.next());
            // Desactivated modules have no builds
            if (builds.size() > 0) {
                MavenBuild moduleBuild = builds.get(0);

                File logFile = moduleBuild.getLogFile();
                MavenModule mavenModule = moduleBuild.getProject();

                ModuleResult moduleResult = buildModuleResult(mavenModule, logFile);

                analysisResult.addResult(moduleResult);
            }
        }

        return analysisResult;
    }

    private static ModuleResult buildModuleResult(MavenModule module, File logFile) throws IOException {
        ModuleResult moduleResult = new ModuleResult();

        BuildLogFileParser logFileParser = new BuildLogFileParser();
        logFileParser.parseLogFile(logFile);

        // extracting dependency section from log file
        String dependencySection = logFileParser.getDependencyAnalyseBlock();

        if (StringUtils.isBlank(dependencySection)) {
            LOGGER.info("No dependency section found. Add dependency:analyze on your job configuration.");
            return moduleResult;
        }

        // extracting informations from dependency section
        Map<DependencyProblemType, List<String>> dependencyProblems = DependencyAnalysisParser
                .parseDependencyAnalyzeSection(dependencySection).getMapOfViolations();

        // populating result
        moduleResult.setModuleName(module.getModuleName());
        moduleResult.setDisplayName(module.getDisplayName());
        moduleResult.setDependencyProblems(dependencyProblems);

        return moduleResult;
    }
}
