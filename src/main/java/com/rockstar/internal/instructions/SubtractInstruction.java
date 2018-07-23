package com.rockstar.internal.instructions;

import com.rockstar.Program;
import com.rockstar.internal.Instruction;
import com.rockstar.internal.Value;

public class SubtractInstruction implements Instruction {
	private final String variableName;
	private final String rhs;
	
	public SubtractInstruction(String variableName,String rhs)	{
		this.variableName=variableName;
		this.rhs=rhs;
	}

	@Override
	public void run(Program state) {
		double subtrahend=state.evaluate(rhs).getValue(Double.class);
		double minuend=state.getVariable(variableName).getValue(Double.class);
		state.assignVariable(variableName,Value.createNumber(minuend-subtrahend));
	}
}
