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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org._3pq.jgrapht.Edge;
import org._3pq.jgrapht.alg.DijkstraShortestPath;
import org._3pq.jgrapht.graph.Pseudograph;

import bzh.plealog.bioinfo.api.data.feature.Feature;
import bzh.plealog.bioinfo.api.data.feature.FeatureTable;
import bzh.plealog.bioinfo.api.data.feature.Qualifier;
import bzh.plealog.bioinfo.api.data.searchresult.SRHit;
import bzh.plealog.bioinfo.api.data.searchresult.SRHsp;
import bzh.plealog.bioinfo.api.data.searchresult.SRIteration;
import bzh.plealog.bioinfo.api.data.searchresult.SROutput;
import bzh.plealog.bioinfo.filter.implem.datamodel.BGDataModel;
import bzh.plealog.hge.api.datamodel.DataGraphModel;
import bzh.plealog.hge.api.hypergraph.HDBConnector;
import bzh.plealog.hge.api.hypergraph.HDGHyperEdge;
import bzh.plealog.hge.api.hypergraph.HDGLink;
import bzh.plealog.hge.api.hypergraph.HDGVertex;
import bzh.plealog.hge.api.hypergraph.HDataGraph;

/**
 * This class implements HDataGraph. It is used to model a Rich Search Result object
 * (SROutput) as an hyper-graph.
 * 
 * @author Patrick G. Durand
 */
public class BGraph extends Pseudograph implements HDataGraph{

  private static final long serialVersionUID = 1721078034851267531L;

  public BGraph(){
    super();
  }

  /**
   * Constructor of a BGraph.
   * 
   * @param ft a FeatureTable from which to create the graph. 
   * @param dgm the data model describing the Rich Search Result as a graph
   */
  public BGraph(FeatureTable ft, DataGraphModel dgm){
    BGHyperEdge  edge;
    BGVertex     vertex, vFT, vFeat, vQual;
    
    if (ft.features()==0)
      return;

    // create the root node of the graph
    vertex = new BGVertex();
    vertex.setData(ft);
    vertex.setType(dgm.getVertexType(BGDataModel.FTABLE_VERTEX_TYPE));
    vFT = vertex;
    this.addVertex(vertex);

    for(Feature feat : Collections.list(ft.enumFeatures())){
      //creates vertex for Feature
      vertex = new BGVertex();
      vertex.setData(feat);
      vertex.setType(dgm.getVertexType(BGDataModel.FEAT_VERTEX_TYPE));
      vFeat = vertex;
      this.addVertex(vertex);
      //creates HyperEdge for relation
      edge = new BGHyperEdge();
      edge.setType(dgm.getHyperEdgeType(BGDataModel.HAS_FEAT_EDGE_TYPE));
      this.addVertex(edge);
      //link BHsp/Feature vertices through hyper edge
      this.addEdge(vFT, edge);
      this.addEdge(vFeat, edge);
      //qualifiers ?
      for(Qualifier qualifier : Collections.list(feat.enumQualifiers())){
        //creates vertex for Qualifier
        vertex = new BGVertex();
        vertex.setData(qualifier);
        vertex.setType(dgm.getVertexType(BGDataModel.QUALIFIER_VERTEX_TYPE));
        vQual = vertex;
        this.addVertex(vertex);
        //creates HyperEdge for relation
        edge = new BGHyperEdge();
        edge.setType(dgm.getHyperEdgeType(BGDataModel.CONTAINS_QUALIFIER_EDGE_TYPE));
        this.addVertex(edge);
        //link Feature/Qualifier vertices through hyper edge
        this.addEdge(vFeat, edge);
        this.addEdge(vQual, edge);
      }
    }
  }
  
  /**
   * Constructor of a BGraph.
   * 
   * @param bo a Rich Search Result from which to create the graph
   * @param dgm the data model describing the Rich Search Result as a graph
   */
  public BGraph(SROutput bo, DataGraphModel dgm){
    super();
    BGVertex     vertex, vBO, vIter, vHit, vHsp, vFeat, vQual;
    BGHyperEdge  edge;
    SRIteration   bi;
    SRHit         hit;
    SRHsp         hsp;
    FeatureTable fTable;
    Feature      feat;
    Qualifier    qualifier;
    Enumeration<Feature>   feats;
    Enumeration<Qualifier> qualifiers;
    int          i, j, k, size, size2, size3;

    if (bo.isEmpty())
      return;

    vertex = new BGVertex();
    vertex.setData(bo);
    vertex.setType(dgm.getVertexType(BGDataModel.SROUTPUT_VERTEX_TYPE));
    vBO = vertex;
    this.addVertex(vertex);

    size = bo.countIteration();
    for(i=0;i<size;i++){
      bi = bo.getIteration(i);
      //creates vertex for BIteration
      vertex = new BGVertex();
          vertex.setData(bi);
          vertex.setType(dgm.getVertexType(BGDataModel.SRITERATION_VERTEX_TYPE));
          vIter = vertex;
          this.addVertex(vertex);
          //creates HyperEdge for relation
          edge = new BGHyperEdge();
          edge.setType(dgm.getHyperEdgeType(BGDataModel.CONTAINS_ITERATION_EDGE_TYPE));
          this.addVertex(edge);
          //link BOutput/BIteration vertices through hyper edge
          this.addEdge(vBO, edge);
          this.addEdge(vIter, edge);
          size2 = bi.countHit();
          for(j=0;j<size2;j++){
            hit = bi.getHit(j);
            //creates vertex for BHit
            vertex = new BGVertex();
            vertex.setData(hit);
            vertex.setType(dgm.getVertexType(BGDataModel.SRHIT_VERTEX_TYPE));
            vHit = vertex;
            this.addVertex(vertex);
            //creates HyperEdge for relation
            edge = new BGHyperEdge();
            edge.setType(dgm.getHyperEdgeType(BGDataModel.CONTAINS_HIT_EDGE_TYPE));
            this.addVertex(edge);
            //link BIteration/BHit vertices through hyper edge
            this.addEdge(vIter, edge);
            this.addEdge(vHit, edge);
            size3 = hit.countHsp();
            for(k=0;k<size3;k++){
              hsp = hit.getHsp(k);
              //creates vertex for BHsp
              vertex = new BGVertex();
              vertex.setData(hsp);
              vertex.setType(dgm.getVertexType(BGDataModel.SRHSP_VERTEX_TYPE));
              vHsp = vertex;
              this.addVertex(vertex);
              //creates HyperEdge for relation
              edge = new BGHyperEdge();
              edge.setType(dgm.getHyperEdgeType(BGDataModel.CONTAINS_HSP_EDGE_TYPE));
              this.addVertex(edge);
              //link BHit/BHsp vertices through hyper edge
              this.addEdge(vHit, edge);
              this.addEdge(vHsp, edge);
              fTable = hsp.getFeatures();
              if (fTable==null)
                continue;
              feats = fTable.enumFeatures();
              while(feats.hasMoreElements()){
                feat = (Feature) feats.nextElement();
                //creates vertex for Feature
                vertex = new BGVertex();
                vertex.setData(feat);
                vertex.setType(dgm.getVertexType(BGDataModel.FEAT_VERTEX_TYPE));
                vFeat = vertex;
                this.addVertex(vertex);
                //creates HyperEdge for relation
                edge = new BGHyperEdge();
                edge.setType(dgm.getHyperEdgeType(BGDataModel.CONTAINS_FEAT_EDGE_TYPE));
                this.addVertex(edge);
                //link BHsp/Feature vertices through hyper edge
                this.addEdge(vHsp, edge);
                this.addEdge(vFeat, edge);
                //qualifiers ?
                qualifiers = feat.enumQualifiers();
                while(qualifiers.hasMoreElements()){
                  qualifier = (Qualifier) qualifiers.nextElement();
                  //creates vertex for Qualifier
                  vertex = new BGVertex();
                  vertex.setData(qualifier);
                  vertex.setType(dgm.getVertexType(BGDataModel.QUALIFIER_VERTEX_TYPE));
                  vQual = vertex;
                  this.addVertex(vertex);
                  //creates HyperEdge for relation
                  edge = new BGHyperEdge();
                  edge.setType(dgm.getHyperEdgeType(BGDataModel.CONTAINS_QUALIFIER_EDGE_TYPE));
                  this.addVertex(edge);
                  //link Feature/Qualifier vertices through hyper edge
                  this.addEdge(vFeat, edge);
                  this.addEdge(vQual, edge);
                }
              }
            }
          }
    }
  }
  public void create(HDBConnector conn, DataGraphModel dgm){

  }

  public boolean containsEdge(HDGHyperEdge edge){
    return (super.containsVertex(edge));
  }

  public boolean containsVertex(HDGVertex vertex){
    return (super.containsVertex(vertex));
  }

  public Enumeration<HDGHyperEdge> edges(){
    return new Enumeration<HDGHyperEdge>() {
      Iterator<?>  iter;
      boolean      bFirst = true;
      Object       edge = null;

      private void initialize(){
        iter = BGraph.this.vertexSet().iterator();
        bFirst = false;
      }

      public synchronized boolean hasMoreElements() {
        if (bFirst)
          initialize();
        if (edge!=null)
          return true;
        while(iter.hasNext()){
          edge = iter.next();
          if (edge instanceof HDGHyperEdge)
            return true;
        }
        edge = null;
        return (false);
      }

      public synchronized HDGHyperEdge nextElement() {
        HDGHyperEdge o = (HDGHyperEdge) edge;
        edge = null;
        return o;
      }
    };
  }

  public Enumeration<HDGVertex> vertices(){
    return new Enumeration<HDGVertex>() {
      Iterator<?>  iter;
      boolean   bFirst = true;
      Object    vertex = null;

      private void initialize(){
        iter = BGraph.this.vertexSet().iterator();
        bFirst = false;
      }

      public synchronized boolean hasMoreElements() {
        if (bFirst)
          initialize();
        if (vertex!=null)
          return true;
        while(iter.hasNext()){
          vertex = iter.next();
          if (vertex instanceof HDGVertex)
            return true;
        }
        vertex = null;
        return (false);
      }

      public synchronized HDGVertex nextElement() {
        HDGVertex o = (HDGVertex) vertex;
        vertex = null;
        return o;
      }
    };
  }

  public Enumeration<HDGHyperEdge> edges(final HDGVertex vertex){
    return new Enumeration<HDGHyperEdge>() {
      Iterator<?>             iter;
      boolean                 bFirst = true;
      HashSet<HDGHyperEdge>   set;

      private void initialize(){
        iter = BGraph.this.edgesOf(vertex).iterator();
        //the following code handles the case where a vertex is
        //involved several times in a single edge (some kind of loop) 
        //ex: if vertex 'a' is involved twice in an edge ('a,a,c' for
            //example), this code returns that edge only one times.
            set = new HashSet<HDGHyperEdge>();
            while(iter.hasNext()){
              set.add((HDGHyperEdge) ((Edge)iter.next()).getTarget());
            }
            iter = set.iterator();
            bFirst = false;
      }

      public synchronized boolean hasMoreElements() {
        if (bFirst)
          initialize();
        return (iter.hasNext());
      }

      public synchronized HDGHyperEdge nextElement() {
        if (bFirst)
          initialize();
        return ((HDGHyperEdge) iter.next());
      }
    };
  }

  public int degree(HDGVertex vertex){
    Iterator<?>            iter;
    HashSet<HDGHyperEdge>  set;

    iter = BGraph.this.edgesOf(vertex).iterator();
    //the following code handles the case where a vertex is
    //involved several times in a single edge (some kind of loop) 
    //ex: if vertex 'a' is involved twice in an edge ('a,a,c' for
    //example), this code returns 1.
    set = new HashSet<HDGHyperEdge>();
    while(iter.hasNext()){
      set.add((HDGHyperEdge) iter.next());
    }
    return (set.size());
  }

  public Enumeration<HDGVertex> vertices(final HDGHyperEdge edge){
    return new Enumeration<HDGVertex>() {
      Iterator<?>  iter;
      boolean      bFirst = true;

      private void initialize(){
        iter = BGraph.this.edgesOf(edge).iterator();
        bFirst = false;
      }

      public synchronized boolean hasMoreElements() {
        if (bFirst)
          initialize();
        return (iter.hasNext());
      }

      public synchronized HDGVertex nextElement() {
        if (bFirst)
          initialize();
        return (HDGVertex) (((Edge)iter.next()).getSource());
      }
    };
  }

  public Enumeration<HDGVertex> oppositeVertices(final HDGHyperEdge edge, final HDGVertex vertex, boolean obeyOri){
    return new Enumeration<HDGVertex>() {
      Iterator<?>          iter;
      boolean              bFirst = true;
      ArrayList<HDGVertex> al;

      private void initialize(){
        iter = BGraph.this.edgesOf(edge).iterator();
        //the following code handles the case where an edge connects
        //more than once a single node (some kind of loop) 
        //ex: if edge connects 'a,a,c' and if vertex is 'a', this
        //code will get 'a,c' as opposite vertices of 'a'
        al = new ArrayList<HDGVertex>();
        Object obj;
        while(iter.hasNext()){
          obj = ((Edge)iter.next()).getSource();
          al.add((HDGVertex) obj);
        }
        al.remove(vertex);
        iter = al.iterator();
        bFirst = false;
      }

      public synchronized boolean hasMoreElements() {
        if (bFirst)
          initialize();
        return (iter.hasNext());
      }

      public synchronized HDGVertex nextElement() {
        if (bFirst)
          initialize();
        return ((HDGVertex) iter.next());
      }
    };
  }

  /**Returns an Enumeration of Lists*/
  public List<HDGLink> findPath(final HDGVertex from, final HDGVertex to){
    int                i;
    ArrayList<HDGLink> newPath = null;
    List<?>            lclPath;

    lclPath = DijkstraShortestPath.findPathBetween(BGraph.this, from, to);
    if (lclPath.size()>0){
      //transfrom the path from a list of real Edge (JGraphT) to a list
      //of HDBLink
      newPath = new ArrayList<HDGLink>();
      for(i=0;i<lclPath.size();i++){
        newPath.add(new BGLink(
            (BGVertex)((Edge)lclPath.get(i)).getSource(),
            (BGHyperEdge)((Edge)lclPath.get(i)).getTarget()
            )
            );
      }
    }
    return newPath;
  }

  /*public String test_findPath(){
		StringBuffer szBuf = new StringBuffer();
		Enumeration  enum1, enum2;
		HDGVertex    vertex, from=null, to=null;
		List         path;
		int          i;

		enum1 = this.vertices();
		while(enum1.hasMoreElements()){
			vertex = (HDGVertex) enum1.nextElement();
			if (vertex.toString().equals("ppB")){
				from = vertex;    		
				break;
			}
		}
		enum1 = this.vertices();
		while(enum1.hasMoreElements()){
			vertex = (HDGVertex) enum1.nextElement();
			if (vertex.toString().equals("ppD")){
				to = vertex;
				break;
			}    		
		}
		szBuf.append("Trying to find a path between "+from.toString()+" and "+to.toString()+"\n");
		enum1 = this.findPath(from, to);
		if (enum1.hasMoreElements()){
			path = (List) enum1.nextElement();
			Object elem;
			HDGHyperEdge edge;
			if (path.size()>0){
				szBuf.append("  got one: ");
				for(i=0;i<path.size();i++){
					elem = path.get(i);
					if (elem instanceof HDGVertex){
						szBuf.append(elem.toString());
					}
					else{
						edge = (HDGHyperEdge) elem;
						szBuf.append("[");
						enum2 = this.vertices(edge);
						while(enum2.hasMoreElements()){
							vertex = (HDGVertex) enum2.nextElement();
							szBuf.append(vertex.toString());
							if (enum2.hasMoreElements())    		
								szBuf.append(",");
						}
						szBuf.append("]");    		
					}
					if ((i+1)<path.size()){
						szBuf.append(" , ");
					}
				}
			}
			else{
				szBuf.append("  No path found between "+from.toString()+" and "+to.toString());
			}
		}
		else{
			szBuf.append("  No path found between "+from.toString()+" and "+to.toString());
		}
		return (szBuf.toString());
    }*/

  private String getStrRepr(Object obj){
    if (obj==null){
      return "?";
    }
    if (obj instanceof SROutput){
      return "Bo";
    }
    else if (obj instanceof SRIteration){
      return "Bi";
    }
    else if (obj instanceof SRHit){
      return ("Hit-"+((SRHit)obj).getHitNum());
    }
    else if (obj instanceof SRHsp){
      return ("Hsp-"+((SRHsp)obj).getHspNum());
    }
    else
      return obj.toString();
  }
  public String test_getContent(){
    StringBuffer szBuf = new StringBuffer();
    Enumeration<HDGVertex>    enum1, enum3;
    Enumeration<HDGHyperEdge> enum2;
    HDGVertex    vertex;
    HDGHyperEdge      edge;

    szBuf.append("Vertices:\n");
    enum1 = this.vertices();
    szBuf.append("  ");
    while(enum1.hasMoreElements()){
      vertex = (HDGVertex) enum1.nextElement();
      szBuf.append(getStrRepr(vertex.getData()));
      if (enum1.hasMoreElements())
        szBuf.append(", ");    		
    }
    szBuf.append("\n");    		

    szBuf.append("Vertices:\n");
    enum1 = this.vertices();
    while(enum1.hasMoreElements()){
      vertex = enum1.nextElement();
      szBuf.append("  "+getStrRepr(vertex.getData()));
      szBuf.append(" connected to: ");
      enum2 = this.edges(vertex);
      while(enum2.hasMoreElements()){
        edge = enum2.nextElement();
        szBuf.append("[");
        enum3 = this.vertices(edge);
        while(enum3.hasMoreElements()){
          vertex = (HDGVertex) enum3.nextElement();
          szBuf.append(getStrRepr(vertex.getData()));
          if (enum3.hasMoreElements())    		
            szBuf.append(",");
        }
        szBuf.append("]");    		
        if (enum2.hasMoreElements())    		
          szBuf.append(" , ");
      }
      szBuf.append("\n");    		
    }
    szBuf.append("HyperEdges:\n");
    enum2 = this.edges();
    szBuf.append("  ");
    while(enum2.hasMoreElements()){
      edge = enum2.nextElement();
      szBuf.append("[");
      enum3 = this.vertices(edge);
      while(enum3.hasMoreElements()){
        vertex = enum3.nextElement();
        szBuf.append(getStrRepr(vertex.getData()));
        if (enum3.hasMoreElements())    		
          szBuf.append(",");
      }
      szBuf.append("]");    		
      if (enum2.hasMoreElements())    		
        szBuf.append(" , ");
    }
    szBuf.append("\n");    		
    szBuf.append("HyperEdges:\n");
    enum2 = this.edges();
    while(enum2.hasMoreElements()){
      edge = enum2.nextElement();
      szBuf.append("  [");
      enum3 = this.vertices(edge);
      while(enum3.hasMoreElements()){
        vertex = (HDGVertex) enum3.nextElement();
        szBuf.append(getStrRepr(vertex.getData()));
        if (enum3.hasMoreElements())    		
          szBuf.append(",");
      }
      szBuf.append("]\n");    		
      enum3 = this.vertices(edge);
      while(enum3.hasMoreElements()){
        vertex = (HDGVertex) enum3.nextElement();
        szBuf.append("    opposite vertices of "+getStrRepr(vertex.getData())+": ");    		
        enum1 = this.oppositeVertices(edge, vertex, false);
        while(enum1.hasMoreElements()){
          vertex = (HDGVertex) enum1.nextElement();
          szBuf.append(getStrRepr(vertex.getData()));
          if (enum1.hasMoreElements())    		
            szBuf.append(", ");
        }
        szBuf.append("\n");    		
      }
    }
    return (szBuf.toString());
  }

  public Enumeration<HDGLink> links(HDGHyperEdge edge){
    return new Enumeration<HDGLink>() {
      public boolean hasMoreElements() {
        return (false);
      }

      public HDGLink nextElement() {
        return (null);
      }
    };
  }

}

