package com.rockstar.internal.instructions;

import com.rockstar.Program;
import com.rockstar.internal.Instruction;
import com.rockstar.internal.Value;

public class Input implements Instruction {
	private final String rhs;
	
	public Input(String rhs)	{
		this.rhs=rhs;
	}

	@Override
	public void run(Program state) {
		String value=System.console().readLine();
		state.assignVariable(rhs,Value.createString(value));
	}
}
