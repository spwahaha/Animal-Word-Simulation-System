package ast;

import java.util.HashMap;
import java.util.Map;

public abstract class NodeImp<E> {
	
	public static <E> Map<String, E> createLookupMap(E[] values){
		Map<String, E> map = new HashMap<String, E>();
		for(E e:values){
			map.put(e.toString(), e);
		}
		
		return map;
		
	}
}
