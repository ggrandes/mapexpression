package org.infra.mapexpression.example;

import java.util.HashMap;

import org.infra.mapexpression.MapExpression;
import org.infra.mapexpression.mapper.MapMapper;

public class Example3 {
	public static void main(final String[] args) throws Throwable {
		final HashMap<String, String> map = new HashMap<String, String>();
		map.put("user", "john");
		final MapExpression m = new MapExpression();
		//
		m.setExpression("Hi ###user###") //
				.setDelimiters("###", "###") //
				.setPostMapper(new MapMapper(map));
		System.out.println(m.parse().eval().get());
		//
		m.setExpression("{{user}}? {{user}}?") //
				.setDelimiters("{{", "}}") //
				.setPostMapper(new MapMapper(map));
		System.out.println(m.parse().eval().get());
		//
		m.setExpression("Hi |user|, how are you?") //
				.setDelimiters("|", "|") //
				.setPostMapper(new MapMapper(map));
		System.out.println(m.parse().eval().get());
	}
}
