package com.rockstar.internal.conditions;

import java.util.Collection;
import java.util.List;

import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.rockstar.Program;
import com.rockstar.internal.Condition;

public class CompositeCondition implements Condition {
	// Only simple conditions of the form (and-all) or (or-all) are allowed for now. Since there are no parentheses, it doesn't get any more complex. 
	private enum ConditionType	{
		AND	{
			@Override
			public boolean isTrue(Collection<Boolean> booleans) {
				return Iterables.all(booleans,Predicates.equalTo(Boolean.TRUE));
			}
		},OR	{
			@Override
			public boolean isTrue(Collection<Boolean> booleans) {
				return Iterables.any(booleans,Predicates.equalTo(Boolean.TRUE));
			}
		};
		public abstract boolean isTrue(Collection<Boolean> booleans);
	}

	private final ConditionType condType;
	private final List<Condition> simpleConditions;
	
	public CompositeCondition(ConditionType condType,List<Condition> simpleConditions)	{
		this.condType=condType;
		this.simpleConditions=simpleConditions;
	}
	
	public static CompositeCondition andCondition(List<Condition> conditions)	{
		return new CompositeCondition(ConditionType.AND,conditions);
	}

	public static CompositeCondition orCondition(List<Condition> conditions)	{
		return new CompositeCondition(ConditionType.OR,conditions);
	}

	@Override
	public boolean evaluate(Program state) {
		Collection<Boolean> conditionals=Collections2.transform(simpleConditions, (Condition cond)->cond.evaluate(state));
		return condType.isTrue(conditionals);
	}
}
