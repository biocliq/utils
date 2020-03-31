package com.zitlab.generic.file;

import java.nio.file.Path;
import java.util.Map.Entry;

import com.zitlab.generic.util.ThreadUtil;

public class ThreadedProcessor extends Thread {
	private Processor processor;
	private Object lock;
	private FileProvider fileQueue;

	public ThreadedProcessor(String name, FileProvider provider, Object lock) {
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
