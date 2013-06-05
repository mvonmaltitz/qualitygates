package de.binarytree.plugins.qualitygates;

import static org.junit.Assert.assertEquals;
import hudson.XmlFile;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class HelloWorldBuilderTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testInitializeWithNullCollection() throws IOException {
        QualityGateBuilder builder = new QualityGateBuilder("Builder", null) {
            @Override
            protected XmlFile getConfigXml() {
                return new XmlFile(new File("/tmp/tmp.xml"));
            }
        };
        assertEquals(0, builder.getNumberOfGates());
    }

}
