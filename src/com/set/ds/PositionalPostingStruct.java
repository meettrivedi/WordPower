package com.set.ds;

public class PositionalPostingStruct {
	//docId, frequency: <postings...>
	private int docId;
	private int [] positions;
	private int frequency;
	
	public int getDocId() {
		return docId;
	}
	public void setDocId(int docId) {
		this.docId = docId;
	}
	public int[] getPositions() {
		return positions;
	}
	public void setPositions(int[] positions) {
		this.positions = positions;
	}
	public int getFrequency() {
		return frequency;
	}
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	
	
}
