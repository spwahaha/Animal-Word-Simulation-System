package demoServlet;

import java.util.HashSet;

import simulation.Critter;
import simulation.HexCoord;

public class WorldDiff {
	protected HashSet<HexCoord> changedHex = new HashSet<HexCoord>();
	protected HashSet<Critter> removedCritters = new HashSet<Critter>();
	
	public WorldDiff(HashSet<HexCoord>  hexDiff, HashSet<Critter> critterDiff){
		this.changedHex = copyhex(hexDiff);
		this.removedCritters = copycri(critterDiff);
	}
	
	private HashSet<Critter> copycri(HashSet<Critter> critterDiff) {
		// TODO Auto-generated method stub
		HashSet<Critter> hs =  new HashSet<Critter>();
		for(Critter hex : critterDiff){
			hs.add(hex);
		}
		return hs;
	}

	private HashSet<HexCoord> copyhex(HashSet<HexCoord> hexDiff) {
		// TODO Auto-generated method stub
		HashSet<HexCoord> hs =  new HashSet<HexCoord>();
		for(HexCoord hex : hexDiff){
			hs.add(hex);
		}
		return hs;
	}

	public HashSet<HexCoord> gethexDiff(){
		return this.changedHex;
	}
	
	public HashSet<Critter> getCritterDiff(){
		return this.removedCritters;
	}
}
