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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import bzh.plealog.hge.api.datamodel.DGMHyperEdgeType;
import bzh.plealog.hge.api.datamodel.DGMLink;

/**
 * This class implements DGMHyperEdgeType. 
 * 
 * @author Patrick G. Durand
 */
public class BHyperEdgeType extends BType implements DGMHyperEdgeType {

  private Hashtable<String, DGMLink> _links;

  public BHyperEdgeType(String typeName, BVertexType from, BVertexType to, Map<String, String> attributes){
    String   lName;

    setContent(typeName, attributes);

    _links = new Hashtable<String, DGMLink>();
    lName = "from"+from.getName();
    _links.put(
        lName, 
        new BLink(lName, from, DGMLink.BOTH)
        );
    lName = "to"+to.getName();
    _links.put(
        lName, 
        new BLink(lName, to, DGMLink.BOTH)
        );
  }

  /**
   * Implementation of DGMHyperEdgeType interface
   */
  public DGMLink getLink(String name){
    return ((DGMLink) _links.get(name));
  }

  /**
   * Implementation of DGMHyperEdgeType interface
   */
  public int size(){
    return _links.size();
  }

  /**
   * Implementation of DGMHyperEdgeType interface
   */
  public Enumeration<DGMLink> getLinks(){
    return (_links.elements());
  }
}
