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

import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Comparator;
import java.util.Collections;
import java.util.HashSet;
import java.util.AbstractSet;

//===============
// Other Imports
//===============

import org.seasr.components.text.datatype.corpora.Annotation;
import org.seasr.components.text.datatype.corpora.AnnotationSet;
import org.seasr.components.text.datatype.corpora.Document;
import org.seasr.components.text.datatype.corpora.FeatureMap;

/**
 * Annotation Set.
 * 
 * @author Xiaolei Li
 * @author D. Searsmith
 */
public class AnnotationSetImpl extends AbstractSet<Annotation> implements
		AnnotationSet {

	// ==============
	// Data Members
	// ==============

	static final long serialVersionUID = 3L;

	/**
	 * The name of this annotation set.
	 */
	private String _name;

	/**
	 * The document this annotation set belongs to.
	 */
	private DocumentImpl _doc;

	/**
	 * Index of annotations in this set by their IDs.
	 */
	private HashMap<Integer, Annotation> _annotsById;

	/**
	 * HashMap of starting node offsets to sets of annotations.
	 */
	private HashMap<Long, ArrayList<Annotation>> _annotsByStartOffset;

	/**
	 * HashMap of annotation types to sets of annotations.
	 */
	private HashMap<String, ArrayList<Annotation>> _annotsByType;

	// ==============
	// Constructors
	// ==============

	/**
	 * Construction given a document.
	 */
	public AnnotationSetImpl(Document doc) {
		_doc = (DocumentImpl) doc;
		_annotsById = new HashMap<Integer, Annotation>();
		_annotsByStartOffset = new HashMap<Long, ArrayList<Annotation>>();
		_annotsByType = new HashMap<String, ArrayList<Annotation>>();
	}

	/**
	 * Construction given a document and a name.
	 */
	public AnnotationSetImpl(Document doc, String name) {
		this(doc);
		_name = name;
	}

	// ================
	// Public Methods
	// ================

	/**
	 * Add a new annotation with existing nodes to this set. Return its ID.
	 */
	public int add(long start, long end, String type, FeatureMap features)
			throws Exception {
		// check the offsets are valid
		if (!_doc.isValidOffsets(start, end)) {
			System.err.println("ERROR: AnnotationSetImpl.add(" + start + ", "
					+ end + ")");
			return -1;
		}

		// the document remembers all annotations associated with it and
		// it will return the next Id in line.
		int id = _doc.getNextAnnotationId();

		// construct new annotation
		AnnotationImpl a = new AnnotationImpl(id, start, end, type, features);

		// add it to this set
		add(a);

		return id;
	}

	/**
	 * Add a new annotation with an existing ID.
	 */
	// public void add(Integer ID, Long start, Long end, String type,
	public void add(int ID, long start, long end, String type,
			FeatureMap features) throws Exception {
		if (!_doc.isValidOffsets(start, end))
			throw new Exception();

		// DC changed this

		// construct new annotation
		AnnotationImpl a = new AnnotationImpl(ID, start, end, type, features);

		// add it to this set
		add(a);
	}

	/**
	 * Add an existing annotation.
	 * 
	 * @return true if insertion is successful.
	 */
	public boolean add(Annotation a) {

		// store the annotation into the hash map
		_annotsById.put(new Integer(a.getId()), a);

		// store the hash from this annotation's starting offset
		long start = a.getStartNodeOffset();

		if (_annotsByStartOffset.containsKey(start)) {
			ArrayList<Annotation> list = _annotsByStartOffset.get(start);
			list.add(a);
		} else {
			ArrayList<Annotation> list = new ArrayList<Annotation>();
			list.add(a);
			_annotsByStartOffset.put(start, list);
		}

		// store the annotation by its type
		if (_annotsByType.containsKey(a.getType())) {
			ArrayList<Annotation> list = (ArrayList<Annotation>) _annotsByType
					.get(a.getType());
			list.add(a);
		} else {
			ArrayList<Annotation> list = new ArrayList<Annotation>();
			list.add(a);
			_annotsByType.put(a.getType(), list);
		}

		return true;
	}

	/**
	 * Get an iterator for this set.
	 */
	public Iterator<Annotation> iterator() {
		return new AnnotationSetIterator();
	}

	/**
	 * Return the size of this set.
	 */
	public int size() {
		return _annotsById.size();
	}

	/**
	 * Remove an element from this set.
	 * 
	 * @return true if removal is successful.
	 */
	public boolean remove(Object o) {
		Annotation a = (Annotation) o;

		if (_annotsById.remove(new Integer(a.getId())) == null)
			return false;

		return true;
	}

	/**
	 * Get an annotation by ID.
	 */
	public Annotation get(int id) {
		return _annotsById.get(new Integer(id));
	}

	/**
	 * Return all annotations in this set.
	 */
	public AnnotationSet get() {
		// i don't have anything, return null
		if (size() == 0)
			return null;

		AnnotationSetImpl aset = new AnnotationSetImpl(_doc);

		Iterator<Annotation> myIter = _annotsById.values().iterator();

		// add all my annotations to a new set
		while (myIter.hasNext()) {
			Annotation a = (Annotation) myIter.next();
			aset.add(a);
		}

		return aset;
	}

	/**
	 * Return all annotations with this given type.
	 */
	public AnnotationSet get(String type) {
		// i don't have anything, return null
		if (size() == 0)
			return null;

		if (_annotsByType.containsKey(type)) {

			ArrayList<Annotation> list = _annotsByType.get(type);

			if (list.size() > 0) {

				AnnotationSetImpl aset = new AnnotationSetImpl(_doc);

				for (int i = 0; i < list.size(); i++) {
					aset.add(list.get(i));
				}

				return aset;
			}
		}

		/*
		 * check all my annotations, and add the ones with the right // type
		 */

		return null;
	}

	/**
	 * Return all annotations with all types in this given set.
	 */
	public AnnotationSet get(Set<String> types) {
		// i don't have anything, return null
		if (size() == 0)
			return null;

		AnnotationSetImpl aset = new AnnotationSetImpl(_doc);

		Iterator<Annotation> myIter = _annotsById.values().iterator();

		// check all my annotations, and add the ones with the right
		// type to the new set
		while (myIter.hasNext()) {
			Annotation a = myIter.next();

			if (types.contains(a.getType()))
				aset.add(a);
		}

		if (aset.size() > 0)
			return aset;

		return null;
	}

	/**
	 * Return all annotations with a given type and features.
	 */
	public AnnotationSet get(String type, FeatureMap features) {
		// i don't have anything, return null
		if (size() == 0)
			return null;

		AnnotationSetImpl aset = new AnnotationSetImpl(_doc);

		Iterator<Annotation> myIter = _annotsById.values().iterator();

		// check all my annotations, and add the ones with the right
		// type and the given features to the new set
		while (myIter.hasNext()) {
			Annotation a = myIter.next();

			if (a.getType().equals(type)
					&& a.getFeatures().entrySet().containsAll(
							features.entrySet()))
				aset.add(a);
		}

		if (aset.size() > 0)
			return aset;

		return null;
	}

	/**
	 * Return all annotations with a given type and set of features.
	 */
	public AnnotationSet get(String type, Set<String> featureNames) {
		// i don't have anything, return null
		if (size() == 0)
			return null;

		AnnotationSetImpl aset = new AnnotationSetImpl(_doc);

		Iterator<Annotation> myIter = _annotsById.values().iterator();

		// check all my annotations, and add the ones with the right
		// type and the given feature names to the new set
		while (myIter.hasNext()) {
			Annotation a = (Annotation) myIter.next();

			if (a.getType().equals(type)
					&& a.getFeatures().keySet().containsAll(featureNames))
				aset.add(a);
		}

		if (aset.size() > 0)
			return aset;

		return null;
	}

	/**
	 * Return all annotations by type, features, and offset.
	 */
	public AnnotationSet get(String type, FeatureMap features, long offset)
	// public AnnotationSet get(String type, FeatureMap features,
	// Long offset)
	{
		// first, filter all the annotations in this set by offset.
		// this is probably more selective.
		AnnotationSetImpl midset = (AnnotationSetImpl) get(offset);

		if (midset == null)
			return null;

		// second, filter all the annotations by type and features
		AnnotationSetImpl finalset = (AnnotationSetImpl) midset.get(type,
				features);

		if (finalset.size() > 0)
			return finalset;

		return null;
	}

	/**
	 * Return all annotations with a starting offset that is greater than or
	 * equal to the given offset.
	 */
	public AnnotationSet get(long offset)
	// public AnnotationSet get(Long offset)
	{
		// i don't have anything, return null
		if (size() == 0)
			return null;

		AnnotationSetImpl aset = new AnnotationSetImpl(_doc);

		Iterator<Annotation> myIter = _annotsById.values().iterator();

		// check all my annotations
		while (myIter.hasNext()) {
			Annotation a = myIter.next();

			// check if this annotation's starting offset is >= the
			// given offset
			if (a.getStartNodeOffset() >= offset)
				aset.add(a);
		}

		if (aset.size() > 0)
			return aset;

		return null;
	}

	/**
	 * Return all annotations that overlap with the given offsets. In other
	 * words, all annotations whose span is inside the interval given by the two
	 * argument offsets are returned.
	 */
	public AnnotationSet get(long offsetStart, long offsetEnd)
	// public AnnotationSet get(Long offsetStart, Long offsetEnd)
	{
		// i don't have anything, return null
		if (size() == 0)
			return null;

		AnnotationSetImpl aset = new AnnotationSetImpl(_doc);

		Iterator<Annotation> myIter = _annotsById.values().iterator();

		// check all my annotations, ignore ones that are totally
		// outside the given range. add the rest.
		while (myIter.hasNext()) {
			Annotation a = myIter.next();

			// if the annotation starts after the argument end or ends
			// before the argument start, ignore it.
			if (a.getStartNodeOffset() > offsetEnd
					|| a.getEndNodeOffset() < offsetStart)
				continue;

			// otherwise, add it
			aset.add(a);
		}

		if (aset.size() > 0)
			return aset;

		return null;
	}

	/**
	 * Returns true if at least one annotation in this set covers a range that
	 * overlaps with the given range.
	 */
	public boolean coversPartialRange(long offsetStart, long offsetEnd) {
		// i don't have anything, return null
		if (size() == 0)
			return false;

		Iterator<Annotation> myIter = _annotsById.values().iterator();

		// check all my annotations, if just one annotation overlaps
		// with the given range, return true
		while (myIter.hasNext()) {
			Annotation a = myIter.next();

			// if the annotation starts after the argument end or ends
			// before the argument start, ignore it.
			if (a.getStartNodeOffset() > offsetEnd
					|| a.getEndNodeOffset() < offsetStart)
				continue;

			// otherwise, found one and return true
			return true;
		}

		return false;
	}

	/**
	 * Return all annotations with the given type and that overlap with the
	 * given offsets.
	 */
	public AnnotationSet get(String type, long offsetStart, long offsetEnd) {
		// first, filter all the annotations in this set by offset.
		// this is probably more selective.
		AnnotationSetImpl midset = (AnnotationSetImpl) get(offsetStart,
				offsetEnd);

		if (midset == null)
			return null;

		// second, filter all the annotations by type
		AnnotationSetImpl finalset = (AnnotationSetImpl) midset.get(type);

		if (finalset.size() > 0)
			return finalset;

		return null;
	}

	/**
	 * Return all annotations that are exclusively inside the given offsets. In
	 * other words, they have to start and end within the given offsets. This is
	 * more restrictive than get(Long offsetStart, Long offsetEnd).
	 */
	public AnnotationSet getContained(long offsetStart, long offsetEnd) {
		// i don't have anything, return null
		if (size() == 0)
			return null;

		AnnotationSetImpl aset = new AnnotationSetImpl(_doc);

		// for all annotations that start within the given range
		for (long i = offsetStart; i <= offsetEnd; i++) {

			if (_annotsByStartOffset.containsKey(i)) {

				ArrayList<Annotation> candidate_set = _annotsByStartOffset
						.get(i);

				// for all annotations that start at this particular
				// offset, check that it ends before the given offsetEnd
				for (int j = 0; j < candidate_set.size(); j++) {
					Annotation a = (Annotation) candidate_set.get(j);

					if (a.getEndNodeOffset() <= offsetEnd) {
						aset.add(a);
					}
				}
			}
		}

		if (aset.size() > 0)
			return aset;

		return null;
	}

	/**
	 * Return all annotations that have exact offsets as the given offsets.
	 */
	public AnnotationSet getExact(long offsetStart, long offsetEnd) {
		// i don't have anything, return null
		if (size() == 0)
			return null;

		if (_annotsByStartOffset.containsKey(offsetStart)) {

			ArrayList<Annotation> candidate_set = _annotsByStartOffset
					.get(offsetStart);

			AnnotationSetImpl aset = new AnnotationSetImpl(_doc);

			for (int i = 0; i < candidate_set.size(); i++) {
				Annotation a = (Annotation) candidate_set.get(i);

				// exact match
				if (a.getEndNodeOffset() == offsetEnd) {
					aset.add(a);
				}
			}

			if (aset.size() > 0)
				return aset;
		}

		return null;
	}

	/**
	 * Return all annotations that have exact starting offsets as the given
	 * offset.
	 */
	public AnnotationSet getExactStart(long offsetStart) {
		// i don't have anything, return null
		if (size() == 0)
			return null;

		if (_annotsByStartOffset.containsKey(offsetStart)) {

			ArrayList<Annotation> candidate_set = _annotsByStartOffset
					.get(offsetStart);

			AnnotationSetImpl aset = new AnnotationSetImpl(_doc);

			for (int i = 0; i < candidate_set.size(); i++) {
				Annotation a = (Annotation) candidate_set.get(i);
				aset.add(a);
			}

			if (aset.size() > 0)
				return aset;
		}

		return null;
	}

	/**
	 * Return all annotations in this set in an ArrayList as sorted by their
	 * starting node's offset in ascending order. If two annotations have the
	 * same starting node, the order is arbitrary.
	 */
	public ArrayList<Annotation> getAllSortedAsArrayList() {
		// i don't have anything, return null
		if (size() == 0)
			return null;

		ArrayList<Annotation> list = new ArrayList<Annotation>();
		Iterator<Annotation> myIter = _annotsById.values().iterator();

		while (myIter.hasNext()) {
			list.add(myIter.next());
		}

		Comparator<Annotation> comp = new Comparator<Annotation>() {
			public int compare(Annotation a, Annotation b) {

				if (a.getStartNodeOffset() < b.getStartNodeOffset()) {
					return -1;
				}
				if (a.getStartNodeOffset() == b.getStartNodeOffset())
					return 0;
				return 1;
			}
		};

		Collections.sort(list, comp);

		return list;
	}

	/**
	 * Get the name of this set.
	 */
	public String getName() {
		return _name;
	}

	/**
	 * Get a set of String objects that list all the annotation types in this
	 * set.
	 */
	public Set<String> getAllTypes() {
		Set<String> allTypes = new HashSet<String>();

		// i don't have anything, return null
		if (size() == 0)
			return null;

		Iterator<Annotation> myIter = _annotsById.values().iterator();

		// go through all my annotations
		while (myIter.hasNext()) {
			Annotation a = myIter.next();

			// add the type. note that if the type already exists, the
			// set is unchanged.
			allTypes.add(a.getType());
		}

		return allTypes;
	}

	/**
	 * Get the document this set is attached to.
	 */
	public Document getDocument() {
		return _doc;
	}

	/**
	 * Inner class that is the iterator of this set.
	 */
	public class AnnotationSetIterator implements Iterator<Annotation> {
		private Iterator<Annotation> iter;

		protected Annotation lastNext = null;

		AnnotationSetIterator() {
			iter = _annotsById.values().iterator();
		}

		public boolean hasNext() {
			return iter.hasNext();
		}

		public Annotation next() {
			return (lastNext = (Annotation) iter.next());
		}

		public void remove() {
			iter.remove();
		}
	}
}
