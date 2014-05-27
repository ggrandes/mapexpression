# mapexpression

MapExpression is an Expression Evaluator for Java. Open Source Java project under Apache License v2.0

### Current Stable Version is [1.0.0](https://maven-release.s3.amazonaws.com/release/org/infra/mapexpression/1.0.0/mapexpression-1.0.0.jar)

---

## DOC

#### Usage Example

```java
import java.util.HashMap;
import org.infra.mapexpression.MapExpression;
import org.infra.mapexpression.mapper.MapMapper;
import org.infra.mapexpression.mapper.SystemPropertyMapper;

public class Example {
	public static void main(final String[] args) throws Throwable {
		final String TEST_TEXT = "Hi ${user.name}, you are ${state}!!";
		final HashMap<String, String> map = new HashMap<String, String>();
		map.put("state", "lucky");
		MapExpression m;
		// Shot
		m = new MapExpression(TEST_TEXT, map, true);
		System.out.println(m.get());
		// Fluent
		m = new MapExpression();
		m.setExpression(TEST_TEXT) //
				.setPreMapper(SystemPropertyMapper.getInstance()) //
				.setPostMapper(new MapMapper(map)) //
				.parseExpression() //
				.eval();
		System.out.println(m.get());
	}
}
```

* More examples in [Example package](https://github.com/ggrandes/mapexpression/tree/master/src/main/java/org/infra/mapexpression/example/)

---

## MAVEN

Add the Figaro maven repository location to your pom.xml: 

    <repositories>
        <repository>
            <id>ggrandes-maven-s3-repo</id>
            <url>https://maven-release.s3.amazonaws.com/release/</url>
        </repository>
    </repositories>

Add the Figaro dependency to your pom.xml:

    <dependency>
        <groupId>org.infra</groupId>
        <artifactId>mapexpression</artifactId>
        <version>1.0.0</version>
    </dependency>

---
Inspired in [Java Expression Language](http://docs.oracle.com/javaee/1.4/tutorial/doc/JSPIntro7.html) and [Spring-Placeholders](http://docs.spring.io/spring/docs/4.0.4.RELEASE/javadoc-api/org/springframework/beans/factory/config/PlaceholderConfigurerSupport.html), this code is Java-minimalistic version.
