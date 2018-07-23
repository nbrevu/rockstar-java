package com.rockstar.internal;

import com.rockstar.Program;

public enum Comparison {
	EQUAL	{
		@Override
		public boolean compare(Value lhs, Value rhs) {
			return lhs.areEquals(rhs);
		}
	},LOWER	{
		@Override
		public boolean compare(Value lhs, Value rhs) {
			double l=lhs.getValue(Double.class);
			double r=rhs.getValue(Double.class);
			return l<r;
		}
	},HIGHER	{
		@Override
		public boolean compare(Value lhs, Value rhs) {
			double l=lhs.getValue(Double.class);
			double r=rhs.getValue(Double.class);
			return l>r;
		}
	};
	
	public final boolean compare(Program state,String lhs,String rhs)	{
		Value lhsValue=state.evaluate(lhs);
		Value rhsValue=state.evaluate(rhs);
		return compare(lhsValue,rhsValue);
	}
	
	protected abstract boolean compare(Value lhs,Value rhs);
}
