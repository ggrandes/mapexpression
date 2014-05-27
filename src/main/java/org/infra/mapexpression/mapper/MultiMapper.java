package org.infra.mapexpression.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

public class MultiMapper implements Mapper {
	private final List<Mapper> mappers;

	public MultiMapper() {
		this(new ArrayList<Mapper>());
	}

	public MultiMapper(final ArrayList<Mapper> mappers) {
		this.mappers = mappers;
	}

	public MultiMapper(final CopyOnWriteArrayList<Mapper> mappers) {
		this.mappers = mappers;
	}

	public MultiMapper(final Vector<Mapper> mappers) {
		this.mappers = mappers;
	}

	public MultiMapper add(final Mapper m) {
		mappers.add(m);
		return this;
	}

	@Override
	public String map(final String input) {
		final int len = mappers.size();
		for (int i = 0; i < len; i++) {
			final Mapper m = mappers.get(i);
			final String result = m.map(input);
			if (result != null)
				return result;
		}
		return null;
	}
}
