package org.javastack.mapexpression.example;

import java.util.HashMap;

import org.javastack.mapexpression.MapExpression;

public class Example1 {
	public static void main(final String[] args) throws Throwable {
		final String TEST_TEXT = "Hi ${user.name}, you are ${state}!!";
		final HashMap<String, String> map = new HashMap<String, String>();
		map.put("state", "lucky");
		final MapExpression m = new MapExpression(TEST_TEXT, map, true);
		System.out.println(m.get());
	}
}
