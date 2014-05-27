package org.infra.mapexpression.example;

import java.util.HashMap;

import org.infra.mapexpression.MapExpression;
import org.infra.mapexpression.mapper.MapMapper;
import org.infra.mapexpression.mapper.SystemPropertyMapper;

public class Example2 {
	public static void main(final String[] args) throws Throwable {
		final String TEST_TEXT = "Hi ${user.name}, you are ${state}!!";
		final HashMap<String, String> map = new HashMap<String, String>();
		map.put("state", "lucky");
		final MapExpression m = new MapExpression();
		m.setExpression(TEST_TEXT) //
				.setPreMapper(SystemPropertyMapper.getInstance()) //
				.setPostMapper(new MapMapper(map)) //
				.parseExpression() //
				.eval();
		System.out.println(m.get());
	}
}
