package de.binarytree.plugins.qualitygates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class ManualGateTest {

    private ManualGate gate;

    @Before
    public void setUp() throws Exception {
        gate = new ManualGate("Gate");
    }

    @Test
    public void testHasOneCheck() {
        assertEquals(gate.getNumberOfSteps(), 1);
    }

    @Test
    public void testGetDisplayName() {
        assertTrue("Display Name contains 'manual'",
                new ManualGate.DescriptorImpl().getDisplayName().toLowerCase()
                        .contains("manual"));
    }

}
