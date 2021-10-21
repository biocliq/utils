package com.biocliq.fluwiz.dataloader.xls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class TestMain1 {

	public static void main(String[] args) {
		String S = "city,temp2,temp\nParis,7,0\nDubai,4,-234\nPorto,-1,234234";
		String c = "temp";
		System.out.println(solution(S, c));
	}

	public static int solution(String S, String C) {
		HashMap<String, ArrayList<String>> data = parseInput(S);
		String[] headers = C.split(",");
		int result = 0;
		for (String header : headers) {
			result = findMax(data.get(header));
		}
		
		return result;
	}

	public static int findMax(ArrayList<String> data) {
		if (null == data)
			return Integer.MIN_VALUE;
		int result = Integer.MIN_VALUE;
		for (String item : data) {
			try {
				int i = Integer.parseInt(item);
				if (i > result)
					result = i;
			} catch (Throwable t) {

			}
		}
		return result;
	}

	public static HashMap<String, ArrayList<String>> parseInput(String S) {
		Scanner scanner = new Scanner(S);
		boolean dataRow = false;
		String[] headers = null;
		HashMap<String, ArrayList<String>> data = new HashMap();
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (dataRow) {
				String[] items = line.split(",");
				int i = 0;
				for (String header : headers) {
					ArrayList<String> list = data.get(header);
					list.add(items[i++]);
				}
			} else {
				headers = line.split(",");
				for (String header : headers) {
					data.put(header, new ArrayList<String>());
				}
				dataRow = true;
			}
		}
		scanner.close();
		return data;
	}

}
