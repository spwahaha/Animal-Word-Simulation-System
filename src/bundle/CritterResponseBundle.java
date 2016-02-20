package bundle;

import java.util.ArrayList;

public class CritterResponseBundle {
	public String species_id;
	public ArrayList<Integer> ids;
	
	public CritterResponseBundle(String name, ArrayList<Integer> list){
		this.species_id = name;
		this.ids = list;
	}
}
