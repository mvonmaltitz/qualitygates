package de.binarytree.plugins.qualitygates.checks.dependencyanalyzer.result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalysisResult {
    private Map<DependencyProblemType, List<String>> violations;

    public AnalysisResult(Map<DependencyProblemType, List<String>> result) {
        this.violations = result;
    }

    public int getNumberOfUnusedDependencies() {
        return getSizeOrZeroIfNull(this.getUnusedDependencies());
    }

    public int getNumberOfUndeclaredDependencies() {
        return getSizeOrZeroIfNull(this.getUndeclaredDependencies());
    }

    public List<String> getUndeclaredDependencies() {
        return this.violations.get(DependencyProblemType.UNDECLARED);
    }

    public List<String> getUnusedDependencies() {
        return this.violations.get(DependencyProblemType.UNUSED);
    }

    public Map<DependencyProblemType, List<String>> getMapOfViolations() {
        return new HashMap<DependencyProblemType, List<String>>(this.violations);
    }

    private int getSizeOrZeroIfNull(List<String> violations) {
        if (violations != null) {
            return this.getUnusedDependencies().size();
        } else {
            return 0;
        }
    }
}
