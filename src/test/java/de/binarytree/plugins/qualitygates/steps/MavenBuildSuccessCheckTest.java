package de.binarytree.plugins.qualitygates.steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import org.junit.Before;
import org.junit.Test;

import de.binarytree.plugins.qualitygates.result.GateStepReport;
import de.binarytree.plugins.qualitygates.steps.MavenBuildSuccessCheck.MavenSuccessCheckDescriptor;

public class MavenBuildSuccessCheckTest {

    private MavenBuildSuccessCheck check;

    private AbstractBuild<?, ?> build;

    private MavenSuccessCheckDescriptor descriptor;

    @Before
    public void setUp() {
        descriptor = new MavenBuildSuccessCheck.MavenSuccessCheckDescriptor();
        build = mock(AbstractBuild.class, RETURNS_DEEP_STUBS);
        check = new MavenBuildSuccessCheck() {
            @Override
            public MavenSuccessCheckDescriptor getDescriptor() {
                return descriptor;
            }
        };
    }

    @Test
    public void testDescriptionsContainMavenAndBuild() {
        assertTrue(descriptor.getDisplayName().contains("Maven"));
        assertTrue(check.getDescription().toLowerCase().contains("maven"));
        assertTrue(check.getDescription().toLowerCase().contains("build"));
    }

    @Test
    public void testBuildSuccess() {
        this.testBuildResult(Result.SUCCESS);
    }

    @Test
    public void testBuildIsNull() {
        build = null;
        GateStepReport result = check.step(build, null, null);
        assertEquals(Result.FAILURE, result.getResult());
    }

    @Test
    public void testBuildUnstable() {
        this.testBuildResult(Result.UNSTABLE);
    }

    @Test
    public void testBuildFailed() {
        this.testBuildResult(Result.FAILURE);
    }

    @Test
    public void testBuildAborted() {
        when(build.getResult()).thenReturn(Result.ABORTED);
        GateStepReport result = check.step(build, null, null);
        assertEquals(Result.FAILURE, result.getResult());
    }

    public void testBuildResult(Result desiredResult) {
        when(build.getResult()).thenReturn(desiredResult);
        GateStepReport result = check.step(build, null, null);
        assertEquals(desiredResult, result.getResult());
    }

}
