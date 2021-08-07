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
import java.util.Map.Entry;

import com.zitlab.generic.util.ThreadUtil;

class ThreadedProcessorRx extends Thread {
	private Processor processor;
	private Object lock;
	private FileProvider fileQueue;

	public ThreadedProcessorRx(String name, FileProvider provider, Object lock) {
		super(name);
		this.fileQueue = provider;
		this.lock = lock;
	}

	public Processor getProcessor() {
		return processor;
	}

	public void setProcessor(Processor processor) {
		this.processor = processor;
	}

	public void run() {
		while (true) {
			try {
				Entry<String, Path> entry = fileQueue.dequeue();
				if (null == entry) {
					synchronized (lock) {
						try {
							System.out.println("Waiting lock " + Thread.currentThread().getName());
							lock.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
							ThreadUtil.sleep(5000L);
						}
						System.out.println("Waiting lock completed" + Thread.currentThread().getName());
					}
					continue;
				}
				Path path = entry.getValue();
				String key = entry.getKey();
				
				if (processor.process(key, path)) {
					fileQueue.onSuccess(key, path);
				} else
					fileQueue.onFailure(key, path);
			} finally {
			}
		}
	}
}
