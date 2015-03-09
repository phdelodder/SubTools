package org.lodder.subtools.sublibrary.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Utils {
	
	public static boolean containsAll(List<Integer> listA, List<Integer> listB) {
		Set<Integer> listAAsSet = new HashSet<Integer>(listA);

		for (Integer integer : listB) {

			if (!listAAsSet.contains(integer)) {
				return false;
			}
		}
		return true;
	}

}
