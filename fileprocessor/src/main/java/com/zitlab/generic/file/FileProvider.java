package com.zitlab.generic.file;

import java.nio.file.Path;
import java.util.Map.Entry;

interface FileProvider {
	public void onSuccess(String key, Path path);
	
	public void onFailure(String key, Path path);

	public Entry<String, Path> dequeue();
}
