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
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.kohsuke.stapler.DataBoundConstructor;

import de.binarytree.plugins.qualitygates.result.BuildResultAction;

/**
 * @author Marcel von Maltitz
 */
public class QualityLine extends Recorder implements Saveable {

    private String name;

    private List<Gate> gates = new LinkedList<Gate>();

    @DataBoundConstructor
    public QualityLine(String name, Collection<Gate> gates) throws IOException {
        this.name = name;
        if (gates != null) {
            this.gates.addAll(gates);
        }
        save();
    }

    public String getName() {
        return name;
    }

    public int getNumberOfGates() {
        return this.gates.size();
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        listener.getLogger().println("Starting QualityLine");
        QualityLineEvaluator gateEvaluator = getGateEvaluatorForGates();
        gateEvaluator.evaluate(build, launcher, listener);
        build.addAction(new BuildResultAction(gateEvaluator));
        listener.getLogger().println("Stopping QualityLine");
        return true;
    }

    protected QualityLineEvaluator getGateEvaluatorForGates() {
        return new QualityLineEvaluator(this.gates);
    }

    public List<Gate> getGates() {
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

    public final void save() throws IOException {
        if (BulkChange.contains(this)) {
            return;
        }
        getConfigXml().write(this);
    }

    protected XmlFile getConfigXml() {
        return new XmlFile(Hudson.XSTREAM, new File(Hudson.getInstance().getRootDir(), "qualitygates.xml"));
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    /**
     * Descriptor for {@link QualityLine}. Used as a singleton. The class is marked as public so that it can be accessed
     * from views.
     * 
     * <p>
     * See <tt>src/main/resources/hudson/plugins/hello_world/HelloWorldBuilder/*.jelly</tt> for the actual HTML fragment
     * for the configuration screen.
     */
    @Extension
    // This indicates to Jenkins that this is an implementation of an extension
    // point
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        /**
         * To persist global configuration information, simply store it in a field and call save().
         * 
         * <p>
         * If you don't want fields to be persisted, use <tt>transient</tt>.
         */

        public Collection<QualityGateDescriptor> getDescriptors() {
            return Gate.all();
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
            return "Quality Line";
        }

    }

}
