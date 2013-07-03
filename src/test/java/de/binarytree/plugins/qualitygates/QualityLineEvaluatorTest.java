package de.binarytree.plugins.qualitygates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import de.binarytree.plugins.qualitygates.result.GateReport;
import de.binarytree.plugins.qualitygates.result.QualityLineReport;

public class QualityLineEvaluatorTest {

    private int gateCounter;
    private LinkedList<Gate> gateList;
    private BuildListener listener;
    private Launcher launcher;
    private AbstractBuild build;

    @Before
    public void setUp() throws Exception {
        gateCounter = 0;
        gateList = new LinkedList<Gate>();
        build = mock(AbstractBuild.class);
        launcher = mock(Launcher.class);
        listener = mock(BuildListener.class);
    }

    @Test
    public void testOneGateSucessfull() {
        gateList.add(this.getGate(Result.SUCCESS));
        QualityLineReport report = this.evaluate();
        assertEquals(1, report.getGateReports().size());
        assertEquals(Result.SUCCESS, report.getGateReports().get(0).getResult());
    }

    @Test
    public void testExecutionStopsAfterUnbuildableGate() {
        this.addGateSequence(new Result[] { Result.SUCCESS, Result.NOT_BUILT,
                Result.SUCCESS, Result.SUCCESS });

        QualityLineReport result = this.evaluate();

        assertEquals(4, result.getGateReports().size());
        this.assertSequence(result, new Result[] { Result.SUCCESS,
                Result.NOT_BUILT, Result.NOT_BUILT, Result.NOT_BUILT });
    }

    @Test
    public void testExecutionStopsAfterFailure() {
        this.addGateSequence(new Result[] { Result.SUCCESS, Result.FAILURE,
                Result.SUCCESS, Result.SUCCESS });

        QualityLineReport report = this.evaluate();

        assertEquals(4, report.getGateReports().size());
        this.assertSequence(report, new Result[] { Result.SUCCESS,
                Result.FAILURE, Result.NOT_BUILT, Result.NOT_BUILT });
    }

    @Test
    public void testReevaluationStopsAfterAlreadyReportedFailure() {

        this.addGateSequence(new Result[] { Result.SUCCESS, Result.FAILURE,
                Result.SUCCESS, Result.SUCCESS });
        QualityLineEvaluator gateEvaluator = new QualityLineEvaluator(
                this.gateList);
        QualityLineReport report = gateEvaluator.evaluate(build, launcher,
                listener);
        this.assertSequence(report, new Result[] { Result.SUCCESS,
                Result.FAILURE, Result.NOT_BUILT, Result.NOT_BUILT });
        report = gateEvaluator.evaluate(build, launcher, listener);
        this.assertSequence(report, new Result[] { Result.SUCCESS,
                Result.FAILURE, Result.NOT_BUILT, Result.NOT_BUILT });

    }

    public void addGateSequence(Result... results) {
        for (int i = 0; i < results.length; i++) {
            gateList.add(this.getGate(results[i]));
        }
    }

    public void assertSequence(QualityLineReport qualityLineReport,
            Result... results) {
        if (qualityLineReport.getGateReports().size() != results.length) {
            fail("Different length of gates and expected results");
        }
        for (int i = 0; i < results.length; i++) {
            assertEquals(results[i], qualityLineReport.getGateReports().get(i)
                    .getResult());
        }

    }

    public QualityLineReport evaluate() {
        return new QualityLineEvaluator(gateList).evaluate(build, launcher,
                listener);
    }

    public Gate getGate(final Result result) {
        String name = "Gate " + gateCounter++;

        LinkedList<GateStep> checkList = new LinkedList<GateStep>();
        return new Gate(name) {
            @Override
            public void doEvaluation(AbstractBuild build, Launcher launcher,
                    BuildListener listener, GateReport gateReport) {
                gateReport.setResult(result);
            }

        };

    }

}
