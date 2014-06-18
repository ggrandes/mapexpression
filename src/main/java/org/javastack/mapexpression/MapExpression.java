/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.javastack.mapexpression;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;

import org.javastack.mapexpression.mapper.MapMapper;
import org.javastack.mapexpression.mapper.Mapper;
import org.javastack.mapexpression.mapper.MultiMapper;
import org.javastack.mapexpression.mapper.SystemPropertyMapper;

/**
 * Map Expression using System Properties, HashMap, and others
 * 
 * @link <a href="http://technobcn.wordpress.com/2013/09/30/java-expression-eval-system-property/">Java:
 *       Expression Eval (System Property)</a>
 */
public class MapExpression {
	private String expression;
	private String evaled;
	private Mapper preMapper = null;
	private Mapper postMapper = null;
	private char[] beginToken = "${".toCharArray();
	private char[] endToken = "}".toCharArray();
	private final ArrayList<Token> tokens = new ArrayList<Token>();
	private final StringBuilder buffer = new StringBuilder();

	/**
	 * Create Empty Map Expression, no expression, no mappers
	 */
	public MapExpression() throws InvalidExpression {
		this(null, null, null, false);
	}

	/**
	 * Create Map Expression without evaluate and SystemProperties as PostMapper
	 * 
	 * @param expression to map
	 * 
	 * @see #MapExpression(String, Mapper, Mapper, boolean)
	 */
	public MapExpression(final String expression) throws InvalidExpression {
		this(expression, null, SystemPropertyMapper.getInstance(), false);
	}

	/**
	 * Create Map Expression with SystemProperties and Map as PostMappers
	 * 
	 * @param expression to map
	 * @param postMap map for parameters
	 * @param evalInit
	 * @throws InvalidExpression
	 * 
	 * @see #MapExpression(String, Mapper, Mapper, boolean)
	 */
	public MapExpression(final String expression, final Map<String, String> postMap, final boolean evalInit)
			throws InvalidExpression {
		this(expression, null, new MultiMapper().add(new MapMapper(postMap)).add(
				SystemPropertyMapper.getInstance()), evalInit);
	}

	/**
	 * Create Map Expression
	 * 
	 * @param expression to map
	 * @param preMapper mapper for parameters
	 * @param postMapper mapper for parameters
	 * @param evalInit false to skip evaluation on creation
	 * @throws InvalidExpression
	 * @see #setPostMapper(Mapper)
	 */
	public MapExpression(final String expression, final Mapper preMapper, final Mapper postMapper,
			final boolean evalInit) throws InvalidExpression {
		this.expression = expression;
		this.preMapper = preMapper;
		this.postMapper = postMapper;
		if (expression != null) {
			parse();
			if (evalInit)
				eval();
		}
	}

	/**
	 * Get Current Expression
	 * 
	 * @return expression
	 */
	public String getExpression() {
		return expression;
	}

	/**
	 * Set new Expression
	 * 
	 * @param expression to parse
	 * @return
	 * @see #parse()
	 */
	public MapExpression setExpression(final String expression) {
		this.expression = expression;
		return this;
	}

	/**
	 * Set delimiters for parsing (default are ${ })
	 * 
	 * @param beginToken (default &quot;${&quot;)
	 * @param endToken (default &quot;}&quot;)
	 * @return
	 * @see #parse()
	 */
	public MapExpression setDelimiters(final String beginToken, final String endToken) {
		this.beginToken = beginToken.toCharArray();
		this.endToken = endToken.toCharArray();
		return this;
	}

	/**
	 * Set pre mapper for parameters
	 * 
	 * @param postMapper
	 * @return
	 */
	public MapExpression setPreMapper(final Mapper preMapper) {
		this.preMapper = preMapper;
		return this;
	}

	/**
	 * Set post mapper for parameters
	 * 
	 * @param postMapper
	 * @return
	 */
	public MapExpression setPostMapper(final Mapper postMapper) {
		this.postMapper = postMapper;
		return this;
	}

	private final String evalMapToken(final int token) throws InvalidExpression {
		final Token tok = tokens.get(token);
		return tok.isString ? tok.token : mapTokenPost(tok.token);
	}

	/**
	 * Force reevaluate expression (if system property is changed after MapExpression was created) and store
	 * for {@link #get()}
	 * 
	 * @throws InvalidExpression
	 * @see #get()
	 * @threadSafe false
	 */
	public MapExpression eval() throws InvalidExpression {
		buffer.setLength(0);
		final int len = tokens.size();
		for (int i = 0; i < len; i++) {
			buffer.append(evalMapToken(i));
		}
		evaled = buffer.toString();
		return this;
	}

	/**
	 * Evaluate expression and write to OutputStream using specified Charset
	 * 
	 * @param out
	 * @param charset
	 * @return
	 * @throws InvalidExpression
	 * @throws IOException
	 */
	public MapExpression eval(final OutputStream out, final Charset charset) throws InvalidExpression,
			IOException {
		final int len = tokens.size();
		for (int i = 0; i < len; i++) {
			out.write(evalMapToken(i).getBytes(charset));
		}
		return this;
	}

	/**
	 * Evaluate expression and write to PrintWriter
	 * 
	 * @param out
	 * @return
	 * @throws InvalidExpression
	 */
	public MapExpression eval(final PrintWriter out) throws InvalidExpression {
		final int len = tokens.size();
		for (int i = 0; i < len; i++) {
			out.print(evalMapToken(i));
		}
		return this;
	}

	/**
	 * Evaluate expression and write to PrintStream
	 * 
	 * @param out
	 * @return
	 * @throws InvalidExpression
	 */
	public MapExpression eval(final PrintStream out) throws InvalidExpression {
		final int len = tokens.size();
		for (int i = 0; i < len; i++) {
			out.print(evalMapToken(i));
		}
		return this;
	}

	/**
	 * Evaluate expression and write to StringBuilder
	 * 
	 * @param out
	 * @return
	 * @throws InvalidExpression
	 */
	public MapExpression eval(final StringBuilder out) throws InvalidExpression {
		final int len = tokens.size();
		for (int i = 0; i < len; i++) {
			out.append(evalMapToken(i));
		}
		return this;
	}

	/**
	 * Evaluate expression and invoke OutputCallback
	 * 
	 * @param out
	 * @return
	 * @throws InvalidExpression
	 * 
	 * @see OutputCallback#writeEvaled(String)
	 */
	public MapExpression eval(final OutputCallback out) throws InvalidExpression {
		final int len = tokens.size();
		for (int i = 0; i < len; i++) {
			out.writeEvaled(evalMapToken(i));
		}
		return this;
	}

	/**
	 * Get previous evaluated expression with {@link #eval()}
	 * 
	 * @return evaluated expression
	 * @see #eval()
	 */
	public String get() {
		return evaled;
	}

	/**
	 * Parse expression
	 * 
	 * @return
	 * @throws InvalidExpression if expression is wrong
	 */
	public MapExpression parse() throws InvalidExpression {
		if (expression == null)
			throw new InvalidExpression("Null Expression", 0);
		tokens.clear();
		if (expression.isEmpty())
			return this;
		// Find all ${tag}
		final int len = expression.length();
		final int beginTokenLen = beginToken.length;
		final int endTokenLen = endToken.length;
		boolean tokenBeginOrEnd = true;
		int last = 0, tokenPos = 0;
		for (int i = 0; i < len;) {
			final char c = expression.charAt(i);
			if (tokenBeginOrEnd) {
				if (tokenPos < beginTokenLen) {
					if (c == beginToken[tokenPos]) {
						tokenPos++;
					} else {
						tokenPos = 0;
					}
					i++;
					continue;
				} else {
					tokens.add(new Token(expression.substring(last, i - beginTokenLen), true));
					tokenBeginOrEnd = false;
					last = i;
					tokenPos = 0;
				}
			} else {
				if (tokenPos < endTokenLen) {
					if (c == endToken[tokenPos]) {
						tokenPos++;
					} else {
						tokenPos = 0; // Reset
						tokenBeginOrEnd = false;
					}
					i++;
					continue;
				} else {
					tokens.add(new Token(mapTokenPre(expression.substring(last, i - endTokenLen)), false));
					tokenBeginOrEnd = true;
					last = i;
					tokenPos = 0;
				}
			}
		}
		if (tokenBeginOrEnd) {
			if (tokenPos == beginTokenLen) {
				throw new InvalidExpression("Not well ended expression: " + //
						expression.substring(last, len), len);
			} else {
				tokens.add(new Token(expression.substring(last, len), true));
			}
		} else {
			if (tokenPos == endTokenLen) {
				tokens.add(new Token(mapTokenPre(expression.substring(last, len - endTokenLen)), false));
			} else {
				throw new InvalidExpression("Not well ended expression: " + //
						expression.substring(last, len), len);
			}
		}
		return this;
	}

	/**
	 * Map Token Pre eval (when parseExpression() is called)
	 * 
	 * @param name
	 * @return value or name if not found
	 * @throws InvalidExpression if expression is wrong
	 */
	private final String mapTokenPre(final String name) throws InvalidExpression {
		if (name.isEmpty())
			throw new InvalidExpression("Invalid name (empty)", 0);
		if (preMapper != null) {
			final String value = preMapper.map(name);
			if (value != null)
				return value;
		}
		return name;
	}

	/**
	 * Map Token Post parse (when eval() is called)
	 * 
	 * @param name
	 * @return value or name if not found
	 * @throws InvalidExpression if expression is wrong
	 */
	private final String mapTokenPost(final String name) throws InvalidExpression {
		if (name.isEmpty())
			throw new InvalidExpression("Invalid name (empty)", 0);
		if (postMapper != null) {
			final String value = postMapper.map(name);
			if (value != null)
				return value;
		}
		return name;
	}

	@Override
	public String toString() {
		return super.toString() + " [expression=" + expression + "]";
	}

	private static final class Token {
		public final String token;
		public final boolean isString;

		public Token(final String token, final boolean isToken) {
			this.token = token;
			this.isString = isToken;
		}

		public String toString() {
			return (isString ? "string=<" : "token=<") + token + ">";
		}
	}
}
