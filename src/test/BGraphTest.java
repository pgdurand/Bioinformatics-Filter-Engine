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
package test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import bzh.plealog.bioinfo.api.data.searchresult.SRHit;
import bzh.plealog.bioinfo.api.data.searchresult.SROutput;
import bzh.plealog.bioinfo.filter.implem.datagraph.BGraph;
import bzh.plealog.bioinfo.filter.implem.datamodel.BGDataModel;
import bzh.plealog.bioinfo.io.searchresult.SerializerSystemFactory;
import bzh.plealog.hge.api.hypergraph.HDGVertex;
import bzh.plealog.hge.api.query.HGEManager;
import bzh.plealog.hge.api.query.HGEQuery;
import bzh.plealog.hge.api.query.HGEResult;

/**
 * This class illustrates the use of the Filtering System using directly the
 * Hyper-Graph Explorer (HGE) API. See source code of TestFilterSystem to see how
 * to use the filtering system through Filters that hide the direct use of HGE.
 * 
 * @author Patrick G. Durand
 */
public class BGraphTest {
  private static SROutput       sro;
  private static BGraph         graph;
  private static BGDataModel    bgdm;
  private static String[] res1 = {
      "gi|56967161|pdb|1XMX|A",
      "gi|42543068|pdb|1NL0|L",
      "gi|83754991|pdb|2EVU|A",
      "gi|82407721|pdb|2A9M|L",
      "gi|13399662|pdb|1EVY|A"};
  private static HashSet<String> resultsQuery1 = new HashSet<>(Arrays.asList(res1));
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    //prepare log4j logger (required by Castor XML framework)
    BasicConfigurator.configure();
    
    //discard castor debug messages
    Logger.getRootLogger().setLevel(Level.INFO);
    
    //load an NCBI Blast data result
    sro = SerializerSystemFactory.getLoaderInstance(
        SerializerSystemFactory.NCBI_LOADER).load(
            new File("./data/test/blastp.xml"));
    
    //prepare the hyper-graph data model describing Rich Search Result
    bgdm = new BGDataModel();
    
    //prepare the hyper-graph representation of a Blast result
    graph = new BGraph(sro, bgdm);
    //the following can be used to dump the hyper-graph content
    //System.out.println(graph.test_getContent());
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }
  
  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  private static HGEQuery prepareQuery1(){

    // Create a new query
    HGEQuery query = HGEManager.newHQuery();

    // Using HGE query requires to model the query as a hyper-graph. To do so, we
    // use the Graph Query Language (part of HGE specs). 
    // To locate HSPs in a Blast result, we say that we want to locate pairs
    // "hit-hsp" as follows:

    query.addDeclaration("g1 as e1:v1-v2 in \""+BGDataModel.CONTAINS_HSP_EDGE_TYPE+"\"");   
    query.addDeclaration("v1 in \""+BGDataModel.SRHIT_VERTEX_TYPE+"\"");   
    query.addDeclaration("v2 in \""+BGDataModel.SRHSP_VERTEX_TYPE+"\"");

    // Like this, we have the topology of the query. It could be run "as is".

    // But let's add a constraint on the sequence alignment size.
    // attributes of vertex types is in BGDataModel class
    // here, 'v2' is an HSP
    query.addConstraint("v2.alignLen<=150");
    query.setReturnDistinct(true);

    //as a result of the query execution, we only want to see instances of
    // variable "v1" (HITs) and "v2" (HSPs) 
    query.setReturnVariables("v1,v2");
    return query;
  }

  // the following illustrates the query topology as it is used by the Blast Filter Tool
  private static HGEQuery prepareQuery2(){
    HGEQuery query = HGEManager.newHQuery();
    query.addDeclaration("g1 as e1:v1-v2 in \""+BGDataModel.CONTAINS_ITERATION_EDGE_TYPE+"\"");   
    query.addDeclaration("e2:v2-v3 in \""+BGDataModel.CONTAINS_HIT_EDGE_TYPE+"\"");   
    query.addDeclaration("e3:v3-v4 in \""+BGDataModel.CONTAINS_HSP_EDGE_TYPE+"\"");   
    query.addDeclaration("v1 in \""+BGDataModel.SROUTPUT_VERTEX_TYPE+"\"");   
    query.addDeclaration("v2 in \""+BGDataModel.SRITERATION_VERTEX_TYPE+"\"");
    query.addDeclaration("v3 in \""+BGDataModel.SRHIT_VERTEX_TYPE+"\"");
    query.addDeclaration("v4 in \""+BGDataModel.SRHSP_VERTEX_TYPE+"\"");
    
    // add some constraints here
    
    //define here what we want to retrieve in the result
    query.setReturnDistinct(false);
    query.setReturnVariables("v2,v3,v4");
    return query;
  }

  @Test
  public void testQuery1(){
    HGEQuery       query;
    Set<HGEResult> rSet;
    
    //setup a basic query using GQL
    query = prepareQuery1();
    
    //run the query
    rSet=query.execute(bgdm, graph);
    
    assertTrue(rSet.size()==5);
    if (rSet.size()!=0){
      Iterator<HGEResult> iter = rSet.iterator();
      while(iter.hasNext()){
        // see method prepareQuery1() to understand that get
        // "v1" value IS a SRHit instance. But we have to retrieve it
        // through HGEResult that serves (in this case) HDGVertex
        // instances. In turn, each HDGVErtex contains a SRHit object.
        HDGVertex vertex = (HDGVertex) iter.next().getValue("v1");
        SRHit hit = (SRHit) vertex.getData();
        assertTrue(resultsQuery1.contains(hit.getHitId()));
      }
    }
  }

  @Test
  public void testQuery2(){
    HGEQuery       query;
    Set<HGEResult> rSet;
    
    //setup a basic query using GQL
    query = prepareQuery2();
    
    //run the query
    rSet=query.execute(bgdm, graph);
    
    //basic display of the results
    assertTrue(rSet.size()==20);
  }
}
