package com.zitlab.generic.file;

import java.nio.file.Path;

public interface Processor {
	public boolean process(String key, Path path);
}
