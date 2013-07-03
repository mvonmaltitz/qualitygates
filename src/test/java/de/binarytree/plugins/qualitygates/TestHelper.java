package de.binarytree.plugins.qualitygates;

import static org.mockito.Mockito.mock;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;

public class TestHelper {
    public static AbstractBuild getBuildMock() {
        return mock(AbstractBuild.class);
    }

    public static Launcher getLauncherMock() {
        return mock(Launcher.class);
    }

    public static BuildListener getListenerMock() {
        return mock(BuildListener.class);
    }
}
