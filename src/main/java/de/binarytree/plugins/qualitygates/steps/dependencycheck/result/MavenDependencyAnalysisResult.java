package de.binarytree.plugins.qualitygates.steps.dependencycheck.result;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class represents the analysis results of a maven dependency check. It
 * contains the found errors.
 * 
 * @author Marcel von Maltitz
 * 
 */
public class MavenDependencyAnalysisResult {
    private Map<DependencyProblemType, List<String>> violationsByType;

    /**
     * Constructs a new structured analysis result based on the given map
     * 
     * @param result
     *            the map to initialize the analysis result whith
     */
    public MavenDependencyAnalysisResult(
            Map<DependencyProblemType, List<String>> result) {
        this.violationsByType = result;
    }

    public MavenDependencyAnalysisResult() {
        this(new HashMap<DependencyProblemType, List<String>>());
    }

    public int getNumberOfUnusedDependencies() {
        return getSizeOrZeroIfNull(this.getUnusedDependencies());
    }

    public int getNumberOfUndeclaredDependencies() {
        return getSizeOrZeroIfNull(this.getUndeclaredDependencies());
    }

    /**
     * Returns the list of undeclared dependencies
     * 
     * @return the list of undeclared dependencies
     */
    public List<String> getUndeclaredDependencies() {
        return this.violationsByType.get(DependencyProblemType.UNDECLARED);
    }

    /**
     * Returns the list of unused dependencies
     * 
     * @return the list of unused dependencies
     */
    public List<String> getUnusedDependencies() {
        return this.violationsByType.get(DependencyProblemType.UNUSED);
    }

    /** 
     * Returns the analysis result as a map of which the keys are the values of {@link DependencyProblemType}
     * @return the analysis result as a map 
     */
    public Map<DependencyProblemType, List<String>> getMapOfViolations() {
        return new HashMap<DependencyProblemType, List<String>>(
                this.violationsByType);
    }

    private int getSizeOrZeroIfNull(List<String> violations) {
        if (violations != null) {
            return violations.size();
        } else {
            return 0;
        }
    }

    /**
     * Adds the given violating dependency to the given problem type but skips it if it is a duplicate.
     * @param currentProblemType the type of the problem 
     * @param violatingDependency the violating dependency
     */
    public void addViolation(DependencyProblemType currentProblemType,
            String violatingDependency) {
        List<String> violations = getListOfViolationsOfType(currentProblemType);
        addViolationIfNotAlreadyPresent(violatingDependency, violations);

    }

    private void addViolationIfNotAlreadyPresent(String violatingDependency,
            List<String> violations) {
        if (!violations.contains(violatingDependency)) {
            violations.add(violatingDependency);
        }
    }

    private List<String> getListOfViolationsOfType(
            DependencyProblemType currentProblemType) {
        List<String> violations = this.violationsByType.get(currentProblemType);
        if (violations == null) {
            violations = new LinkedList<String>();
            this.violationsByType.put(currentProblemType, violations);
        }
        return violations;
    }
}
