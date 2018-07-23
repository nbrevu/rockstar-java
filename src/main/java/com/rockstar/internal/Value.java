package com.rockstar.internal;

import com.rockstar.RockstarException;

public class Value {
	// For now, only integers are accepted. This is actually not compliant with the language specs.
	public enum Content	{
		NUMBER,STRING,BOOLEAN,NULL_TYPE,MYSTERIOUS;
	}

	public final static Value NULL=new Value(Content.NULL_TYPE,null);
	public final static Value MYSTERIOUS=new Value();
	public final static Value TRUE=new Value(Content.BOOLEAN,Boolean.TRUE);
	public final static Value FALSE=new Value(Content.BOOLEAN,Boolean.FALSE);
	
	private final static double EPS=1e-5;
	
	private Content contentType;
	// No union or anything like that. An Object does what we want.
	private Object value;
	
	public Value()	{
		contentType=null;	// Mysterious.
	}
	
	private Value(Content currentContentType,Object value)	{
		this.contentType=currentContentType;
		this.value=value;
	}
	
	public Content getContentType()	{
		return contentType;
	}
	
	@Override
	public String toString()	{
		switch (contentType)	{
		case NUMBER:
		case STRING:
		case BOOLEAN:
			return value.toString();
		case NULL_TYPE:
			return "null";
		case MYSTERIOUS:
			return "mysterious";
		default:
			return "";
		}
	}
	
	public <T> T getValue(Class<T> theClass)	{
		try	{
			if (contentType==Content.NULL_TYPE) return nullValue(theClass);
			return theClass.cast(value);
		}	catch (ClassCastException exc)	{
			// This needs improvement.
			throw new RockstarException("Wrong type (expected "+theClass.getSimpleName()+", found "+value.getClass().getSimpleName()+").");
		}
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T nullValue(Class<T> theClass)	{
		// I realize that this is atrocious. I'm sorry. It's not like the rest of the code is awesome.
		if (Boolean.class.equals(theClass)) return (T)Boolean.FALSE;
		else if (Double.class.equals(theClass)) return (T)Double.valueOf(0.0);
		else if (String.class.equals(theClass)) return (T)"";
		else return null;
	}
	
	public static Value createString(String string)	{
		return new Value(Content.STRING,string);
	}
	
	public static Value createNumber(double value)	{
		return new Value(Content.NUMBER,Double.valueOf(value));
	}
	
	public boolean areEquals(Value other)	{
		// I don't want to use equals() for this.
		if ((contentType==Content.MYSTERIOUS)||(other.contentType==Content.MYSTERIOUS)) return false;
		else if (contentType==Content.NULL_TYPE) return other.isNull();
		else if (other.contentType==Content.NULL_TYPE) return isNull();
		else if (contentType!=other.contentType) return false;
		else switch (contentType)	{
		case NUMBER:
			return Math.abs(getValue(Double.class)-other.getValue(Double.class))<EPS;
		case STRING:
			return getValue(String.class).equals(other.getValue(String.class));
		case BOOLEAN:
			return getValue(Boolean.class).booleanValue()==other.getValue(Boolean.class).booleanValue();
		case NULL_TYPE:
		case MYSTERIOUS:
		default:
			return false;
		}
	}
	
	private boolean isNull()	{
		switch (contentType)	{
		case NUMBER:
			return Math.abs(getValue(Double.class).doubleValue())<EPS;
		case STRING:
			return getValue(String.class).equals("");
		case BOOLEAN:
			return getValue(Boolean.class).booleanValue()==false;
		case NULL_TYPE:
			return true;
		case MYSTERIOUS:
			return false;
		default:
			return false;
		}
	}
}
