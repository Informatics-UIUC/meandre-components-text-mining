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

package org.seasr.components.text.datatype.corpora.impl;

//==============
// Java Imports
//==============

import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

//===============
// Other Imports
//===============

import org.seasr.components.text.datatype.corpora.Annotation;
import org.seasr.components.text.datatype.corpora.AnnotationConstants;
import org.seasr.components.text.datatype.corpora.Document;
import org.seasr.components.text.datatype.corpora.FeatureBearer;
import org.seasr.components.text.datatype.corpora.FeatureMap;
import org.seasr.components.text.util.feature_maps.*;
import org.seasr.components.text.util.Factory;

/**
 * Annotation
 * 
 * @author Xiaolei Li
 */
public class AnnotationImpl extends AbstractFeatureBearer implements
		Annotation, FeatureBearer {

	// ==============
	// Data Members
	// ==============

	static final long serialVersionUID = 3L;

	/**
	 * ID of this annotation.
	 */
	private int id;

	// private Integer id;

	/**
	 * Type of this annotation.
	 */
	private String type;

	/**
	 * Start node.
	 */
	private long startOffset = -1;

	// private Node start;

	/**
	 * End node.
	 */
	private long endOffset = -1;

	// private Node end;

	// ==============
	// Constructors
	// ==============
	
	public AnnotationImpl(int id, long start, long end, String type,
			FeatureMap features) {
		this.id = id;
		this.startOffset = start;
		this.endOffset = end;
		this.type = type;
		this.features = features;
	}

	public AnnotationImpl() {
	}

	private void setState(int id, long start, long end, String type,
			FeatureMap features) {
		this.id = id;
		this.startOffset = start;
		this.endOffset = end;
		this.type = type;
		this.features = features;
	}	
	
	public String getContent(Document sofa) {
		if (getFeatures().get(AnnotationConstants.TOKEN_ANNOT_FEAT_INTITLE_BOOL) != null) {
			return sofa.getTitle()
					.substring((int) startOffset, (int) endOffset);
		} else {
			return sofa.getContent().substring((int) startOffset,
					(int) endOffset);
		}
	}

	/**
	 * Return the ID of this annotation.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Return the type of annotation.
	 * 
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * Return the starting node of the annotation.
	 * 
	 * @return
	 */
	public long getStartNodeOffset() {
		return startOffset;
	}

	/**
	 * Return the ending node of the annotation.
	 * 
	 * @return
	 */
	public long getEndNodeOffset() {
		return endOffset;
	}

	/**
	 * Check if this annotation is compatible with another. Compatible means
	 * they're co-extensive and the FeatureMap of <b>this</b> annotation is a
	 * subset of <b>annot</b>'s.
	 * 
	 * @return
	 */
	public boolean isCompatible(Annotation annot) {
		// nothing to compare with
		if (annot == null)
			return false;

		// check for co-extensiveness and feature subsumption
		if (coextensive(annot) && subsumesFeaturesOf(annot))
			return true;

		return false;
	}

	/**
	 * Check if this annotation is compatible with another annotation on the
	 * given set of features.
	 * 
	 * @return
	 */
	public boolean isCompatible(Annotation annot, Set<String> featureNamesSet) {
		// no feature name set is given, so this becomes the regular
		// isCompatible comparison
		if (featureNamesSet == null)
			return isCompatible(annot);

		// nothing to compare with
		if (annot == null)
			return false;

		// check for co-extensiveness and feature subsumption
		if (coextensive(annot) && subsumesFeaturesOf(annot, featureNamesSet))
			return true;

		return false;
	}

	/**
	 * Check if this annotation is partially compatible with another. Partially
	 * compatible means they overlap and the FeatureMap of this annotation is a
	 * subset of annot's FeatureMap.
	 * 
	 * @return
	 */
	public boolean isPartiallyCompatible(Annotation annot) {
		// nothing to compare with
		if (annot == null)
			return false;

		// check for node overlap and feature subsumption
		if (overlaps(annot) && subsumesFeaturesOf(annot))
			return true;

		return false;
	}

	/**
	 * Check if this annotation is partially compatible with another given a set
	 * of features.
	 * 
	 * @return
	 */
	public boolean isPartiallyCompatible(Annotation annot,
			Set<String> featureNamesSet) {
		// no feature name set is given, so this becomes the regular
		// isPartiallyCompatible comparison
		if (featureNamesSet == null)
			return isPartiallyCompatible(annot);

		// nothing to compare with
		if (annot == null)
			return false;

		// check for node overlap and feature subsumption
		if (overlaps(annot) && subsumesFeaturesOf(annot, featureNamesSet))
			return true;

		return false;
	}

	private boolean subsumesFeaturesOf(Annotation annot) {
		// if annot has no features
		if (annot.getFeatures() == null) {

			// if i also have no features, i'm a subset
			if (this.getFeatures() == null)
				return true;

			// if i have some features, then i cannot be a subset
			if (this.getFeatures() != null)
				return false;
		}

		// annot has features, check for subsumption
		if (annot.getFeatures().subsumes(this.getFeatures()))
			return true;

		return false;
	}

	private boolean subsumesFeaturesOf(Annotation annot,
			Set<String> featureNamesSet) {
		if (featureNamesSet == null)
			return subsumesFeaturesOf(annot);

		// if annot has no features
		if (annot.getFeatures() == null) {

			// if i also have no features, i'm a subset
			if (this.getFeatures() == null)
				return true;

			// if i have some features, then i cannot be a subset
			if (this.getFeatures() != null)
				return false;
		}

		// annot has features, check for subsumption
		if (annot.getFeatures().subsumes(this.getFeatures(), featureNamesSet))
			return true;

		return false;
	}

	/**
	 * Two annotations are co-extensive if their offsets are the same. Note that
	 * they are also co-extensive if matching nodes and/or offsets are both
	 * null.
	 * 
	 * @return
	 */
	public boolean coextensive(Annotation annot) {
		// nothing to compare to
		if (annot == null)
			return false;

		// if exactly one of the starting nodes is null, then no way the
		// annotations can be co-extensive
		if ((annot.getStartNodeOffset() == -1)
				^ (this.getStartNodeOffset() == -1))
			return false;

		// both starting nodes are not null
		if (this.getStartNodeOffset() != -1) {

			// if exactly one of the starting nodes' offset is null,
			// then the annotations are not co-extensive
			if ((this.getStartNodeOffset() == -1)
					^ (annot.getStartNodeOffset() == -1))
				return false;

			// both offsets are not null
			// if (this.getStartNode() != null) {
			if (this.getStartNodeOffset() != -1) {

				// if they aren't equal, then the annotations are not
				// co-extensive
				if (!(this.getStartNodeOffset() == annot.getStartNodeOffset()))
					return false;
			}
		}

		// now do the same checks for the ending nodes

		// if exactly one of the ending nodes is null, then no way the
		// annotations can be co-extensive
		if ((annot.getEndNodeOffset() == -1) ^ (this.getEndNodeOffset() == -1))
			return false;

		// both ending nodes are not null
		// if (this.getEndNode() != null) {
		if (this.getEndNodeOffset() != -1) {

			// if exactly one of the ending nodes' offset is null,
			// then the annotations are not co-extensive
			if ((this.getEndNodeOffset() == -1)
					^ (annot.getEndNodeOffset() == -1))
				return false;

			// both offsets are not null
			// if (this.getEndNode() != null) {
			if (this.getEndNodeOffset() != -1) {

				// if they aren't equal, then the annotations are not
				// co-extensive
				if (!(this.getEndNodeOffset() == annot.getEndNodeOffset()))
					return false;
			}
		}

		// gone through all the checks, they must be equal.
		return true;
	}

	/**
	 * Check if this annotation overlaps with another.
	 * 
	 * @return
	 */
	public boolean overlaps(Annotation annot) {
		// nothing to compare to
		if (annot == null)
			return false;

		// if any of the nodes or offsets are null, then the annotations
		// do not overlap
		if (this.getStartNodeOffset() == -1 || this.getEndNodeOffset() == -1
				|| annot.getStartNodeOffset() == -1
				|| this.getEndNodeOffset() == -1)
			return false;

		// nothing is null. now we can compare actual values.

		// annot ends before this one starts, so they don't overlap
		if (annot.getEndNodeOffset() < this.getStartNodeOffset())
			return false;

		// annot starts after this one ends, so they don't overlap
		if (annot.getStartNodeOffset() > this.getEndNodeOffset())
			return false;

		return true;
	}
	
	/**
	 * Decodes a SEASR encoded Annotation which is a string representation
	 * of an Annotation object's state.
	 */
	public Annotation decode(String val) throws FeatureValueEncoderDecoderException {
		
		int id = -1;
		String type = null;
		long bOffset = -1;
		long eOffset = -1;
		FeatureMap map = Factory.newFeatureMap();
		
		int beg = val.indexOf("{");
		if (beg == -1) {
			throw new FeatureValueEncoderDecoderException(
					"String submitted to method 'decode' not properly encoded: "
							+ val);
		}
		int end = val.indexOf("}");
		if (end == -1) {
			throw new FeatureValueEncoderDecoderException(
					"String submitted to method 'decode' not properly encoded: "
							+ val);
		}
		val = val.substring(beg + 1, end);
		StringTokenizer toker = new StringTokenizer(val, ",");
		
		id  = Integer.parseInt(toker.nextToken());
		type  = toker.nextToken();
		bOffset = Long.parseLong(toker.nextToken());
		eOffset = Long.parseLong(toker.nextToken());
		Map<String, String> dmap = FeatureValueEncoderDecoder.decodeToMap(toker.nextToken());
		
		for (String k:dmap.keySet()) {
			String v = dmap.get(k);
			map.put(k,v);
		}
		
		this.setState(id, bOffset, eOffset, type, map);
		
		return this;
	}
	
	
	/**
	 * Endodes this objects state to a SEASR string encoding.
	 */
	public String encode() throws FeatureValueEncoderDecoderException {
		StringBuffer sb = new StringBuffer();

		sb.append("^annot{");
		sb.append(this.id);
		sb.append(",");
		sb.append(this.type);
		sb.append(",");
		sb.append(this.startOffset);
		sb.append(",");
		sb.append(this.endOffset);
		sb.append(",");
		sb.append(FeatureValueEncoderDecoder.encodeMap(this.features));
		sb.append("}");

		return sb.toString();
	}
}
