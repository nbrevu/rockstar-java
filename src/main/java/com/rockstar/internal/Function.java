package com.rockstar.internal;

import java.util.List;
import java.util.Map;

import com.rockstar.Program;
import com.rockstar.RockstarException;

public class Function {
	private List<String> argumentNames;
	private String returnValueExpr;
	private List<Instruction> code;
	
	public Function(List<String> argumentNames,String returnValueExpr,List<Instruction> code)	{
		this.argumentNames=argumentNames;
		this.returnValueExpr=returnValueExpr;
		this.code=code;
	}
	
	public Value call(List<Value> arguments,Map<String,Function> functions)	{
		Program program=new Program(code,functions);
		int N=argumentNames.size();
		if (N!=arguments.size()) throw new RockstarException("Expected "+N+" arguments, found "+arguments.size()+".");
		for (int i=0;i<N;++i) program.assignVariable(argumentNames.get(i),arguments.get(i));
		program.run();
		return program.evaluate(returnValueExpr);
	}
}
