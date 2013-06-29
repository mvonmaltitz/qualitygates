package de.binarytree.plugins.qualitygates;

import hudson.model.AbstractBuild;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import hudson.Launcher;
import hudson.XmlFile;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class TestHelper {
	public static AbstractBuild getBuildMock() {
		return mock(AbstractBuild.class);
	} 
	public static Launcher getLauncherMock(){
		return mock(Launcher.class); 
	}
	public static BuildListener getListenerMock(){
		return mock(BuildListener.class);
	}
}
