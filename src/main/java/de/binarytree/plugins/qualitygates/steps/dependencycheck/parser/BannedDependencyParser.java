package de.binarytree.plugins.qualitygates.steps.dependencycheck.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

public final class BannedDependencyParser{

	private BannedDependencyParser(){
	}

	private static final Pattern ARTIFACT_PATTERN = Pattern.compile("Found Banned Dependency: (.*:.*:.*:.*)");

	public static BannedDependencyAnalysisResult parseDependencyAnalyzeSection(String content) throws IOException{

		BannedDependencyAnalysisResult result = new BannedDependencyAnalysisResult();
		List<String> lines = IOUtils.readLines(new StringReader(content));
		for(String line: lines){
			if(!StringUtils.isBlank(line)){
				Matcher matcher = ARTIFACT_PATTERN.matcher(line);
				if(matcher.matches()){
					String bannedDependency = matcher.group(1);
					result.addBannedDependency(bannedDependency);
				}
			}
		}

		return result;
	}
}
