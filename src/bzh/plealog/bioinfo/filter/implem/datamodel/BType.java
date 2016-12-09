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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

import bzh.plealog.hge.api.datamodel.DGMAttribute;
import bzh.plealog.hge.api.datamodel.DGMAttributeException;
import bzh.plealog.hge.api.datamodel.DGMType;

/**
 * This class implements DGMType. 
 * 
 * @author Patrick G. Durand
 */
public class BType implements DGMType {
  protected DGMType         _parent;
  protected String          _typeName;
  protected ArrayList<DGMType>              _children;
  protected Hashtable<String, DGMAttribute> _attributes;

  private static final Logger _logger = Logger.getLogger(BType.class.getName());

  public void setContent(String typeName, Map<String, String> attributes){
    BAttribute        attr;
    Iterator<String>  iter;
    String            attrName, attrDataType;

    _typeName = typeName;

    _attributes = new Hashtable<String, DGMAttribute>();
    if (attributes==null || attributes.isEmpty())
      return;
    iter = attributes.keySet().iterator();
    while(iter.hasNext()){
      attrName = (String) iter.next();
      attrDataType = (String) attributes.get(attrName);
      try{
        attr = new BAttribute(attrName, attrDataType);
        _attributes.put(attr.getName(), attr);
      }
      catch (DGMAttributeException ex){
        _logger.warning(
            "Attribute '"+attrName+
            "' from model element '"+typeName+
            "' is invalid: "+ex.getMessage()+
            " (Continue)"
            );
      }
    }
  }
  public void setParent(DGMType obj){
    _parent = obj;
  }

  public void addChild(DGMType obj){
    if (_children==null)
      _children = new ArrayList<DGMType>();
    _children.add(obj);
  }

  /**
   * Implementation of DGMType interface
   */
  public String getName(){
    return (_typeName);
  }

  /**
   * Implementation of DGMType interface
   */
  public DGMAttribute getAttribute(String name){
    return ((DGMAttribute) _attributes.get(name));
  }

  /**
   * Implementation of DGMType interface
   */
  public Enumeration<DGMAttribute> getAttributes(){
    return _attributes.elements();
  }

  /**
   * Implementation of DGMType interface
   */
  public DGMType getParent(){
    return (_parent);
  }

  /**
   * Implementation of DGMType interface
   */
  public Enumeration<DGMType> getChildren(){
    return new Enumeration<DGMType>() {
      int count = 0, elementCount = (_children!=null?_children.size():0);

      public boolean hasMoreElements() {
        return (count < elementCount);
      }

      public DGMType nextElement() {
        synchronized (BType.this) {
          if (count < elementCount) {
            return _children.get(count++);
          }
        }
        throw new NoSuchElementException("BType Children Enumeration");
      }
    };
  }

  /**
   * Implementation of DGMType interface
   */
  public void addAttribute(DGMAttribute attr){
    String name = attr.getName();
    if (_attributes.containsKey(name)){
      _attributes.remove(name);
    }
    _attributes.put(attr.getName(), attr);
  }

  public String toString(){
    return (getName());
  }

}
