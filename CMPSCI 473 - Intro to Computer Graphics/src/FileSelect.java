import java.io.*;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

class FileSelect 
{
   public static byte[] bytes;
   public static RandomAccessFile rand_file;
   
   public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {

        @Override
        public void run() {
            JFileChooser fc = new JFileChooser();
            int returnVal = fc.showOpenDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
            	
                File file = fc.getSelectedFile();

                //This is where a real application would open the file.
                System.out.println("Opening: " + file.getName() + ".\n");
                System.out.println("Size in Bytes: " + file.getFreeSpace() + ".\n");
                try{
                    rand_file = new RandomAccessFile(file.getAbsoluteFile(), "rw");
                    System.out.println("Size in Bytes: " + rand_file.length() + ".\n");
                    rand_file.seek(0);
                    bytes = new byte[(int)rand_file.length()];
                    rand_file.readFully(bytes);
                    System.out.println("Output: " + bytes.toString() + ".\n");

                    
                    rand_file.close(); 
                   } catch (IOException e) {//If the file is not found
           			e.printStackTrace();  //Print trace the error
           		}

            } else {
                System.out.println("Open command cancelled by user.\n");
            }
        
        }
    });
   }
}
