package de.binarytree.plugins.qualitygates;

import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import java.util.LinkedList;
import java.util.List;

import de.binarytree.plugins.qualitygates.result.GateResult;
import de.binarytree.plugins.qualitygates.result.GatesResult;

public class GateEvaluator {
	private List<QualityGate> gates = new LinkedList<QualityGate>();
	private boolean executeGates;
	private GatesResult gatesResult;

	public GateEvaluator(List<QualityGate> gates) {
		this(gates, new GatesResult());
	}

	public GateEvaluator(List<QualityGate> gates, GatesResult gatesResult) {
		if (gates != null) {
			this.gates.addAll(gates);
		}
		this.gatesResult = gatesResult;
	}

	public GatesResult evaluate(AbstractBuild build, Launcher launcher,
			BuildListener listener) {
		executeGates = true;
		for (QualityGate gate : this.gates) {
			evaluateGate(build, launcher, listener, gate);
		}
		return gatesResult;
	}

	protected void evaluateGate(AbstractBuild build, Launcher launcher,
			BuildListener listener, QualityGate gate) {
		GateResult gateResult;
		if (hasNotFullyBeenExecuted(gate)) {
			gateResult = processGateAndReport(build, launcher, listener, gate);
		} else {
			gateResult = getFormerGateResultFor(gate);
		}
		if (shouldStopExecutionDueTo(gateResult)) {
			executeGates = false;
		}
	}

	protected boolean hasNotFullyBeenExecuted(QualityGate gate) {
		Result result = this.gatesResult.getResultFor(gate);
		return result.equals(Result.NOT_BUILT);
	}

	private GateResult processGateAndReport(AbstractBuild build,
			Launcher launcher, BuildListener listener, QualityGate gate) {
		GateResult gateResult;
		if (executeGates) {
			gateResult = executeGateAndAddToReport(build, launcher, listener,
					gate);
		} else {
			gateResult = addNotBuiltGateDocumentationToReport(gate);
		}
		return gateResult;
	}

	protected GateResult executeGateAndAddToReport(AbstractBuild build,
			Launcher launcher, BuildListener listener, QualityGate gate) {
		GateResult gateResult = gate.check(build, launcher, listener);
		gatesResult.addGateResult(gateResult);
		return gateResult;
	}

	private GateResult getFormerGateResultFor(QualityGate gate) {
		return this.gatesResult.getGateResultFor(gate);
	}

	protected GateResult addNotBuiltGateDocumentationToReport(QualityGate gate) {
		GateResult gateResult = gate.document();
		gatesResult.addGateResult(gateResult);
		return gateResult;
	}

	protected boolean shouldStopExecutionDueTo(GateResult gateResult) {
		Result result = gateResult.getResult();
		return result.equals(Result.FAILURE) || result.equals(Result.NOT_BUILT);
	}

	public GatesResult getLatestResults() {
		return this.gatesResult;
	}
}