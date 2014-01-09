package de.binarytree.plugins.qualitygates.steps;

import static org.junit.Assert.assertThat;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WebappDeployActionTest {

    private WebappDeployAction action;

    private String properties;

    private String containerList;

    private String managerUrl;

    private String managerUser;

    private String managerPassword;

    private String context;

    private int deployContextLimit;

    private String warFile;

    private String additionalParameters;

    @Before
    public void setUp() throws Exception {
        action = null;
        properties = "folder/file.properties";
        containerList = "container1:8080, container2.domain.lan:8080";
        managerUrl = "/manager";
        managerUser = "user";
        managerPassword = "password";
        context = "/containercontext";
        deployContextLimit = 10;
        warFile = "../Project/target/myapp-1.0-SNAPSHOT.war";
        additionalParameters = "-v --ignore-errors";

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testCommandWhenSpecifyingPropertiesFile() {
        action = new WebappDeployAction(properties, null, null, null, null, null, 0, null, null);
        assertStartsWithCommand();
        assertUsesPropertyFile(properties);
        assertEndsWithMode();
    }

    @Test
    public void testCommandWhenSpecifyingContainerList() {
        action = new WebappDeployAction(null, containerList, null, null, null, null, 0, null, null);
        assertStartsWithCommand();
        assertDefinesContainerList();
        assertEndsWithMode();
    }

    @Test
    public void testCommandWhenSpecifyingManagerURL() {
        action = new WebappDeployAction(null, null, managerUrl, null, null, null, 0, null, null);
        assertStartsWithCommand();
        assertDefinesManagerUrl();
        assertEndsWithMode();
    }

    @Test
    public void testCommandWhenSpecifyingManagerUser() {
        action = new WebappDeployAction(null, null, null, managerUser, null, null, 0, null, null);
        assertStartsWithCommand();
        assertDefinesManagerUser();
        assertEndsWithMode();
    }

    @Test
    public void testCommandWhenSpecifyingManagerPassword() {
        action = new WebappDeployAction(null, null, null, null, managerPassword, null, 0, null, null);
        assertStartsWithCommand();
        assertDefinesManagerPassword();
        assertEndsWithMode();
    }

    @Test
    public void testCommandWhenSpecifyingContext() {
        action = new WebappDeployAction(null, null, null, null, null, context, 0, null, null);
        assertStartsWithCommand();
        assertDefinesContext();
        assertEndsWithMode();
    }

    @Test
    public void testCommandWhenSpecifyingContextLimit() {
        action = new WebappDeployAction(null, null, null, null, null, null, deployContextLimit, null, null);
        assertStartsWithCommand();
        assertDefinesDeployContextLimit();
        assertEndsWithMode();
    }

    @Test
    public void testCommandWhenSpecifyingWarFile() {
        action = new WebappDeployAction(null, null, null, null, null, null, 0, warFile, null);
        assertStartsWithCommand();
        assertDefinesWarFile();
        assertEndsWithMode();
    }

    @Test
    public void testCommandWhenSpecifyingOtherParameter() {
        action = new WebappDeployAction(null, null, null, null, null, null, 0, null, additionalParameters);
        assertStartsWithCommand();
        assertDefinesAdditionalParameters();
        assertEndsWithMode();
    }


    @Test
    public void testCommandWhenSpecifyingPropertiesAndManagerURL() {
        action = new WebappDeployAction(properties, null, managerUrl, null, null, null, 0, null, null);
        assertStartsWithCommand();
        assertUsesPropertyFile(properties);
        assertDefinesManagerUrl();
        assertEndsWithMode();
    }

    @Test
    public void testCommandWhenSpecifyingAllInformation() {
        action = new WebappDeployAction(properties, containerList, managerUrl, managerUser, managerPassword, context,
                deployContextLimit, warFile, additionalParameters);
        assertStartsWithCommand();
        assertUsesPropertyFile(properties);
        assertDefinesContainerList();
        assertDefinesManagerUser();
        assertDefinesManagerPassword();
        assertDefinesContext();
        assertDefinesDeployContextLimit();
        assertDefinesWarFile();
        assertDefinesAdditionalParameters();
        assertEndsWithMode();
    }

    private void assertStartsWithCommand() {
        assertStartsWith("webapp-deploy -N ", action.getCommand());
    }

    private void assertUsesPropertyFile(String properties) {
        assertContains(" -p " + properties + " ", action.getCommand());
    }

    private void assertDefinesContainerList() {
        assertContainsOption("deploy.container.list", containerList);
    }

    private void assertDefinesManagerUrl() {
        assertContainsOption("deploy.tomcat.manager.url", managerUrl);
    }

    private void assertDefinesManagerUser() {
        assertContainsOption("deploy.tomcat.manager.user", managerUser);
    }

    private void assertDefinesManagerPassword() {
        assertContainsOption("deploy.tomcat.manager.password", managerPassword);
    }

    private void assertDefinesContext() {
        assertContains("-c " + context, action.getCommand());
    }

    private void assertDefinesDeployContextLimit() {
        assertContainsOption("deploy.context.limit", Integer.toString(deployContextLimit));
    }

    private void assertDefinesWarFile() {
        assertContains("-w " + warFile, action.getCommand());
    }

    private void assertDefinesAdditionalParameters() {
        assertContains(" " + additionalParameters + " ", action.getCommand());

    }
    private void assertEndsWithMode() {
        assertEndsWith(" update", action.getCommand());
    }

    private void assertContainsOption(String option, String value) {
        assertContains(String.format(" -D %s='%s'", option, value), action.getCommand());
    }

    private void assertEndsWith(String suffix, String string) {
        assertThat(string, Matchers.endsWith(suffix));
    }

    private void assertStartsWith(String prefix, String string) {
        assertThat(string, Matchers.startsWith(prefix));
    }

    private void assertContains(String content, String string) {
        assertThat(string, Matchers.containsString(content));
    }

}
