package datalogger;

public class Student {
	private final String name;
	private final float grade;
	private final String address;
   private int times;
	
	public Student(String n, float g, String a) {
		name = n;
		grade = g;
		address = a;
		times = 0;
	}
	
	public String toString() {
	   times++;
		return name + " " + grade + " " + address;
	}
	
	public int getTimes() {
	   return times;
	}
	
}
