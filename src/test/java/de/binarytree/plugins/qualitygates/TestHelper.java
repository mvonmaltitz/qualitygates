package de.binarytree.plugins.qualitygates;

import static org.mockito.Mockito.*;

import java.io.PrintStream;

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
        BuildListener listener = mock(BuildListener.class);
        PrintStream logger = mock(PrintStream.class); 
        when(listener.getLogger()).thenReturn(logger); 
        return listener; 
    }
}
