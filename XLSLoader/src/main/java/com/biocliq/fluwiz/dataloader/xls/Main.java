package com.biocliq.fluwiz.dataloader.xls;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.zitlab.palmyra.client.PalmyraClient;
import com.zitlab.palmyra.client.pojo.Tuple;

public class Main {

	private static int noRecords = 0;
	private static int errRecords = 0;
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

	public static void main(String[] args) throws Exception {

		Config appConfig = new Config();

		PalmyraClient client = new PalmyraClient(appConfig.getServerURL(), appConfig.getServerUser(),
				appConfig.getServerPassword(), appConfig.getContextPath());

		int startRow = appConfig.getIntProperty("row.start", 4);
		String mappingFile = appConfig.getProperty("xls.mapping");
		String xlsFileName = appConfig.getProperty("xls.fileLocation");
		String ciType = appConfig.getProperty("target.ciType");

		MappingLoader defectMapping = new MappingLoader(mappingFile);
		List<Mapping> mappings = defectMapping.getMappings();

		if (null == xlsFileName) {
			throw new RuntimeException("No filename provided under - xls.fileLocation in application.properties");
		}
		File xlsFolder = new File(xlsFileName);
		if (!xlsFolder.exists()) {
			throw new RuntimeException("file " + xlsFileName + " not found in the system");
		}

		File[] files = xlsFolder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".xlsx") || name.endsWith(".xls");
			}
		});
		for (File file : files) {
			doMigration(client, startRow, ciType, mappings, file);
		}
	}

	private static void doMigration(PalmyraClient client, int startRow, String ciType, List<Mapping> mappings,
			File xlsFile) throws IOException, InvalidFormatException {
		noRecords = 0;
		errRecords = 0;
		String fileName = xlsFile.getName();
		System.out.println("=========================================================");
		System.out.println("Started processing the file " + fileName);
		Workbook workbook;
		try {
			workbook = new XSSFWorkbook(xlsFile);
		} catch (Throwable t) {
			FileInputStream fis = new FileInputStream(xlsFile);
			workbook = new HSSFWorkbook(fis);
		}

		int endRow = -1;

		int currentRow = 0;

		int numCellCheck = mappings.size() / 2;

		DataFormatter formatter = new DataFormatter();

		Sheet sheet = workbook.getSheetAt(0);

		Object value = null;
		Cell cell = null;

		Iterator<Row> rowIterator = sheet.iterator();
		while (rowIterator.hasNext()) {
			currentRow++;
			Tuple tuple = new Tuple();
			tuple.setType(ciType);
			Row row = rowIterator.next();

			if (startRow > currentRow)
				continue;

			if (row.getPhysicalNumberOfCells() < numCellCheck) {
				System.out.println(row.getPhysicalNumberOfCells() + " " + numCellCheck);
				break;
			}

			for (Mapping mapping : mappings) {
				cell = row.getCell(mapping.getColumn());
				if (null != cell) {
					CellType cellType = cell.getCellType();
					switch (cellType) {
					case NUMERIC:
						if (DateUtil.isCellDateFormatted(cell, null)) {
							value = cell.getDateCellValue();
							value = sdf.format(value);
							break;
						} else {
							value = getCellValue(cell, mapping, formatter);
							break;
						}
					case BLANK:
						value = null;
						break;
					default:
						value = getCellValue(cell, mapping, formatter);
						break;
					}
					if (mapping.getDataType() == Mapping.BIT) {
						value = (null == value) ? false
								: (value.toString().toLowerCase().startsWith("y") ? true : false);
					}
					String key = mapping.getProperty();
					tuple.setRefAttribute(key, value);
				}
			}
			saveData(tuple, client);
			if (endRow > 0 && endRow == currentRow)
				break;

		}

		workbook.close();
		System.out.println(noRecords + " records has been synced with the system from " + fileName);
		if(errRecords > 0) {
			System.out.println(errRecords + " has been ignored due to errors");
		}
		System.out.println("=========================================================");
	}

	public static Object getCellValue(Cell cell, Mapping mapping, DataFormatter formatter) {
		Object value;
		int dt = mapping.getDataType();
		Object val = formatter.formatCellValue(cell).trim();
		if(null == val)
			return null;
		try {
			switch (dt) {
			case Mapping.INTEGER:
				value = Integer.parseInt(val.toString());
				break;
			case Mapping.DOUBLE:
				value = Double.parseDouble(val.toString());
				break;
			case Mapping.DATE:{
				if(val instanceof Date) {
					value = sdf.format(val);
				}else
					value = val;
				break;
			}
				 
			default:
				value = val;
				break;
			}
		} catch (NumberFormatException nfe) {
			value = null;
		}
		return value;
	}

	public static void saveData(Tuple tuple, PalmyraClient client) {
		try {
			{
				client.save(tuple);
				noRecords++;
			}
		} catch (Throwable e) {
			System.out.println(tuple.getAttribute("raisedOn"));
			System.out.println("Error while processing record " + tuple.getAttributeAsString("defectId") + " error:"
					+ e.getMessage());
			errRecords++;
		}
	}

}
