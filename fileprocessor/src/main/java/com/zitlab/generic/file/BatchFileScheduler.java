package com.zitlab.generic.file;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.zitlab.generic.util.ThreadUtil;

public class BatchFileScheduler extends Thread implements FileProvider {
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

	public int getRequeueInterval() {
		return requeueInterval;
	}

	public void setRequeueInterval(int requeueInterval) {
		this.requeueInterval = requeueInterval;
	}

	public BatchFileScheduler(String folder, int threads) {
		this.fileStorage = new FileStorage(folder);
		int folderLength = folder.length()+1;
		Processor processor = new Processor() {
			@Override
			public boolean process(String folder, Path path) {
				String key = folder.substring(folderLength);
				if (inflight.containsKey(key) || currentSet.containsKey(key))
					return true;
				if(currentSet.size() < MaxQueueSize)
					currentSet.put(key, path);
				return true;
			}
		};
		this.fileStorage.setProcessor(processor);
		this.threads = threads;
	}

	@Override
	public void run() {
		for (int i = 0; i < threads; i++) {
			ThreadedProcessor processor = new ThreadedProcessor("ThreadFileProcessor_" + i, this, fileArrived);
			processor.setProcessor(factory.getInstance());
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
				e.printStackTrace();
			}
		}
	}

	public ProcessorFactory getFactory() {
		return factory;
	}

	public void setFactory(ProcessorFactory factory) {
		this.factory = factory;
	}
}
