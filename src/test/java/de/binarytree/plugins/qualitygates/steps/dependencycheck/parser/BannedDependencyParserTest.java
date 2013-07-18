package de.binarytree.plugins.qualitygates.steps.dependencycheck.parser;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class BannedDependencyParserTest{

	private final String SECTION_15 = "\nNon matching line\nFound Banned Dependency: org.springframework:spring-support:jar:2.0.8\n" + "Found Banned Dependency: org.springframework:spring-tx:jar:2.5.6\n" + "Found Banned Dependency: org.springframework:spring-context-support:jar:2.5.6\n" + "Found Banned Dependency: org.springframework:spring-core:jar:2.5.6\n" + "Found Banned Dependency: org.springframework:spring-mock:jar:2.0.8\n" + "Found Banned Dependency: org.springframework:spring-webmvc:jar:2.5.6\n" + "Found Banned Dependency: com.unitedinternet.portal.commons.http-utils:http-utils:jar:1.2\n" + "Found Banned Dependency: org.springframework:spring-context:jar:2.5.6\n" + "Found Banned Dependency: org.springframework:spring-web:jar:2.5.6\n" + "Found Banned Dependency: org.springframework:spring-aspects:jar:2.5.6\n" + "Found Banned Dependency: org.springframework:spring-aop:jar:2.5.6\n" + "Found Banned Dependency: org.springframework:spring-jdbc:jar:2.5.6\n" + "Found Banned Dependency: org.springframework:spring-test:jar:2.5.6\n" + "Found Banned Dependency: org.springframework:spring:jar:2.5.6\n" + "Found Banned Dependency: org.springframework:spring-beans:jar:2.5.6";
	private final String SECTION_10 = "Found Banned Dependency: org.springframework:spring-webmvc:jar:2.5.6\n" + "Found Banned Dependency: com.unitedinternet.portal.commons.http-utils:http-utils:jar:1.2\n" + "Found Banned Dependency: org.springframework:spring-context:jar:2.5.6\n" + "Found Banned Dependency: org.springframework:spring-web:jar:2.5.6\n" + "Found Banned Dependency: org.springframework:spring-aspects:jar:2.5.6\n" + "Found Banned Dependency: org.springframework:spring-aop:jar:2.5.6\n" + "Found Banned Dependency: org.springframework:spring-jdbc:jar:2.5.6\n" + "Found Banned Dependency: org.springframework:spring-test:jar:2.5.6\n" + "Found Banned Dependency: org.springframework:spring:jar:2.5.6\n" + "Found Banned Dependency: org.springframework:spring-beans:jar:2.5.6";
	private final String SECTION_WITH_DUPLICATES = "Found Banned Dependency: org.springframework:spring-webmvc:jar:2.5.6\n" 
	+ "Found Banned Dependency: com.unitedinternet.portal.commons.http-utils:http-utils:jar:1.2\n" 
			+ "Found Banned Dependency: org.springframework:spring-context:jar:2.5.6\n" 
	+ "Found Banned Dependency: org.springframework:spring-web:jar:2.5.6\n" 
			+ "Found Banned Dependency: org.springframework:spring-aspects:jar:2.5.6\n" 
	+ "Found Banned Dependency: org.springframework:spring-aop:jar:2.5.6\n" 
			+ "Found Banned Dependency: org.springframework:spring-jdbc:jar:2.5.6\n" 
			+ "Found Banned Dependency: org.springframework:spring-context:jar:2.5.6\n" 
	+ "Found Banned Dependency: org.springframework:spring-web:jar:2.5.6\n" 
			+ "Found Banned Dependency: org.springframework:spring-aspects:jar:2.5.6\n" 
	+ "Found Banned Dependency: org.springframework:spring-aop:jar:2.5.6\n" 
			+ "Found Banned Dependency: org.springframework:spring-jdbc:jar:2.5.6\n" 
	+ "Found Banned Dependency: org.springframework:spring-test:jar:2.5.6\n" 
			+ "Found Banned Dependency: org.springframework:spring:jar:2.5.6\n" + "Found Banned Dependency: org.springframework:spring-beans:jar:2.5.6";
	
	

	@Test
	public void testParserfinds15DependenciesInSection() throws IOException{
		BannedDependencyAnalysisResult analysis = BannedDependencyParser.parseDependencyAnalyzeSection(SECTION_15); 
		assertEquals(15, analysis.getNumberOfBannedDependencies()); 
		
	}
	@Test
	public void testParserfinds10DependenciesInSection() throws IOException{
		BannedDependencyAnalysisResult analysis = BannedDependencyParser.parseDependencyAnalyzeSection(SECTION_10); 
		assertEquals(10, analysis.getNumberOfBannedDependencies()); 
	}
	@Test
	public void testParserEliminatesDuplicates() throws IOException{
		BannedDependencyAnalysisResult analysis = BannedDependencyParser.parseDependencyAnalyzeSection(SECTION_WITH_DUPLICATES); 
		assertEquals(10, analysis.getNumberOfBannedDependencies()); 
	}
	@Test
	public void testGettingDependenciesReturnsListWithCorrectSizeAndContent() throws IOException{
		BannedDependencyAnalysisResult analysis = BannedDependencyParser.parseDependencyAnalyzeSection(SECTION_WITH_DUPLICATES); 
		assertEquals(10, analysis.getBannedDependencies().size()); 
		assertEquals(analysis.getNumberOfBannedDependencies(), analysis.getBannedDependencies().size()); 
		for(String dependency : analysis.getBannedDependencies()) {
			assertFalse(dependency.contains("Found Banned")); 
			assertTrue(dependency.matches(".*:.*:.*:.*")); 
		}
	}

}
