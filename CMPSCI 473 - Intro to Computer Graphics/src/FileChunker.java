import java.io.IOException;
import java.io.RandomAccessFile;


public class FileChunker {
	
	public long LENGTH;
	public int TYPE;
	//SEPERATES BYTES OF THE FILE FOR DRAWING//
	public FileChunker(RandomAccessFile RAF){
		try {
			LENGTH = RAF.length();
			TYPE = determine_TYPE();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public int determine_TYPE(){
		if((LENGTH % 3) == 0){
			return 1;
		}
		else if(LENGTH % 5 == 0){
			return 2;
		}
		else return 0;
	}
}
