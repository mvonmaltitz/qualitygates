package de.binarytree.plugins.qualitygates;

import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import java.util.LinkedList;
import java.util.List;

import de.binarytree.plugins.qualitygates.result.GateReport;
import de.binarytree.plugins.qualitygates.result.QualityLineReport;

/**
 * This class holds the evaluation algorithm of the quality line. Each gate is
 * evaluated and the report built is saved. When a gate fails, the evaluation
 * normally happening after this gate is skipped. Nevertheless these gates,
 * which have not been executed are saved in the reported. 
 * 
 * @author Marcel von Maltitz
 * 
 */
public class QualityLineEvaluator {
    private List<Gate> gates = new LinkedList<Gate>();
    private boolean executeGates;
    private QualityLineReport qualityLineReport;

    public QualityLineEvaluator(List<Gate> gates) {
        this(gates, new QualityLineReport());
    }

    public QualityLineEvaluator(List<Gate> gates,
            QualityLineReport qualityLineReport) {
        if (gates != null) {
            this.gates.addAll(gates);
        }
        this.qualityLineReport = qualityLineReport;
    }

    public QualityLineReport evaluate(AbstractBuild<?, ?> build, Launcher launcher,
            BuildListener listener) {
        executeGates = true;
        for (Gate gate : this.gates) {
            evaluateGate(build, launcher, listener, gate);
        }
        return qualityLineReport;
    }

    protected void evaluateGate(AbstractBuild<?, ?> build, Launcher launcher,
            BuildListener listener, Gate gate) {
        GateReport gateReport;
        if (hasNotFullyBeenExecuted(gate)) {
            gateReport = processGateAndReport(build, launcher, listener, gate);
        } else {
            gateReport = getFormerGateResultFor(gate);
        }
        if (shouldStopExecutionDueTo(gateReport)) {
            executeGates = false;
        }
    }

    protected boolean hasNotFullyBeenExecuted(Gate gate) {
        Result result = this.qualityLineReport.getResultFor(gate);
        return result.equals(Result.NOT_BUILT);
    }

    private GateReport processGateAndReport(AbstractBuild<?, ?> build,
            Launcher launcher, BuildListener listener, Gate gate) {
        GateReport gateReport;
        if (executeGates) {
            gateReport = executeGateAndAddToReport(build, launcher, listener,
                    gate);
        } else {
            gateReport = addNotBuiltGateDocumentationToReport(gate);
        }
        return gateReport;
    }

    protected GateReport executeGateAndAddToReport(AbstractBuild<?, ?> build,
            Launcher launcher, BuildListener listener, Gate gate) {
        GateReport gateReport = gate.evaluate(build, launcher, listener);
        qualityLineReport.addGateReport(gateReport);
        return gateReport;
    }

    private GateReport getFormerGateResultFor(Gate gate) {
        return this.qualityLineReport.getGateReportFor(gate);
    }

    protected GateReport addNotBuiltGateDocumentationToReport(Gate gate) {
        GateReport gateReport = gate.createEmptyGateReport();
        qualityLineReport.addGateReport(gateReport);
        return gateReport;
    }

    protected boolean shouldStopExecutionDueTo(GateReport gateReport) {
        Result result = gateReport.getResult();
        return result.equals(Result.FAILURE) || result.equals(Result.NOT_BUILT);
    }

    public QualityLineReport getLatestResults() {
        return this.qualityLineReport;
    }
}