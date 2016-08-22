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
package bzh.plealog.bioinfo.filter.implem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bzh.plealog.bioinfo.api.filter.BOperatorAccessors;
import bzh.plealog.bioinfo.api.filter.BRule;
import bzh.plealog.bioinfo.io.filter.BRuleIO;

/**
 * This is a default implementation of interface BRule. For internal use only.
 * 
 * @author Patrick G. Durand
 */
public class BRuleImplem implements BRule {

	private           String accessor;
	private           String operator;
	private           Object value;
	private transient String strRepr_;
	private transient String htmlStrRepr_;
	
	public BRuleImplem(){}
	
	public BRuleImplem(BRuleIO rule){
		this(rule.getAccessor(), rule.getOperator(), rule.getValue());
	}

	public BRuleImplem(String accessor, String operator, Object value){
		setAccessor(accessor);
		setOperator(operator);
		setValue(value);
	}
	
	public Object clone(){
		BRuleImplem rule = new BRuleImplem();
		rule.copy(this);
		return rule;
	}
	
	protected void copy(BRule src){
		this.setAccessor(src.getAccessor());
		this.setOperator(src.getOperator());
		Object v = src.getValue();
		if (v instanceof List)
			this.setValue(new ArrayList<Object>((List<?>)v));
		else if (v instanceof Set)
			this.setValue(new HashSet<Object>((Set<?>)v));
		else //basic types: Number, Boolean, Character, String
			this.setValue(v);
		
	}
	
	/**
	 * Implementation of BRule interface.
	 */
	public String getAccessor() {
		return accessor;
	}

	/**
	 * Implementation of BRule interface.
	 */
	public void setAccessor(String acc) {
		accessor = acc;
		htmlStrRepr_=strRepr_=null;
	}

	/**
	 * Implementation of BRule interface.
	 */
	public String getOperator() {
		return operator;
	}

	/**
	 * Implementation of BRule interface.
	 */
	public void setOperator(String ope) {
		operator = ope;
		htmlStrRepr_=strRepr_=null;
	}

	/**
	 * Implementation of BRule interface.
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Implementation of BRule interface.
	 */
	public void setValue(Object obj) {
		value = obj;
		htmlStrRepr_=strRepr_=null;
	}

  /**
   * Implementation of BRule interface.
   */
	public String getHtmlString(BOperatorAccessors fModel){
		StringBuffer buf;
		
		if (htmlStrRepr_!=null)
			return htmlStrRepr_;
		buf = new StringBuffer();
		buf.append("<b>"+accessor+"</b>");
		buf.append(" <i>");
		buf.append(fModel.getTextForOperator(operator));
		buf.append("</i> '");
		String val = value.toString();
		if (val.startsWith("(?i)(")){
			val = val.substring(5, val.length()-1);
		}
		buf.append(val+"'");
		htmlStrRepr_ = buf.toString();
		return htmlStrRepr_;

	}
  /**
   * Implementation of BRule interface.
   */
	public String getTxtString(BOperatorAccessors fModel){
		StringBuffer buf;
		
		if (strRepr_!=null)
			return strRepr_;
		buf = new StringBuffer();
		buf.append(accessor);
		buf.append(" ");
		buf.append(fModel.getTextForOperator(operator));
		buf.append(" ");
		buf.append(value);
		strRepr_ = buf.toString();
		return strRepr_;
	}
  /**
   * Implementation of BRule interface.
   */
	public String toString(){
		StringBuffer buf;
		
		if (strRepr_!=null)
			return strRepr_;
		buf = new StringBuffer();
		buf.append(accessor);
		buf.append(" ");
		buf.append(operator);
		buf.append(" ");
		buf.append(value);
		strRepr_ = buf.toString();
		return strRepr_;
	}
}
