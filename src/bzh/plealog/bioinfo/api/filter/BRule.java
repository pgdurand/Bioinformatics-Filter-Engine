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

/**
 * This interface defines a BRule. It is a basic element of a
 * BFilter defining a single constraint on SRHit/SRHSP attributes. A 
 * rule is made of three parts: an accessor, an operator and a value.
 * They are used to create a simple boolean expression such as 
 * &lt;accessor&gt; &lt;operator&gt; &lt;value&gt;.
 * 
 * @author Patrick G. Durand
 */
public interface BRule extends Cloneable {

  /**
   * Returns the name of the SRHIT or SRHsp attribute that have to be
   * accessed to apply this constraint. Returned value is
   * a visible accessor name as defined in BAccessors.
   */
  public String getAccessor();

  /**
   * Sets the name of the SRHIT or SRHsp attribute that have to be
   * accessed to apply this constraint.
   * 
   * @param acc must be a visible accessor name as defined in BAccessors
   */
  public void setAccessor(String acc);

  /**
   * Returns the operator to use in this constraint.
   */
  public String getOperator();

  public void setOperator(String ope);

  /**
   * Returns the value that have to be compared with the BHit or BHsp
   * value accessed through the accessor.
   */
  public Object getValue();

  public void setValue(Object obj);

  /**
   * Returns a HTML representation of the filter. This is mainly used for display
   * purpose in a user interface.
   */
  public String getHtmlString(BOperatorAccessors fModel);

  public String getTxtString(BOperatorAccessors fModel);

  /**
   * Forces the implementation of a clone method.
   */
  public Object clone();
}
