package de.binarytree.plugins.qualitygates.result;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import org.junit.Before;
import org.junit.Test;

import de.binarytree.plugins.qualitygates.GateStep;
import de.binarytree.plugins.qualitygates.GateStepDescriptor;

public class CheckResultTest {

    class MockCheck extends GateStep {
        private String name;

        public MockCheck(String name) {
            this.name = name;
        }

        public GateStepDescriptor getDescriptor() {
            return new DescriptorImpl();
        }

        public boolean equals(Object o) {
            return o instanceof MockCheck
                    && ((MockCheck) o).name.equals(this.name);
        }

        class DescriptorImpl extends GateStepDescriptor {

            @Override
            public String getDisplayName() {
                return "Check Display Name";
            }

        }

        @Override
        public void doStep(AbstractBuild build, Launcher launcher,
                BuildListener listener, GateStepReport checkReport) {
        }

        @Override
        public String getDescription() {
            return "Check Description";
        }

    };

    private GateStep check = new MockCheck("MockCheck");
    private GateStepReport checkReport;

    @Before
    public void setUp() {
        checkReport = new GateStepReport(check);
    }

    @Test
    public void testSettingAndGettingOfValidReasonlessResultOfCheck() {
        Result[] results = new Result[] { Result.SUCCESS, Result.NOT_BUILT };
        for (Result result : results) {
            checkReport.setResult(result);
            assertEquals(result, checkReport.getResult());
        }
    }

    @Test
    public void testSettingAndGettingOfInvalidReasonlessResultOfCheck() {
        Result[] results = new Result[] { Result.FAILURE, Result.UNSTABLE };
        for (Result result : results) {
            try {
                checkReport.setResult(result);
                fail("Negative results without reason should be disallowed.");
            } catch (IllegalArgumentException e) {
                assertEquals(Result.NOT_BUILT, checkReport.getResult());
                assertNull(checkReport.getReason());
            }
        }
    }

    @Test
    public void testSettingAndGettingOfValidReasonfullResultOfCheck() {
        String reason = "It didn't function";
        Result[] results = new Result[] { Result.FAILURE, Result.UNSTABLE };
        for (Result result : results) {
            checkReport.setResult(result, reason);
            assertEquals(result, checkReport.getResult());
            assertEquals(reason, checkReport.getReason());
        }
    }

    @Test
    public void testGetCheckResultDocumentation() {
        GateStepReport checkReport = check.createEmptyGateStepReport();
        assertEquals(Result.NOT_BUILT, checkReport.getResult());
    }

    @Test
    public void testReferencesSameGate() {
        GateStepReport result1 = new GateStepReport(check);
        GateStepReport result2 = new GateStepReport(check);
        assertTrue(result1.referencesSameStepAs(result2));
    }

    @Test
    public void testReferencesSameCheck() {
        GateStepReport result1 = new GateStepReport(check);
        assertTrue(result1.references(check));
    }

    @Test
    public void testReferencesEqualCheckFalse() {
        GateStep check1 = new MockCheck("Eins");
        GateStep check2 = new MockCheck("Zwei");
        GateStepReport result1 = new GateStepReport(check1);
        assertFalse(result1.references(check2));
    }

    @Test
    public void testReferencesEqualCheckTrue() {
        GateStep check1 = new MockCheck("Eins");
        GateStep check2 = new MockCheck("Eins");
        GateStepReport result1 = new GateStepReport(check1);
        assertTrue(result1.references(check2));
    }
}
