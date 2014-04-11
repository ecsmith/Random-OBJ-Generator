import java.io.*;

class FileCopy
{
   public static void main(String[] args) 
   {
      try 
      {
         File fileIn  = new File("source.txt");
         File fileOut = new File("target.txt");

         FileInputStream streamIn   = new FileInputStream(fileIn);
         FileOutputStream streamOut = new FileOutputStream(fileOut);

         int c;
         while ((c = streamIn.read()) != -1) 
         {
            streamOut.write(c);
         }

         streamIn.close();
         streamOut.close();
      }
      catch (FileNotFoundException e) 
      {
         System.err.println("FileCopy: " + e);
      } 
      catch (IOException e) 
      {
         System.err.println("FileCopy: " + e);
      }
   }
}