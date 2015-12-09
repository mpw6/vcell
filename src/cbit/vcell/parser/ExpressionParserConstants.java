/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

/* Generated By:JJTree&JavaCC: Do not edit this line. ExpressionParserConstants.java */
package cbit.vcell.parser;

public interface ExpressionParserConstants {

  int EOF = 0;
  int QUOTE = 5;
  int RELATIONAL_OPERATOR = 6;
  int LT = 7;
  int GT = 8;
  int LE = 9;
  int GE = 10;
  int EQ = 11;
  int NE = 12;
  int AND = 13;
  int OR = 14;
  int NOT = 15;
  int POWER = 16;
  int ADD = 17;
  int SUB = 18;
  int MULT = 19;
  int DIV = 20;
  int FLOATING_POINT_LITERAL = 21;
  int EXPONENT = 22;
  int INTEGER_LITERAL = 23;
  int IDENTIFIER = 24;
  int ID = 25;
  int LETTER = 26;
  int DIGIT = 27;

  int DEFAULT = 0;

  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "\"\\\'\"",
    "<RELATIONAL_OPERATOR>",
    "\"<\"",
    "\">\"",
    "\"<=\"",
    "\">=\"",
    "\"==\"",
    "\"!=\"",
    "<AND>",
    "<OR>",
    "<NOT>",
    "\"^\"",
    "\"+\"",
    "\"-\"",
    "\"*\"",
    "\"/\"",
    "<FLOATING_POINT_LITERAL>",
    "<EXPONENT>",
    "<INTEGER_LITERAL>",
    "<IDENTIFIER>",
    "<ID>",
    "<LETTER>",
    "<DIGIT>",
    "\";\"",
    "\"(\"",
    "\")\"",
    "\",\"",
  };

}