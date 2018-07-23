package com.rockstar;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import com.google.common.io.Files;
import com.rockstar.internal.Condition;
import com.rockstar.internal.Function;
import com.rockstar.internal.Instruction;
import com.rockstar.internal.conditions.CompositeCondition;
import com.rockstar.internal.instructions.Assignment;
import com.rockstar.internal.instructions.BlockInstruction;
import com.rockstar.internal.instructions.Conditional;
import com.rockstar.internal.instructions.Decrement;
import com.rockstar.internal.instructions.Increment;
import com.rockstar.internal.instructions.Input;
import com.rockstar.internal.instructions.Loop;
import com.rockstar.internal.instructions.Output;
import com.rockstar.internal.instructions.SpecialBlockInstruction;
import com.rockstar.internal.instructions.SubtractInstruction;
import com.rockstar.parser.AssignmentParser;
import com.rockstar.parser.ConditionParser;
import com.rockstar.parser.FunctionHeaderMatcher;

public class Parser {
	public final static List<String> COMMON_VARIABLE_NAMES=Arrays.asList("the ","my ","your ","The ","My ","Your ");
	
	public static String parseAsCommonVariableName(String name)	{
		for (String prefix:COMMON_VARIABLE_NAMES)	{
			if (name.startsWith(prefix)) return name.substring(prefix.length());
		}
		return null;
	}
	
	public static String parseVariableName(String name)	{
		String varName=parseAsCommonVariableName(name);
		// If it's not a common name, it must be a proper name.
		return (varName==null)?ensureProperName(name):varName;
	}
	
	public static boolean isProperVariableName(String name)	{
		String[] split=name.split(" ");
		for (String component:split)	{
			if (component.length()<=0) continue;
			char firstLetter=component.charAt(0);
			if ((firstLetter<'A')||(firstLetter>'Z')) return false;
		}
		return true;
	}
	
	public static Program parse(String fileName) throws IOException	{
		try (BufferedReader lineReader=Files.newReader(new File(fileName), Charset.defaultCharset()))	{
			return parse(Iterators.peekingIterator(lineReader.lines().iterator()));
		}
	}
	
	public static Program parse(PeekingIterator<String> lines)	{
		Map<String,Function> functions=new HashMap<>();
		List<Instruction> instructions=new ArrayList<>();
		while (lines.hasNext())	{
			String line=lines.next().trim();
			if (line.isEmpty()) continue;
			FunctionHeaderMatcher functionMatcher=new FunctionHeaderMatcher(line);
			if (functionMatcher.isFunction())	{
				Function fun=parseFunction(functionMatcher,lines);
				functions.put(functionMatcher.getFunctionName(),fun);
			}
			else instructions.add(parseInstruction(line,lines));
		}
		return new Program(instructions, functions);
	}
	
	private static Function parseFunction(FunctionHeaderMatcher header,PeekingIterator<String> lines)	{
		List<Instruction> instructions=new ArrayList<>();
		for (;;)	{
			String line=lines.next().trim();
			if (line.isEmpty()) continue;
			if (line.startsWith("Give back "))	{
				String rhs=line.substring(10);	// "Give back ".length()=10.
				return new Function(header.getArgNames(),rhs,instructions);
			}	else instructions.add(parseInstruction(line,lines));
		}
	}
	
	private static Instruction parseInstruction(String line,PeekingIterator<String> lines)	{
		// This requires the iterator because maybe it's a block instruction.
		if (line.startsWith("Put ")) return parseAsPutInto(line);
		else if (line.startsWith("Take ")) return parseAsTakeFrom(line);
		else if (line.startsWith("Build ")) return parseAsBuildUp(line);
		else if (line.startsWith("Knock ")) return parseAsKnockDown(line);
		else if (line.startsWith("Say ")) return new Output(line.substring(4));
		else if (line.startsWith("Shout ")) return new Output(line.substring(6));
		else if (line.startsWith("Whisper ")) return new Output(line.substring(8));
		else if (line.startsWith("Scream ")) return new Output(line.substring(7));
		else if (line.startsWith("Listen to ")) return new Input(parseVariableName(line.substring(10)));
		else if (line.equals("Continue")||line.equalsIgnoreCase("take it to the top")) return SpecialBlockInstruction.CONTINUE;
		else if (line.equals("Break")||line.equalsIgnoreCase("break it down!")) return SpecialBlockInstruction.BREAK;
		else if (line.startsWith("If ")) return parseIf(line,lines);
		else if (line.startsWith("While ")) return parseWhile(line,lines);
		else if (line.startsWith("Until ")) return parseUntil(line,lines);
		AssignmentParser assignment=new AssignmentParser(line);
		if (assignment.isAssignment()) return assignment.createInstruction();
		throw new RockstarException("Unknown sentence: "+line+".");
	}
	
	private static Instruction parseIf(String line,PeekingIterator<String> lines)	{
		List<Instruction> instructions=new ArrayList<>();
		Condition cond=parseCondition(line.substring(3));
		instructions.add(parseInstruction(lines.next(),lines));
		for (;;)	{
			String newLine=lines.peek().trim();
			if (newLine.startsWith("And "))	{
				instructions.add(parseInstruction(newLine.substring(4),lines));
				lines.next();	// To advance the line we just read.
			}
			else break;
		}
		return new Conditional(cond,new BlockInstruction(instructions));
	}
	
	private static Instruction parseWhile(String line,PeekingIterator<String> lines)	{
		Condition condition=parseCondition(line.substring(6));
		BlockInstruction block=parseLoop(lines);
		return Loop.createWhileLoop(condition, block);
	}
	
	private static Instruction parseUntil(String line,PeekingIterator<String> lines)	{
		Condition condition=parseCondition(line.substring(6));
		BlockInstruction block=parseLoop(lines);
		return Loop.createUntilLoop(condition, block);
	}
	
	private static Condition parseCondition(String condString)	{
		if (condString.endsWith(",")) condString=condString.substring(0,condString.length()-1);
		if (condString.contains(" and "))	{
			String[] split=condString.split(" and ");
			List<Condition> conds=parseConditions(split);
			return CompositeCondition.andCondition(conds);
		}	else if (condString.contains(" or "))	{
			String[] split=condString.split(" and ");
			List<Condition> conds=parseConditions(split);
			return CompositeCondition.orCondition(conds);
		}	else return parseSimpleCondition(condString);
	}
	
	private static List<Condition> parseConditions(String[] conditions)	{
		List<Condition> result=new ArrayList<>();
		for (String str:conditions) result.add(parseSimpleCondition(str));
		return result;
	}
	
	private static Condition parseSimpleCondition(String cond)	{
		return ConditionParser.parseCondition(cond);
	}
	
	private static BlockInstruction parseLoop(PeekingIterator<String> lines)	{
		List<Instruction> instructions=new ArrayList<>();
		for (;;)	{
			String line=lines.next().trim();
			if (line.isEmpty()) continue;
			if (line.equals("End")||line.equals("And around we go")) return new BlockInstruction(instructions);
			else instructions.add(parseInstruction(line,lines));
		}

	}
	
	private static Instruction parseAsPutInto(String line)	{
		String[] split=line.substring(4).split(" into ");	// The 4 is because we remove "put ".
		if (split.length!=2) throw new RockstarException("Malformed assignment instruction.");
		String varName=parseVariableName(split[1]);
		return new Assignment(varName,split[0]);
	}
	
	private static Instruction parseAsTakeFrom(String line)	{
		String[] split=line.substring(5).split(" from ");	// The 5 is because we remove "take ".
		if (split.length!=2) throw new RockstarException("Malformed assignment instruction.");
		String varName=parseVariableName(split[1]);
		return new SubtractInstruction(varName,split[0]);
	}
	
	private static Instruction parseAsBuildUp(String line)	{
		String varName=parseVariableName(removeSuffix(line.substring(6)," up"));
		return new Increment(varName);
	}
	
	private static Instruction parseAsKnockDown(String line)	{
		String varName=parseVariableName(removeSuffix(line.substring(6)," down"));
		return new Decrement(varName);
	}
	
	private static String removeSuffix(String line,String suffix)	{
		if (line.endsWith(suffix)) return line.substring(0,line.length()-suffix.length());
		else throw new RockstarException("Malformed increment or decrement instruction.");
	}
	
	private static String ensureProperName(String in)	{
		if (!isProperVariableName(in)) throw new RockstarException("Malformed variable name.");
		return in;
	}
	
	public static void main(String[] args)	{
		if (args.length<1)	{
			System.out.println("I'm caught in a nigthmare, alone in the night.");
			System.out.println("Please specify a Rockstar script to run, to make me feel less alone.");
			return;
		}
		try	{
			Program program=parse(args[0]);
			program.run();
		}	catch (IOException exc)	{
			System.out.println("Error reading the script: "+exc.getMessage()+".");
		}	catch (RockstarException exc)	{
			System.out.println(exc.getMessage());
			System.out.println("You rock me like a gentle breeze. In order to rock me like a hurricane, fix the script and run it again.");
		}	catch (NoSuchElementException exc)	{
			// This happens when parsing stops (EOF) in the middle of an if/while/until loop or a function.
			System.out.println("Malformed instruction block: end of file reached prematurely.");
			System.out.println("You rock me like a gentle breeze. In order to rock me like a hurricane, fix the script and run it again.");
		}
	}
}
