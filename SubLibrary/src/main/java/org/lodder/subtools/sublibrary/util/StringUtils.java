package org.lodder.subtools.sublibrary.util;

public class StringUtils {

	public static String[] join(String[]... arrays) {
		// calculate size of target array
		int size = 0;
		for (String[] array : arrays) {
			size += array.length;
		}

		// create list of appropriate size
		java.util.List<String> list = new java.util.ArrayList<String>(size);

		// add arrays
		for (String[] array : arrays) {
			list.addAll(java.util.Arrays.asList(array));
		}

		// create and return final array
		return list.toArray(new String[size]);
	}

	public static String RemoveIllegalFilenameChars(String s) {
		s = s.replace("/", "");
		s = s.replace("\0", "");
		return s;
	}
}
