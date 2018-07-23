package com.rockstar.internal.instructions;

import java.util.List;

import com.rockstar.Program;
import com.rockstar.internal.Instruction;

public class BlockInstruction implements Instruction {
	public List<Instruction> instructions;
	
	public BlockInstruction(List<Instruction> instructions)	{
		this.instructions=instructions;
	}

	@Override
	public void run(Program state) {
		for (Instruction instr:instructions) instr.run(state);
	}
}
