package com.rockstar.internal.instructions;

import com.rockstar.Program;
import com.rockstar.internal.Instruction;
import com.rockstar.internal.Value;

public class Output implements Instruction {
	private final String rhs;
	
	public Output(String rhs)	{
		this.rhs=rhs;
	}

	@Override
	public void run(Program state) {
		Value output=state.evaluate(rhs);
		System.out.println(output.toString());
	}
}
