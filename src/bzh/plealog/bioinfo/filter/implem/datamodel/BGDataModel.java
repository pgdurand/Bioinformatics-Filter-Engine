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

import bzh.plealog.hge.api.datamodel.DGMAttribute;
import bzh.plealog.hge.api.datamodel.DGMHyperEdgeType;
import bzh.plealog.hge.api.datamodel.DGMLink;
import bzh.plealog.hge.api.datamodel.DGMType;
import bzh.plealog.hge.api.datamodel.DGMVertexType;
import bzh.plealog.hge.api.hypergraph.HDBConnector;
import bzh.plealog.hge.api.hypergraph.HDGHyperEdge;
import bzh.plealog.hge.api.hypergraph.HDGVertex;

/**
 * This class implements DataGraphModel to model SROutput objects tree as a graph.
 * This constitutes the graph data model used to explore a SROutput with HGE system. 
 * 
 * @author Patrick G. Durand
 */
public class BGDataModel implements bzh.plealog.hge.api.datamodel.DataGraphModel, HDBConnector{
  protected Hashtable<String, DGMVertexType>     _vTypes;
  protected Hashtable<String, DGMHyperEdgeType>  _eTypes;

  public static final String SROUTPUT_VERTEX_TYPE = "SROutput";
  public static final String SRITERATION_VERTEX_TYPE = "SRIteration";
  public static final String SRHIT_VERTEX_TYPE = "SRHit";
  public static final String SRHSP_VERTEX_TYPE = "SRHSP";
  public static final String FEAT_VERTEX_TYPE = "Feature";
  public static final String QUALIFIER_VERTEX_TYPE = "Qualifier";

  public static final String CONTAINS_ITERATION_EDGE_TYPE = "containsIteration";
  public static final String CONTAINS_HIT_EDGE_TYPE = "containsHit";
  public static final String CONTAINS_HSP_EDGE_TYPE = "containsHsp";
  public static final String CONTAINS_FEAT_EDGE_TYPE = "containsFeat";
  public static final String CONTAINS_QUALIFIER_EDGE_TYPE = "containsQualifier";

  public static final String DATA_MODEL_NAME = "BlastDataGraphModel";

  public BGDataModel(){
    _vTypes = new Hashtable<String, DGMVertexType>();
    _eTypes = new Hashtable<String, DGMHyperEdgeType>();
    initialize();
  }

  private void initialize(){
    BVertexType    vType;
    BHyperEdgeType eType;
    Hashtable<String, String> attributes;

    vType = new BVertexType(SROUTPUT_VERTEX_TYPE, null);
    _vTypes.put(vType.getName(), vType);

    vType = new BVertexType(SRITERATION_VERTEX_TYPE, null);
    _vTypes.put(vType.getName(), vType);

    //  *** IMPORTANT NOTICE ***
    //when modifying this Map, also modify 
    //com.plealog.bioinfo.filter.implem.datagraph.BGUtils
    //com.plealog.bioinfo.filter.api.BAccessors
    attributes = new Hashtable<String, String>();
    attributes.put("accession", "string");
    attributes.put("countHsp", "integer");
    attributes.put("definition", "string");
    attributes.put("id", "string");
    attributes.put("length", "integer");
    attributes.put("numi", "integer");
    attributes.put("siType", "string");
    attributes.put("siTopo", "string");
    attributes.put("siDiv", "string");
    attributes.put("siOrg", "string");
    attributes.put("siTax", "string");
    attributes.put("siCDate", "integer");
    attributes.put("siUDate", "integer");
    attributes.put("qgCover","double");
    attributes.put("hgCover","double");

    vType = new BVertexType(SRHIT_VERTEX_TYPE, attributes);
    _vTypes.put(vType.getName(), vType);

    //  *** IMPORTANT NOTICE ***
    //when modifying this Map, also modify 
    //com.plealog.bioinfo.filter.implem.datagraph.BGUtils
    //com.plealog.bioinfo.filter.api.BAccessors
    attributes = new Hashtable<String, String>();
    attributes.put("nums","integer");
    attributes.put("bitScore","double");
    attributes.put("score","double");
    attributes.put("evalue","double");
    attributes.put("identity","double");
    attributes.put("positive","double");
    attributes.put("gaps","double");
    attributes.put("alignLen","integer");
    //attributes.put("density","integer");

    attributes.put("qFrom","integer");
    attributes.put("qTo","integer");
    attributes.put("qGaps","integer");
    attributes.put("qFrame","integer");
    attributes.put("qCover","double");
    attributes.put("qSequence","string");

    attributes.put("hFrom","integer");
    attributes.put("hTo","integer");
    attributes.put("hGaps","integer");
    attributes.put("hFrame","integer");
    attributes.put("hCover","double");
    attributes.put("hSequence","string");

    vType = new BVertexType(SRHSP_VERTEX_TYPE, attributes);
    _vTypes.put(vType.getName(), vType);

    //  *** IMPORTANT NOTICE ***
    //when modifying this Map, also modify 
    //com.plealog.bioinfo.filter.implem.datagraph.BGUtils
    //com.plealog.bioinfo.filter.api.BAccessors
    attributes = new Hashtable<String, String>();
    attributes.put("key", "string");
    attributes.put("from", "integer");
    attributes.put("to", "integer");

    vType = new BVertexType(FEAT_VERTEX_TYPE, attributes);
    _vTypes.put(vType.getName(), vType);

    //  *** IMPORTANT NOTICE ***
    //when modifying this Map, also modify 
    //com.plealog.bioinfo.filter.implem.datagraph.BGUtils
    //com.plealog.bioinfo.filter.api.BAccessors
    attributes = new Hashtable<String, String>();
    attributes.put("qualName", "string");
    attributes.put("qualValue", "string");

    vType = new BVertexType(QUALIFIER_VERTEX_TYPE, attributes);
    _vTypes.put(vType.getName(), vType);

    eType = new BHyperEdgeType(
        CONTAINS_ITERATION_EDGE_TYPE, 
        (BVertexType) _vTypes.get(SROUTPUT_VERTEX_TYPE), 
        (BVertexType) _vTypes.get(SRITERATION_VERTEX_TYPE), 
        null);
    _eTypes.put(eType.getName(), eType);
    eType = new BHyperEdgeType(
        CONTAINS_HIT_EDGE_TYPE, 
        (BVertexType) _vTypes.get(SRITERATION_VERTEX_TYPE), 
        (BVertexType) _vTypes.get(SRHIT_VERTEX_TYPE), 
        null);
    _eTypes.put(eType.getName(), eType);
    eType = new BHyperEdgeType(
        CONTAINS_HSP_EDGE_TYPE, 
        (BVertexType) _vTypes.get(SRHIT_VERTEX_TYPE), 
        (BVertexType) _vTypes.get(SRHSP_VERTEX_TYPE), 
        null);
    _eTypes.put(eType.getName(), eType);
    eType = new BHyperEdgeType(
        CONTAINS_FEAT_EDGE_TYPE, 
        (BVertexType) _vTypes.get(SRHSP_VERTEX_TYPE), 
        (BVertexType) _vTypes.get(FEAT_VERTEX_TYPE), 
        null);
    _eTypes.put(eType.getName(), eType);
    eType = new BHyperEdgeType(
        CONTAINS_QUALIFIER_EDGE_TYPE, 
        (BVertexType) _vTypes.get(FEAT_VERTEX_TYPE), 
        (BVertexType) _vTypes.get(QUALIFIER_VERTEX_TYPE), 
        null);
    _eTypes.put(eType.getName(), eType);
  }

  /**
   * Implementation of DataGraphModel interface.
   */ 
  public Enumeration<DGMHyperEdgeType> getHyperEdgeTypes(){
    return (_eTypes.elements());
  }
  /**
   * Implementation of DataGraphModel interface.
   */ 
  public Enumeration<DGMVertexType> getVertexTypes(){
    return (_vTypes.elements());
  }
  /**
   * Implementation of DataGraphModel interface.
   */ 
  public Enumeration<DGMHyperEdgeType> getRootHyperEdgeTypes(){
    return (_eTypes.elements());
  }
  /**
   * Implementation of DataGraphModel interface.
   */ 
  public Enumeration<DGMVertexType> getRootVertexTypes(){
    return (_vTypes.elements());
  }
  /**
   * Implementation of HDBConnection interface. This implementation always
   * returns null.
   */ 
  public Enumeration<HDGVertex> getVertices(){
    return null;
  }
  /**
   * Implementation of HDBConnection interface. This implementation always
   * returns null.
   */ 
  public Enumeration<HDGHyperEdge> getHyperEdges(){
    return null;
  }
  /**
   * Implementation of DGMAttribute interface.
   */ 
  public DGMType getType(String name){
    DGMType ds;

    ds = getHyperEdgeType(name);
    if (ds==null)
      ds=getVertexType(name);
    return (ds);
  }
  /**
   * Implementation of DGMAttribute interface.
   */ 
  public DGMHyperEdgeType getHyperEdgeType(String name){
    return ((DGMHyperEdgeType) _eTypes.get(name));
  }
  /**
   * Implementation of DGMAttribute interface.
   */ 
  public DGMVertexType getVertexType(String name){
    return ((DGMVertexType) _vTypes.get(name));
  }
  /**
   * Implementation of DGMAttribute interface.
   */ 
  public String getName(){
    return (DATA_MODEL_NAME);
  }
  /**
   * Utility method that returns a String representation of the vertices contained
   * in this data model.
   */
  public String getVertexTypesHierarchy(){
    Enumeration<DGMVertexType>  enum1;
    Enumeration<DGMType>        enum2;
    Enumeration<DGMAttribute>   enum3;
    DGMVertexType               ds;
    StringBuffer                szBuf = new StringBuffer();

    enum1 = this.getVertexTypes();
    while(enum1.hasMoreElements()){
      ds = enum1.nextElement();
      szBuf.append(ds.getName());
      szBuf.append(", Parent: ");
      szBuf.append((ds.getParent()!=null?ds.getParent().getName():"-"));

      enum2 = ds.getChildren();
      szBuf.append(", Children: ");
      if (!enum2.hasMoreElements()){
        szBuf.append("-");
      }
      else{
        while(enum2.hasMoreElements()){
          szBuf.append(enum2.nextElement().toString());
          if (enum2.hasMoreElements())
            szBuf.append(", ");
        }
      }
      szBuf.append(".\n");
      enum3 = ds.getAttributes();
      while(enum3.hasMoreElements()){
        szBuf.append("  - ");
        szBuf.append(enum3.nextElement().toString());
        szBuf.append("\n");
      }
    }
    return(szBuf.toString());
  }

  /**
   * Utility method that returns a String representation of the hyper edges contained
   * in this data model.
   */
  public String getEdgeTypesHierarchy(){
    Enumeration<DGMHyperEdgeType>  enum1;
    Enumeration<DGMType>           enum2;
    Enumeration<DGMAttribute>      enum3;
    Enumeration<DGMLink>           enum4;
    DGMHyperEdgeType               ds;
    DGMLink                        link;
    StringBuffer                   szBuf = new StringBuffer();

    enum1 = this.getHyperEdgeTypes();
    while(enum1.hasMoreElements()){
      ds = enum1.nextElement();
      szBuf.append(ds.getName());
      szBuf.append(", Parent: ");
      szBuf.append((ds.getParent()!=null?ds.getParent().getName():"-"));

      enum2 = ds.getChildren();
      szBuf.append(", Children: ");
      if (!enum2.hasMoreElements()){
        szBuf.append("-");
      }
      else{
        while(enum2.hasMoreElements()){
          szBuf.append(enum2.nextElement().toString());
          if (enum2.hasMoreElements())
            szBuf.append(", ");
        }
      }
      szBuf.append(".\n");
      enum3 = ds.getAttributes();
      while(enum3.hasMoreElements()){
        szBuf.append("  - ");
        szBuf.append(enum3.nextElement().toString());
        szBuf.append("\n");
      }
      enum4 = ds.getLinks();
      while(enum4.hasMoreElements()){
        link = enum4.nextElement();
        szBuf.append("  * ");
        szBuf.append(link.getVertexType().toString());
        szBuf.append(" (");
        szBuf.append(link.getName());
        szBuf.append(": ");
        szBuf.append(DGMLink.ORIENTATION_REPR[link.getOrientation()]);
        szBuf.append(")\n");
      }
    }
    return(szBuf.toString());
  }

  /**
   * Returns a String representation of both vertices and hyper edges contained
   * in this data model.
   */
  public String toString(){
    return ("Vertex types:\n"+getVertexTypesHierarchy()+"Edge types:\n"+getEdgeTypesHierarchy());
  }
}
