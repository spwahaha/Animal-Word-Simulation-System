package simulation;

public class Food implements Placeable{
	protected int value;
	public Food(){
		this.value = 0;
	}
	
	public Food(int value){
		this.value = value;
	}
	
	public int getFoodValue(){
		return this.value;
	}
	
	public void setFoodValue(int value){
		this.value = value;
	}
}
