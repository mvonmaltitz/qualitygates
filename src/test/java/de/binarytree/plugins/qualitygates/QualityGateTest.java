package de.binarytree.plugins.qualitygates;

import static org.junit.Assert.assertEquals;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import de.binarytree.plugins.qualitygates.result.GateStepReport;
import de.binarytree.plugins.qualitygates.result.GateReport;
import de.binarytree.plugins.qualitygates.steps.GateStep;
import de.binarytree.plugins.qualitygates.steps.GateStepDescriptor;

public class QualityGateTest {

    private Gate gate;
    private LinkedList<GateStep> checkList;

    class MockCheck extends GateStep {

        private String name;

        public MockCheck(String name) {
            this.name = name;
        }

        @Override
        public String getDescription() {
            return "Check Description";
        }

        public GateStepDescriptor getDescriptor() {
            return new GateStepDescriptor() {

                @Override
                public String getDisplayName() {
                    return name;
                }

            };
        }

        @Override
        public void doStep(AbstractBuild build, BuildListener listener,
                Launcher launcher, GateStepReport checkReport) {
        }

    }

    @Before
    public void setUp() throws Exception {
        GateStep check1 = new MockCheck("Check1");
        GateStep check2 = new MockCheck("Check2");
        checkList = new LinkedList<GateStep>();
        checkList.add(check1);
        checkList.add(check2);
        gate = new Gate("Name") {
            @Override
            public void doEvaluation(AbstractBuild build, Launcher launcher,
                    BuildListener listener, GateReport gateReport) {
                // TODO Auto-generated method stub

            }

        };

    }

    @Test
    public void testGetDocumentation() {
        GateReport gateReport = gate.document();
        assertEquals(Result.NOT_BUILT, gateReport.getResult());
    }

    @Test
    public void testNameParam() {
        assertEquals("Name", gate.getName());
    }

}
