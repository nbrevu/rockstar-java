package com.rockstar.internal.instructions;

import com.rockstar.Program;
import com.rockstar.internal.Instruction;

public enum SpecialBlockInstruction implements Instruction {
	CONTINUE	{
		@Override
		public void run(Program state)	{
			throw new Break(false);
		}
	},BREAK	{
		@Override
		public void run(Program state)	{
			throw new Break(true);
		}
	}
}
