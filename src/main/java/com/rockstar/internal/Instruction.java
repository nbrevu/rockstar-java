package com.rockstar.internal;

import com.rockstar.Program;

public interface Instruction {
	void run(Program state);
}
