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

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zitlab.generic.util.ThreadUtil;

public class MultiThreadedProcessor extends Thread implements FileProvider {
	private static final Logger logger = LoggerFactory.getLogger(MultiThreadedProcessor.class);
	private ProcessorFactory factory;
	private int MaxQueueSize = 4000;

	// Set of files yet to be processed.
	private LinkedHashMap<String, Path> currentSet = new LinkedHashMap<String, Path>();
	// Set of files being processed currently

	private HashMap<String, Path> inflight = new HashMap<String, Path>();
	private FileStorage fileStorage;
	private int threads;
	private Object lock = new Object();
	private Object fileArrived = new Object();

	// Requeue Interval in seconds
	private int requeueInterval = 60;

	public MultiThreadedProcessor(String folder, int threads, ProcessorFactory factory) {
		this.factory = factory;
		this.threads = threads;

		this.fileStorage = new FileStorage(folder);
		int folderLength = folder.length() + 1;
		
		this.fileStorage.setProcessor(new Processor() {
			@Override
			public boolean process(String folder, Path path) {
				String key = folder.substring(folderLength);
				if (inflight.containsKey(key) || currentSet.containsKey(key))
					return true;
				if (currentSet.size() < MaxQueueSize)
					currentSet.put(key, path);
				return true;
			}
		});

	}

	@Override
	public void run() {
		for (int i = 0; i < threads; i++) {
			ThreadedProcessor processor = new ThreadedProcessor("ThreadFileProcessor_" + i, factory.getInstance());
			processor.start();
		}

		while (true) {
			requeue();
			ThreadUtil.sleep(requeueInterval * 1000);
		}
	}

	public void onSuccess(String key, Path path) {
		synchronized (lock) {
			inflight.remove(key);
			currentSet.remove(key);
		}
	}

	public void onFailure(String key, Path path) {
		synchronized (lock) {
			inflight.remove(key);
			currentSet.remove(key);
		}
	}

	public Entry<String, Path> dequeue() {
		synchronized (lock) {
			Iterator<Entry<String, Path>> it = currentSet.entrySet().iterator();
			if (it.hasNext()) {
				Entry<String, Path> entry = it.next();
				String key = entry.getKey();
				Path value = entry.getValue();
				inflight.put(key, value);
				currentSet.remove(key);
				return entry;
			}
		}
		return null;
	}

	public void requeue() {
		synchronized (lock) {
			this.fileStorage.process();
			try {
				if (currentSet.size() > 0) {
					synchronized (fileArrived) {
						this.fileArrived.notifyAll();
					}
				}
			} catch (Throwable e) {
				logger.error("Error while requeueing", e);
			}
		}
	}

	private class ThreadedProcessor extends Thread {
		private Processor processor;

		public ThreadedProcessor(String name, Processor processor) {
			super(name);
			this.processor = processor;
		}

		public void run() {
			while (true) {
				try {
					Entry<String, Path> entry = MultiThreadedProcessor.this.dequeue();
					if (null == entry) {
						synchronized (fileArrived) {
							try {
								fileArrived.wait();
							} catch (InterruptedException e) {
								e.printStackTrace();
								ThreadUtil.sleep(5000L);
							}
						}
						continue;
					}
					Path path = entry.getValue();
					String key = entry.getKey();

					if (processor.process(key, path)) {
						MultiThreadedProcessor.this.onSuccess(key, path);
					} else
						MultiThreadedProcessor.this.onFailure(key, path);
				} finally {
				}
			}
		}
	}

	public int getRequeueInterval() {
		return requeueInterval;
	}

	public void setRequeueInterval(int requeueInterval) {
		this.requeueInterval = requeueInterval;
	}
}
