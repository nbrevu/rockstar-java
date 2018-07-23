package com.rockstar.internal.instructions;

// Yep, I'm using exceptions for flow control.
public class Break extends RuntimeException {
	private static final long serialVersionUID = -4240558310816083370L;
	
	private boolean exitLoop=false;
	
	public Break(boolean exitLoop)	{
		this.exitLoop=exitLoop;
	}
	
	public boolean shouldExitLoop()	{
		return exitLoop;
	}
}
