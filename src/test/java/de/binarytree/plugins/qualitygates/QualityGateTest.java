package de.binarytree.plugins.qualitygates;

import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import java.util.LinkedList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import de.binarytree.plugins.qualitygates.checks.Check;
import de.binarytree.plugins.qualitygates.checks.CheckDescriptor;
import de.binarytree.plugins.qualitygates.result.CheckReport;
import de.binarytree.plugins.qualitygates.result.GateReport;

public class QualityGateTest {

	private Gate gate;
	private LinkedList<Check> checkList;

	class MockCheck extends Check {

		private String name;

		public MockCheck(String name) {
			this.name = name;
		}

		@Override
		public String getDescription() {
			return "Check Description";
		}

		public CheckDescriptor getDescriptor() {
			return new CheckDescriptor() {

				@Override
				public String getDisplayName() {
					return name;
				}

			};
		}

		@Override
		public void doCheck(AbstractBuild build, BuildListener listener,
				Launcher launcher, CheckReport checkReport) {
		}

	}

	@Before
	public void setUp() throws Exception {
		Check check1 = new MockCheck("Check1");
		Check check2 = new MockCheck("Check2");
		checkList = new LinkedList<Check>();
		checkList.add(check1);
		checkList.add(check2);
		gate = new Gate("Name", checkList) {
			@Override
			public void doCheck(AbstractBuild build, Launcher launcher,
					BuildListener listener, GateReport gateReport) {
				// TODO Auto-generated method stub

			}

		};

	}

	@Test
	public void testGetDocumentation() {
		GateReport gateReport = gate.document();
		assertEquals(Result.NOT_BUILT, gateReport.getResult());
		assertEquals(2, gateReport.getCheckResults().size());
	}

	@Test
	public void testNameParam() {
		assertEquals("Name", gate.getName());
	}

	@Test
	public void testGivenCollectionIsNotDirectlySet() {
		assertNotSame(gate.getChecks(), checkList);
	}

	@Test
	public void testChecksContainOnlyChecksOfCollectionDuringConstruction() {
		assertEquals(gate.getChecks(), checkList);
	}

}
