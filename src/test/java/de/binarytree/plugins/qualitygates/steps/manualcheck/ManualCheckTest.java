package de.binarytree.plugins.qualitygates.steps.manualcheck;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import org.junit.Before;
import org.junit.Test;

import de.binarytree.plugins.qualitygates.result.GateStepReport;
import de.binarytree.plugins.qualitygates.steps.manualcheck.ManualCheck.DescriptorImpl;

public class ManualCheckTest {
    class MockManualCheck extends ManualCheck {
        public MockManualCheck(String usernameIfUserUnknown) {
            super(usernameIfUserUnknown);
        }

        public MockManualCheck(String hash, String usernameIfUserUnkown) {
            this(usernameIfUserUnkown);
            this.setHash(hash);
        }

        @Override
        public DescriptorImpl getDescriptor() {
            return new ManualCheck.DescriptorImpl();
        }
    }

    private ManualCheck check;

    private AbstractBuild build;

    private BuildListener listener;

    private Launcher launcher;

    private String unknownUser = "Unknown User";

    @Before
    public void setUp() throws Exception {
        check = new MockManualCheck(unknownUser);
        build = mock(AbstractBuild.class);
        listener = mock(BuildListener.class);
        launcher = mock(Launcher.class);
    }

    @Test
    public void testCheckResult() {
        GateStepReport report = new GateStepReport(check);
        check.doStep(build, launcher, listener, report);
        assertEquals(Result.NOT_BUILT, report.getResult());
        assertTrue(report.getReason().contains("href"));
        assertApprovalReset();
    }

    @Test
    public void testCheckBeingManuallyApprovedLeadsToSuccess() {
        GateStepReport report = new GateStepReport(check);
        check.approve();
        assertTrue(check.isApproved());
        assertFalse(check.isDisapproved());
        check.doStep(build, launcher, listener, report);
        assertEquals(Result.SUCCESS, report.getResult());
        assertTrue(report.getReason().contains("approved"));
        assertTrue(report.getReason().contains(unknownUser));
        assertApprovalReset();
    }

    private void assertApprovalReset() {
        assertFalse(check.isApproved());
        assertFalse(check.isDisapproved());
    }

    @Test
    public void testCheckBeingManuallyDisapprovedLeadsToFailure() {
        GateStepReport report = new GateStepReport(check);
        check.disapprove();
        assertFalse(check.isApproved());
        assertTrue(check.isDisapproved());
        check.doStep(build, launcher, listener, report);
        assertEquals(Result.FAILURE, report.getResult());
        assertTrue(report.getReason().contains("disapproved"));
        assertTrue(report.getReason().contains(unknownUser));
        assertApprovalReset();
    }

    @Test
    public void testEqualsTrue() {
        ManualCheck check1 = new MockManualCheck("hash", "user1");
        ManualCheck check2 = new MockManualCheck("hash", "user2");
        assertEquals(check1, check2);
        assertEquals(check1.hashCode(), check2.hashCode());

    }

    @Test
    public void testEqualsFalse() {
        ManualCheck check1 = new MockManualCheck("hash1", "user1");
        ManualCheck check2 = new MockManualCheck("hash2", "user1");
        assertNotEquals(check1, check2);
        assertNotEquals(check1.hashCode(), check2.hashCode());

    }

    @Test
    public void testDescriptionsContainManual() {
        DescriptorImpl descriptor = new ManualCheck.DescriptorImpl();
        assertTrue(descriptor.getDisplayName().toLowerCase().contains("manual"));
        assertTrue(check.getDescription().toLowerCase().contains("manual"));
    }

}
