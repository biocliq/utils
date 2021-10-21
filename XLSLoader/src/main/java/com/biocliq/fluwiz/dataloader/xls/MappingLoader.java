package com.biocliq.fluwiz.dataloader.xls;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MappingLoader {
	
	private String mappingFile; // "defect_mapping.properties"
	private List<Mapping> mappings;
	
	public MappingLoader(String mappingFile) throws IOException{
		this.mappingFile = mappingFile;
		this.loadMapping();
	}
	
	public void loadMapping() throws IOException{
		InputStream inStream = Main.class.getClassLoader().getResourceAsStream(this.mappingFile);
		Properties props = new Properties();
		props.load(inStream);
		mappings = getMapping(props);
	}
	
	public List<Mapping> getMappings(){
		return mappings;
	}
	
	private List<Mapping> getMapping(Properties props) {
		List<Mapping> result = new ArrayList<Mapping>();
		String index = null;
		String dataType = null;
		for (String key : props.stringPropertyNames()) {
			if (key.contains(".")) {
				int pos = key.indexOf(".");
				index = key.substring(0, pos);
				dataType = key.substring(pos + 1, key.length());
			} else {
				index = key;
				dataType = null;
			}
			try {
				int position = Integer.parseInt(index) - 1;
				String value = props.getProperty(key);
				if (null != value && value.length() > 1) {
					Mapping mapping = new Mapping(position, dataType, value);
					result.add(mapping);
				}
			} catch (NumberFormatException nfe) {
				continue;
			}
		}

		return result;
	}
}
