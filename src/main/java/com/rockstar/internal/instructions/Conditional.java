package com.rockstar.internal.instructions;

import com.rockstar.Program;
import com.rockstar.internal.Condition;
import com.rockstar.internal.Instruction;

public class Conditional implements Instruction {
	private final Condition condition;
	private final BlockInstruction block;
	
	public Conditional(Condition condition,BlockInstruction block)	{
		this.condition=condition;
		this.block=block;
	}
	
	@Override
	public void run(Program state) {
		if (condition.evaluate(state)) block.run(state);
	}
}
