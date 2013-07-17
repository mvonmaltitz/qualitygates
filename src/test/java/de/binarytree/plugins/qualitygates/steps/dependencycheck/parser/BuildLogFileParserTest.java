package de.binarytree.plugins.qualitygates.steps.dependencycheck.parser;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.StringReader;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.binarytree.plugins.qualitygates.steps.dependencycheck.parser.BuildLogFileParser.Goal;

public class BuildLogFileParserTest extends AbstractParserTestUtils {

    BuildLogFileParser parser;

    @Before
    public void setUp() {
        parser = new BuildLogFileParser();
    }

    @Test
    public void testGoalPatternMatches() {
        String header = "[INFO] --- maven-dependency-plugin:2.1:analyze (default-cli) @ projectone ---";
        Goal goal = BuildLogFileParser.Goal.getMatchingGoal(header);
        assertNotNull(goal);
    }

    @Test
    public void testGetDependencyAnalyzeSectionNotPresent() throws Exception {
        File file = getFile("log_build_without_dependency_analyze");

        parser.parseLogFile(file);

        Assert.assertNull("No dependency:analyze block must be found", parser.getDependencyAnalyseBlock());

    }

    @Test
    public void testGetDependencyAnalyzeSectionEmpty() throws Exception {
        File file = getFile("log_build_with_empty_dependency_analyze");

        parser.parseLogFile(file);

        Assert.assertNotNull("No dependency:analyze block found", parser.getDependencyAnalyseBlock());
    }

    @Test
    public void testGetDependencyAnalyseSectionEmptyWithExecutionId() throws Exception {
        File file = getFile("log_build_with_empty_dependency_analyze_with_execution_id");

        parser.parseLogFile(file);

        Assert.assertNotNull("No dependency:analyze block found", parser.getDependencyAnalyseBlock());
    }

    @Test
    public void testGetDependencyFromRealProjectLog() throws Exception {
        File file = getFile("log_build_with_unused");

        parser.parseLogFile(file);

        String result = parser.getDependencyAnalyseBlock();

        Assert.assertNotNull("dependency:analyze block must be found", result);

        List<String> lines = IOUtils.readLines(new StringReader(result));
        Assert.assertEquals("Wrong number of line returned, ", 48, lines.size());

    }

    @Test
    public void testGetDependencyAnalyzeSectionPresent() throws Exception {
        File file = getFile("log_build_with_dependency_analyze");

        parser.parseLogFile(file);

        String result = parser.getDependencyAnalyseBlock();

        Assert.assertNotNull("dependency:analyze block must be found", result);

        List<String> lines = IOUtils.readLines(new StringReader(result));
        Assert.assertEquals("Wrong number of line returned, ", 6, lines.size());

    }

    @Test
    public void testGetDependencyAnalyzeOnlySectionPresent() throws Exception {
        File file = getFile("log_build_with_dependency_analyze_only");

        parser.parseLogFile(file);

        String result = parser.getDependencyAnalyseBlock();

        Assert.assertNotNull("dependency:analyze-only block must be found", result);

        List<String> lines = IOUtils.readLines(new StringReader(result));
        Assert.assertEquals("Wrong number of line returned, ", 10, lines.size());

    }

    @Test
    public void testGetDependencyAnalyseSectionPresentWithExecutionId() throws Exception {
        File file = getFile("log_build_with_dependency_analyze_with_execution_id");

        parser.parseLogFile(file);

        String result = parser.getDependencyAnalyseBlock();

        Assert.assertNotNull("dependency:analyze block must be found", result);
        List<String> lines = IOUtils.readLines(new StringReader(result));
        Assert.assertEquals("Wrong number of line returned, ", 3, lines.size());

    }

    @Test
    public void testGetBannedDependencyAnalyseSectionPresent() throws Exception {
        File file = getFile("log_build_with_banned_dependencies");

        parser.parseLogFile(file);

        String result = parser.getBannedDependencyAnalyseBlock();

        Assert.assertNotNull("dependency:analyze block must be found", result);
        List<String> lines = IOUtils.readLines(new StringReader(result));
        System.out.println(result);
        Assert.assertEquals("Wrong number of line returned, ", 15, lines.size());

    }
}
