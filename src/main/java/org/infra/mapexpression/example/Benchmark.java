package org.infra.mapexpression.example;

import java.util.HashMap;

import org.infra.mapexpression.MapExpression;
import org.infra.mapexpression.mapper.MapMapper;
import org.infra.mapexpression.mapper.SystemPropertyMapper;

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
		System.out.println("TOTAL=" + TOTAL);
		// Benchmark Parse+Eval
		begin = System.currentTimeMillis();
		for (int i = 0; i < TOTAL; i++) {
			m.parse().eval().get();
		}
		diff = Math.max(1, (System.currentTimeMillis() - begin));
		System.out.println("Parse+Eval=" + diff + "ms" + " evals/ms=" + (TOTAL / diff));
		// Benchmark EvalOnly
		begin = System.currentTimeMillis();
		for (int i = 0; i < TOTAL; i++) {
			m.eval().get();
		}
		diff = Math.max(1, (System.currentTimeMillis() - begin));
		System.out.println("EvalOnly=" + diff + "ms" + " evals/ms=" + (TOTAL / diff));
	}
}
