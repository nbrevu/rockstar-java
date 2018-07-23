package com.rockstar.parser;

import java.util.Arrays;
import java.util.List;

import com.rockstar.Parser;
import com.rockstar.internal.Instruction;
import com.rockstar.internal.instructions.Assignment;

public class AssignmentParser {
	private final static List<String> ALIASES=Arrays.asList(" is "," was "," were "," says ");
	
	private String lhs;
	private String rhs;
	private boolean isCorrect;
	
	public AssignmentParser(String line)	{
		for (String alias:ALIASES)	{
			String[] split=line.split(alias);
			if (split.length!=2) continue;
			lhs=split[0];
			rhs=split[1];
			isCorrect=true;
			return;
		}
		isCorrect=false;
	}
	
	public boolean isAssignment()	{
		return isCorrect;
	}
	
	public Instruction createInstruction()	{
		String varName=Parser.parseVariableName(lhs);
		return new Assignment(varName,rhs);
	}
}
