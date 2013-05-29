package de.binarytree.plugins.qualitygates;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class HelloWorldBuilderTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testInitializeWithNullCollection() throws IOException {
		QualityGateBuilder builder = new QualityGateBuilder("Builder", null);
		assertEquals(0, builder.getNumberOfGates());
	}

}
