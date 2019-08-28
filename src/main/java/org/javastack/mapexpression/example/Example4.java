package org.javastack.mapexpression.example;

import java.util.Collections;

import org.javastack.mapexpression.MapExpression;
import org.javastack.mapexpression.mapper.MapMapper;

public class Example4 {
	public static void main(final String[] args) throws Throwable {
		final String NL = System.getProperty("line.separator");
		final String TEST_TEXT = "Hi ${name}, ${q}?" + NL;
		final MapExpression m = new MapExpression();
		// Parse only once time
		m.setExpression(TEST_TEXT) //
				.setPreMapper(new MapMapper(Collections.singletonMap("name", "user"))) //
				.parse();
		// Eval many times
		m.eval(System.out, new MapMapper(Collections.singletonMap("q", "how many words are there")));
		m.eval(System.out, new MapMapper(Collections.singletonMap("q", "can you pass a basic unit test")));
	}
}
