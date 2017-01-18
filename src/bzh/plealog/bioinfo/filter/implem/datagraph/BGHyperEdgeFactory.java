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

import java.util.List;

import bzh.plealog.hge.api.hypergraph.HDGHyperEdge;
import bzh.plealog.hge.api.hypergraph.HDGHyperEdgeFactory;
import bzh.plealog.hge.api.hypergraph.HDGVertex;

/**
 * This class implements HDGHyperEdgeFactory.
 * 
 * @author Patrick G. Durand
 */
public class BGHyperEdgeFactory implements HDGHyperEdgeFactory{
	/**
	 * Implementation of HDGHyperEdgeFactory interface.
	 * 
	 * @see bzh.plealog.hge.api.hypergraph.HDGHyperEdgeFactory#createHDGEdge(List)
	 */ 
    public HDGHyperEdge createHDGEdge(List<HDGVertex> vertices){
        return new BGHyperEdge();
    }
    
}
