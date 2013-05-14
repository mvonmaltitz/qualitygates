package de.binarytree.plugins.qualitygates;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import java.io.PrintStream;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import de.binarytree.plugins.qualitygates.checks.Check;

public class QualityGateImplTest {

	private Result SUCCESS = Result.SUCCESS;
	private Result FAILURE = Result.FAILURE;
	private Result ABORTED = Result.ABORTED;
	private Result UNSTABLE = Result.UNSTABLE;
	private Result NOT_BUILT = Result.NOT_BUILT;

	private QualityGateImpl gate;
	private AbstractBuild build;
	private LinkedList<Check> checkList;
	private BuildListener listener;

	@Before
	public void setUp() throws Exception {
		PrintStream stream = mock(PrintStream.class); 
		
		build = mock(AbstractBuild.class);
		listener = mock(BuildListener.class);
		when(listener.getLogger()).thenReturn(stream); 
		checkList = new LinkedList<Check>();
	}

	@Test
	public void testAddCheck() {
		checkList.add(this.getCheckMockWithResult(SUCCESS));
		gate = new QualityGateImpl("Eins", checkList);
		assertEquals(1, gate.getNumberOfChecks());
	}

	@Test
	public void testAddTwoChecks() {
		checkList.add(this.getCheckMockWithResult(SUCCESS));
		checkList.add(this.getCheckMockWithResult(SUCCESS));
		gate = new QualityGateImpl("Eins", checkList);
		assertEquals(2, gate.getNumberOfChecks());
	}

	@Test
	public void testGateIsSuccessfullWhenChecksSuccessfull() {
		checkList.add(this.getCheckMockWithResult(SUCCESS));
		checkList.add(this.getCheckMockWithResult(SUCCESS));
		gate = new QualityGateImpl("Eins", checkList);
		this.performGateCheckAndExpect(SUCCESS);
	}

	@Test
	public void testGateAbortedWhenOneCheckAborted() {
		checkList.add(this.getCheckMockWithResult(ABORTED));
		checkList.add(this.getCheckMockWithResult(SUCCESS));
		gate = new QualityGateImpl("Eins", checkList);
		this.performGateCheckAndExpect(ABORTED);
	}

	@Test
	public void testGateAbortedWhenOneCheckAbortedAndAnotherFailed() {
		checkList.add(this.getCheckMockWithResult(ABORTED));
		checkList.add(this.getCheckMockWithResult(FAILURE));
		gate = new QualityGateImpl("Eins", checkList);
		this.performGateCheckAndExpect(ABORTED);
	}

	@Test
	public void testGateFailsWhenOneChecksFails() {
		checkList.add(this.getCheckMockWithResult(Result.SUCCESS));
		checkList.add(this.getCheckMockWithResult(FAILURE));
		gate = new QualityGateImpl("Eins", checkList);
		this.performGateCheckAndExpect(FAILURE);
	}

	@Test
	public void testInitializeGateWithNullCollection() {
		QualityGateImpl gate = new QualityGateImpl("NullGate", null);
		assertEquals(0, gate.getNumberOfChecks());
	}

	@Test
	public void testEmptyGateIsUnstable(){
		gate = new QualityGateImpl("NullGate", null);
		this.performGateCheckAndExpect(UNSTABLE); 
		
	}
	public void performGateCheckAndExpect(Result expectedResult) {
		Result result = gate.doCheck(build, null, listener);
		assertEquals(expectedResult, result);
	}

	public Check getCheckMockWithResult(Result result) {
		Check check = mock(Check.class);
		when(check.doCheck(any(AbstractBuild.class), any(BuildListener.class), any(Launcher.class))).thenReturn(result);
		return check;
	}
}
