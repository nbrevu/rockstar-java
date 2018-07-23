package com.rockstar.internal.instructions;

import com.rockstar.Program;
import com.rockstar.internal.Instruction;
import com.rockstar.internal.Value;

public class Decrement implements Instruction {
	private final String varName;
	
	public Decrement(String varName)	{
		this.varName=varName;
	}

	@Override
	public void run(Program state) {
		Value currentValue=state.getVariable(varName);
		double doubleValue=currentValue.getValue(Double.class);
		state.assignVariable(varName,Value.createNumber(doubleValue-1));
	}
}
