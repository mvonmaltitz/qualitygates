package de.binarytree.plugins.qualitygates.steps.dependencycheck.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import de.binarytree.plugins.qualitygates.steps.dependencycheck.result.BannedDependencyAnalysisResult;

/**
 * This class parses the given string and extracts information about found banned dependencies.
 * 
 * @author Marcel von Maltitz
 * 
 */
public final class BannedDependencyParser {

    private BannedDependencyParser() {
    }

    private static final Pattern ARTIFACT_PATTERN = Pattern.compile("Found Banned Dependency: (.*:.*:.*:.*)");

    /**
     * Analyzes the dependency section and transforms the result into a structured analysis result
     * 
     * @param content
     *            the string to be analyzed
     * @return a structured result
     * @throws IOException
     *             when the log file could not be accessed or read
     */
    public static BannedDependencyAnalysisResult parseDependencyAnalyzeSection(String content) throws IOException {

        BannedDependencyAnalysisResult result = new BannedDependencyAnalysisResult();
        List<String> lines = IOUtils.readLines(new StringReader(content));
        for (String line : lines) {
            analyzeLine(result, line);
        }
        return result;
    }

    private static void analyzeLine(BannedDependencyAnalysisResult result, String line) {
        if (!StringUtils.isBlank(line)) {
            Matcher matcher = ARTIFACT_PATTERN.matcher(line);
            if (matcher.matches()) {
                result.addBannedDependency(getDependencyAndAddToResult(matcher));
            }
        }
    }

    private static String getDependencyAndAddToResult(Matcher matcher) {
        return matcher.group(1);
    }
}
