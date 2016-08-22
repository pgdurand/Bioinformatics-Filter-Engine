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
import java.util.ArrayList;
import java.util.Iterator;

import bzh.plealog.bioinfo.api.filter.BFilter;
import bzh.plealog.bioinfo.api.filter.BRule;

/**
 * This is an implementation of BFilter made only for serialization. 
 * For internal use only.
 * 
 * @author Patrick G. Durand
 */
public class BFilterIO implements Serializable {
  private static final long serialVersionUID = -3675053121147621773L;
  @SuppressWarnings("unused")
  private int       xmlVersion = 1;
  private String    name;
  private String    description;
  private boolean   exclusive;
  private ArrayList<BRuleIO> rules;

  public BFilterIO(){
    rules = new ArrayList<BRuleIO>();
  }

  public BFilterIO(BFilter filter){
    this();

    setName(filter.getName());
    setDescription(filter.getDescription());
    setExclusive(filter.getExclusive());

    Iterator<BRule> iter = filter.getRules();
    while(iter.hasNext()){
      rules.add(new BRuleIO((BRule)iter.next()));
    }
  }
  public Object clone(){
    BFilterIO filter = new BFilterIO();
    filter.copy(this);
    return filter;
  }

  protected void copy(BFilterIO src){
    Iterator<BRuleIO> iter;

    this.setName(src.getName());
    this.setDescription(src.getDescription());
    this.setExclusive(src.isExclusive());
    iter = src.getRules().iterator();
    while(iter.hasNext()){
      this.add((BRuleIO)((BRuleIO)iter.next()).clone());
    }
  }

  public void add(BRuleIO rule){
    rules.add(rule);
  }
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isExclusive() {
    return exclusive;
  }

  public void setExclusive(boolean exclusive) {
    this.exclusive = exclusive;
  }

  public ArrayList<BRuleIO> getRules() {
    return rules;
  }

  public void setRules(ArrayList<BRuleIO> rules) {
    this.rules = rules;
  }
}
