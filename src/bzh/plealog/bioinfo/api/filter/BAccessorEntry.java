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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import bzh.plealog.hge.api.datamodel.DGMAttribute;


/**
 * This class defines an entry of the BAccessors dictionary. Each such
 * entry contains information about the accessor of BHit/BHsp: a name to
 * be shown to the user, a name used internally by the filter engine, the
 * object type to which it relates (BHit or BHsp) and the value type targeted
 * by the accessor (String, Long, Double, ...). 
 * 
 * @author Patrick G. Durand
 */
public class BAccessorEntry {

	private String   accessorVisibleName_;
	private String   accessorName_;
	private String   objectType_;
	private String[] operators_;
	private String   helpMsg_;
	private int      dataType_;
	private boolean  isFunction_;
	private boolean  allowCaseSensitive_ = true;
	
	private static final SimpleDateFormat _dateFormatter = new SimpleDateFormat("yyyyMMdd");

	public BAccessorEntry(){}
	
	/**
	 * Constructor
	 * 
	 * @param visibleName the name of the accessor to be displayed to the user
	 * @param name the name of the accessor to be used in the filter engine
	 * @param objType must be one of BAccessors XXX_TYPE constants.
	 * @param operators array of valid operators
	 * @param dataType one of hge.datamodel.api.DGMAttribute DT_XXX constants.
	 * */
	public BAccessorEntry(String visibleName, String name, String objType, 
			String[] operators, int dataType){
		setAccessorVisibleName(visibleName);
		setAccessorName(name);
		setObjectType(objType);
		setOperators(operators);
		setDataType(dataType);
	}
	/**
	 * Returns the accessor name. This string is used internally by the filter
	 * engine to access real data.
	 */
	public String getAccessorName() {
		return accessorName_;
	}
	/**
	 * Sets the accessor name. This string is used internally by the filter
	 * engine to access real data.
	 */
	public void setAccessorName(String accessorName) {
		this.accessorName_ = accessorName;
	}
	/**
	 * Returns the accessor visible name. This string is intended to be shown to the user
	 * since the accessor name can be quite osbcure.
	 */
	public String getAccessorVisibleName() {
		return accessorVisibleName_;
	}
	/**
	 * Sets the accessor visible name. This string is intended to be shown to the user
	 * since the accessor name can be quite osbcure.
	 */
	public void setAccessorVisibleName(String accessorVisibleName) {
		this.accessorVisibleName_ = accessorVisibleName;
	}
	/**
	 * Returns the data type. One of hge.datamodel.api.DGMAttribute DT_XXX constants.
	 */
	public int getDataType() {
		return dataType_;
	}
	/**
	 * Sets the data type. 
	 * 
	 * @param dataType must be one of hge.datamodel.api.DGMAttribute DT_XXX constants.
	 */
	public void setDataType(int dataType) {
		this.dataType_ = dataType;
	}

	/**
	 * Returns the object type. One of BAccessors XXX_TYPE constants.
	 */
	public String getObjectType() {
		return objectType_;
	}

	/**
	 * Sets the object type. 
	 * 
	 * @param objectType must be one of BAccessors XXX_TYPE constants.
	 */
	public void setObjectType(String objectType) {
		this.objectType_ = objectType;
	}

	/**
	 * Returns the operators that are acceptable for this accessor.
	 */
	public String[] getOperators() {
		return operators_;
	}

	/**
	 * Sets the operators that are acceptable for this accessor.
	 */
	public void setOperators(String[] operators) {
		this.operators_ = operators;
	}

	/**
	 * Specifies that this accessor is the name of a function call.
	 */
	public void setFunctionAccessor(boolean val){
		isFunction_ = val;
	}

	/**
	 * Figures out if this accessor is the name of a function call.
	 */
	public boolean getFunctionAccessor(){
		return isFunction_;
	}
	/**
	 * Given a String this method will try to convert it to a valid object
	 * representation based upon this accessor data type and the operator used.
	 * This method is used to create a List of values.
	 * 
	 * @param value the value to convert.
	 * @param valSperarator the character used to identify the various values. If null, uses
	 * the semicolon character.
	 * @return a List of String, Character, Boolean, Double or Long.
	 */
	public Object getValidValuesAsList(String value, String valSperarator){
		Object            oRet;
		StringTokenizer   tokenizer;
		String            str, sep;
		ArrayList<Object> lst;
		
		if (value==null)
			return null;
		sep =  valSperarator!=null? valSperarator:";";
		if (value.indexOf(sep)==-1)
			return null;
		tokenizer = new StringTokenizer(value, sep);
		lst = new ArrayList<Object>();
		while(tokenizer.hasMoreTokens()){
			str = tokenizer.nextToken().trim();
			oRet = getValidValue(str);
			if (oRet==null)
				return null;
			lst.add(oRet);
		}
		return lst;
	}
	
	/**
	 * Given a String this method will try to convert it to a valid object
	 * representation based upon this accessor data type. If the String
	 * is null or if it does not conform to any valid data type (string,
	 * character, boolean, char, number), the method return null.
	 * 
	 * @param value the value to convert.
	 * @return either a String, a Character, a Boolean, a Double or a Long. If
	 * parameter value conforms to String data type, then value is returned as
	 * is (i.e., it is not clone).
	 */
	public Object getValidValue(String value){
		Object oRet = null;
		
		if (value==null)
			return null;
		
		switch(dataType_){
			case DGMAttribute.DT_STRING:
				oRet=value;
				break;
			case DGMAttribute.DT_BOOLEAN:
				if (value.equalsIgnoreCase("true")){
					oRet = Boolean.TRUE;
				}
				else if (value.equalsIgnoreCase("false")){
					oRet = Boolean.FALSE;
				}
				break;
			case DGMAttribute.DT_CHARACTER:
				if (value.length()==1){
					oRet = new Character(value.charAt(0));
				}
				break;
			case DGMAttribute.DT_DOUBLE:
				try {
					oRet = Double.valueOf(value);
				}
				catch (NumberFormatException e) {
				}
				break;
			case DGMAttribute.DT_LONG:
				try {
					oRet = Long.valueOf(value);
				}
				catch (NumberFormatException e) {
				}
				break;
			case DGMAttribute.DT_DATE:
				try {
					//check date format first (yyyyMMdd)
					_dateFormatter.parse(value);
					oRet = Long.valueOf(value);
				} catch (Exception e) {
				}
		}
		return oRet;
	}
	/**
	 * Evaluates an object to figure out if its conform to this accessor data
	 * type. This method handles all type of objects?
	 * @param data the value to check
	 */
	public boolean isValidValue(Object data){
		boolean bRet = false;
		
		if (data==null)
			return false;
		if (data instanceof Set || data instanceof List){//only handles Set or List
			if (((Collection<?>)data).isEmpty())
				return false;
			for(Object item : ((Collection<?>)data)){
				if (!isValidAtomicValue(item)){
					return false;
				}
			}
			bRet = true;
		}
		else{
			bRet = isValidAtomicValue(data);
		}
		return bRet;
	}
	/**
	 * Evaluates an object to figure out if its conform to this accessor data
	 * type. This method only handles basic java types.
	 * @param value the value to check
	 */
	private boolean isValidAtomicValue(Object value){
		boolean bRet = false;
		switch(dataType_){
		case DGMAttribute.DT_STRING:
			if (value instanceof String)
				bRet=true;
			break;
		case DGMAttribute.DT_BOOLEAN:
			if (value instanceof Boolean)
				bRet=true;
			break;
		case DGMAttribute.DT_CHARACTER:
			if (value instanceof Character)
				bRet=true;
			break;
		case DGMAttribute.DT_DOUBLE:
			if (value instanceof Float || value instanceof Double)
				bRet=true;
			break;
		case DGMAttribute.DT_LONG:
		case DGMAttribute.DT_DATE:
			if (value instanceof Short || value instanceof Byte || value instanceof Integer
					|| value instanceof Long)
				bRet=true;
			break;
		}
		return bRet;
	}
	/**
	 * Set help message associated to the accessor.
	 * @param msg the help message
	 * */
	public void setHelpMsg(String msg){
		helpMsg_ = msg;
	}
  /**
   * Get help message associated to the accessor.
   * @return the help message
   * */
	public String getHelpMsg(){
		return helpMsg_;
	}
	/**
	 * Figure out whether or not the accessor is case sensitive.
	 * @return true or false
	 */
	public boolean isAllowCaseSensitive() {
		return allowCaseSensitive_;
	}

  /**
   * Figure out whether or not the accessor is case sensitive.
   * @param allowCaseSensitive true or false
   */
	public void setAllowCaseSensitive(boolean allowCaseSensitive) {
		this.allowCaseSensitive_ = allowCaseSensitive;
	}

	/**
	 * Return a string representation of this accessor.
	 * @return a string
	 */
	public String toString(){
		return accessorVisibleName_;
	}
}
