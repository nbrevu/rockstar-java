package com.rockstar.internal.conditions;

import com.rockstar.Program;
import com.rockstar.internal.Condition;
import com.rockstar.internal.Value;

public class ValueCondition implements Condition {
	private final String expression;
	
	public ValueCondition(String expression)	{
		this.expression=expression;
	}

	@Override
	public boolean evaluate(Program state) {
		Value val=state.evaluate(expression);
		switch (val.getContentType())	{
		case NUMBER:
			return val.getValue(Double.class)==0.0;
		case STRING:
			return val.getValue(String.class).equals("");
		case BOOLEAN:
			return val.getValue(Boolean.class);
		case NULL_TYPE: // fall-through.
		case MYSTERIOUS: // fall-through.
		default:
			return false;
		}
	}
}
