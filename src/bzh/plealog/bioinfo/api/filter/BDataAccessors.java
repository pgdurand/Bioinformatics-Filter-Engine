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
package bzh.plealog.bioinfo.api.filter;

import java.util.Enumeration;

/**
 * This interface defines a filterable data model. It aims at being the
 * gateway between the filter engine and the filter UI.
 * 
 * @author Patrick G. Durand
 */
public interface BDataAccessors extends BOperatorAccessors{
  // Accessors used elsewhere should be declared here as static String.
  // Never modify the strings; this is a design drawback issue: those
  // strings are used in the Filter IO system. If you modify the strings,
  // then not only the IO system will fail to reload filters from their
  // XML serialization, the filtering engine will fail too!
  public static final String ACC_HitAccession     = "Hit Accession";
  public static final String ACC_HitRank          = "Hit rank";
  public static final String ACC_HitHspCount      = "Number of HSPs";
  public static final String ACC_HitDefinition    = "Hit definition";
  public static final String ACC_HitIdentifier    = "Hit identifier";
  public static final String ACC_HitLength        = "Hit Length";
  public static final String ACC_HitQueryCoverage = "Hit/Global Query Coverage";
  public static final String ACC_HitHitCoverage   = "Hit/Global Hit Coverage";

  public static final String ACC_HspRank          = "HSP rank";
  public static final String ACC_HspQueryCoverage = "HSP/Local Query Coverage";
  public static final String ACC_HspHitCoverage   = "HSP/Local Hit Coverage";

  public static final String ACC_BitScore         = "HSP bit score"; 
  public static final String ACC_Score            = "HSP score";
  public static final String ACC_EValue           = "HSP E-Value";
  public static final String ACC_PctIdentity      = "HSP % of identities";
  public static final String ACC_PctPositive      = "HSP % of positives";
  public static final String ACC_PctGap           = "HSP % of gaps";
  public static final String ACC_AlignLength      = "HSP alignment length";
  
  public static final String ACC_HspQueryFrom     = "HSP query from";
  public static final String ACC_HspQueryTo       = "HSP query to";
  public static final String ACC_HspQueryFrame    = "HSP query frame";
  public static final String ACC_HspQueryGaps     = "HSP query gaps";
  public static final String ACC_HspQuerySequence = "Query sequence";
  
  public static final String ACC_HspHitFrom       = "HSP hit from";
  public static final String ACC_HspHitTo         = "HSP hit to";
  public static final String ACC_HspHitFrame      = "HSP hit frame";
  public static final String ACC_HspHitGaps       = "HSP hit gaps";
  public static final String ACC_HspHitSequence   = "Hit sequence";
  
  public static final String ACC_FeatureType      = "Feature: type";
  public static final String ACC_QualifierName    = "Feature: Qualifier name";
  public static final String ACC_QualifierValue   = "Feature: Qualifier value";
  
  public static final String ACC_SeqMolType       = "SeqInfo: Molecular type";
  public static final String ACC_SeqTopology      = "SeqInfo: Topology";
  public static final String ACC_SeqDivision      = "SeqInfo: Division";
  public static final String ACC_SeqOrganism      = "SeqInfo: Organism";
  public static final String ACC_SeqTaxonomy      = "SeqInfo: Taxonomy";
  public static final String ACC_SeqCreationDate  = "SeqInfo: Creation date";
  public static final String ACC_SeqUpdateDate    = "SeqInfo: Update date";
  
	/**
	 * Return an enumeration over the accessor visible names of a data model.
	 */
	public Enumeration<String> getAccessorVisibleNames();
	
	/**
	 * Return a BAccessorEntry given its visible name.
	 */
	public BAccessorEntry getAccessorEntry(String visibleName);
	
	/**
	 * Add a BAccessorEntry.
	 */
	public void addAccessorEntry(BAccessorEntry entry);
}
