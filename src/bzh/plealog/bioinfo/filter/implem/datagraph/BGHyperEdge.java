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

import bzh.plealog.hge.api.datamodel.DGMHyperEdgeType;
import bzh.plealog.hge.api.datamodel.DGMType;
import bzh.plealog.hge.api.hypergraph.HDGHyperEdge;

/**
 * This class implements HDGHyperEdge. It is used to link BGVertex
 * objects.
 * 
 * @author Patrick G. Durand
 */
public class BGHyperEdge implements HDGHyperEdge{

    private Object  _data;
    private DGMType _type;

	/**
	 * Implementation of HDGHyperEdge interface.
	 */ 
    public DGMType getType(){
        return _type;
    }
    
	/**
	 * Implementation of HDGHyperEdge interface.
	 */ 
    public void setType(DGMType type){
        if (!(type instanceof DGMHyperEdgeType))
            throw new RuntimeException("not a DGMHyperEdgeType");
        _type = type;
    }        

	/**
	 * Implementation of HDGHyperEdge interface.
	 */ 
    public void setData(Object data){
        _data = data;
    }
    
	/**
	 * Implementation of HDGHyperEdge interface.
	 */ 
    public Object getData(){
        return _data;
    }
    
	/**
	 * Implementation of HDGHyperEdge interface.
	 */ 
    public Object getValue(String attribute){
        return (BGUtils.getValue(_data, _type.getName(), attribute));
    }

    public Object clone(){
    	BGHyperEdge edge;
        edge = new BGHyperEdge();
        edge.setType(this.getType());
        edge.setData(this.getData());
        return(edge);
    }

    public String toString(){
        return (getType().getName());
    }
}
