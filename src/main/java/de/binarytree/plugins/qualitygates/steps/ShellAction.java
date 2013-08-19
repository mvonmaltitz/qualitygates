package de.binarytree.plugins.qualitygates.steps;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.tasks.Shell;

import org.kohsuke.stapler.DataBoundConstructor;

import de.binarytree.plugins.qualitygates.GateStep;
import de.binarytree.plugins.qualitygates.GateStepDescriptor;
import de.binarytree.plugins.qualitygates.result.GateStepReport;

/**
 * This action gets a shell command in form of a string and instantiates a {@link Shell}
 * when being executed which itself executes the given command.
 * 
 * @author Marcel von Maltitz
 * 
 */
public class ShellAction extends GateStep {
    private String command;

    /**
     * Constructs a new action
     * @param command the command to be executed
     */
    @DataBoundConstructor
    public ShellAction(String command) {
        this.command = command;
    }

    public String getCommand() {
        return this.command;
    }

    @Override
    public void doStep(AbstractBuild<?, ?> build, Launcher launcher,
            BuildListener listener, GateStepReport actionReport) {
        Shell shell = this.getShell();
        try {
            boolean success = shell.perform(build, launcher, listener);
            if (success) {
                actionReport.setResult(Result.SUCCESS);
            } else {
                actionReport.setResult(Result.FAILURE, shell.getCommand()
                        + " could not be performed. Check log for details.");
            }
        } catch (InterruptedException e) {
            failStepWithExceptionAsReason(actionReport, e);
        }

    }

    protected Shell getShell() {
        return new Shell(this.command);

    }

    @Override
    public String getDescription() {
        return "$ " + this.command;
    }

    @Extension
    public static class DescriptorImpl extends GateStepDescriptor {
        @Override
        public String getDisplayName() {
            return "Execution of shell script";
        }
    }

}
