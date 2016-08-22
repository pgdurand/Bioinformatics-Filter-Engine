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
package bzh.plealog.bioinfo.io.filter;

import java.io.Serializable;

import bzh.plealog.bioinfo.api.filter.BRule;


/**
 * This is an implementation of BRule made only for serialization. 
 * For internal use only.
 * 
 * @author Patrick G. Durand
 */
public class BRuleIO implements Serializable{

  private static final long serialVersionUID = 7783494142549235827L;
  private           String accessor;
  private           String operator;
  private           Object value;
  private transient String strRepr_;

  public BRuleIO(){}

  public BRuleIO(BRule rule){
    this(rule.getAccessor(), rule.getOperator(), rule.getValue());
  }

  public BRuleIO(String accessor, String operator, Object value){
    setAccessor(accessor);
    setOperator(operator);
    setValue(value);
  }

  public Object clone(){
    BRuleIO rule = new BRuleIO();
    rule.copy(this);
    return rule;
  }

  protected void copy(BRuleIO src){
    this.setAccessor(src.getAccessor());
    this.setOperator(src.getOperator());
    this.setValue(src.getValue());
  }

  public String getAccessor() {
    return accessor;
  }

  public void setAccessor(String acc) {
    accessor = acc;
    strRepr_=null;
  }

  public String getOperator() {
    return operator;
  }

  public void setOperator(String ope) {
    operator = ope;
    strRepr_=null;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object obj) {
    value = obj;
    strRepr_=null;
  }

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
