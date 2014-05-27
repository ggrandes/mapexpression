package org.infra.mapexpression.mapper;

import java.util.Map;

public class MapMapper implements Mapper {
	private final Map<String, String> map;

	public MapMapper(final Map<String, String> map) {
		this.map = map;
	}

	@Override
	public String map(final String input) {
		if (map != null) {
			final String m = map.get(input);
			if (m != null)
				return m;
		}
		return null;
	}
}
