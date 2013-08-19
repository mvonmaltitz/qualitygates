package de.binarytree.plugins.qualitygates.steps.dependencycheck.result;

import java.util.regex.Pattern;

/**
 * Types of dependency problems. Namely unused declared and used undeclared
 * dependencies.
 * 
 * @author Marcel von Maltitz
 * 
 */
public enum DependencyProblemType {
    UNUSED(".*Unused declared.*"), UNDECLARED(".*Used undeclared.*");

    private Pattern pattern;

    private DependencyProblemType(String regex) {
        pattern = Pattern.compile(regex);
    }

    /**
     * Returns the type of which the regex pattern matches the given string
     * @param line the line to be matched by the pattern
     * @return the according problem type
     */
    public static DependencyProblemType matchAny(String line) {
        for (DependencyProblemType problem : DependencyProblemType.values()) {
            if (problem.pattern.matcher(line).matches()) {
                return problem;
            }
        }
        return null;
    }
};
