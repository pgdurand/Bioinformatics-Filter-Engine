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

import bzh.plealog.hge.api.datamodel.DGMLink;
import bzh.plealog.hge.api.hypergraph.HDGHyperEdge;
import bzh.plealog.hge.api.hypergraph.HDGLink;
import bzh.plealog.hge.api.hypergraph.HDGVertex;

/**
 * This class implements HDGLink.
 * 
 * @author Patrick G. Durand
 */
public class BGLink implements HDGLink {

    private BGVertex    _vertex;
    private BGHyperEdge _edge;
    
    public BGLink(BGVertex v, BGHyperEdge e){
        _vertex=v;
        _edge=e;
    }
	/**
	 * Implementation of HDGLink interface.
	 * 
	 * @see com.plealog.hge.api.hypergraph.HDGLink#getVertex()
	 */
	public HDGVertex getVertex() {
		return _vertex;
	}

	/**
	 * Implementation of HDGLink interface.
	 * 
	 * @see com.plealog.hge.api.hypergraph.HDGLink#getHyperEdge()
	 */
	public HDGHyperEdge getHyperEdge() {
		return _edge;
	}

	/**
	 * Implementation of HDGLink interface.
	 * 
	 * @see com.plealog.hge.api.hypergraph.HDGLink#getOrientation()
	 */
	public int getOrientation() {
		return DGMLink.BOTH;
	}

}
