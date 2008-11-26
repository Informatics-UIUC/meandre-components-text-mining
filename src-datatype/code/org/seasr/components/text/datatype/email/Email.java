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

package org.seasr.components.text.datatype.email;

//==============
// Java Imports
//==============

import java.io.*;
import java.util.*;

/**
 * This data structure holds the content of a single email message. It consists
 * of the important headers such as subject, sender, receiver, date, etc. The
 * full header is also included as a Hashtable for reference. The body of the
 * email is represented as a single String.
 * 
 * @author Xiaolei Li
 * @author D. Searsmith
 */
public class Email implements Serializable {

	// ==============
	// Data Members
	// ==============

	private static final long serialVersionUID = 1L;

	/**
	 * The subject of the email.
	 */
	public String _subject;

	/**
	 * The sender of the email.
	 */
	public String _sender;

	/**
	 * The receiver of the email in the To: or Cc: fields; multiple entries are
	 * separated by spaces.
	 */
	public ArrayList<String> _receiver;

	/**
	 * The date of the email.
	 */
	public String _emailDate;

	/**
	 * The classification of the email.
	 */
	public String _classification;

	/**
	 * The body of the email.
	 */
	public String _body;

	// ==============
	// Constructors
	// ==============

	/**
	 * Initializes an empty email.
	 */
	public Email() {
		_subject = "";
		_sender = "";
		_receiver = new ArrayList<String>();
		_emailDate = "";
		_body = "";
	}

	/**
	 * Initializes an email with all the important headers and body. The full
	 * header Hashtable is not setup here.
	 * 
	 * @param subject
	 *            subject of the email.
	 * @param sender
	 *            sender of the email.
	 * @param receiver_to
	 *            receiver in the To: field, multiple entries are to be
	 *            separated by commas.
	 * @param email_date
	 *            date of the email.
	 * @param body
	 *            body of the email.
	 */
	public Email(String subject, String sender, ArrayList<String> receiver,
			String email_date, String body) {
		/* copy over the info */
		_subject = subject;
		_sender = sender;
		_receiver = receiver;
		_emailDate = email_date;
		_body = body;
	}

	// ================
	// Public Methods
	// ================

	/**
	 * Set the subject of the email.
	 * 
	 * @param subject
	 *            the subject of the email
	 */
	public void setSubject(String subject) {
		_subject += " " + subject;
	}

	/**
	 * Set the sender of the email.
	 * 
	 * @param sender
	 *            the sender of the email
	 */
	public void setSender(String sender) {
		_sender += " " + sender;
	}

	/**
	 * Set the receiver (in the To: field) of the email.
	 * 
	 * @param receiver_to
	 *            the receiver of the email
	 */
	public void setReceiver(String receiver) {
		_receiver.add(receiver);
	}

	/**
	 * Set the date of the email.
	 * 
	 * @param email_date
	 *            the date of the email
	 */
	public void setEmailDate(String email_date) {
		_emailDate += " " + email_date;
	}

	/**
	 * Set the body of the email.
	 * 
	 * @param body
	 *            the body of the email
	 */
	public void setBody(String body) {
		_body += " " + body;
	}

	/**
	 * Set the classification of the email.
	 * 
	 * @param klass
	 *            the classification of the email
	 */
	public void setClassification(String klass) {
		_classification = klass;
	}

	/**
	 * Get the subject of the email.
	 * 
	 * @return the subject of the email
	 */
	public String getSubject() {
		return _subject;
	}

	/**
	 * Get the sender of the email.
	 * 
	 * @return the sender of the email
	 */
	public String getSender() {
		return _sender;
	}

	/**
	 * Get the receiver (in the To: field) of the email.
	 * 
	 * @return the receiver of the email
	 */
	public ArrayList<String> getReceiver() {
		return _receiver;
	}

	public String getReceiverString() {
		String ret = "";
		if (getReceiver().size() > 0) {
			ret += "Receiver: " + getReceiver().get(0) + "\n";
			for (int i = 1, n = getReceiver().size(); i < n; i++) {
				ret += (String) getReceiver().get(i) + "\n";
			}
		}
		return ret;
	}

	/**
	 * Get the date of the email.
	 * 
	 * @return the date of the email
	 */
	public String getEmailDate() {
		return _emailDate;
	}

	/**
	 * Get the classification of the email.
	 * 
	 * @return the classification of the email
	 */
	public String getClassification() {
		return _classification;
	}

	/**
	 * Get the body of the email.
	 * 
	 * @return the body of the email
	 */
	public String getBody() {
		return _body;
	}

	/**
	 * Add an entry to the full header of the email. One entry consists of its
	 * title and content. If an entry of the same title already exists, append
	 * the new content onto the existing one.
	 * 
	 * @param title
	 *            the entry title (i.e. "From", "To", etc.).
	 * @param content
	 *            the content of the added entry.
	 */
	public void addHeaderEntry(String title, String content) {

		if (title.equals("Subject") || title.equals("SUBJECT"))
			setSubject(content);
		else if (title.equals("From") || title.equals("CREATOR"))
			setSender(content);
		else if (title.equals("To") || title.equals("Cc") || title.equals("TO")
				|| title.equals("CC"))
			setReceiver(content);
		else if (title.equals("Date") || title.equals("CREATION DATE/TIME"))
			setEmailDate(content);
		else if (title.equals("X-Class"))
			setClassification(content);
	}

	/**
	 * Prints the full header.
	 */
	public void printFullHeader() {
		printImportantHeader();
	}

	/**
	 * Prints the important headers.
	 */
	public void printImportantHeader() {
		System.out.print(printImportantHeaderStr());
	}

	/**
	 * Prints the body.
	 */
	public String printBodyStr() {
		return "Body:\n" + _body + "\n";
	}

	public String printImportantHeaderStr() {
		return "Subject:  " + _subject + "\n" + "Sender:   " + _sender + "\n"
				+ "Receiver: " + getReceiverString() + "\n" + "Date:     "
				+ _emailDate + "\n" + "X-Class:  " + _classification + "\n";
	}

	/**
	 * Prints the body.
	 */
	public void printBody() {
		System.out.print(printBodyStr());
	}

	public String toString() {
		return printImportantHeaderStr() + printBodyStr();
	}
}
