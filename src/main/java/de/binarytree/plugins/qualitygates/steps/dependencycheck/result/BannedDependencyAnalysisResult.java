package de.binarytree.plugins.qualitygates.steps.dependencycheck.result;

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

    /**
     * Adds a banned dependency to the set
     * 
     * @param bannedDependency
     *            the dependency to be added
     */
    public void addBannedDependency(String bannedDependency) {
        this.bannedDependencies.add(bannedDependency);
    }

    public int getNumberOfBannedDependencies() {
        return this.bannedDependencies.size();
    }

    /**
     * Returns all added banned dependencies as a list. It is guaranteed that there are not duplicates in this list
     * 
     * @return a list of banned dependencies
     */
    public List<String> getBannedDependencies() {
        return new LinkedList<String>(this.bannedDependencies);
    }
}
