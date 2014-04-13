import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;


public class FileChunker {
	
	public ArrayList<Byte> bytes_vertices = new ArrayList<Byte>();
	public ArrayList<Byte> bytes_faces = new ArrayList<Byte>();
	public ArrayList<Byte> bytes_normals = new ArrayList<Byte>();
	public long LENGTH;
	public int TYPE;
	
	//SEPERATES BYTES OF THE FILE FOR DRAWING//
	public FileChunker(RandomAccessFile RAF){
		try {
			LENGTH = RAF.length();
			TYPE = determine_TYPE();
			populate();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void populate() {
		
		
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
