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

import java.util.Iterator;

import bzh.plealog.bioinfo.api.data.searchresult.SROutput;

/**
 * This interface defines a SROutput filter. Such a filter aims at applying
 * a set of constraints to retrieve BHits/BHSPs satisfying those constraints. 
 * 
 * @author Patrick G. Durand
 */
public interface BFilter extends Cloneable {
  public static final String FILTER_NAME_NONE = "none";

  /**
   * Compiles the set of BRules contained in this filter to create a ready
   * to execute filter.
   */
  public void compile() throws BFilterException;

  /**
   * Executes this filter on a SROutput.
   * 
   * @return a filtered SROutput or null if not result found.
   */
  public SROutput execute(SROutput bo) throws BFilterException;

  /**
   * Adds a rule to this filter.
   */
  public void add(BRule rule) throws BFilterException;

  /**
   * Removes a rule from this filter.
   */
  public void remove(BRule rule);

  /**
   * Returns an Iterator over the BRule contained in this filter.
   */
  public Iterator<BRule> getRules();

  /**
   * Returns the number of rules contains in this filter.
   */
  public int size();

  /**
   * Returns the name of this filter. This name also corresponds to what is
   * sometimes called an alias name.
   */
  public String getName();

  /**
   * Sets the name of this filter.
   */
  public void setName(String filterName);

  /**
   * Returns the description of this filter.
   */
  public String getDescription();

  /**
   * Sets the description of this filter.
   */
  public void setDescription(String filterDescription);

  /**
   * Returns the type of filter. It can be either exclusive (and) or
   * inclusive (or).
   */
  public boolean getExclusive();

  /**
   * Sets the type of filter. It can be either exclusive (and) or
   * inclusive (or).
   */
  public void setExclusive(boolean val);

  /**
   * Returns a HTML representation of the filter. This is mainly used for display
   * purpose in a user interface.
   */
  public String getHtmlString();

  public String getTxtString();

  /**
   * Forces the implementation of a clone method.
   */
  public Object clone();
}
