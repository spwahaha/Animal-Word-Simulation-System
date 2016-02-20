package simulation;

public class HexCoord implements Placeable{
	protected int col;
	protected int row;
	
	public HexCoord(){
		
	}
	
	public HexCoord(int col, int row){
		this.col = col;
		this.row = row;
	}
	
	public void setCol(int col){
		this.col = col;
	}
	
	public void setRow(int row){
		this.row = row;
	}
	
	public int getCol(){
		return this.col;
	}
	
	public int getRow(){
		return this.row;
	}
	
	public boolean equals(Object hex){
		if(!(hex instanceof HexCoord)) return false;
		return this.col == ((HexCoord)hex).col && this.row==((HexCoord)hex).row;
	}
	
	public int hashCode(){
		return this.col*this.row;
		
	}
	
	public String toString(){
		return "c" + this.col + "r" + this.row;
	}

	
	
}
