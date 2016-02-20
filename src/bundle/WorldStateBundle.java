package bundle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import demoServlet.WorldDiff;
import simulation.Critter;
import simulation.Food;
import simulation.HexCoord;
import simulation.Placeable;
import simulation.Rock;

/**
 * world state bundle without update_since bundle
 * @author pang
 *
 */
public class WorldStateBundle {
	public int current_timestep;
	public int current_version_number;
//	int update_since;
	public float rate;
	public String name;
	public int population;
	public int rows;
	public int cols;
	public ArrayList<Integer> dead_critters;
	public ArrayList<ObjBundle> state;
	
	public WorldStateBundle(){
		state = new ArrayList<ObjBundle>();
		dead_critters = new ArrayList<Integer>();
	}
	
	public void setCurrent_timeStep(int n){
		this.current_timestep = n;
	}
	
	public void setCurrent_version(int n){
		this.current_version_number = n;
	}
	
	public void setRate(float rate2){
		this.rate = rate2;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setPopulation(int n){
		this.population = n;
	}
	
	public void setRow(int n){
		this.rows = n;
	}
	
	public void setCol(int n){
		this.cols = n;
	}
	
	public void addDeadCritters(int n){
		this.dead_critters.add(n);
	}
	
	public void addState(ObjBundle obj){
		this.state.add(obj);
	}
	
	/**
	 * set the bundle according to api
	 * @param updateSince update since parameter
	 * @param current_version current version of the world
	 * @param versionList version list of the world
	 * @param worldMap the world hexcoord object map
	 * @param id id of the user(session_id)
	 * @param critterToUser records the critter user relationship
	 */
	public void setBundleState(int updateSince, int current_version, ArrayList<WorldDiff> versionList,
			Hashtable<HexCoord, Placeable> worldMap, int id, Hashtable<Integer, Integer> critterToUser) {
		// TODO Auto-generated method stub
		// first merge the version from updateSince to current_version
		// get bundle by info from worldmap, 
		// during getting bundle, set critter according to critter user relationship
		
		// merge all changes
		HashSet<HexCoord> hexChanges = new HashSet<HexCoord>();
		ArrayList<Integer> dieCritters = new ArrayList<Integer>();
		for(int i = updateSince; i < versionList.size(); i++){
			HashSet<HexCoord> hexDiff = versionList.get(i).gethexDiff();
			HashSet<Critter> CriDif = versionList.get(i).getCritterDiff();
			for(HexCoord hc:hexDiff){
				hexChanges.add(hc);
			}
			for(Critter cri:CriDif){
				dieCritters.add(cri.getId());
			}
			
		}
		// set dead critters
		this.dead_critters = dieCritters;
		for(HexCoord hex:hexChanges){
			Placeable obj = worldMap.get(hex);
			if(obj==null){
				//nothing here
				ObjBundle bd = new ObjBundle(hex.getCol(), hex.getRow(),"nothing");
				state.add(bd);
			}else if(obj instanceof Rock){
				//rock here
				ObjBundle bd = new ObjBundle(hex.getCol(), hex.getRow(),"rock");
				state.add(bd);
			}else if(obj instanceof Food){
				//food here
				ObjBundle bd = new ObjBundle(hex.getCol(), hex.getRow(),
						"food");
				bd.value = ((Food)obj).getFoodValue();
				state.add(bd);
			}else if(obj instanceof Critter){
				//critter here
				Critter cri = (Critter)obj;
				if(critterToUser.get(cri.getId()).equals(id)){
					// critter created by this id
					ObjBundle bd = new ObjBundle(hex.getCol(), hex.getRow(),"critter");
					setCritterBundle(bd,cri);
					bd.setExecutedRule(cri.getLastRuleIndex());
					StringBuilder sb = new StringBuilder();
					cri.getRules().prettyPrint(sb);
					bd.setProgram(sb.toString());
					state.add(bd);
				}else{
					ObjBundle bd = new ObjBundle(hex.getCol(), hex.getRow(),"critter");
					setCritterBundle(bd,cri);
					state.add(bd);
				}
				
			}
		}

		
	}
	
	private void setCritterBundle(ObjBundle bd, Critter cri){
		bd.setId(cri.getId());
		bd.setSpecies(cri.getName());
		int[] mem = new int[8];
		for(int i = 0; i < 8; i++){
			mem[i] = cri.getMem(i);
		}
		bd.setMem(mem);
		bd.setDir(cri.getDirection());
	}
}	
