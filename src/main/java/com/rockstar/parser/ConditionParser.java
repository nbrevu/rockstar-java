package com.rockstar.parser;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.rockstar.RockstarException;
import com.rockstar.internal.Comparison;
import com.rockstar.internal.Condition;
import com.rockstar.internal.conditions.ComparisonCondition;
import com.rockstar.internal.conditions.InvertedCondition;
import com.rockstar.internal.conditions.ValueCondition;

public class ConditionParser {
	private final static List<String> IS_NOT_IDS=Arrays.asList(" is not "," ain't ");
	private final static List<String> IS_IDS=Arrays.asList(" is ");
	private final static Set<String> HIGHER_IDS=ImmutableSet.of("higher","greater","bigger","stronger");
	private final static Set<String> LOWER_IDS=ImmutableSet.of("lower","less","smaller","weaker");
	
	public static Condition parseCondition(String str)	{
		// "Is not" must be tried before "is" because "is not" includes "is".
		for (String notId:IS_NOT_IDS) if (str.contains(notId)) return new InvertedCondition(parseCondition(str,notId));
		for (String id:IS_IDS) if (str.contains(id)) return parseCondition(str,id);
		return new ValueCondition(str);
	}
	
	public static Condition parseCondition(String str,String id)	{
		String[] split=str.split(id);
		if (split.length!=2) throw new RockstarException("Malformed condition.");
		String lhs=split[0];
		if (split[1].contains(" than "))	{
			String[] resplit=split[1].split(" than ");
			if (HIGHER_IDS.contains(resplit[0])) return new ComparisonCondition(lhs,resplit[1],Comparison.HIGHER);
			else if (LOWER_IDS.contains(resplit[0])) return new ComparisonCondition(lhs,resplit[1],Comparison.LOWER);
			else throw new RockstarException("Malformed condition.");
		}	else return new ComparisonCondition(lhs,split[1],Comparison.EQUAL);
	}
}
