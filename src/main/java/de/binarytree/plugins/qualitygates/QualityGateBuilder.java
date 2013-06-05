package de.binarytree.plugins.qualitygates;

import hudson.BulkChange;
import hudson.Extension;
import hudson.Launcher;
import hudson.XmlFile;
import hudson.model.BuildListener;
import hudson.model.Saveable;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Hudson;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import de.binarytree.plugins.qualitygates.result.BuildResultAction;
import de.binarytree.plugins.qualitygates.result.GatesResult;

/**
 * @author Marcel von Maltitz
 */
public class QualityGateBuilder extends Builder implements Saveable {

    private String name;

    private List<QualityGate> gates = new LinkedList<QualityGate>();

    private GatesResult gatesResult;

    @DataBoundConstructor
    public QualityGateBuilder(String name, Collection<QualityGate> gates) throws IOException {
        this.name = name;
        if (gates != null) {
            this.gates.addAll(gates);
        }
        save();
    }

    /**
     * We'll use this from the <tt>config.jelly</tt>.
     */
    public String getName() {
        return name;
    }

    public int getNumberOfGates() {
        return this.gates.size();
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        GateEvaluator gateEvaluator = new GateEvaluator(this.gates);
        gateEvaluator.evaluate(build, launcher, listener);
        build.addAction(new BuildResultAction(gateEvaluator));
        return true;
    }

    public List<QualityGate> getGates() {
        return gates;
    }

    // Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    protected void load() throws IOException {
        XmlFile xml = getConfigXml();
        if (xml.exists()) {
            xml.unmarshal(this);
        }
    }

    public void save() throws IOException {
        if (BulkChange.contains(this)) {
            return;
        }
        getConfigXml().write(this);
    }

    protected XmlFile getConfigXml() {
        return new XmlFile(Hudson.XSTREAM, new File(Hudson.getInstance().getRootDir(), "qualitygates.xml"));
    }

    /**
     * Descriptor for {@link QualityGateBuilder}. Used as a singleton. The class is marked as public so that it can be
     * accessed from views.
     * 
     * <p>
     * See <tt>src/main/resources/hudson/plugins/hello_world/HelloWorldBuilder/*.jelly</tt> for the actual HTML fragment
     * for the configuration screen.
     */
    @Extension
    // This indicates to Jenkins that this is an implementation of an extension
    // point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        /**
         * To persist global configuration information, simply store it in a field and call save().
         * 
         * <p>
         * If you don't want fields to be persisted, use <tt>transient</tt>.
         */

        public Collection<QualityGateDescriptor> getDescriptors() {
            return QualityGate.all();
        }

        /**
         * Performs on-the-fly validation of the form field 'name'.
         * 
         * @param value
         *            This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         */
        public FormValidation doCheckName(@QueryParameter String value) throws IOException, ServletException {
            if (value.length() == 0) {
                return FormValidation.error("Please set a name");
            }
            if (value.length() < 4) {
                return FormValidation.warning("Isn't the name too short?");
            }
            return FormValidation.ok();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project
            // types
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        @Override
        public String getDisplayName() {
            return "Quality Gates";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            // To persist global configuration information,
            // set that to properties and call save().
            // ^Can also use req.bindJSON(this, formData);
            // (easier when there are many fields; need set* methods for this,
            // like setUseFrench)
            // save();
            return super.configure(req, formData);
        }

    }

}
