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
package bzh.plealog.bioinfo.filter.implem.datagraph;

import bzh.plealog.hge.api.datamodel.DGMType;
import bzh.plealog.hge.api.datamodel.DGMVertexType;
import bzh.plealog.hge.api.hypergraph.HDGVertex;

/**
 * This class implements HDGVertex. It is used to wrap Rich Search Result objects
 * (SROutput, SRIteration, SRHit and SRHsp) within the framework of an hyper-graph.
 * 
 * @author Patrick G. Durand
 */
public class BGVertex implements HDGVertex{

  private Object  _data;
  private DGMType _type;

  public BGVertex (){
  }

  public BGVertex (Object data){
    setData(data);
  }

  /**
   * Implementation of HDGVertex interface.
   */ 
  public DGMType getType(){
    return _type;
  }

  /**
   * Implementation of HDGVertex interface.
   */ 
  public void setType(DGMType type){
    if (!(type instanceof DGMVertexType))
      throw new RuntimeException("not a DGMVertexType");
    _type = type;
  }        

  /**
   * Implementation of HDGVertex interface.
   */ 
  public void setData(Object data){
    _data = data;
  }

  /**
   * Implementation of HDGVertex interface.
   */ 
  public Object getData(){
    return _data;
  }

  /**
   * Implementation of HDGVertex interface.
   */ 
  public Object getValue(String attribute){
    return (BGUtils.getValue(_data, _type.getName(), attribute));
  }

  public int hashCode(){
    if (_data!=null)
      return _data.hashCode();
    else
      return super.hashCode();
  }

  public boolean equals(Object obj){
    if (_data!=null){
      if (obj!=null && obj instanceof HDGVertex){
        return (_data.equals(((HDGVertex)obj).getData()));
      }
      else{
        return _data.equals(obj);
      }
    }
    else{
      return super.equals(obj);
    }
  }

  public String toString(){
    if (_data!=null)
      return _data.toString();
    else
      return super.toString();
  }

  public Object clone(){
    BGVertex vertex;

    vertex = new BGVertex();
    vertex.setType(this.getType());
    vertex.setData(this.getData());
    return (vertex);
  }        
}
