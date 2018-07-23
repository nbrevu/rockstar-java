package com.rockstar.internal.conditions;

import com.rockstar.Program;
import com.rockstar.internal.Condition;

public class InvertedCondition implements Condition {
	private final Condition baseCondition;
	
	public InvertedCondition(Condition baseCondition)	{
		this.baseCondition=baseCondition;
	}

	@Override
	public boolean evaluate(Program state) {
		return !baseCondition.evaluate(state);
	}
}
