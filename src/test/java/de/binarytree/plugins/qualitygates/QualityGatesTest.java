package de.binarytree.plugins.qualitygates;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class QualityGatesTest {

	private QualityGates gates;

	@Before
	public void setUp() throws Exception {
		gates = new QualityGates(); 
	}

	@Test
	public void testAddGate() {
		QualityGate gate = new QualityGateImpl(); 
		gates.add(gate); 
		assertEquals(1, gates.getNumberOfGates()); 
	}
	@Test
	public void testAddTwoGates() {
		QualityGate gate1 = new QualityGateImpl(); 
		QualityGate gate2 = new QualityGateImpl(); 
		gates.add(gate1); 
		gates.add(gate2); 
		assertEquals(2, gates.getNumberOfGates()); 
	}

}
