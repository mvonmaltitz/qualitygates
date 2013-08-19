package de.binarytree.plugins.qualitygates.steps.dependencycheck.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import de.binarytree.plugins.qualitygates.steps.dependencycheck.result.MavenDependencyAnalysisResult;
import de.binarytree.plugins.qualitygates.steps.dependencycheck.result.DependencyProblemType;

/**
 * Parse the content of dependency:* sections and organize the detected
 * problems.
 * 
 * @author Vincent Sellier
 */
public final class DependencyAnalysisParser {
    private DependencyAnalysisParser() {
    }

    private static final Pattern ARTIFACT_PATTERN = Pattern
            .compile(".*:.*:.*:.*:.*");

    /**
     * Types of possible dependency problems.
     * 
     * @author Marcel von Maltitz
     * 
     */
    public static enum DependencyProblemTypesDetection {
        UNUSED(DependencyProblemType.UNUSED, ".*Unused declared.*"), UNDECLARED(
                DependencyProblemType.UNDECLARED, ".*Used undeclared.*");

        private Pattern pattern;

        private DependencyProblemType problemType;

        private DependencyProblemTypesDetection(
                DependencyProblemType problemType, String regex) {
            this.problemType = problemType;
            pattern = Pattern.compile(regex);
        }

        private DependencyProblemType getProblemType() {
            return problemType;
        }

        /**
         * Returns the problem type of which the regex pattern matches the given
         * string
         * 
         * @param line
         *            the string to be analysed
         * @return the type which of problem found in the given string
         */
        public static DependencyProblemType matchAny(String line) {
            for (DependencyProblemTypesDetection problem : DependencyProblemTypesDetection
                    .values()) {
                if (problem.pattern.matcher(line).matches()) {
                    return problem.getProblemType();
                }
            }
            return null;
        }
    };

    /**
     * Analyzes the given content of the dependency section and transforms into
     * a structured analysis result.
     * 
     * @param content the log section content to be analyzed 
     * @return a structured result of the analysis
     * @throws IOException  when reading the log failed
     */
    public static MavenDependencyAnalysisResult parseDependencyAnalyzeSection(
            String content) throws IOException {

        MavenDependencyAnalysisResult result = new MavenDependencyAnalysisResult();
        List<String> lines = IOUtils.readLines(new StringReader(content));

        DependencyProblemType currentProblemType = null;
        for (String line : lines) {
            if (!StringUtils.isBlank(line)) {
                DependencyProblemType problemType = DependencyProblemTypesDetection
                        .matchAny(line);
                if (problemType != null) {
                    currentProblemType = problemType;
                } else {
                    if (currentProblemType != null
                            && ARTIFACT_PATTERN.matcher(line).matches()) {
                        // removing log level
                        String violatingDependency = line.substring(
                                line.lastIndexOf(']') + 1).trim();
                        result.addViolation(currentProblemType,
                                violatingDependency);
                    }
                }
            }
        }

        return result;
    }
}
