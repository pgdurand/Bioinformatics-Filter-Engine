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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashSet;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import bzh.plealog.bioinfo.api.core.config.CoreSystemConfigurator;
import bzh.plealog.bioinfo.api.data.searchresult.SROutput;
import bzh.plealog.bioinfo.api.data.searchresult.io.SRLoader;
import bzh.plealog.bioinfo.api.data.searchresult.io.SRWriter;
import bzh.plealog.bioinfo.api.filter.BFilter;
import bzh.plealog.bioinfo.api.filter.BOperatorAccessors;
import bzh.plealog.bioinfo.api.filter.BRule;
import bzh.plealog.bioinfo.api.filter.config.FilterSystemConfigurator;
import bzh.plealog.bioinfo.filter.implem.BAccessorsBOutput;
import bzh.plealog.bioinfo.io.searchresult.SerializerSystemFactory;

/**
 * Unit tests of the Filtering System.
 * 
 * @author Patrick G. Durand
 */
public class TestFilterSystem {
	private static File            blastFile;
	private static File            tmpFile;
	private static File            filterFile;
	private static SRLoader         ncbiBlastLoader;
	private static SRWriter         ncbiBlastWriter;
	private static HashSet<String> hitIDs;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// init logger system (used by castor)
		BasicConfigurator.configure();
		//discard castor debug messages
		Logger.getRootLogger().setLevel(Level.INFO);
		
		// init API factories
		CoreSystemConfigurator.initializeSystem();
		FilterSystemConfigurator.initializeSystem();
		// sample NCBI legacy Blast result
		blastFile = new File("data/test/blastp.xml");
		// setup a temp file (will be deleted in tearDownAfterClass())
		tmpFile = File.createTempFile("blastTest", ".xml");
		// setup a file filter
		filterFile = new File("data/test/filter1.xml");
		// setup an NCBI Blast Loader (XML)
		ncbiBlastLoader = SerializerSystemFactory.getLoaderInstance(SerializerSystemFactory.NCBI_LOADER);
		// setup an NCBI Blast Writer (XML)
		ncbiBlastWriter = SerializerSystemFactory.getWriterInstance(SerializerSystemFactory.NCBI_WRITER);
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

	@Test
	public void testBasicFilter() {
		// read NCBI XML blast file
		SROutput bo = ncbiBlastLoader.load(blastFile);
		assertNotNull(bo);
		assertTrue(bo.getIteration(0).countHit()==19);
		
		// create simple filter
		BFilter filter = FilterSystemConfigurator.getFilterFactory().createFilter(
				// data model relying on BOutput
				FilterSystemConfigurator.getFilterableModel(), 
				// Filter name (internal use only)
				"test" 
			); 
		BRule rule = FilterSystemConfigurator.getRuleFactory().createRule(
				// rule applies on Hit Accession Number
				BAccessorsBOutput.ACC_HitAccession, 
				// test for equality 
				BOperatorAccessors.OPE_Equal, 
				// the value to check
				"1FQY-A" 
			);
		filter.add(rule);
		
		// apply the filter on the data
		SROutput boDest = filter.execute(bo);
		
		// we must have a single hit
		assertNotNull(boDest);
		assertTrue(boDest.countIteration()==1);
		assertTrue(boDest.getIteration(0).countHit()==1);
		
		// ok, write out the result
		ncbiBlastWriter.write(tmpFile, boDest);
	}

	@Test
	public void testSetFilter() {
		// read NCBI XML blast file
		SROutput bo = ncbiBlastLoader.load(blastFile);
		assertNotNull(bo);
		assertTrue(bo.getIteration(0).countHit()==19);
		
		// create a filter relying on Set function
		BFilter filter = FilterSystemConfigurator.getFilterFactory().createFilter(
				// data model relying on BOutput
				FilterSystemConfigurator.getFilterableModel(), 
				// Filter name (internal use only)
				"test" 
			); 
		
		BRule rule = FilterSystemConfigurator.getRuleFactory().createRule(
				// rule applies on Hit Accession 
				BAccessorsBOutput.ACC_HitAccession, 
				// test for "contains" 
				BOperatorAccessors.OPE_FUNC_StrInSet, 
				// the value to check
				hitIDs 
			);
		filter.add(rule);
		
		// apply the filter on the data
		SROutput boDest = filter.execute(bo);
		
		// we must have a single hit
		assertNotNull(boDest);
		assertTrue(boDest.countIteration()==1);
		assertTrue(boDest.getIteration(0).countHit()==hitIDs.size());
		
		// we must have these two Hits in the right order
		assertTrue(boDest.getIteration(0).getHit(0).getHitNum()==1);
		assertTrue(boDest.getIteration(0).getHit(0).getHitAccession().equals("1FQY-A"));
		assertTrue(boDest.getIteration(0).getHit(1).getHitNum()==4);
		assertTrue(boDest.getIteration(0).getHit(1).getHitAccession().equals("1YMG-A"));
		
		// ok, write out the result
		ncbiBlastWriter.write(tmpFile, boDest);
	}
	
	@Test
	public void testFileFilter() {
		// read NCBI XML blast file
		SROutput bo = ncbiBlastLoader.load(blastFile);
		assertNotNull(bo);
		assertTrue(bo.getIteration(0).countHit()==19);
		
		// load the filter from a file
		BFilter filter = FilterSystemConfigurator.getSerializer().load(FilterSystemConfigurator.getFilterableModel(), filterFile);

		// apply the filter on the data
		SROutput boDest = filter.execute(bo);
		
		// we must have a single hit
		assertNotNull(boDest);
		assertTrue(boDest.getIteration(0).countHit()==15);
	}
}
