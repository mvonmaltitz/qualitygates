package de.binarytree.plugins.qualitygates.steps.dependencycheck.result;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MavenDependencyAnalysisResult {
    private Map<DependencyProblemType, List<String>> violationsByType;

    public MavenDependencyAnalysisResult(Map<DependencyProblemType, List<String>> result) {
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

    public List<String> getUndeclaredDependencies() {
        return this.violationsByType.get(DependencyProblemType.UNDECLARED);
    }

    public List<String> getUnusedDependencies() {
        return this.violationsByType.get(DependencyProblemType.UNUSED);
    }

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
