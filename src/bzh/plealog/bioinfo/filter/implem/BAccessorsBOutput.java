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
package bzh.plealog.bioinfo.filter.implem;

import java.util.Enumeration;
import java.util.Hashtable;

import bzh.plealog.bioinfo.api.filter.BAccessorEntry;
import bzh.plealog.bioinfo.api.filter.BDataAccessors;
import bzh.plealog.bioinfo.api.filter.BOperatorAccessors;
import bzh.plealog.bioinfo.filter.implem.datamodel.BGDataModel;
import bzh.plealog.hge.api.datamodel.DGMAttribute;

/**
 * This is an implementation of BAccessors aims at handling SROutput data model.
 * 
 * @author Patrick G. Durand
 */
public class BAccessorsBOutput implements BOperatorAccessors, BDataAccessors {
  private Hashtable<String, BAccessorEntry>  accessors_;
  private Hashtable<String, String>          opeSymbolToTxt_;
  private Hashtable<String, String>          opeTxtToSymbol_;

  protected static Hashtable<String, String> accessorRenaming_;

  /**
   * Constructor. Create a default data model including SearchResults, Features, SequenceInfo, Sequences and DataNumbering.
   */
  public BAccessorsBOutput(){
    this(true, true, true, true);
  }
  /**
   * Constructor. Provide a control on whet to include in the data model. It always contains SearchResult, but use the
   * constructor arguments to tell whether of not to include other data models.
   */
  public BAccessorsBOutput(boolean includeFeature, boolean includeSeqInfo, boolean includeSequences, boolean includeDataNumbering){
    init(includeFeature, includeSeqInfo, includeSequences, includeDataNumbering);

    // This table reports renaming of Accessors.
    // It is used within FilterSerializerImplem.
    accessorRenaming_ = new Hashtable<String, String>();
    //key: old name ; value: new name
    accessorRenaming_.put("Query Coverage", "HSP/Local Query Coverage");
    accessorRenaming_.put("Hit Coverage", "HSP/Local Hit Coverage");
  }
  private void init(boolean includeFeature, boolean includeSeqInfo, boolean includeSequences, boolean includeDataNumbering){
    BAccessorEntry entry;
    /*
     * Note: it is possible to use special functions of HGE query language
     * while keeping out of view from the GUI. For that purpose, DO NOT
     * declare the usage of such functions in the tables below. As an example,
     * consider the function 'strInSet' declared in OPE_FOR_STRING.
     */
    opeSymbolToTxt_ = new Hashtable<String, String>();
    opeSymbolToTxt_.put("==","is equal to");
    opeSymbolToTxt_.put("!=","is not equal to");
    opeSymbolToTxt_.put("<","is less than");
    opeSymbolToTxt_.put(">","is greater than");
    opeSymbolToTxt_.put("<=","is less than or equal to");
    opeSymbolToTxt_.put(">=","is greater than or equal to");
    opeSymbolToTxt_.put("::","contains");
    opeSymbolToTxt_.put("!:","does not contain");
    opeSymbolToTxt_.put(OPE_InRangeInclusive,"is in the range (inclusive)");
    opeSymbolToTxt_.put(OPE_InRangeExclusive,"is in the range (exclusive)");

    opeTxtToSymbol_ = new Hashtable<String, String>();
    opeTxtToSymbol_.put("is equal to","==");
    opeTxtToSymbol_.put("is not equal to","!=");
    opeTxtToSymbol_.put("is less than","<");
    opeTxtToSymbol_.put("is greater than",">");
    opeTxtToSymbol_.put("is less than or equal to","<=");
    opeTxtToSymbol_.put("is greater than or equal to",">=");
    opeTxtToSymbol_.put("contains","::");
    opeTxtToSymbol_.put("does not contain","!:");
    opeTxtToSymbol_.put("is in the range (inclusive)", OPE_InRangeInclusive);
    opeTxtToSymbol_.put("is in the range (exclusive)", OPE_InRangeExclusive);

    //  *** IMPORTANT NOTICE ***
    //when modifying the following maps, also modify 
    //filter.implem.datamodel.BGDataModel#initialize()
    //filter.implem.datagraph.BGUtils
    accessors_ = new Hashtable<String, BAccessorEntry>();

    //accessors for BHit
    entry = new BAccessorEntry(ACC_HitAccession,"accession", 
        BGDataModel.SRHIT_VERTEX_TYPE, OPE_FOR_STRING, DGMAttribute.DT_STRING);
    accessors_.put(entry.getAccessorVisibleName(), entry);
    if (includeDataNumbering){
      entry = new BAccessorEntry(ACC_HitHspCount,"countHsp", 
          BGDataModel.SRHIT_VERTEX_TYPE, OPE_FOR_NUMBERS, DGMAttribute.DT_LONG);/**/
      accessors_.put(entry.getAccessorVisibleName(), entry);
    }
    entry = new BAccessorEntry(ACC_HitDefinition,"definition", 
        BGDataModel.SRHIT_VERTEX_TYPE, OPE_FOR_STRING, DGMAttribute.DT_STRING);
    accessors_.put(entry.getAccessorVisibleName(), entry);
    if (includeDataNumbering){
      entry = new BAccessorEntry(ACC_HitIdentifier,"id", 
          BGDataModel.SRHIT_VERTEX_TYPE, OPE_FOR_STRING, DGMAttribute.DT_STRING);/**/
      accessors_.put(entry.getAccessorVisibleName(), entry);
      entry = new BAccessorEntry(ACC_HitLength,"length", 
          BGDataModel.SRHIT_VERTEX_TYPE, OPE_FOR_NUMBERS, DGMAttribute.DT_LONG);/**/
      accessors_.put(entry.getAccessorVisibleName(), entry);
      entry = new BAccessorEntry(ACC_HitRank,"numi", 
          BGDataModel.SRHIT_VERTEX_TYPE, OPE_FOR_NUMBERS, DGMAttribute.DT_LONG);/**/
      accessors_.put(entry.getAccessorVisibleName(), entry);
      //part of data numbering because these values are computed with more than one hit/hsps
      entry = new BAccessorEntry(ACC_HitQueryCoverage,"qgCover", 
          BGDataModel.SRHIT_VERTEX_TYPE, OPE_FOR_NUMBERS, DGMAttribute.DT_DOUBLE);/**/
      accessors_.put(entry.getAccessorVisibleName(), entry);
      entry = new BAccessorEntry(ACC_HitHitCoverage,"hgCover", 
          BGDataModel.SRHIT_VERTEX_TYPE, OPE_FOR_NUMBERS, DGMAttribute.DT_DOUBLE);/**/
      accessors_.put(entry.getAccessorVisibleName(), entry);
    }		
    //accessors for BHsp
    entry = new BAccessorEntry(ACC_HspRank,"nums", 
        BGDataModel.SRHSP_VERTEX_TYPE, OPE_FOR_NUMBERS, DGMAttribute.DT_LONG);/**/
    accessors_.put(entry.getAccessorVisibleName(), entry);
    entry = new BAccessorEntry(ACC_HspQueryCoverage,"qCover", 
        BGDataModel.SRHSP_VERTEX_TYPE, OPE_FOR_NUMBERS, DGMAttribute.DT_DOUBLE);
    accessors_.put(entry.getAccessorVisibleName(), entry);
    entry = new BAccessorEntry(ACC_HspHitCoverage,"hCover", 
        BGDataModel.SRHSP_VERTEX_TYPE, OPE_FOR_NUMBERS, DGMAttribute.DT_DOUBLE);
    accessors_.put(entry.getAccessorVisibleName(), entry);

    //accessors for BHsp.Scores
    entry = new BAccessorEntry(ACC_BitScore,"bitScore", 
        BGDataModel.SRHSP_VERTEX_TYPE, OPE_FOR_NUMBERS, DGMAttribute.DT_DOUBLE);
    accessors_.put(entry.getAccessorVisibleName(), entry);
    entry = new BAccessorEntry(ACC_Score,"score", 
        BGDataModel.SRHSP_VERTEX_TYPE, OPE_FOR_NUMBERS, DGMAttribute.DT_DOUBLE);
    accessors_.put(entry.getAccessorVisibleName(), entry);
    entry = new BAccessorEntry(ACC_EValue,"evalue", 
        BGDataModel.SRHSP_VERTEX_TYPE, OPE_FOR_NUMBERS, DGMAttribute.DT_DOUBLE);
    accessors_.put(entry.getAccessorVisibleName(), entry);
    entry = new BAccessorEntry(ACC_PctIdentity,"identity", 
        BGDataModel.SRHSP_VERTEX_TYPE, OPE_FOR_NUMBERS, DGMAttribute.DT_DOUBLE);
    accessors_.put(entry.getAccessorVisibleName(), entry);
    entry = new BAccessorEntry(ACC_PctPositive,"positive", 
        BGDataModel.SRHSP_VERTEX_TYPE, OPE_FOR_NUMBERS, DGMAttribute.DT_DOUBLE);
    accessors_.put(entry.getAccessorVisibleName(), entry);
    entry = new BAccessorEntry(ACC_PctGap,"gaps", 
        BGDataModel.SRHSP_VERTEX_TYPE, OPE_FOR_NUMBERS, DGMAttribute.DT_DOUBLE);
    accessors_.put(entry.getAccessorVisibleName(), entry);
    entry = new BAccessorEntry(ACC_AlignLength,"alignLen", 
        BGDataModel.SRHSP_VERTEX_TYPE, OPE_FOR_NUMBERS, DGMAttribute.DT_LONG);
    accessors_.put(entry.getAccessorVisibleName(), entry);

    //accessors for BHsp.QuerySequence
    if (includeSequences){
      entry = new BAccessorEntry(ACC_HspQueryFrom,"qFrom", 
          BGDataModel.SRHSP_VERTEX_TYPE, OPE_FOR_NUMBERS, DGMAttribute.DT_LONG);
      accessors_.put(entry.getAccessorVisibleName(), entry);
      entry = new BAccessorEntry(ACC_HspQueryTo,"qTo", 
          BGDataModel.SRHSP_VERTEX_TYPE, OPE_FOR_NUMBERS, DGMAttribute.DT_LONG);
      accessors_.put(entry.getAccessorVisibleName(), entry);
      entry = new BAccessorEntry(ACC_HspQueryFrame,"qFrame", 
          BGDataModel.SRHSP_VERTEX_TYPE, OPE_FOR_NUMBERS, DGMAttribute.DT_LONG);
      accessors_.put(entry.getAccessorVisibleName(), entry);
      entry = new BAccessorEntry(ACC_HspQueryGaps,"qGaps", 
          BGDataModel.SRHSP_VERTEX_TYPE, OPE_FOR_NUMBERS, DGMAttribute.DT_LONG);
      entry = new BAccessorEntry(ACC_HspQuerySequence,"qSequence", 
          BGDataModel.SRHSP_VERTEX_TYPE, OPE_FOR_STRING, DGMAttribute.DT_STRING);
      accessors_.put(entry.getAccessorVisibleName(), entry);
    }
    //accessors for BHsp.HitSequence
    if(includeSequences){
      entry = new BAccessorEntry(ACC_HspHitFrom,"hFrom", 
          BGDataModel.SRHSP_VERTEX_TYPE, OPE_FOR_NUMBERS, DGMAttribute.DT_LONG);
      accessors_.put(entry.getAccessorVisibleName(), entry);
      entry = new BAccessorEntry(ACC_HspHitTo,"hTo", 
          BGDataModel.SRHSP_VERTEX_TYPE, OPE_FOR_NUMBERS, DGMAttribute.DT_LONG);
      accessors_.put(entry.getAccessorVisibleName(), entry);
      entry = new BAccessorEntry(ACC_HspHitFrame,"hFrame", 
          BGDataModel.SRHSP_VERTEX_TYPE, OPE_FOR_NUMBERS, DGMAttribute.DT_LONG);
      accessors_.put(entry.getAccessorVisibleName(), entry);
      entry = new BAccessorEntry(ACC_HspHitGaps,"hGaps", 
          BGDataModel.SRHSP_VERTEX_TYPE, OPE_FOR_NUMBERS, DGMAttribute.DT_LONG);
      entry = new BAccessorEntry(ACC_HspHitSequence,"hSequence", 
          BGDataModel.SRHSP_VERTEX_TYPE, OPE_FOR_STRING, DGMAttribute.DT_STRING);
      accessors_.put(entry.getAccessorVisibleName(), entry);
    }
    //accessors for Features
    if (includeFeature){
      entry = new BAccessorEntry(ACC_FeatureType,"key", 
          BGDataModel.FEAT_VERTEX_TYPE, OPE_FOR_STRING, DGMAttribute.DT_STRING);
      accessors_.put(entry.getAccessorVisibleName(), entry);
      entry = new BAccessorEntry(ACC_QualifierName,"qualName", 
          BGDataModel.QUALIFIER_VERTEX_TYPE, OPE_FOR_STRING, DGMAttribute.DT_STRING);
      accessors_.put(entry.getAccessorVisibleName(), entry);
      entry = new BAccessorEntry(ACC_QualifierValue,"qualValue", 
          BGDataModel.QUALIFIER_VERTEX_TYPE, OPE_FOR_STRING, DGMAttribute.DT_STRING);
      accessors_.put(entry.getAccessorVisibleName(), entry);
    }
    //accessors for SequenceInfo
    if (includeSeqInfo){
      entry = new BAccessorEntry(ACC_SeqMolType,"siType", 
          BGDataModel.SRHIT_VERTEX_TYPE, OPE_FOR_STRING, DGMAttribute.DT_STRING);
      accessors_.put(entry.getAccessorVisibleName(), entry);
      entry = new BAccessorEntry(ACC_SeqTopology,"siTopo", 
          BGDataModel.SRHIT_VERTEX_TYPE, OPE_FOR_STRING, DGMAttribute.DT_STRING);
      accessors_.put(entry.getAccessorVisibleName(), entry);
      entry = new BAccessorEntry(ACC_SeqDivision,"siDiv", 
          BGDataModel.SRHIT_VERTEX_TYPE, OPE_FOR_STRING, DGMAttribute.DT_STRING);
      accessors_.put(entry.getAccessorVisibleName(), entry);
      entry = new BAccessorEntry(ACC_SeqOrganism,"siOrg", 
          BGDataModel.SRHIT_VERTEX_TYPE, OPE_FOR_STRING, DGMAttribute.DT_STRING);
      accessors_.put(entry.getAccessorVisibleName(), entry);
      entry = new BAccessorEntry(ACC_SeqTaxonomy,"siTax", 
          BGDataModel.SRHIT_VERTEX_TYPE, OPE_FOR_STRING, DGMAttribute.DT_STRING);
      accessors_.put(entry.getAccessorVisibleName(), entry);
      entry = new BAccessorEntry(ACC_SeqCreationDate,"siCDate", 
          BGDataModel.SRHIT_VERTEX_TYPE, OPE_FOR_NUMBERS, DGMAttribute.DT_LONG);
      entry.setHelpMsg(DATE_HLP_MSG);
      accessors_.put(entry.getAccessorVisibleName(), entry);
      entry = new BAccessorEntry(ACC_SeqUpdateDate,"siUDate", 
          BGDataModel.SRHIT_VERTEX_TYPE, OPE_FOR_NUMBERS, DGMAttribute.DT_LONG);
      entry.setHelpMsg(DATE_HLP_MSG);
      accessors_.put(entry.getAccessorVisibleName(), entry);
    }
  }
  /**
   * Get an accessor by name.
   * @param visibleName accessor name. One of the ACC_XXX constants defined in this class.
   * @return an accessor entry or null if not found*/
  public BAccessorEntry getAccessorEntry(String visibleName) {
    return (BAccessorEntry) accessors_.get(visibleName);
  }

  /**
   * Return an enumeration over all the accessor names contained in this data model.
   * 
   * @return an enumeration
   * */
  public Enumeration<String> getAccessorVisibleNames() {
    return accessors_.keys();
  }

  /**
   * Return an operator description given the operator symbol.
   * @param txtSymbol the operator symbol. e.g. "=="
   * @return the operator description. e.g. "is equal to"
   * */
  public String getOperatorForText(String txtSymbol) {
    return opeTxtToSymbol_.get(txtSymbol);
  }

  /**
   * Return an operator symbol given the operator description.
   * @param opeSymbol the operator description. e.g. "is equal to"
   * @return the operator symbol. e.g. "=="
   * */
  public String getTextForOperator(String opeSymbol) {
    return opeSymbolToTxt_.get(opeSymbol);
  }

  public void addAccessorEntry(BAccessorEntry entry){
    String name = entry.getAccessorVisibleName();
    if (accessors_.containsKey(name)){
      accessors_.remove(name);
    }
    accessors_.put(name, entry);
  }
}
