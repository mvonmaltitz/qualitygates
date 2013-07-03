package de.binarytree.plugins.qualitygates.steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import hudson.model.AbstractBuild;

import org.junit.Test;

import de.binarytree.plugins.qualitygates.steps.POMCheck;
import de.binarytree.plugins.qualitygates.steps.POMCheck.DescriptorImpl;

public class POMCheckTest {

    private POMCheck pomCheck;
    private AbstractBuild build;

    @Test
    public void testDescriptionContainsPOM() {
        DescriptorImpl descriptor = new POMCheck.DescriptorImpl();
        assertTrue(descriptor.getDisplayName().contains("POM"));
    }

    @Test
    public void testSettingAndGettingExpression() {
        pomCheck = new POMCheck("Expression", true);
        assertEquals("Expression", pomCheck.getExpression());
    }

    @Test
    public void testGettingFile() {
        pomCheck = new POMCheck("Expression", true);
        assertEquals("pom.xml", pomCheck.getTargetFile());
    }
}
