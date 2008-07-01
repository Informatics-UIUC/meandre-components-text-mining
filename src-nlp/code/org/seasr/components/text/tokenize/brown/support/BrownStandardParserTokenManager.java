/** * University of Illinois/NCSA * Open Source License *  * Copyright (c) 2008, Board of Trustees-University of Illinois.   * All rights reserved. *  * Developed by:  *  * Automated Learning Group * National Center for Supercomputing Applications * http://www.seasr.org *  *   * Permission is hereby granted, free of charge, to any person obtaining a copy * of this software and associated documentation files (the "Software"), to * deal with the Software without restriction, including without limitation the * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or * sell copies of the Software, and to permit persons to whom the Software is * furnished to do so, subject to the following conditions:  *  *  * Redistributions of source code must retain the above copyright notice, *    this list of conditions and the following disclaimers.  *  *  * Redistributions in binary form must reproduce the above copyright notice, *    this list of conditions and the following disclaimers in the  *    documentation and/or other materials provided with the distribution.  *  *  * Neither the names of Automated Learning Group, The National Center for *    Supercomputing Applications, or University of Illinois, nor the names of *    its contributors may be used to endorse or promote products derived from *    this Software without specific prior written permission.  *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE * CONTRIBUTORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS * WITH THE SOFTWARE. */ /* Generated By:JavaCC: Do not edit this line. BrownStandardParserTokenManager.java */package org.seasr.components.text.tokenize.brown.support;/** *  * Parser for tokenizing a corpus into Brown Corpus Standard Format. *  */public class BrownStandardParserTokenManager implements		BrownStandardParserConstants {	private final int jjMoveStringLiteralDfa0_0() {		return jjMoveNfa_0(2, 0);	}	private final void jjCheckNAdd(int state) {		if (jjrounds[state] != jjround) {			jjstateSet[jjnewStateCnt++] = state;			jjrounds[state] = jjround;		}	}	private final void jjAddStates(int start, int end) {		do {			jjstateSet[jjnewStateCnt++] = jjnextStates[start];		} while (start++ != end);	}	private final void jjCheckNAddTwoStates(int state1, int state2) {		jjCheckNAdd(state1);		jjCheckNAdd(state2);	}//	private final void jjCheckNAddStates(int start, int end) {//		do {//			jjCheckNAdd(jjnextStates[start]);//		} while (start++ != end);//	}//	private final void jjCheckNAddStates(int start) {//		jjCheckNAdd(jjnextStates[start]);//		jjCheckNAdd(jjnextStates[start + 1]);//	}	static final long[] jjbitVec0 = { 0x0L, 0x0L, 0xffffffffffffffffL,			0xffffffffffffffffL };	private final int jjMoveNfa_0(int startState, int curPos) {//		int[] nextStates;		int startsAt = 0;		jjnewStateCnt = 50;		int i = 1;		jjstateSet[0] = startState;//		int j; 		int kind = 0x7fffffff;		for (;;) {			if (++jjround == 0x7fffffff)				ReInitRounds();			if (curChar < 64) {				long l = 1L << curChar;				/*MatchLoop:*/ do {					switch (jjstateSet[--i]) {					case 2:						if (kind > 10)							kind = 10;						if ((0xfc00fffe00000000L & l) != 0L) {							if (kind > 5)								kind = 5;						} else if ((0x3ff000000000000L & l) != 0L) {							if (kind > 9)								kind = 9;							jjCheckNAdd(29);						} else if ((0x100002600L & l) != 0L) {							if (kind > 2)								kind = 2;						}						if ((0x8000400200000000L & l) != 0L)							jjCheckNAdd(24);						else if (curChar == 39)							jjstateSet[jjnewStateCnt++] = 28;						else if (curChar == 45) {							if (kind > 6)								kind = 6;							jjCheckNAdd(26);						}						break;					case 0:						if (curChar == 46 && kind > 1)							kind = 1;						break;					case 13:						if (curChar == 32)							jjstateSet[jjnewStateCnt++] = 12;						break;					case 14:						if (curChar == 46)							jjstateSet[jjnewStateCnt++] = 13;						break;					case 19:						if (curChar == 32)							jjstateSet[jjnewStateCnt++] = 18;						break;					case 22:						if ((0x100002600L & l) != 0L && kind > 2)							kind = 2;						break;					case 23:						if ((0x8000400200000000L & l) != 0L)							jjCheckNAdd(24);						break;					case 24:						if ((0x100002600L & l) == 0L)							break;						if (kind > 4)							kind = 4;						jjCheckNAdd(24);						break;					case 25:						if ((0xfc00fffe00000000L & l) != 0L && kind > 5)							kind = 5;						break;					case 26:						if (curChar != 45)							break;						if (kind > 6)							kind = 6;						jjCheckNAdd(26);						break;					case 27:						if (curChar == 39)							jjstateSet[jjnewStateCnt++] = 28;						break;					case 29:						if ((0x3ff000000000000L & l) == 0L)							break;						if (kind > 9)							kind = 9;						jjCheckNAdd(29);						break;					case 30:						if (kind > 10)							kind = 10;						break;					case 46:						if (curChar == 46)							jjCheckNAdd(47);						break;					case 48:						if (curChar != 46)							break;						if (kind > 3)							kind = 3;						jjCheckNAdd(47);						break;					default:						break;					}				} while (i != startsAt);			} else if (curChar < 128) {				long l = 1L << (curChar & 077);				/*MatchLoop:*/ do {					switch (jjstateSet[--i]) {					case 2:						if (kind > 10)							kind = 10;						if ((0x7fffffe07fffffeL & l) != 0L) {							if (kind > 8)								kind = 8;							jjCheckNAddTwoStates(46, 49);						} else if ((0x78000001f8000001L & l) != 0L) {							if (kind > 5)								kind = 5;						}						if (curChar == 67)							jjAddStates(0, 1);						else if (curChar == 83)							jjAddStates(2, 3);						else if (curChar == 77)							jjAddStates(4, 6);						else if (curChar == 101)							jjstateSet[jjnewStateCnt++] = 20;						else if (curChar == 111)							jjstateSet[jjnewStateCnt++] = 15;						else if (curChar == 73)							jjstateSet[jjnewStateCnt++] = 8;						else if (curChar == 80)							jjstateSet[jjnewStateCnt++] = 5;						else if (curChar == 68)							jjstateSet[jjnewStateCnt++] = 1;						break;					case 1:					case 32:						if (curChar == 114)							jjCheckNAdd(0);						break;					case 3:						if (curChar == 102)							jjCheckNAdd(0);						break;					case 4:						if (curChar == 111)							jjstateSet[jjnewStateCnt++] = 3;						break;					case 5:						if (curChar == 114)							jjstateSet[jjnewStateCnt++] = 4;						break;					case 6:						if (curChar == 80)							jjstateSet[jjnewStateCnt++] = 5;						break;					case 7:						if (curChar == 99)							jjCheckNAdd(0);						break;					case 8:						if (curChar == 110)							jjstateSet[jjnewStateCnt++] = 7;						break;					case 9:						if (curChar == 73)							jjstateSet[jjnewStateCnt++] = 8;						break;					case 10:					case 39:						if (curChar == 116)							jjCheckNAdd(0);						break;					case 11:						if (curChar == 105)							jjstateSet[jjnewStateCnt++] = 10;						break;					case 12:						if (curChar == 99)							jjstateSet[jjnewStateCnt++] = 11;						break;					case 15:						if (curChar == 112)							jjstateSet[jjnewStateCnt++] = 14;						break;					case 16:						if (curChar == 111)							jjstateSet[jjnewStateCnt++] = 15;						break;					case 17:						if (curChar == 108)							jjCheckNAdd(0);						break;					case 18:						if (curChar == 97)							jjstateSet[jjnewStateCnt++] = 17;						break;					case 20:						if (curChar == 116)							jjstateSet[jjnewStateCnt++] = 19;						break;					case 21:						if (curChar == 101)							jjstateSet[jjnewStateCnt++] = 20;						break;					case 25:						if ((0x78000001f8000001L & l) != 0L && kind > 5)							kind = 5;						break;					case 28:						if ((0x7fffffe07fffffeL & l) == 0L)							break;						if (kind > 7)							kind = 7;						jjstateSet[jjnewStateCnt++] = 28;						break;					case 30:						if (kind > 10)							kind = 10;						break;					case 31:						if (curChar == 77)							jjAddStates(4, 6);						break;					case 33:					case 35:						if (curChar == 115)							jjCheckNAdd(0);						break;					case 34:						if (curChar == 114)							jjstateSet[jjnewStateCnt++] = 33;						break;					case 36:						if (curChar == 83)							jjAddStates(2, 3);						break;					case 37:						if (curChar == 110)							jjCheckNAdd(0);						break;					case 38:						if (curChar == 101)							jjstateSet[jjnewStateCnt++] = 37;						break;					case 40:						if (curChar == 67)							jjAddStates(0, 1);						break;					case 41:						if (curChar == 111)							jjCheckNAdd(0);						break;					case 42:						if (curChar == 112)							jjCheckNAdd(0);						break;					case 43:						if (curChar == 114)							jjstateSet[jjnewStateCnt++] = 42;						break;					case 44:						if (curChar == 111)							jjstateSet[jjnewStateCnt++] = 43;						break;					case 45:						if ((0x7fffffe07fffffeL & l) == 0L)							break;						if (kind > 8)							kind = 8;						jjCheckNAddTwoStates(46, 49);						break;					case 47:						if ((0x7fffffe07fffffeL & l) != 0L)							jjstateSet[jjnewStateCnt++] = 48;						break;					case 49:						if ((0x7fffffe07fffffeL & l) == 0L)							break;						if (kind > 8)							kind = 8;						jjCheckNAdd(49);						break;					default:						break;					}				} while (i != startsAt);			} else {				int i2 = (curChar & 0xff) >> 6;				long l2 = 1L << (curChar & 077);				/*MatchLoop:*/ do {					switch (jjstateSet[--i]) {					case 2:						if ((jjbitVec0[i2] & l2) != 0L && kind > 10)							kind = 10;						break;					default:						break;					}				} while (i != startsAt);			}			if (kind != 0x7fffffff) {				jjmatchedKind = kind;				jjmatchedPos = curPos;				kind = 0x7fffffff;			}			++curPos;			if ((i = jjnewStateCnt) == (startsAt = 50 - (jjnewStateCnt = startsAt)))				return curPos;			try {				curChar = input_stream.readChar();			} catch (java.io.IOException e) {				return curPos;			}		}	}	static final int[] jjnextStates = { 41, 44, 38, 39, 32, 34, 35, };	public static final String[] jjstrLiteralImages = { "", null, null, null,			null, null, null, null, null, null, null, };	public static final String[] lexStateNames = { "DEFAULT", };	private ASCII_CharStream input_stream;	private final int[] jjrounds = new int[50];	private final int[] jjstateSet = new int[100];	protected char curChar;	public BrownStandardParserTokenManager(ASCII_CharStream stream) {		if (ASCII_CharStream.staticFlag)			throw new Error(					"ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");		input_stream = stream;	}	public BrownStandardParserTokenManager(ASCII_CharStream stream, int lexState) {		this(stream);		SwitchTo(lexState);	}	public void ReInit(ASCII_CharStream stream) {		jjmatchedPos = jjnewStateCnt = 0;		curLexState = defaultLexState;		input_stream = stream;		ReInitRounds();	}	private final void ReInitRounds() {		int i;		jjround = 0x80000001;		for (i = 50; i-- > 0;)			jjrounds[i] = 0x80000000;	}	public void ReInit(ASCII_CharStream stream, int lexState) {		ReInit(stream);		SwitchTo(lexState);	}	public void SwitchTo(int lexState) {		if (lexState >= 1 || lexState < 0)			throw new TokenMgrError("Error: Ignoring invalid lexical state : "					+ lexState + ". State unchanged.",					TokenMgrError.INVALID_LEXICAL_STATE);		else			curLexState = lexState;	}	private final Token jjFillToken() {		Token t = Token.newToken(jjmatchedKind);		t.kind = jjmatchedKind;		String im = jjstrLiteralImages[jjmatchedKind];		t.image = (im == null) ? input_stream.GetImage() : im;		t.beginLine = input_stream.getBeginLine();		t.beginColumn = input_stream.getBeginColumn();		t.endLine = input_stream.getEndLine();		t.endColumn = input_stream.getEndColumn();		return t;	}	int curLexState = 0;	int defaultLexState = 0;	int jjnewStateCnt;	int jjround;	int jjmatchedPos;	int jjmatchedKind;	public final Token getNextToken() {//		int kind;//		Token specialToken = null;		Token matchedToken;		int curPos = 0;		/*EOFLoop:*/ for (;;) {			try {				curChar = input_stream.BeginToken();			} catch (java.io.IOException e) {				jjmatchedKind = 0;				matchedToken = jjFillToken();				return matchedToken;			}			jjmatchedKind = 0x7fffffff;			jjmatchedPos = 0;			curPos = jjMoveStringLiteralDfa0_0();			if (jjmatchedKind != 0x7fffffff) {				if (jjmatchedPos + 1 < curPos)					input_stream.backup(curPos - jjmatchedPos - 1);				matchedToken = jjFillToken();				return matchedToken;			}			int error_line = input_stream.getEndLine();			int error_column = input_stream.getEndColumn();			String error_after = null;			boolean EOFSeen = false;			try {				input_stream.readChar();				input_stream.backup(1);			} catch (java.io.IOException e1) {				EOFSeen = true;				error_after = curPos <= 1 ? "" : input_stream.GetImage();				if (curChar == '\n' || curChar == '\r') {					error_line++;					error_column = 0;				} else					error_column++;			}			if (!EOFSeen) {				input_stream.backup(1);				error_after = curPos <= 1 ? "" : input_stream.GetImage();			}			throw new TokenMgrError(EOFSeen, curLexState, error_line,					error_column, error_after, curChar,					TokenMgrError.LEXICAL_ERROR);		}	}}