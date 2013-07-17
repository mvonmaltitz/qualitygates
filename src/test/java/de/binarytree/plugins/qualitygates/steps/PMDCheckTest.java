package de.binarytree.plugins.qualitygates.steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class PMDCheckTest {
    private PMDCheck check;

    @Before
    public void setUp() {
        check = new PMDCheck(1, 2);
    }

    @Test
    public void testDescription() {
        assertTrue(check.getDescription().toLowerCase().contains("pmd"));
    }

    @Test
    public void testInitializiation() {
        assertEquals(check.getTargetFile(), PMDCheck.FILE);
        assertEquals(check.getExpression(), PMDCheck.VIOLATION_EXPRESSION);
    }
}
