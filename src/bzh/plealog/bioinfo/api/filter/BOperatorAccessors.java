/* Copyright (C) 2006-2016 Patrick G. Durand
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  You may obtain a copy of the License at
 *
 *     https://www.gnu.org/licenses/agpl-3.0.txt
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 */
package bzh.plealog.bioinfo.api.filter;

import java.util.Enumeration;

/**
 * This interface defines the operators available in a filterable data model. 
 * It aims at being the gateway between the filter engine and the filter UI.
 * 
 * @author Patrick G. Durand
 */
public interface BOperatorAccessors {
  // messages
  public static final String InRangeHlpMsg = "To specify a range, enter two values (lower value first) separated by a semicolon. Example: 15;53.";
  public static final String DATE_HLP_MSG = "Date format is YYYYmmdd. Example: to set 'Aug 5, 1999', enter: 19990805.";

  //convenient constants targeting specific functions defined in the HGE library
  public static final String OPE_FUNC_StrInSet     = "strInSet";
  public static final String OPE_FUNC_StrNotInSet  = "strNotInSet";
  public static final String OPE_FUNC_LongInSet    = "longInSet";
  public static final String OPE_FUNC_LongNotInSet = "longNotInSet";

  // range operators
  public static final String OPE_InRangeInclusive  = "[]";
  public static final String OPE_InRangeExclusive  = "][";

  // equality operators
  public static final String OPE_Equal             = "==";
  public static final String OPE_NotEqual          = "!=";
  public static final String OPE_LessThan          = "<";
  public static final String OPE_GreatherThan      = ">";
  public static final String OPE_LessThanEqual     = "<=";
  public static final String OPE_GreatherThanEqual = ">=";

  // pattern matching operators
  public static final String OPE_MatchRegExp       = "::=";
  public static final String OPE_NotMatchRegExp    = "!:";

  public static final String[] OPE_FOR_NUMBERS = {
    //basic numbers operations
    OPE_Equal,
    OPE_NotEqual,
    OPE_LessThan,
    OPE_GreatherThan,
    OPE_LessThanEqual,
    OPE_GreatherThanEqual, 
    //range operators
    OPE_InRangeInclusive,
    OPE_InRangeExclusive,
    //special functions
    OPE_FUNC_LongInSet, 
    OPE_FUNC_LongNotInSet};
  public static final String[] OPE_FOR_STRING = {
    //basic string operators
    OPE_Equal,
    OPE_NotEqual,
    //match operators (RegExp)
    OPE_MatchRegExp,
    OPE_NotMatchRegExp,
    //special functions
    OPE_FUNC_StrInSet,
    OPE_FUNC_StrNotInSet
  };
  /**
   * Return an enumeration over the accessor visible names of a data model.
   */
  public Enumeration<String> getAccessorVisibleNames();

  /**
   * Return a BAccessorEntry given its visible name.
   */
  public BAccessorEntry getAccessorEntry(String visibleName);

  /**
   * Return the human friendly text of an operator given its symbol.
   */
  public String getTextForOperator(String opeSymbol);
  /**
   * Return the symbol of an operator given its human friendly text .
   */
  public String getOperatorForText(String txtSymbol);
}
