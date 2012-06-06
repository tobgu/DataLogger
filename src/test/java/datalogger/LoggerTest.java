package datalogger;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class LoggerTest
{
   @Test
   public void annotatedMethodShouldReturnBla() {
      printStudents();
   }
   
   private static void printStudents() {
      StudentPrinter printer = new StudentPrinter();
      List<Student> sList = new ArrayList<Student>();
      Student s1 = new Student("Alex", 3.0f, "Down town");
      sList.add(s1);
      Student s2 = new Student("Hanna", 5.0f, "Up town");
      sList.add(s2);

      int sum = 0;
      long startTime = System.currentTimeMillis();
      for(int i=0; i<2; i++) {
         for(Student s : sList) {
            sum += printer.printStudent(s);        
         }
      }
      
      System.out.println("Number of students printed: " + 
            sum + 
            "\nIn " + ((System.currentTimeMillis() - startTime) + " ms"));
      System.out.println("s1=" + s1.getTimes() + ", s2=" + s2.getTimes());
   }
}
