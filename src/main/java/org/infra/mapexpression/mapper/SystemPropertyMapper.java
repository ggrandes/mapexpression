package org.infra.mapexpression.mapper;

public class SystemPropertyMapper implements Mapper {
	private static final SystemPropertyMapper singleton = new SystemPropertyMapper();

	private SystemPropertyMapper() {
	}

	@Override
	public String map(final String propName) {
		return System.getProperty(propName);
	}

	public static SystemPropertyMapper getInstance() {
		return singleton;
	}
}
