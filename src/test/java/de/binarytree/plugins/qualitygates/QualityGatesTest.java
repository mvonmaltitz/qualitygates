package de.binarytree.plugins.qualitygates;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import de.binarytree.plugins.qualitygates.checks.Check;

public class QualityGatesTest{

	private QualityGates gates;
	private LinkedList<Check> collection;

	@Before
	public void setUp() throws Exception {
		collection = new LinkedList<Check>(); 
		gates = new QualityGates(); 
	}

	@Test
	public void testAddGate() {
		Gate gate = new AndGate("Eins", collection);  
		gates.add(gate); 
		assertEquals(1, gates.getNumberOfGates()); 
	}
	@Test
	public void testAddTwoGates() {
		Gate gate1 = new AndGate("Eins", collection);  
		Gate gate2 = new AndGate("Zwei", collection); 
		gates.add(gate1); 
		gates.add(gate2); 
		assertEquals(2, gates.getNumberOfGates()); 
	}

}
