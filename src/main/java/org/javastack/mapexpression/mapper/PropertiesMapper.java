package org.javastack.mapexpression.mapper;

import java.util.Properties;

public class PropertiesMapper implements Mapper {
	private final Properties prop;

	public PropertiesMapper(final Properties prop) {
		this.prop = prop;
	}

	@Override
	public String map(final String input) {
		if (prop != null) {
			final String m = prop.getProperty(input);
			if (m != null)
				return m;
		}
		return null;
	}
}
