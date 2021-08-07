/*******************************************************************************
 * Copyright 2020 BioCliq Technologies
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.zitlab.generic.file;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class FileStorage {
	private String folder;
	private int fileLimit;
	private Processor processor;
	private Predicate<Path> filter = Files::isRegularFile;

	public FileStorage(String folder) {
		this(folder, 4000);
	}
	
	public FileStorage(String folder, int limit) {
		this.folder = folder;
		this.fileLimit = limit;
	}
	
	public FileStorage(String folder, int limit, Processor processor) {
		this(folder, limit);
		this.processor = processor;
	}
	
	public FileStorage(String folder, int limit, Processor processor, Predicate<Path> predicate) {
		this(folder, limit);
		this.processor = processor;
		this.filter = predicate;
	}

	public void process(String key, Path file) {
		processor.process(key, file);
	}

	public void process() {
		try (Stream<Path> paths = Files.walk(Paths.get(folder)).filter(filter).limit(fileLimit)) {
			paths.forEach(filePath -> {
				try {
					String folder = filePath.toAbsolutePath().normalize().toString();
					processor.process(folder, filePath);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		} catch (Throwable e) {
			System.out.println("Error while processing folder " + folder + ", " + e.getMessage());
			e.printStackTrace();
		}
	}

	public Predicate<Path> getFilter() {
		return filter;
	}

	public void setFilter(Predicate<Path> filter) {
		this.filter = filter;
	}

	public Processor getProcessor() {
		return processor;
	}

	public void setProcessor(Processor processor) {
		this.processor = processor;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getFolder() {
		return folder;
	}
}
