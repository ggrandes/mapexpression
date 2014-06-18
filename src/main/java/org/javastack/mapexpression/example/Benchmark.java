package org.javastack.mapexpression.example;

import java.util.HashMap;

import org.javastack.mapexpression.MapExpression;
import org.javastack.mapexpression.mapper.MapMapper;
import org.javastack.mapexpression.mapper.SystemPropertyMapper;

public class Benchmark {
	public static void main(final String[] args) throws Throwable {
		final int TOTAL = (int) 1e7;
		final String TEST_TEXT = "Hi ${user.name}, you are ${state}!!";
		final HashMap<String, String> map = new HashMap<String, String>();
		map.put("state", "lucky");
		final MapExpression m = new MapExpression() //
				.setExpression(TEST_TEXT) //
				.setPreMapper(SystemPropertyMapper.getInstance()) //
				.setPostMapper(new MapMapper(map)); //
		//
		long begin, diff;
		System.out.println("TestName | Iterations | Time | Iterations/Second");
		// Benchmark ParseOnly
		begin = System.currentTimeMillis();
		for (int i = 0; i < TOTAL; i++) {
			m.parse();
		}
		diff = Math.max(1, (System.currentTimeMillis() - begin));
		System.out.println(printTest("ParseOnly", TOTAL, diff));
		// Benchmark EvalOnly
		begin = System.currentTimeMillis();
		for (int i = 0; i < TOTAL; i++) {
			m.eval().get();
		}
		diff = Math.max(1, (System.currentTimeMillis() - begin));
		System.out.println(printTest("EvalOnly", TOTAL, diff));
		// Benchmark Parse+Eval
		begin = System.currentTimeMillis();
		for (int i = 0; i < TOTAL; i++) {
			m.parse().eval().get();
		}
		diff = Math.max(1, (System.currentTimeMillis() - begin));
		System.out.println(printTest("Parse+Eval", TOTAL, diff));
	}

	private static final String printTest(final String name, final int total, final long diff) {
		return name + " | " + total + " | " + diff + "ms" + " | " + (total / diff * 1000);
	}
}
