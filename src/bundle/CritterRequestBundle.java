package bundle;

import java.util.ArrayList;

import simulation.Critter;
import simulation.HexCoord;

public class CritterRequestBundle {
	public String species_id;
	public String program;
	public int[] mem;
	public ArrayList<PositionBundle> positions;
	public Integer num;
	
	public CritterRequestBundle(Critter cri, ArrayList<HexCoord> positions){
		this.species_id = cri.getName();
		StringBuilder sb = new StringBuilder();
		cri.getRules().prettyPrint(sb);
		this.program = sb.toString();
		mem = new int[8];
		for(int i = 0; i < 8; i++){
			mem[i] = cri.getMem(i);
		}
		if(positions!=null){
			this.positions = new ArrayList<PositionBundle>();
			for(HexCoord hex : positions){
				PositionBundle pb = new PositionBundle(hex.getCol(), hex.getRow());
				this.positions.add(pb);
			}
		}
	}
	
}
