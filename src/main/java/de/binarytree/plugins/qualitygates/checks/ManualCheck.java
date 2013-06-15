package de.binarytree.plugins.qualitygates.checks;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.User;

import java.util.Random;

import org.kohsuke.stapler.DataBoundConstructor;

import de.binarytree.plugins.qualitygates.result.CheckReport;

public class ManualCheck extends Check {

    public static final String AWAITING_MANUAL_APPROVAL = "Awaiting manual approval.";

    public transient String hash;

    public transient boolean approved;

    @DataBoundConstructor
    public ManualCheck() {
        this.approved = false;
    }

    public void approve() {
        this.approved = true;
    }

    @Override
    public void doCheck(AbstractBuild build, BuildListener listener, Launcher launcher, CheckReport checkReport) {
        this.hash = Long.toString(System.currentTimeMillis()) + Integer.toString((new Random()).nextInt());
        if (!approved) {
            String approveLink = generateApproveLink();
            checkReport.setResult(Result.NOT_BUILT, AWAITING_MANUAL_APPROVAL + approveLink);
        } else {
            checkReport.setResult(Result.SUCCESS, "Manually approved by " + this.getCurrentUserOrUnknown());
            this.approved = false;
        }
    }

    private String generateApproveLink() {
        return " <a href='approve?id=" + this.hash + "'>Approve</a>";
    }

    public String getCurrentUserOrUnknown() {
        User currentUser = User.current();
        if (currentUser != null) {
            return currentUser.getFullName();
        } else {
            return "Unknown";
        }
    }

    @Override
    public String getDescription() {
        return "Wait for manual approval (" + this.hash + ")";
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof ManualCheck) && ((ManualCheck) o).hash.equals(this.hash);
    }

    @Extension
    public static class DescriptorImpl extends CheckDescriptor {
        @Override
        public String getDisplayName() {
            return "Manual Check";
        }
    }

    public boolean hasHash(String hash) {
        return this.hash.equals(hash);
    }

}
