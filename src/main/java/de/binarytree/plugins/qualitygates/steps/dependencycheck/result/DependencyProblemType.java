package de.binarytree.plugins.qualitygates.steps.dependencycheck.result;

import java.util.regex.Pattern;

/**
 * Types of dependency problems. Namely unused declared and used undeclared
 * dependencies.
 * 
 * @author mvm
 * 
 */
public enum DependencyProblemType {
    UNUSED(".*Unused declared.*"), UNDECLARED(".*Used undeclared.*");

    private Pattern pattern;

    private DependencyProblemType(String regex) {
        pattern = Pattern.compile(regex);
    }

    public static DependencyProblemType matchAny(String line) {
        for (DependencyProblemType problem : DependencyProblemType.values()) {
            if (problem.pattern.matcher(line).matches()) {
                return problem;
            }
        }
        return null;
    }
};
