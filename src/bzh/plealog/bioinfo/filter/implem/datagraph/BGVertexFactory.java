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

import org._3pq.jgrapht.VertexFactory;

import bzh.plealog.hge.api.hypergraph.HDGVertex;
import bzh.plealog.hge.api.hypergraph.HDGVertexFactory;

/**
 * This class implements HDGVertexFactory.
 * 
 * @author Patrick G. Durand
 */
public class BGVertexFactory implements VertexFactory, HDGVertexFactory{

    public Object createVertex(){
        return new BGVertex();
    }

    public HDGVertex createDGVertex(){
        return new BGVertex();
    }
}
