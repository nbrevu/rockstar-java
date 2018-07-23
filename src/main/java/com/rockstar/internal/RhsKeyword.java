package com.rockstar.internal;

import java.util.HashMap;
import java.util.Map;

import com.rockstar.Program;

public enum RhsKeyword {
	// This enum stores the keywords that have a standalone meaning. I.e., "the"/"my"/"your" are not included here.
	IT("it",InternalMeaning.PRONOUN),
	HE("he",InternalMeaning.PRONOUN),	//
	SHE("she",InternalMeaning.PRONOUN),	//
	HIM("him",InternalMeaning.PRONOUN),	//
	HER("her",InternalMeaning.PRONOUN),	//
	THEY("they",InternalMeaning.PRONOUN),	//
	THEM("them",InternalMeaning.PRONOUN),	//
	NULL("null",InternalMeaning.NULL),		//
	NOTHING("nothing",InternalMeaning.NULL),	//
	NOWHERE("nowhere",InternalMeaning.NULL),	//
	NOBODY("nobody",InternalMeaning.NULL),	//
	MYSTERIOUS("mysterious",InternalMeaning.MYSTERIOUS);
	
	private final String keyword;
	private final InternalMeaning meaning;
	private RhsKeyword(String keyword,InternalMeaning meaning)	{
		this.keyword=keyword;
		this.meaning=meaning;
	}
	
	private static final Map<String,RhsKeyword> ALL_KEYWORDS=new HashMap<>();
	static	{
		for (RhsKeyword keyword:values()) ALL_KEYWORDS.put(keyword.keyword, keyword);
	}
	
	public static RhsKeyword getKeywordIfExisting(String rhs)	{
		return ALL_KEYWORDS.get(rhs);
	}
	
	public Value interpret(Program state)	{
		return meaning.interpret(state);
	}
	
	private enum InternalMeaning	{
		NULL	{
			@Override
			public Value interpret(Program state)	{
				return Value.NULL;
			}
		},
		MYSTERIOUS	{
			@Override
			public Value interpret(Program state)	{
				return Value.MYSTERIOUS;
			}
		},
		PRONOUN	{
			@Override
			public Value interpret(Program state)	{
				String lastVariableName=state.getLastReferencedVariable();
				if (lastVariableName==null) return Value.MYSTERIOUS;
				else return state.getVariable(lastVariableName);
			}
		};
		
		public abstract Value interpret(Program state);
	}
}
