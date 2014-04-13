import java.io.*;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

class FileSelect 
{
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
                    RandomAccessFile FILE = new RandomAccessFile(file.getAbsoluteFile(), "rw");
                    System.out.println("Size in Bytes: " + FILE.length() + ".\n");
                    FILE.close(); 
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
