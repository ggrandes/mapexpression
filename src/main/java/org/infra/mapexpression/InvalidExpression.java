package org.infra.mapexpression;

import java.text.ParseException;

public class InvalidExpression extends ParseException {
	private static final long serialVersionUID = 42L;

	public InvalidExpression(final String s, final int errorOffset) {
		super(s, errorOffset);
	}
}
