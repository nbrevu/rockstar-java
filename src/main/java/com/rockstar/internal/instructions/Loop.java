package com.rockstar.internal.instructions;

import com.rockstar.Program;
import com.rockstar.internal.Condition;
import com.rockstar.internal.Instruction;

public class Loop implements Instruction {
	private final Condition condition;
	private final boolean reverseConditionResult;
	private final BlockInstruction block;
	
	private Loop(Condition condition,boolean reverseConditionResult,BlockInstruction block)	{
		this.condition=condition;
		this.reverseConditionResult=reverseConditionResult;
		this.block=block;
	}
	
	public static Loop createWhileLoop(Condition condition,BlockInstruction block)	{
		return new Loop(condition,false,block);
	}
	
	public static Loop createUntilLoop(Condition condition,BlockInstruction block)	{
		return new Loop(condition,true,block);
	}
	
	@Override
	public void run(Program state) {
		while (evaluateCondition(state)) try	{
			block.run(state);
		}	catch (Break b)	{
			if (b.shouldExitLoop()) return;
		}
	}
	
	private boolean evaluateCondition(Program state)	{
		boolean condResult=condition.evaluate(state);
		return reverseConditionResult?!condResult:condResult;
	}
}
