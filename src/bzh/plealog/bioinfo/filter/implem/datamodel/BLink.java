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
package bzh.plealog.bioinfo.filter.implem.datamodel;

import bzh.plealog.hge.api.datamodel.DGMLink;
import bzh.plealog.hge.api.datamodel.DGMVertexType;

/**
 * This class implements DGMLink. 
 * 
 * @author Patrick G. Durand
 */
public class BLink implements DGMLink {
  private String       _name;
  private String       _repr;
  private BVertexType  _class;
  private int          _ori;

  public BLink(String name, BVertexType cl, int ori){
    _name = name;
    _class = cl;
    _ori = ori;
    _repr = _name + ": " + cl.getName();
  }

  /**
   * Implementation of DGMLink interface
   */
  public String getName(){
    return _name;
  }

  /**
   * Implementation of DGMLink interface
   */
  public DGMVertexType getVertexType(){
    return _class;
  }

  public String toString(){
    return _repr;
  }

  /**
   * Implementation of DGMLink interface
   */
  public int getOrientation(){
    return _ori;
  }

}
