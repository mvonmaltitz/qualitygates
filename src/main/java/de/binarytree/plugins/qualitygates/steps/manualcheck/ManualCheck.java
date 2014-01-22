package de.binarytree.plugins.qualitygates.steps.manualcheck;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.User;

import java.util.Random;

import org.kohsuke.stapler.DataBoundConstructor;

import de.binarytree.plugins.qualitygates.GateStep;
import de.binarytree.plugins.qualitygates.GateStepDescriptor;
import de.binarytree.plugins.qualitygates.result.GateStepReport;

/**
 * This check is a manual check. That means, it has to be (dis)approved
 * manually. At the next evaluation it then gives the desired result.
 *
 * @author Marcel von Maltitz
 *
 */
public class ManualCheck extends GateStep {
    private enum Approval {
        APPROVED, DISAPPROVED, NOT_SET;
    };

    private static final String UNKNOWN_USER = "Unknown";
    public static final Random RAND = new Random();

    private String hash;

    private Approval approved;
    private String unknownUser = UNKNOWN_USER;

    @DataBoundConstructor
    public ManualCheck() {
        this.approved = Approval.NOT_SET;
    }

    /**
     * Constructs a new manual check. Normally approving or disapproving logs
     * the name of the responsible person. When the account of this person
     * cannot be determined, usernameIfUserUnknown is used.
     *
     * @param usernameIfUserUnknown
     *            the value to be used of the (dis)approving account cannot be
     *            determined
     */
    public ManualCheck(String usernameIfUserUnknown) {
        this();
        this.unknownUser = usernameIfUserUnknown;
    }

    public void approve() {
        this.approved = Approval.APPROVED;
    }

    public void disapprove() {
        this.approved = Approval.DISAPPROVED;
    }

    public boolean isApproved() {
        return this.approved == Approval.APPROVED;
    }

    public boolean isDisapproved() {
        return this.approved == Approval.DISAPPROVED;
    }

    @Override
    public void doStep(AbstractBuild<?, ?> build, Launcher launcher,
            BuildListener listener, GateStepReport checkReport) {
        this.hash = Long.toString(System.currentTimeMillis())
                + Integer.toString(RAND.nextInt());
        if (this.isApproved()) {
            checkReport.setResult(Result.SUCCESS, "Manually approved by "
                    + this.getCurrentUserOrUnknown());
        } else if (this.isDisapproved()) {
            checkReport.setResult(Result.FAILURE, "Manually disapproved by "
                    + this.getCurrentUserOrUnknown());
        } else {
            String links = generateLinks();
            checkReport.setResult(Result.NOT_BUILT, links);
        }
        resetFlag();
    }

    private void resetFlag() {
        this.approved = Approval.NOT_SET;
    }

    protected void setHash(String hash) {
        this.hash = hash;
    }

    private String generateLinks() {
        return " <a href='approve?id=" + this.hash
                + "'>Approve</a> <a href='disapprove?id=" + this.hash
                + "'>Disapprove</a>";
    }

    /**
     * Returns the name of the current user using {@link User}. If the user cannot be determined, {@link #unknownUser} is used
     * @return the name of the current user
     */
    public String getCurrentUserOrUnknown() {
        User currentUser = User.current();
        if (currentUser != null) {
            return currentUser.getFullName();
        } else {
            return this.unknownUser;
        }
    }

    @Override
    public String getDescription() {
        return "Waiting for manual approval (Hash: " + this.hash + ")";
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ManualCheck
                && ((ManualCheck) o).hash.equals(this.hash);
    }

    @Override
    public int hashCode() {
        return this.hash.hashCode();
    }

    @Extension
    public static class DescriptorImpl extends GateStepDescriptor {
        @Override
        public String getDisplayName() {
            return "Manual Check";
        }
    }

    public boolean hasHash(String hash) {
        return hash.equals(this.hash);
    }

}
