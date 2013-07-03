package de.binarytree.plugins.qualitygates.steps;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import hudson.Launcher;
import hudson.Launcher.LocalLauncher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.TaskListener;
import hudson.model.AbstractBuild;
import hudson.tasks.Shell;
import hudson.util.StreamTaskListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.binarytree.plugins.qualitygates.result.GateStepReport;
import de.binarytree.plugins.qualitygates.steps.ShellAction;

public class ShellActionTest {

    private String command;

    private ShellAction check;

    private GateStepReport checkReport;

    private LocalLauncher launcher;

    private ByteArrayOutputStream stream;

    private Shell shell = mock(Shell.class);

    private AbstractBuild build;

    class MockExecutionCheck extends ShellAction {

        public MockExecutionCheck(String command) {
            super(command);
        }

        @Override
        public DescriptorImpl getDescriptor() {
            return new DescriptorImpl();
        }

        @Override
        public Shell getShell() {
            return shell;
        }
    }

    @Before
    public void setUp() throws Exception {
        stream = new ByteArrayOutputStream();
        TaskListener taskListener = new StreamTaskListener(stream);
        launcher = new Launcher.LocalLauncher(taskListener);
        command = "echo /tmp/testdatei";
        check = new MockExecutionCheck(command);
        checkReport = new GateStepReport(check);
        build = mock(AbstractBuild.class);
    }

    @Test
    public void testSettingAndGettingParameter() {
        assertEquals(command, check.getCommand());
        assertTrue(check.getDescription().startsWith("$ "));
    }

    @Test
    public void testExecutionOfCommand() throws IOException,
            InterruptedException {
        when(
                shell.perform(any(AbstractBuild.class), any(Launcher.class),
                        any(BuildListener.class))).thenReturn(true);
        check.doStep(build, null, launcher, checkReport);
        String logOutput = new String(stream.toByteArray());
        assertEquals(Result.SUCCESS, checkReport.getResult());
        Mockito.verify(shell).perform(any(AbstractBuild.class),
                any(Launcher.class), any(BuildListener.class));
    }

    @Test
    public void testExecutionOfUnknownCommand() throws InterruptedException {
        when(
                shell.perform(any(AbstractBuild.class), any(Launcher.class),
                        any(BuildListener.class))).thenReturn(false);
        String unknownCommand = "asdlkjfalsdfjaldfjald";
        check = new MockExecutionCheck(unknownCommand);
        check.doStep(build, null, launcher, checkReport);
        String logOutput = new String(stream.toByteArray());
        assertEquals(Result.FAILURE, checkReport.getResult());
        Mockito.verify(shell).perform(any(AbstractBuild.class),
                any(Launcher.class), any(BuildListener.class));
    }

    @Test
    public void testExecutionOfNonZeroCommand() throws InterruptedException {
        when(
                shell.perform(any(AbstractBuild.class), any(Launcher.class),
                        any(BuildListener.class))).thenReturn(false);
        String command = "cat /aljf/aljf/asdfj/asdfjkdfjdkfj.txt";
        check = new MockExecutionCheck(command);
        check.doStep(build, null, launcher, checkReport);
        String logOutput = new String(stream.toByteArray());
        assertEquals(Result.FAILURE, checkReport.getResult());
        Mockito.verify(shell).perform(any(AbstractBuild.class),
                any(Launcher.class), any(BuildListener.class));
    }

    @Test
    public void testReportIsFailureOnInterruptedException()
            throws InterruptedException {
        when(
                shell.perform(any(AbstractBuild.class), any(Launcher.class),
                        any(BuildListener.class))).thenThrow(
                new InterruptedException());
        check.doStep(build, null, launcher, checkReport);
        String logOutput = new String(stream.toByteArray());
        assertEquals(Result.FAILURE, checkReport.getResult());
        Mockito.verify(shell).perform(any(AbstractBuild.class),
                any(Launcher.class), any(BuildListener.class));
    }

}
