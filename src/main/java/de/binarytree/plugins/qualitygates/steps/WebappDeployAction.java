package de.binarytree.plugins.qualitygates.steps;

import hudson.Extension;
import hudson.util.FormValidation;

import java.util.LinkedList;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import de.binarytree.plugins.qualitygates.GateStepDescriptor;

public class WebappDeployAction extends ShellAction {

    private static final String WARFILE_FLAG = "w";

    private static final String PROPERTIES_FLAG = "p";

    private static final String CONTEXT_FLAG = "c";

    private String propertiesFile;

    private String managerUrl;

    private final String COMMAND = "webapp-deploy";

    private final String OPTIONS = "-N";

    private final String MODE = "update";

    private String containerList;

    private String managerUser;

    private String managerPassword;

    private String context;

    private int deployContextLimit;

    private String warFile;

    private String additionalParameters;

    @DataBoundConstructor
    public WebappDeployAction(String propertiesFile, String containerList, String managerUrl, String managerUser,
            String managerPassword, String context, int deployContextLimit, String warFile, String additionalParameters) {
        super();
        this.propertiesFile = propertiesFile;
        this.containerList = containerList;
        this.managerUrl = managerUrl;
        this.managerUser = managerUser;
        this.managerPassword = managerPassword;
        this.context = context;
        this.deployContextLimit = deployContextLimit;
        this.warFile = warFile;
        this.additionalParameters = additionalParameters;
    }

    public String getPropertiesFile() {
        return propertiesFile;
    }

    public String getManagerUrl() {
        return managerUrl;
    }

    public String getContainerList() {
        return containerList;
    }

    public String getManagerUser() {
        return managerUser;
    }

    public String getManagerPassword() {
        return managerPassword;
    }

    public String getContext() {
        return context;
    }

    public int getDeployContextLimit() {
        return deployContextLimit;
    }

    public String getWarFile() {
        return warFile;
    }

    public String getAdditionalParameters() {
        return additionalParameters;
    }

    private String generateCommand() {
        LinkedList<String> list = new LinkedList<String>();
        addCommand(list);
        addDefaultOptions(list);
        addPropertyFileOption(list);
        addContainerListOption(list);
        addTomcatManagerOptions(list);
        addContextOptions(list);
        addWarFileOption(list);
        addAdditionalOptions(list);
        addMode(list);

        return generateListFromString(list);

    }

    private void addContextOptions(LinkedList<String> list) {
        addContextOption(list);
        addDeployContextLimitOption(list);
    }

    private void addTomcatManagerOptions(LinkedList<String> list) {
        addManagerUrlOption(list);
        addManagerUserOption(list);
        addManagerPasswordOption(list);
    }

    private String generateListFromString(LinkedList<String> list) {
        String command = "";
        for (String part : list) {
            command += part;
            command += " ";
        }
        return command.trim();
    }

    private void addMode(LinkedList<String> list) {
        list.add(MODE);
    }

    private void addWarFileOption(LinkedList<String> list) {
        if (isDefined(warFile)) {
            list.add(generateWarFileOption(warFile));
        }
    }

    private void addAdditionalOptions(LinkedList<String> list) {
        if (isDefined(additionalParameters)) {
            list.add(additionalParameters);
        }
    }

    private void addDeployContextLimitOption(LinkedList<String> list) {
        if (deployContextLimit > 0) {
            list.add(generateGenericOption("deploy.context.limit", Integer.toString(deployContextLimit)));
        }
    }

    private void addContextOption(LinkedList<String> list) {
        if (isDefined(context)) {
            list.add(generateContextOption(context));
        }
    }

    private void addManagerPasswordOption(LinkedList<String> list) {
        if (isDefined(managerPassword)) {
            list.add(generateGenericOption("deploy.tomcat.manager.password", managerPassword));
        }
    }

    private void addManagerUserOption(LinkedList<String> list) {
        if (isDefined(managerUser)) {
            list.add(generateGenericOption("deploy.tomcat.manager.user", managerUser));
        }
    }

    private void addManagerUrlOption(LinkedList<String> list) {
        if (isDefined(managerUrl)) {
            list.add(generateGenericOption("deploy.tomcat.manager.url", managerUrl));
        }
    }

    private void addContainerListOption(LinkedList<String> list) {
        if (isDefined(containerList)) {
            list.add(generateGenericOption("deploy.container.list", containerList));
        }
    }

    private void addPropertyFileOption(LinkedList<String> list) {
        if (isDefined(propertiesFile)) {
            list.add(generatePropertyFileOption(propertiesFile));
        }
    }

    private void addDefaultOptions(LinkedList<String> list) {
        list.add(OPTIONS);
    }

    private void addCommand(LinkedList<String> list) {
        list.add(COMMAND);
    }

    private String generateContextOption(String context) {
        return generateFlagOption(CONTEXT_FLAG, context);
    }

    private String generatePropertyFileOption(String propertiesFile) {
        return generateFlagOption(PROPERTIES_FLAG, propertiesFile);
    }

    private String generateWarFileOption(String warFile) {
        return generateFlagOption(WARFILE_FLAG, warFile);
    }

    private String generateFlagOption(String flag, String value) {
        return "-" + flag + " " + value;

    }

    private String generateGenericOption(String option, String value) {
        return String.format("-D %s='%s'", option, value);
    }

    @Override
    public String getCommand() {
        if (!isDefined(super.getCommand())) {
            setCommand(generateCommand());
        }

        return super.getCommand();
    }

    private boolean isDefined(String s) {
        return (s != null) && !s.isEmpty();
    }

    @Extension
    public static class WebappDeployActionDescriptor extends GateStepDescriptor {

        @Override
        public String getDisplayName() {
            return "Execution of webapp-deploy";
        }

        public FormValidation doCheckContext(@QueryParameter String value) {
            if (value.length() == 0) {
                return FormValidation.ok();
            } else {
                if (value.startsWith("/")) {
                    return FormValidation.ok();
                } else {
                    return FormValidation.error("Context must start with a slash");
                }
            }
        }

    }
}
