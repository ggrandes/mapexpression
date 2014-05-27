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
package org.infra.mapexpression;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;

import org.infra.mapexpression.mapper.MapMapper;
import org.infra.mapexpression.mapper.Mapper;
import org.infra.mapexpression.mapper.MultiMapper;
import org.infra.mapexpression.mapper.SystemPropertyMapper;

/**
 * Map Expression using System Properties, HashMap, and others
 * 
 * @link <a href="http://technobcn.wordpress.com/2013/09/30/java-expression-eval-system-property/">Java:
 *       Expression Eval (System Property)</a>
 */
public class MapExpression {
	public final String expression;
	private String mapped;
	private Mapper preMapper = null;
	private Mapper postMapper = null;
	private final ArrayList<Token> tokens = new ArrayList<Token>();
	private final StringBuilder buffer = new StringBuilder();

	/**
	 * Create Map Expression without evaluate and SystemProperties as PostMapper
	 * 
	 * @param expression to map
	 * @see #MapExpression(String, boolean)
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
		parseExpression(expression);
		if (evalInit)
			eval();
	}

	/**
	 * Set post mapper for parameters
	 * 
	 * @param postMapper
	 */
	public void setPostMapper(final Mapper postMapper) {
		this.postMapper = postMapper;
	}

	private final String evalMapToken(final int token) throws InvalidExpression {
		final Token tok = tokens.get(token);
		return tok.isString ? tok.token : mapTokenPost(tok.token);
	}

	/**
	 * Force reevaluate expression (if system property is changed after MapExpression was created)
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
		mapped = buffer.toString();
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
	 * Get previous evaluated expression
	 * 
	 * @return evaluated expression
	 * @see #eval()
	 */
	public String get() {
		return mapped;
	}

	/**
	 * Parse input expression
	 * 
	 * @param expression
	 * @throws InvalidExpression if expression is wrong
	 */
	void parseExpression(final String expression) throws InvalidExpression {
		if (expression == null)
			throw new IllegalArgumentException();
		tokens.clear();
		if (expression.isEmpty())
			return;
		// Find all ${tag}
		final int len = expression.length();
		int last = 0;
		for (int i = 0; i < len; i++) {
			final char cbegin = expression.charAt(i);
			if (cbegin == '$' && ((i + 1) < len)) {
				final char cnext = expression.charAt(++i);
				if (cnext == '{') {
					tokens.add(new Token(expression.substring(last, i - 1), true));
					last = i + 1;
					for (; i < len; i++) {
						final char cend = expression.charAt(i);
						if (cend == '}') {
							tokens.add(new Token(mapTokenPre(expression.substring(last, i)), false));
							last = i + 1;
							break;
						}
					}
					if ((i == len) && (last <= len))
						throw new InvalidExpression("Not well ended expression: "
								+ expression.substring(last - 2, len), len);
				}
			}
		}
		if (last < len)
			tokens.add(new Token(expression.substring(last, len), true));
	}

	/**
	 * Map Token Pre eval (when parseExpression() is called)
	 * 
	 * @param name
	 * @return value or name if not found
	 * @throws InvalidExpression if expression is wrong
	 */
	String mapTokenPre(final String name) throws InvalidExpression {
		if (name.isEmpty())
			throw new InvalidExpression("Invalid name (empty)", 0);
		if (preMapper != null) {
			return preMapper.map(name);
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
	String mapTokenPost(final String name) throws InvalidExpression {
		if (name.isEmpty())
			throw new InvalidExpression("Invalid name (empty)", 0);
		if (postMapper != null) {
			return postMapper.map(name);
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
	}
}
