package com.rockstar.internal.instructions;

import com.rockstar.Program;
import com.rockstar.internal.Instruction;
import com.rockstar.internal.Value;

public class Assignment implements Instruction {
	private final String variableName;
	private final String rhs;
	public Assignment(String variableName,String rhs)	{
		this.variableName=variableName;
		this.rhs=rhs;
	}

	@Override
	public void run(Program state) {
		Value result=state.evaluate(rhs);
		state.assignVariable(variableName,result);
	}
}
