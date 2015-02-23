import java.io.Serializable;
import java.util.ArrayList;


public class DBlock implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String docId;
	Short termFreq;
	ArrayList<Short> positions;
	
	
	public String getDocId() {
		return docId;
	}


	public void setDocId(String docId) {
		this.docId = docId;
	}


	public Short getTermFreq() {
		return termFreq;
	}


	public void setTermFreq(Short termFreq) {
		this.termFreq = termFreq;
	}


	public ArrayList<Short> getPositions() {
		return positions;
	}


	public void setPositions(ArrayList<Short> positions) {
		this.positions = positions;
	}


	@Override
    public String toString() {
        return String.format(docId+" "+termFreq + " " + positions);
    }

}
