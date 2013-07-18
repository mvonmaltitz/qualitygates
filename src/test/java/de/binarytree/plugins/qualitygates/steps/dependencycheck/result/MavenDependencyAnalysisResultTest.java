package de.binarytree.plugins.qualitygates.steps.dependencycheck.result;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.binarytree.plugins.qualitygates.steps.dependencycheck.result.MavenDependencyAnalysisResult;
import de.binarytree.plugins.qualitygates.steps.dependencycheck.result.DependencyProblemType;

public class MavenDependencyAnalysisResultTest {

    private String violation;
    private MavenDependencyAnalysisResult analysis;
    private String violation2;

    @Before
    public void setUp() {
        analysis = new MavenDependencyAnalysisResult();
        violation = "Violation";
        violation2 = "Violation2";
    }

    @Test
    public void testCountOfUndeclaredIsCorrect() {
        analysis.addViolation(DependencyProblemType.UNDECLARED, violation);
        analysis.addViolation(DependencyProblemType.UNDECLARED, violation2);
        assertEquals(2, analysis.getNumberOfUndeclaredDependencies());
        assertEquals(0, analysis.getNumberOfUnusedDependencies());
    }

    @Test
    public void testCountOfUnusedIsCorrect() {
        MavenDependencyAnalysisResult analysis = new MavenDependencyAnalysisResult();
        String violation = "Violation";
        String violation2 = "Violation2";
        analysis.addViolation(DependencyProblemType.UNUSED, violation);
        analysis.addViolation(DependencyProblemType.UNUSED, violation2);
        assertEquals(0, analysis.getNumberOfUndeclaredDependencies());
        assertEquals(2, analysis.getNumberOfUnusedDependencies());
    }

    @Test
    public void testDuplicatesGetEliminated() {
        MavenDependencyAnalysisResult analysis = new MavenDependencyAnalysisResult();
        String violation = "Violation";
        String violation2 = "Violation";
        analysis.addViolation(DependencyProblemType.UNUSED, violation);
        analysis.addViolation(DependencyProblemType.UNUSED, violation2);
        assertEquals(0, analysis.getNumberOfUndeclaredDependencies());
        assertEquals(1, analysis.getNumberOfUnusedDependencies());
    }

}
