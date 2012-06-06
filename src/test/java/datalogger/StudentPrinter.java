package datalogger;

import java.util.HashMap;
import java.util.Map;

import datalogger.DoLog;
import datalogger.LogLevel;


public class StudentPrinter {
	private final Map<String, String> students = new HashMap<String, String>();
	
	@DoLog(level = LogLevel.INFO)
	public int printStudent(Object s) {
	   students.put(s.toString(), s.toString());
	   return 1;
	}
	
	public int getNumberOfPrintedstudents() {
	   return students.size();
	}
	
}
