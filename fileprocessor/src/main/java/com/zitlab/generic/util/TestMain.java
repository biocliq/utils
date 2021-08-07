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
package com.zitlab.generic.util;

import java.nio.file.Path;

import com.zitlab.generic.file.MultiThreadedProcessor;
import com.zitlab.generic.file.Processor;
import com.zitlab.generic.file.ProcessorFactory;

public class TestMain {
	public static void main(String args[]) {
		ProcessorFactory factory = new ProcessorFactory() {			
			@Override
			public Processor getInstance() {
				return new Processor() {
					@Override
					public boolean process(String key, Path path) {
						System.out.println(Thread.currentThread().getName() + " " + key + " " + path.getFileName() +"  " + path.toAbsolutePath().toString());
						//ThreadUtil.sleep(2000);
						return true;
					}
				};
			}
		};
		
		MultiThreadedProcessor sched = new MultiThreadedProcessor(
				"D:\\raja\\palmyra\\server\\palmyra_server\\appdata\\uiweb\\pg", 4, factory);
		
		sched.start();
	}
}
