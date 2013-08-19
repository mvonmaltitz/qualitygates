package de.binarytree.plugins.qualitygates.steps.dependencycheck.parser;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * This class groups a set of information about banned dependencies.
 * 
 * @author Marcel von Maltitz
 * 
 */
public class BannedDependencyAnalysisResult {
    private Set<String> bannedDependencies = new HashSet<String>();

    public void addBannedDependency(String bannedDependency) {
        this.bannedDependencies.add(bannedDependency);
    }

    public int getNumberOfBannedDependencies() {
        return this.bannedDependencies.size();
    }

    public List<String> getBannedDependencies() {
        return new LinkedList<String>(this.bannedDependencies);
    }
}
