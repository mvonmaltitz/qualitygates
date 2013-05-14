package de.binarytree.plugins.qualitygates.result;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import de.binarytree.plugins.qualitygates.QualityGate;

public class GatesResultTest {
	private GatesResult gatesResult;
	private QualityGate gate;

	@Before
	public void setUp() {
		gatesResult = new GatesResult();
		gate = mock(QualityGate.class);
		when(gate.getName()).thenReturn("Gate Name");
	}

	@Test
	public void testNewGateResultHasCorrectGateName() {
		GateResult gateResult = gatesResult.addResultFor(gate);
		assertEquals(gate.getName(), gateResult.getGateName());
	}

	@Test
	public void testAddingAGateIncrementsNumberOfGates() {
		gatesResult.addResultFor(gate);
		assertEquals(1, gatesResult.getNumberOfGates());
		gatesResult.addResultFor(gate);
		assertEquals(2, gatesResult.getNumberOfGates());
	}

}
