/**
 * University of Illinois/NCSA
 * Open Source License
 * 
 * Copyright (c) 2008, Board of Trustees-University of Illinois.  
 * All rights reserved.
 * 
 * Developed by: 
 * 
 * Automated Learning Group
 * National Center for Supercomputing Applications
 * http://www.seasr.org
 * 
 *  
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal with the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions: 
 * 
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimers. 
 * 
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimers in the 
 *    documentation and/or other materials provided with the distribution. 
 * 
 *  * Neither the names of Automated Learning Group, The National Center for
 *    Supercomputing Applications, or University of Illinois, nor the names of
 *    its contributors may be used to endorse or promote products derived from
 *    this Software without specific prior written permission. 
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * CONTRIBUTORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * WITH THE SOFTWARE.
 */

/**
 * @author D. Searsmith
 */
package org.seasr.components.text.util.feature_maps;

// ==============
// Java Imports
// ==============

import java.util.*;

import org.seasr.components.text.datatype.corpora.Annotation;
import org.seasr.components.text.datatype.corpora.impl.AnnotationImpl;

/**
 * This parser relies on a coding system that should be used for coding data
 * structures as Strings.
 * 
 * For sets: ^set{<val_1>, ... , <val_n>} For lists: ^list{<val_1>, ... ,
 * <val_n>} For maps: ^map{<key_1>, <val_1>, ... , <key_n>, <val_n>}
 * 
 * @author dsears
 * 
 * TODO: Unit Test
 * 
 */
public class FeatureValueEncoderDecoder {

	// ==============
	// Data Members
	// ==============

	private static int s_SetEncoding = 0;
	private static int s_ListEncoding = 1;

	// ================
	// Constructor(s)
	// ================

	public FeatureValueEncoderDecoder() {
	}

	// ================
	// Static Methods
	// ================

	static public void main(String[] args) {
		String mapS = "^map{a,1,b,2,c,3}";
		String setS = "^set{a,b,c,go;&commastop;&commago}";
		String listS = "^list{a,b,c}";

		try {

			System.out.println("Decoding: " + setS);
			Set<String> set = decodeToSet(setS);
			for (String s : set) {
				System.out.print(s + " // ");
			}
			System.out.println("\n\n");

			System.out.println("Decoding: " + listS);
			List<String> list = decodeToList(listS);
			for (String s : list) {
				System.out.print(s + " // ");
			}
			System.out.println("\n\n");

			System.out.println("Decoding: " + mapS);
			Map<String, String> map = decodeToMap(mapS);
			for (String s : map.keySet()) {
				System.out.print(s + " , ");
				s = map.get(s);
				System.out.print(s + " // ");
			}
			System.out.println("\n\n");

			System.out.println("Encoding Set: " + "a // b // c // go,stop,go");
			set.clear();
			set.add("a");
			set.add("b");
			set.add("c");
			set.add("go,stop,go");
			String s = encodeSet(set);
			System.out.println(s + "\n\n");

			System.out.println("Encoding List: " + "a // b // c // go,stop,go");
			list.clear();
			list.add("a");
			list.add("b");
			list.add("c");
			list.add("go,stop,go");
			s = encodeList(list);
			System.out.println(s + "\n\n");

			System.out.println("Encoding Map: " + "a,1 // b,2 // c,3");
			map.clear();
			map.put("a", "1");
			map.put("b", "2");
			map.put("c", "3");
			s = encodeMap(map);
			System.out.println(s + "\n\n");

		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	// Decode with strings
	
	static public HashMap<String, String> decodeToMap(String val)
			throws FeatureValueEncoderDecoderException {
		if (!val.startsWith("^map{")) {
			throw new FeatureValueEncoderDecoderException(
					"String submitted to method 'decodeToMap' does not represent an encoded map: "
							+ val);
		}
		HashMap<String, String> ret = new HashMap<String, String>();
		int beg = val.indexOf("{");
		if (beg == -1) {
			throw new FeatureValueEncoderDecoderException(
					"String submitted to method 'decodeToMap' not properly encoded: "
							+ val);
		}
		int end = val.indexOf("}");
		if (end == -1) {
			throw new FeatureValueEncoderDecoderException(
					"String submitted to method 'decodeToMap' not properly encoded: "
							+ val);
		}
		val = val.substring(beg + 1, end);
		StringTokenizer toker = new StringTokenizer(val, ",");
		if ((toker.countTokens() % 2) != 0) {
			throw new FeatureValueEncoderDecoderException(
					"String submitted to method 'decodeToMap' has odd number of elements, "
							+ "must have an even number of elements that make up 'key' and 'value' "
							+ "pairs: " + toker.countTokens());
		}
		while (toker.hasMoreTokens()) {
			String k = deNormalize(toker.nextToken());
			String v = deNormalize(toker.nextToken());
			ret.put(k, v);
		}
		return ret;
	}

	static public ArrayList<String> decodeToList(String val)
			throws FeatureValueEncoderDecoderException {
		if (!val.startsWith("^list{")) {
			throw new FeatureValueEncoderDecoderException(
					"String submitted to method 'decodeToList' does not represent an encoded list: "
							+ val);
		}
		return new ArrayList<String>(decodeToColl(val, s_ListEncoding));
	}

	static public HashSet<String> decodeToSet(String val)
			throws FeatureValueEncoderDecoderException {
		if (!val.startsWith("^set{")) {
			throw new FeatureValueEncoderDecoderException(
					"String submitted to method 'decodeToSet' does not represent an encoded set: "
							+ val);
		}
		return new HashSet<String>(decodeToColl(val, s_SetEncoding));
	}

	// Decode with annotations
	
	static public HashMap<String, Annotation> decodeToMapofStringstoAnnotations(
			String val) throws FeatureValueEncoderDecoderException {
		if (!val.startsWith("^map{")) {
			throw new FeatureValueEncoderDecoderException(
					"String submitted to method 'decodeToMapofStringstoAnnotations' does not represent an encoded map: "
							+ val);
		}
		HashMap<String, Annotation> ret = new HashMap<String, Annotation>();
		int beg = val.indexOf("{");
		if (beg == -1) {
			throw new FeatureValueEncoderDecoderException(
					"String submitted to method 'decodeToMapofStringstoAnnotations' not properly encoded: "
							+ val);
		}
		int end = val.indexOf("}");
		if (end == -1) {
			throw new FeatureValueEncoderDecoderException(
					"String submitted to method 'decodeToMapofStringstoAnnotations' not properly encoded: "
							+ val);
		}
		val = val.substring(beg + 1, end);
		StringTokenizer toker = new StringTokenizer(val, ",");
		if ((toker.countTokens() % 2) != 0) {
			throw new FeatureValueEncoderDecoderException(
					"String submitted to method 'decodeToMapofStringstoAnnotations' has odd number of elements, "
							+ "must have an even number of elements that make up 'key' and 'value' "
							+ "pairs: " + toker.countTokens());
		}
		while (toker.hasMoreTokens()) {
			String k = deNormalize(toker.nextToken());
			Annotation v = decodeToAnnotation(toker.nextToken());
			ret.put(k, v);
		}
		return ret;
	}

	static public ArrayList<Annotation> decodeToListofAnnotations(String val)
			throws FeatureValueEncoderDecoderException {
		if (!val.startsWith("^list{")) {
			throw new FeatureValueEncoderDecoderException(
					"String submitted to method 'decodeToList' does not represent an encoded list: "
							+ val);
		}
		return new ArrayList<Annotation>(decodeToCollofAnnots(val,
				s_ListEncoding));
	}

	static public HashSet<Annotation> decodeToSetofAnnotations(String val)
			throws FeatureValueEncoderDecoderException {
		if (!val.startsWith("^set{")) {
			throw new FeatureValueEncoderDecoderException(
					"String submitted to method 'decodeToSet' does not represent an encoded set: "
							+ val);
		}
		return new HashSet<Annotation>(decodeToCollofAnnots(val, s_SetEncoding));
	}

	// Encode with strings

	/**
	 * For maps: <Java Set Instance> -> ^map{<key_1>, <val_1>, ... , <key_n>,
	 * <val_n>}
	 * 
	 * @param val
	 *            A map of strings to strings to be encoded.
	 * @return A string that represents the information in the map.
	 */
	static public String encodeMap(Map<String, String> val)
			throws FeatureValueEncoderDecoderException {
		StringBuffer ret = new StringBuffer();
		if (val == null) {
			throw new FeatureValueEncoderDecoderException(
					"Null values are not permitted in SEASR feature maps.");
		}
		ret.append("^map{");
		for (String s : val.keySet()) {

			if (s == null) {
				throw new FeatureValueEncoderDecoderException(
						"Null keys are not permitted in SEASR feature maps.");
			}
			ret.append(normalize(s));
			ret.append(",");

			s = val.get(s);
			if (s == null) {
				throw new FeatureValueEncoderDecoderException(
						"Null values are not permitted in SEASR feature maps.");
			}
			ret.append(normalize(s));
			ret.append(",");

		}
		ret.deleteCharAt(ret.length() - 1);
		ret.append("}");
		return ret.toString();
	}

	/**
	 * For lists: <Java List Instance> -> ^list{<val_1>, ... , <val_n>}
	 * 
	 * @param val
	 *            A list of strings to be encoded.
	 * @return A string that represents the information in the list.
	 */
	static public String encodeList(List<String> val)
			throws FeatureValueEncoderDecoderException {
		if (val == null) {
			throw new FeatureValueEncoderDecoderException(
					"Null values are not permitted in SEASR feature maps.");
		}
		return encodeColl(val, "list");
	}

	/**
	 * For sets: <Java Set Instance> -> ^set{<val_1>, ... , <val_n>}
	 * 
	 * @param val
	 *            A set of strings to be encoded.
	 * @return A string that represents the information in the set.
	 */
	static public String encodeSet(Set<String> val)
			throws FeatureValueEncoderDecoderException {
		if (val == null) {
			throw new FeatureValueEncoderDecoderException(
					"Null values are not permitted in SEASR feature maps.");
		}
		return encodeColl(val, "set");
	}

	
	// Encode with Annotations
	
	/**
	 * For maps: <Java Set Instance> -> ^map{<key_1>, <val_1>, ... , <key_n>,
	 * <val_n>}
	 * 
	 * @param val
	 *            A map of strings to strings to be encoded.
	 * @return A string that represents the information in the map.
	 */
	static public String encodeMapofStrongstoAnnotations(Map<String, Annotation> val)
			throws FeatureValueEncoderDecoderException {
		StringBuffer ret = new StringBuffer();
		if (val == null) {
			throw new FeatureValueEncoderDecoderException(
					"Null values are not permitted in SEASR feature maps.");
		}
		ret.append("^map{");
		for (String s : val.keySet()) {

			if (s == null) {
				throw new FeatureValueEncoderDecoderException(
						"Null keys are not permitted in SEASR feature maps.");
			}
			ret.append(normalize(s));
			ret.append(",");

			Annotation a = val.get(s);
			if (s == null) {
				throw new FeatureValueEncoderDecoderException(
						"Null values are not permitted in SEASR feature maps.");
			}
			ret.append(encodeAnnotation(a));
			ret.append(",");

		}
		ret.deleteCharAt(ret.length() - 1);
		ret.append("}");
		return ret.toString();
	}
	
	
	static public String encodeListofAnnotations(List<Annotation> val)
			throws FeatureValueEncoderDecoderException {
		if (val == null) {
			throw new FeatureValueEncoderDecoderException(
					"Null values are not permitted in SEASR feature maps.");
		}
		return encodeCollofAnnots(val, "list");
	}

	static public String encodeSetofAnnotations(Set<Annotation> val)
			throws FeatureValueEncoderDecoderException {
		if (val == null) {
			throw new FeatureValueEncoderDecoderException(
					"Null values are not permitted in SEASR feature maps.");
		}
		return encodeCollofAnnots(val, "set");
	}

	// =================
	// Private Methods
	// =================

	static private String normalize(String s) {
		return s.replaceAll(",", ";&comma");
	}

	static private String deNormalize(String s) {
		return s.replaceAll(";&comma", ",");
	}

	static private String encodeColl(Collection<String> val, String collType)
			throws FeatureValueEncoderDecoderException {
		StringBuffer ret = new StringBuffer();
		ret.append("^");
		ret.append(collType);
		ret.append("{");
		for (String s : val) {
			if (s == null) {
				throw new FeatureValueEncoderDecoderException(
						"Null values are not permitted in SEASR feature sets.");
			}
			ret.append(normalize(s));
			ret.append(",");
		}
		ret.deleteCharAt(ret.length() - 1);
		ret.append("}");
		return ret.toString();
	}

	static private String encodeCollofAnnots(Collection<Annotation> val, String collType)
			throws FeatureValueEncoderDecoderException {
		StringBuffer ret = new StringBuffer();
		ret.append("^");
		ret.append(collType);
		ret.append("{");
		for (Annotation s : val) {
			if (s == null) {
				throw new FeatureValueEncoderDecoderException(
						"Null values are not permitted in SEASR feature sets.");
			}
			ret.append(encodeAnnotation(s));
			ret.append(",");
		}
		ret.deleteCharAt(ret.length() - 1);
		ret.append("}");
		return ret.toString();
	}

	static private Collection<String> decodeToColl(String val, int type)
			throws FeatureValueEncoderDecoderException {
		Collection<String> ret = null;
		if (type == s_SetEncoding) {
			ret = new HashSet<String>();
		} else if (type == s_ListEncoding) {
			ret = new ArrayList<String>();
		} else {
			throw new FeatureValueEncoderDecoderException(
					"Type specified for decoding is unrecognized: " + type);
		}
		int beg = val.indexOf("{");
		if (beg == -1) {
			throw new FeatureValueEncoderDecoderException(
					"String submitted to method 'decodeToColl' not properly encoded: "
							+ val);
		}
		int end = val.indexOf("}");
		if (end == -1) {
			throw new FeatureValueEncoderDecoderException(
					"String submitted to method 'decodeToColl' not properly encoded: "
							+ val);
		}
		val = val.substring(beg + 1, end);
		StringTokenizer toker = new StringTokenizer(val, ",");
		while (toker.hasMoreTokens()) {
			String s = deNormalize(toker.nextToken());
			ret.add(s);
		}
		return ret;
	}

	static private Collection<Annotation> decodeToCollofAnnots(String val,
			int type) throws FeatureValueEncoderDecoderException {
		Collection<Annotation> ret = null;
		if (type == s_SetEncoding) {
			ret = new HashSet<Annotation>();
		} else if (type == s_ListEncoding) {
			ret = new ArrayList<Annotation>();
		} else {
			throw new FeatureValueEncoderDecoderException(
					"Type specified for decoding is unrecognized: " + type);
		}
		int beg = val.indexOf("{");
		if (beg == -1) {
			throw new FeatureValueEncoderDecoderException(
					"String submitted to method 'decodeToCollofAnnots' not properly encoded: "
							+ val);
		}
		int end = val.indexOf("}");
		if (end == -1) {
			throw new FeatureValueEncoderDecoderException(
					"String submitted to method 'decodeToCollofAnnots' not properly encoded: "
							+ val);
		}
		val = val.substring(beg + 1, end);
		StringTokenizer toker = new StringTokenizer(val, ",");
		while (toker.hasMoreTokens()) {
			Annotation s = decodeToAnnotation(toker.nextToken());
			ret.add(s);
		}
		return ret;
	}

	static private Annotation decodeToAnnotation(String s) throws FeatureValueEncoderDecoderException{
		return new AnnotationImpl().decode(s);
	}

	static private String encodeAnnotation(Annotation a) throws FeatureValueEncoderDecoderException{
		return a.encode();
	}

}
