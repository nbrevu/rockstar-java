package com.rockstar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.CharMatcher;
// Guava's Optional allows chained "or".
import com.google.common.base.Optional;
import com.rockstar.internal.Function;
import com.rockstar.internal.Instruction;
import com.rockstar.internal.RhsKeyword;
import com.rockstar.internal.Value;

public class Program {
	private final List<Instruction> instructions;
	// If the first letter of the name is lowercase, it's a common variable. Otherwise it's a proper variable.
	private final Map<String,Function> functions;
	private final Map<String,Value> variables;
	private String lastReferencedVariable;
	
	public Program(List<Instruction> instructions,Map<String,Function> functions)	{
		this.instructions=instructions;
		this.functions=functions;
		variables=new HashMap<>();
		lastReferencedVariable=null;
	}
	
	public Value getVariable(String name)	{
		lastReferencedVariable=name;
		return variables.getOrDefault(name,Value.MYSTERIOUS);
	}
	
	public void assignVariable(String name,Value value)	{
		lastReferencedVariable=name;
		variables.put(name,value);
	}
	
	public String getLastReferencedVariable()	{
		return lastReferencedVariable;
	}
	
	public Value evaluate(String rhs)	{
		return getAsKeyword(rhs).
				or(getAsCommonVariable(rhs)).
				or(getAsProperVariable(rhs)).
				or(getAsFunctionCall(rhs)).
				or(getAsBooleanLiteral(rhs)).
				or(getAsNumberLiteral(rhs)).
				or(getAsStringLiteral(rhs)).
				or(getAsPoeticStringLiteral(rhs)).
				or(getAsPoeticNumberLiteral(rhs)).
				or(Value.MYSTERIOUS);
	}
	
	private Optional<Value> getAsKeyword(String rhs)	{
		RhsKeyword keyword=RhsKeyword.getKeywordIfExisting(rhs);
		return (keyword==null)?Optional.absent():Optional.of(keyword.interpret(this));
	}
	
	private Optional<Value> getAsCommonVariable(String rhs)	{
		String name=Parser.parseAsCommonVariableName(rhs);
		// Will return Mysterious if not declared yet.
		return (name==null)?Optional.absent():Optional.of(getVariable(name));
	}
	
	private Optional<Value> getAsProperVariable(String rhs)	{
		return Parser.isProperVariableName(rhs)?Optional.of(getVariable(rhs)):Optional.absent(); 
	}
	
	private Optional<Value> getAsBooleanLiteral(String rhs)	{
		if ("true".equals(rhs)) return Optional.of(Value.TRUE);
		else if ("false".equals(rhs)) return Optional.of(Value.FALSE);
		else return Optional.absent();
	}
	
	private Optional<Value> getAsNumberLiteral(String rhs)	{
		// There are more elegant ways to do this. They also take much more code.
		try	{
			double value=Double.parseDouble(rhs);
			return Optional.of(Value.createNumber(value));
		}	catch (NumberFormatException nfe)	{
			return Optional.absent();
		}
	}
	
	private Optional<Value> getAsStringLiteral(String rhs)	{
		if (rhs.length()>2)	{
			char quote=rhs.charAt(0);
			if ((quote=='\'')||(quote=='\"'))	{
				if (rhs.charAt(rhs.length()-1)==quote)	{
					String literal=rhs.substring(1,rhs.length()-1);
					return Optional.of(Value.createString(literal));
				}
			}
		}
		return Optional.absent();
	}
	
	private Optional<Value> getAsPoeticStringLiteral(String rhs)	{
		if (rhs.endsWith("\\n"))	{
			String literal=rhs.substring(0,rhs.length()-2);
			return Optional.of(Value.createString(literal));
		}	else return Optional.absent();
	}
	
	private Optional<Value> getAsPoeticNumberLiteral(String rhs)	{
		// Surely there are better ways to do this. I'm not bothering for now.
		StringBuilder numberBuilder=new StringBuilder();
		String[] numbers=rhs.split(" ");
		for (String number:numbers)	{
			int count=CharMatcher.inRange('a','z').or(CharMatcher.inRange('A','Z')).countIn(number);
			numberBuilder.append(count%10);
			if (number.contains(".")) numberBuilder.append('.');
		}
		try	{
			double literal=Double.parseDouble(numberBuilder.toString());
			return Optional.of(Value.createNumber(literal));
		}	catch (NumberFormatException exc)	{
			return Optional.absent();
		}
	}
	
	private Optional<Value> getAsFunctionCall(String rhs)	{
		String[] split=rhs.split(" taking ");
		if (split.length!=2) return Optional.absent();
		String functionName=split[0];
		Function function=functions.get(functionName);
		if (function==null) return Optional.absent();
		String[] resplit=split[1].split(", ");
		List<Value> arguments=new ArrayList<>();
		for (String expr:resplit) arguments.add(evaluate(expr));
		return Optional.of(function.call(arguments, functions));
	}
	
	public void run()	{
		for (Instruction instr:instructions) instr.run(this);
	}
}
