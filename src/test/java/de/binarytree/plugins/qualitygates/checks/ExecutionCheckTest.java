package de.binarytree.plugins.qualitygates.checks;

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

import de.binarytree.plugins.qualitygates.result.CheckReport;

public class ExecutionCheckTest {

    private String command;

    private ExecutionCheck check;

    private CheckReport checkReport;

    private LocalLauncher launcher;

    private ByteArrayOutputStream stream;

    private Shell shell = mock(Shell.class);

    private AbstractBuild build;

    class MockExecutionCheck extends ExecutionCheck {

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
        checkReport = new CheckReport(check);
        build = mock(AbstractBuild.class);
    }

    @Test
    public void testSettingAndGettingParameter() {
        assertEquals(command, check.getCommand());
    }

    @Test
    public void testExecutionOfCommand() throws IOException, InterruptedException {
        when(shell.perform(any(AbstractBuild.class), any(Launcher.class), any(BuildListener.class))).thenReturn(true);
        check.doCheck(build, null, launcher, checkReport);
        String logOutput = new String(stream.toByteArray());
        assertEquals(Result.SUCCESS, checkReport.getResult());
        Mockito.verify(shell).perform(any(AbstractBuild.class), any(Launcher.class), any(BuildListener.class));
    }

    @Test
    public void testExecutionOfUnknownCommand() throws InterruptedException {
        when(shell.perform(any(AbstractBuild.class), any(Launcher.class), any(BuildListener.class))).thenReturn(false);
        String unknownCommand = "asdlkjfalsdfjaldfjald";
        check = new MockExecutionCheck(unknownCommand);
        check.doCheck(build, null, launcher, checkReport);
        String logOutput = new String(stream.toByteArray());
        assertEquals(Result.FAILURE, checkReport.getResult());
        Mockito.verify(shell).perform(any(AbstractBuild.class), any(Launcher.class), any(BuildListener.class));
    }

    @Test
    public void testExecutionOfNonZeroCommand() throws InterruptedException {
        when(shell.perform(any(AbstractBuild.class), any(Launcher.class), any(BuildListener.class))).thenReturn(false);
        String command = "cat /aljf/aljf/asdfj/asdfjkdfjdkfj.txt";
        check = new MockExecutionCheck(command);
        check.doCheck(build, null, launcher, checkReport);
        String logOutput = new String(stream.toByteArray());
        assertEquals(Result.FAILURE, checkReport.getResult());
        Mockito.verify(shell).perform(any(AbstractBuild.class), any(Launcher.class), any(BuildListener.class));
        System.out.println(checkReport.getReason());
    }

}
