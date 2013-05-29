package de.binarytree.plugins.qualitygates.checks;

import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

import de.binarytree.plugins.qualitygates.result.CheckResult;

public abstract class SonarCheck extends Check {

    @Override
    public abstract void doCheck(AbstractBuild build, BuildListener listener, Launcher launcher, CheckResult checkResult);

    @Override
    public String toString() {
        return super.toString() + "[Sonar]";
    }

    public static abstract class SonarCheckDescriptor extends CheckDescriptor {

        private String host;

        private int port;

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            if (formData.has("host")) {
                host = formData.getString("host");
                port = formData.getInt("port");
                save();
            }
            return super.configure(req, formData);
        }

        public int getPort() {
            return port;
        }

        public String getHost() {
            return host;
        }
    }

}
