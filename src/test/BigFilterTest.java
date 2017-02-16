/* Copyright (C) 2006-2017 Patrick G. Durand
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import bzh.plealog.bioinfo.api.core.config.CoreSystemConfigurator;
import bzh.plealog.bioinfo.api.data.feature.Feature;
import bzh.plealog.bioinfo.api.data.feature.FeatureTable;
import bzh.plealog.bioinfo.api.data.searchresult.SRHsp;
import bzh.plealog.bioinfo.api.data.searchresult.SROutput;
import bzh.plealog.bioinfo.api.data.searchresult.io.SRLoader;
import bzh.plealog.bioinfo.api.filter.BFilter;
import bzh.plealog.bioinfo.api.filter.config.FilterSystemConfigurator;
import bzh.plealog.bioinfo.filter.implem.datagraph.BGraph;
import bzh.plealog.bioinfo.filter.implem.datamodel.BGDataModel;
import bzh.plealog.bioinfo.io.searchresult.SerializerSystemFactory;
import bzh.plealog.hge.api.query.HGEManager;
import bzh.plealog.hge.api.query.HGEQuery;
import bzh.plealog.hge.api.query.HGEResult;

/**
 * Unit tests of the Filtering System.
 * 
 * @author Patrick G. Durand
 */
public class BigFilterTest {
  private static File blastFile_simple;
  private static File tmpFile;
  private static File varFeatureFilterFile;
  private static File varQualifierFilterFile;
  private static SRLoader ncbiBlastLoader;
  private static HashSet<String> hitIDs;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    // init logger system (used by castor)
    BasicConfigurator.configure();
    // discard castor debug messages
    Logger.getRootLogger().setLevel(Level.INFO);

    // init API factories
    CoreSystemConfigurator.initializeSystem();
    FilterSystemConfigurator.initializeSystem();
    // sample NCBI legacy Blast result
    blastFile_simple = new File("data/test/blastp_simple.xml");
    // setup a temp file (will be deleted in tearDownAfterClass())
    tmpFile = File.createTempFile("blastTest", ".xml");
    // setup a file filter
    varFeatureFilterFile = new File("data/test/filter5.xml");
    varQualifierFilterFile = new File("data/test/filter4.xml");
    // setup an NCBI Blast Loader (XML)
    ncbiBlastLoader = SerializerSystemFactory.getLoaderInstance(SerializerSystemFactory.NCBI_LOADER);
    // control values
    hitIDs = new HashSet<String>();
    hitIDs.add("1FQY-A");
    hitIDs.add("1YMG-A");
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    tmpFile.delete();
  }

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  private FeatureTable makeFakeFeatureTable2() {
    FeatureTable ft;
    Feature feat;

    ft = CoreSystemConfigurator.getFeatureTableFactory().getFTInstance();
    // Variant feature
    feat = CoreSystemConfigurator.getFeatureTableFactory().getFInstance();
    feat.setKey("Variant");
    feat.setFrom(25);
    feat.setTo(25);
    feat.setStrand(Feature.PLUS_STRAND);
    feat.addQualifier("Consequence", "missense_variant");
    feat.addQualifier("Clinical", "uncertain significance; likely pathogenic");
    ft.addFeature(feat);

    return ft;
  }

  private FeatureTable makeFakeFeatureTable3() {
    FeatureTable ft;
    Feature feat;

    ft = CoreSystemConfigurator.getFeatureTableFactory().getFTInstance();
    // Variant feature
    feat = CoreSystemConfigurator.getFeatureTableFactory().getFInstance();
    feat.setKey("Variant");
    feat.setFrom(25);
    feat.setTo(25);
    feat.setStrand(Feature.PLUS_STRAND);
    feat.addQualifier("Consequence", "unknown");
    feat.addQualifier("Clinical", "begnin");
    ft.addFeature(feat);

    return ft;
  }

  private static HGEQuery prepareQuery1(){
    // We define an Hyper-Graph-Explorer query using Hyper-Graph Query (HQL)
    // language. This language is defined here:
    // https://github.com/pgdurand/Hyper-Graph-Explorer/tree/master/doc
    HGEQuery query = HGEManager.newHQuery();
    // here, our query in a simple graph made of two vertices:
    // Feature->Qualifiers.
    // In plain text, query is defined as follows:
    // g1 as e2:v2-v3 in "containsQualifier"
    // v2 in "Feature"
    // v3 in "Qualifier"
    query.addDeclaration("g1 as e2:v2-v3 in \""+BGDataModel.CONTAINS_QUALIFIER_EDGE_TYPE+"\"");   
    query.addDeclaration("v2 in \""+BGDataModel.FEAT_VERTEX_TYPE+"\"");
    query.addDeclaration("v3 in \""+BGDataModel.QUALIFIER_VERTEX_TYPE+"\"");

    // Like this, we have the topology of the query. It could be run "as is".

    // But let's add a constraint on the feature type.
    // Attributes of vertex types is declared in BGDataModel class
    // here, 'v2' is a Feature
    query.addConstraint("v2.key==\"Variant\"");

    //as a result of the query execution, we only want to see instances of
    // variable "v2" (Features) and "v3" (Qualifiers) 
    query.setReturnVariables("v2,v3");
    return query;
  }

  private static HGEQuery prepareQuery2(){
    // same as prepareQuery1(), but with different constraints
    HGEQuery query = HGEManager.newHQuery();
    query.addDeclaration("g1 as e2:v2-v3 in \""+BGDataModel.CONTAINS_QUALIFIER_EDGE_TYPE+"\"");   
    query.addDeclaration("v2 in \""+BGDataModel.FEAT_VERTEX_TYPE+"\"");
    query.addDeclaration("v3 in \""+BGDataModel.QUALIFIER_VERTEX_TYPE+"\"");
    query.addConstraint("v2.key==\"Variant\"");
    query.addConstraint("v3.qualName==\"Clinical\"");
    query.addConstraint("v3.qualValue::\"pathogen\"");
    query.setReturnVariables("v2,v3");
    return query;
  }

  /**
   * Add thousands of fake features for our tests.
   */
  private void updateBO(SROutput bo){
    //do not modify these numbers! Otherwise you'll change test results !!!
    int a = 10, b = 5000;

    for (int i = 1; i < a; i++) {
      SRHsp hsp = bo.getIteration(0).getHit(0).getHsp(0).clone(true);
      hsp.setHspNum(i);
      hsp.setFeatures(makeFakeFeatureTable2());
      bo.getIteration(0).getHit(0).addHsp(hsp);
    }
    for (int i = a; i < b; i++) {
      SRHsp hsp = bo.getIteration(0).getHit(0).getHsp(0).clone(true);
      hsp.setHspNum(i);
      hsp.setFeatures(makeFakeFeatureTable3());
      bo.getIteration(0).getHit(0).addHsp(hsp);
    }
  }
  
  private String getMethodCaller(){
    StackTraceElement[] elements = new Throwable().getStackTrace();
    return elements[1].getMethodName();
  }
  
  @Test
  public void testLargeFilteringFeatureDirect(){
    // read NCBI XML blast file
    SROutput bo = ncbiBlastLoader.load(blastFile_simple);
    assertNotNull(bo);

    updateBO(bo);
    
    //prepare the hyper-graph data model describing Rich Search Result
    BGDataModel bgdm = new BGDataModel();
    
    //prepare the hyper-graph representation of a Blast result
    BGraph graph = new BGraph(bo, bgdm);
    //the following can be used to dump the hyper-graph content
    //System.out.println(graph.test_getContent());

    HGEQuery       query;
    Set<HGEResult> rSet;
    
    //setup a basic query using GQL
    query = prepareQuery1();
    
    long tim = System.currentTimeMillis();
    //run the query
    rSet=query.execute(bgdm, graph);
    
    assertTrue(rSet.size()==9998);
    System.out.println(String.format("%s(): %d ms", getMethodCaller(), System.currentTimeMillis() - tim));
  }

  @Test
  public void testLargeFilteringQualifierDirect(){
    // read NCBI XML blast file
    SROutput bo = ncbiBlastLoader.load(blastFile_simple);
    assertNotNull(bo);

    updateBO(bo);
    
    //prepare the hyper-graph data model describing Rich Search Result
    BGDataModel bgdm = new BGDataModel();
    
    //prepare the hyper-graph representation of a Blast result
    BGraph graph = new BGraph(bo, bgdm);
    //the following can be used to dump the hyper-graph content
    //System.out.println(graph.test_getContent());

    HGEQuery       query;
    Set<HGEResult> rSet;
    
    //setup a basic query using GQL
    query = prepareQuery2();
    
    long tim = System.currentTimeMillis();
    //run the query
    rSet=query.execute(bgdm, graph);
    
    assertTrue(rSet.size()==9);
    System.out.println(String.format(String.format("%s(): %d ms", getMethodCaller(), System.currentTimeMillis() - tim)));
  }

  @Test
  public void testQualifierFiltering() {
    // read NCBI XML blast file
    SROutput bo = ncbiBlastLoader.load(blastFile_simple);
    assertNotNull(bo);

    updateBO(bo);
    
    // load the filter from a file
    BFilter filter = FilterSystemConfigurator.getSerializer().load(FilterSystemConfigurator.getFilterableModel(),
        varQualifierFilterFile);
    //System.out.println(filter.getTxtString());

    long tim = System.currentTimeMillis();

    // apply the filter on the data
    SROutput boDest = filter.execute(bo);

    // we must have a single hit
    assertNotNull(boDest);
    assertTrue(boDest.getIteration(0).getHit(0).countHsp()==9);
    System.out.println(String.format(String.format("%s(): %d ms", getMethodCaller(), System.currentTimeMillis() - tim)));
  }

  @Test
  public void testFeatureFiltering() {
    // read NCBI XML blast file
    SROutput bo = ncbiBlastLoader.load(blastFile_simple);
    assertNotNull(bo);

    updateBO(bo);
    
    // load the filter from a file
    BFilter filter = FilterSystemConfigurator.getSerializer().load(FilterSystemConfigurator.getFilterableModel(),
        varFeatureFilterFile);

    //System.out.println(filter.getTxtString());
    long tim = System.currentTimeMillis();

    // apply the filter on the data
    SROutput boDest = filter.execute(bo);

    // we must have a single hit
    assertNotNull(boDest);
    assertTrue(boDest.getIteration(0).getHit(0).countHsp()==4999);
    System.out.println(String.format(String.format("%s(): %d ms", getMethodCaller(), System.currentTimeMillis() - tim)));
  }

}
