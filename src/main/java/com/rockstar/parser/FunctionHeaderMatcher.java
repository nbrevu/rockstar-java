package com.rockstar.parser;

import java.util.ArrayList;
import java.util.List;

import com.rockstar.Parser;

public class FunctionHeaderMatcher {
	private boolean isCorrect;
	private String functionName;
	private List<String> argumentNames;
	
	public FunctionHeaderMatcher(String line)	{
		isCorrect=true;
		if (!line.contains(" takes "))	{
			isCorrect=false;
			return;
		}
		String[] split=line.split(" takes ");
		if (split.length!=2)	{
			isCorrect=false;
			return;
		}
		functionName=split[0];
		if (functionName.contains(" "))	{
			isCorrect=false;
			return;
		}
		String[] names=split[1].split(" and ");
		argumentNames=new ArrayList<>();
		for (String name:names)	{
			String varName=Parser.parseAsCommonVariableName(name);
			if (varName==null)	{
				isCorrect=false;
				return;
			}	else argumentNames.add(varName);
		}
	}
	
	public boolean isFunction()	{
		return isCorrect;
	}
	
	public String getFunctionName()	{
		return functionName;
	}
	
	public List<String> getArgNames()	{
		return argumentNames;
	}
}
