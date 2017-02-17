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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bzh.plealog.bioinfo.api.data.searchresult.SRHit;
import bzh.plealog.bioinfo.api.data.searchresult.SRHsp;
import bzh.plealog.bioinfo.api.data.searchresult.SRIteration;
import bzh.plealog.bioinfo.api.data.searchresult.SROutput;
import bzh.plealog.bioinfo.api.data.searchresult.utils.SRFactory;
import bzh.plealog.bioinfo.api.filter.BAccessorEntry;
import bzh.plealog.bioinfo.api.filter.BFilter;
import bzh.plealog.bioinfo.api.filter.BFilterException;
import bzh.plealog.bioinfo.api.filter.BOperatorAccessors;
import bzh.plealog.bioinfo.api.filter.BRule;
import bzh.plealog.bioinfo.api.filter.config.FilterSystemConfigurator;
import bzh.plealog.bioinfo.filter.implem.datagraph.BGraph;
import bzh.plealog.bioinfo.filter.implem.datamodel.BGDataModel;
import bzh.plealog.bioinfo.io.filter.BFilterIO;
import bzh.plealog.bioinfo.io.filter.BRuleIO;
import bzh.plealog.hge.api.datamodel.DGMAttribute;
import bzh.plealog.hge.api.hypergraph.HDGVertex;
import bzh.plealog.hge.api.query.HGEManager;
import bzh.plealog.hge.api.query.HGEQuery;
import bzh.plealog.hge.api.query.HGEResult;

/**
 * This is a default implementation of interface BFilter. For internal use only.
 * It contains a gateway between the high level Filter API and the underlying 
 * low-level Hyper-Graph Explorer (HGE) implementation that really does the 
 * filtering job.
 * 
 * @author Patrick G. Durand
 */
public class BFilterImplem implements BFilter {
  private String           name_;
  private String           description_;
  private boolean          exclusive_;
  private ArrayList<BRule> rules_;
  private HGEQuery         query_;
  private BGDataModel      bGraphModel_;
  private BIterationSorter iterComparator;
  private BHitSorter       hitComparator;
  private BHspSorter       hspComparator;
  private BOperatorAccessors filterModel_;
  private boolean verbose_;
  private Hashtable<String, String> mapper_;
  
  protected static final String GR_VAR = "g1 as e1:";
  protected static final String E2_VAR = "e2";
  protected static final String E3_VAR = "e3";
  protected static final String E4_VAR = "e4";
  protected static final String E5_VAR = "e5";
  protected static final String BO_VAR = "v1";
  protected static final String BI_VAR = "v2";
  protected static final String BH_VAR = "v3";
  protected static final String BS_VAR = "v4";
  protected static final String FT_VAR = "v5";
  protected static final String QL_VAR = "v6";
  protected static final String IN_VAR = " in \"";

  public BFilterImplem(BOperatorAccessors fModel){
    this(fModel, new BGDataModel());
  }

  public BFilterImplem(BOperatorAccessors fModel, BGDataModel graphDataModel){
    filterModel_ = fModel;
    rules_ = new ArrayList<BRule>();
    bGraphModel_ = graphDataModel;
    description_="no description";
    iterComparator = new BIterationSorter();
    hitComparator = new BHitSorter();
    hspComparator = new BHspSorter();
    
  }
  public BFilterImplem(BOperatorAccessors fModel, BFilterIO filter){
    this(fModel, new BGDataModel(), filter);
  }

  public BFilterImplem(BOperatorAccessors fModel, BGDataModel graphDataModel, BFilterIO filter){
    this(fModel, graphDataModel);

    setName(filter.getName());
    setDescription(filter.getDescription());
    setExclusive(filter.isExclusive());

    Iterator<BRuleIO> iter = filter.getRules().iterator();
    while(iter.hasNext()){
      BRuleIO bio = iter.next();
      rules_.add(FilterSystemConfigurator.getRuleFactory().createRule(bio.getAccessor(), bio.getOperator(), bio.getValue()));
    }
    
  }
  public BFilterImplem(BOperatorAccessors fModel, String filterName){
    this(fModel, new BGDataModel(), filterName);
  }

  public BFilterImplem(BOperatorAccessors fModel, BGDataModel graphDataModel, String filterName){
    this(fModel, graphDataModel);
    setName(filterName);
  }

  public BFilterImplem(BFilterImplem src){
    this( src.filterModel_, src.bGraphModel_);
    copy(src);
        
  }
  public Object clone(){
    BFilterImplem filter = new BFilterImplem(filterModel_);
    filter.copy(this);
    return filter;
  }

  protected void copy(BFilter src){
    Iterator<BRule> iter;

    this.setName(src.getName());
    this.setDescription(src.getDescription());
    this.setExclusive(src.getExclusive());
    iter = src.getRules();
    while(iter.hasNext()){
      this.add((BRule) iter.next().clone());
    }
  }

  /**
   * Return a correspondence between query variable names and data types.
   */
  protected Map<String, String> getTypeToVariable(){
    if (mapper_!=null){
      return mapper_;
    }
    mapper_ = new Hashtable<>();
    mapper_.put(BGDataModel.SRHIT_VERTEX_TYPE, BH_VAR);
    mapper_.put(BGDataModel.SRHSP_VERTEX_TYPE, BS_VAR);
    mapper_.put(BGDataModel.FEAT_VERTEX_TYPE, FT_VAR);
    mapper_.put(BGDataModel.QUALIFIER_VERTEX_TYPE, QL_VAR);
    return mapper_;
  }
  
  /**
   * Format a standard data accessor as an HQL declaration.
   * 
   * @param szBuf accessor description will be added in this buffer
   * @param rule rule to format as a HQL declaration.
   */
  private void formatStdAccessor(StringBuffer szBuf, BRule rule) throws BFilterException{
    BAccessorEntry entry;
    String         name, objType;

    //get the name of the data accessor
    name = rule.getAccessor();
    // from the data model, get the description entry
    entry = filterModel_.getAccessorEntry(name);
    // then get the data type
    objType = entry.getObjectType();
    // and the corresponding query variable name
    name = getTypeToVariable().get(objType);
    if (name==null){
      throw new BFilterException("Object type "+objType+" is not supported!");
    }
    szBuf.append(name);
    szBuf.append(".");
    szBuf.append(entry.getAccessorName());
  }

  /**
   * Format a standard function as an HQL declaration.
   * 
   * @param szBuf function description will be added in this buffer
   * @param rule rule to format as a HQL declaration.
   */
  private void formatFuncAccessor(StringBuffer szBuf, BRule rule) throws BFilterException{
    BAccessorEntry entry;
    String         name, objType;

    name = rule.getAccessor();
    entry = filterModel_.getAccessorEntry(name);
    szBuf.append(entry.getAccessorName());
    szBuf.append("(");
    objType = entry.getObjectType();
    // and the corresponding query variable name
    name = getTypeToVariable().get(objType);
    if (name==null){
      throw new BFilterException("Object type "+objType+" is not supported!");
    }
    szBuf.append(name);
    szBuf.append(")");
  }

  private void appendAtomicValue(StringBuffer szBuf, Object value, int dataType){
    switch(dataType){
      case DGMAttribute.DT_STRING:
        szBuf.append("\"");
        szBuf.append(value);
        szBuf.append("\"");
        break;
      case DGMAttribute.DT_CHARACTER:
        szBuf.append("'");
        szBuf.append(value);
        szBuf.append("'");
        break;
      default:
        szBuf.append(value);
        break;
    }
  }
  private void formatStdRule(StringBuffer szBuf, BRule rule) throws BFilterException{
    BAccessorEntry entry;
    String         name;
    name = rule.getAccessor();
    entry = filterModel_.getAccessorEntry(name);
    if (entry.getFunctionAccessor()){
      formatFuncAccessor(szBuf, rule);
    }
    else{
      formatStdAccessor(szBuf, rule);
    }
    szBuf.append(rule.getOperator());
    appendAtomicValue(szBuf, rule.getValue(), entry.getDataType());
  }
  private void formatRangeRule(StringBuffer szBuf, BRule rule) throws BFilterException{
    BAccessorEntry entry;
    String         name;
    boolean        inclusive;

    szBuf.append("( ");
    name = rule.getAccessor();
    entry = filterModel_.getAccessorEntry(name);
    inclusive = rule.getOperator().equals(BOperatorAccessors.OPE_InRangeInclusive);
    if (entry.getFunctionAccessor()){
      formatFuncAccessor(szBuf, rule);
    }
    else{
      formatStdAccessor(szBuf, rule);
    }
    szBuf.append(inclusive?">=":">");
    appendAtomicValue(szBuf, ((List<?>)rule.getValue()).get(0), entry.getDataType());
    szBuf.append(" and ");
    if (entry.getFunctionAccessor()){
      formatFuncAccessor(szBuf, rule);
    }
    else{
      formatStdAccessor(szBuf, rule);
    }
    szBuf.append(inclusive?"<=":"<");
    appendAtomicValue(szBuf, ((List<?>)rule.getValue()).get(1), entry.getDataType());
    szBuf.append(")");
  }
  private void formatStrInSetRule(StringBuffer szBuf, BRule rule) throws BFilterException{
    BAccessorEntry entry;
    String         name;
    Set<?>         values;
    Iterator<?>    iter;
    Object         data;
    int            dType;

    szBuf.append(rule.getOperator());
    szBuf.append("(");
    name = rule.getAccessor();
    entry = filterModel_.getAccessorEntry(name);
    if (entry.getFunctionAccessor()){
      formatFuncAccessor(szBuf, rule);
    }
    else{
      formatStdAccessor(szBuf, rule);
    }

    try {
      values = (Set<?>) rule.getValue();
    } catch (ClassCastException e) {
      throw new BFilterException(rule.getOperator()+": wrong values: expected a Set. Found: "+
          rule.getValue().getClass().getName());
    }
    szBuf.append(", {");
    dType = entry.getDataType();
    iter = values.iterator();
    while(iter.hasNext()){
      data = iter.next();
      appendAtomicValue(szBuf, data, dType);
      if (iter.hasNext()){
        szBuf.append(",");
      }
    }
    szBuf.append("})");
  }
  /**
   * Format a rule as a HQL declaration.
   * 
   * @param rule the rule to format
   * 
   * @return an well-formed Hyper-Graph Query language constraint declaration.
   */
  protected String formatRule(BRule rule) throws BFilterException{
    StringBuffer   szBuf;
    String         ope;

    szBuf = new StringBuffer();
    ope = rule.getOperator();
    if (ope.equals(BOperatorAccessors.OPE_FUNC_StrInSet)||ope.equals(BOperatorAccessors.OPE_FUNC_StrNotInSet)||
        ope.equals(BOperatorAccessors.OPE_FUNC_LongInSet)||ope.equals(BOperatorAccessors.OPE_FUNC_LongNotInSet)){
      formatStrInSetRule(szBuf, rule);
    }
    else if (ope.equals(BOperatorAccessors.OPE_InRangeExclusive)||ope.equals(BOperatorAccessors.OPE_InRangeInclusive)){
      formatRangeRule(szBuf, rule);
    }
    else{
      formatStdRule(szBuf, rule);
    }
    return szBuf.toString();
  }

  /**
   * Utility method looking at all the rules of the Filter to figure out
   * if we have to filter features. Return true in this case, false otherwise.
   */
  protected boolean hasToFilterFeatures(){
    int            i, size;
    BRule          rule;
    BAccessorEntry entry;

    size = rules_.size();
    for(i=0;i<size;i++){
      rule = (BRule) rules_.get(i);
      entry = filterModel_.getAccessorEntry(rule.getAccessor());
      if (entry.getObjectType().equals(BGDataModel.FEAT_VERTEX_TYPE)){
        return true;
      }
    }
    return false;	
  }
  /**
   * Utility method looking at all the rules of the Filter to figure out
   * if we have to filter features. Return true in this case, false otherwise.
   */
  protected boolean hasToFilterQualifiers(){
    int            i, size;
    BRule          rule;
    BAccessorEntry entry;

    size = rules_.size();
    for(i=0;i<size;i++){
      rule = (BRule) rules_.get(i);
      entry = filterModel_.getAccessorEntry(rule.getAccessor());
      if (entry.getObjectType().equals(BGDataModel.QUALIFIER_VERTEX_TYPE)){
        return true;
      }
    }
    return false;	
  }
  /**
   * Implementation of BFilter interface.
   */
  public void compile() throws BFilterException{
    StringBuffer buf;
    HGEQuery     query;
    int          i, size;
    boolean      filterFeat, filterQual;

    if (rules_.isEmpty())
      return;
    filterFeat = hasToFilterFeatures();
    filterQual = hasToFilterQualifiers();
    query = HGEManager.newHQuery();
    //defines the skeleton of the filter: a graph
    query.addDeclaration(GR_VAR+BO_VAR+"-"+BI_VAR+IN_VAR+BGDataModel.CONTAINS_ITERATION_EDGE_TYPE+"\"");   
    query.addDeclaration(E2_VAR+":"+BI_VAR+"-"+BH_VAR+IN_VAR+BGDataModel.CONTAINS_HIT_EDGE_TYPE+"\"");   
    query.addDeclaration(E3_VAR+":"+BH_VAR+"-"+BS_VAR+IN_VAR+BGDataModel.CONTAINS_HSP_EDGE_TYPE+"\"");   
    if (filterFeat || filterQual)
      query.addDeclaration(E4_VAR+":"+BS_VAR+"-"+FT_VAR+IN_VAR+BGDataModel.CONTAINS_FEAT_EDGE_TYPE+"\"");   
    if (filterQual)
      query.addDeclaration(E5_VAR+":"+FT_VAR+"-"+QL_VAR+IN_VAR+BGDataModel.CONTAINS_QUALIFIER_EDGE_TYPE+"\"");   
    query.addDeclaration(BO_VAR+IN_VAR+BGDataModel.SROUTPUT_VERTEX_TYPE+"\"");   
    query.addDeclaration(BI_VAR+IN_VAR+BGDataModel.SRITERATION_VERTEX_TYPE+"\"");
    query.addDeclaration(BH_VAR+IN_VAR+BGDataModel.SRHIT_VERTEX_TYPE+"\"");
    query.addDeclaration(BS_VAR+IN_VAR+BGDataModel.SRHSP_VERTEX_TYPE+"\"");
    if (filterFeat || filterQual)
      query.addDeclaration(FT_VAR+IN_VAR+BGDataModel.FEAT_VERTEX_TYPE+"\"");
    if (filterQual)
      query.addDeclaration(QL_VAR+IN_VAR+BGDataModel.QUALIFIER_VERTEX_TYPE+"\"");
    //adds constraints
    buf = new StringBuffer();
    size = rules_.size();
    for(i=0;i<size;i++){
      buf.append(formatRule((BRule) rules_.get(i)));
      if ((i+1)<size){
        buf.append(exclusive_?" and ":" or ");
      }
    }
    query.addConstraint(buf.toString());
    query.setReturnDistinct(false);
    query.setReturnVariables(BO_VAR+","+BI_VAR+","+BH_VAR+","+BS_VAR);
    setQuery(query);
  }

  private SROutput getBOutput(SRFactory bf, SROutput src){
    SROutput dest;

    dest = bf.createBOutput();
    dest.setBlastType(src.getBlastType());
    dest.setBlastOutputParam(src.getBlastOutputParam());
    dest.setRequestInfo(src.getRequestInfo());

    return dest;
  }

  private SRIteration getBIteration(SRFactory bf, SRIteration src){
    SRIteration bi;

    bi = bf.createBIteration();
    bi.setIterationMessage(src.getIterationMessage());
    bi.setIterationStat(src.getIterationStat());
    return bi;
  }

  /**
   * Converts an HGE result object to a BOutput. If parameter rSet is null
   * or empty, this method returns null.
   */
  protected SROutput prepareResult(Set<HGEResult> rSet){
    Hashtable<Object, Object>    ht;
    Iterator<HGEResult>          iter;
    HGEResult    result;
    SROutput      boSrc, boDest;
    SRIteration   biSrc, biDest, bi;
    SRHit         bhSrc, bhDest, bh;
    SRHsp         bsSrc, bsDest;
    SRFactory     bf;
    int          i, j, size, size2;

    if (rSet==null || rSet.isEmpty())
      return null;
    bf = FilterSystemConfigurator.getSRFactory();
    boDest=null;
    ht = new Hashtable<Object, Object>();
    iter = rSet.iterator();
    while(iter.hasNext()){
      result = iter.next();
      //get SROutput
      boSrc = (SROutput) ((HDGVertex)result.getValue(BO_VAR)).getData();
      boDest = (SROutput) ht.get(boSrc);
      if (boDest==null){
        boDest = getBOutput(bf, boSrc);
        ht.put(boSrc, boDest);
      }
      //get SRIteration
      biSrc = (SRIteration) ((HDGVertex)result.getValue(BI_VAR)).getData();
      biDest = (SRIteration) ht.get(biSrc);
      if (biDest==null){
        biDest = getBIteration(bf, biSrc);
        biDest.setIterationIterNum(biSrc.getIterationIterNum());
        boDest.addIteration(biDest);
        ht.put(biSrc, biDest);
      }
      //get SRHit
      bhSrc = (SRHit) ((HDGVertex)result.getValue(BH_VAR)).getData();
      bhDest = (SRHit) ht.get(bhSrc);
      if (bhDest==null){
        bhDest = (SRHit) bhSrc.clone(true);
        bhDest.setHitNum(bhSrc.getHitNum());
        bhDest.setSequenceInfo(bhSrc.getSequenceInfo());
        biDest.addHit(bhDest);
        ht.put(bhSrc, bhDest);
      }
      //get SRHsp
      bsSrc = (SRHsp) ((HDGVertex)result.getValue(BS_VAR)).getData();
      bsDest = (SRHsp) ht.get(bsSrc);
      if (bsDest==null){
        bsDest = (SRHsp) bsSrc.clone(false);
        bhDest.addHsp(bsDest);
        ht.put(bsSrc, bsDest);
      }
    }
    //sort data
    if (boDest!=null && !boDest.isEmpty()){
      Collections.sort(boDest.getIterations(), iterComparator);
      size = boDest.countIteration();
      for(i=0;i<size;i++){
        bi = boDest.getIteration(i);
        Collections.sort(bi.getHits(), hitComparator);
        size2 = bi.countHit();
        for(j=0;j<size2;j++){
          bh = bi.getHit(j);
          Collections.sort(bh.getHsps(), hspComparator);
        }
      }
    }

    return boDest;
  }

  private class BIterationSorter implements Comparator<SRIteration>{
    public int compare(SRIteration o1,SRIteration o2){
      return (o1.getIterationIterNum()-o2.getIterationIterNum());
    }
  }

  private class BHitSorter implements Comparator<SRHit>{
    public int compare(SRHit o1,SRHit o2){
      return (o1.getHitNum()-o2.getHitNum());
    }
  }

  private class BHspSorter implements Comparator<SRHsp>{
    public int compare(SRHsp o1,SRHsp o2){
      return (o1.getHspNum()-o2.getHspNum());
    }
  }

  /**
   * For internal use only.
   */
  protected HGEQuery getQuery(){
    return query_;
  }
  /**
   * For internal use only.
   */
  protected void setQuery(HGEQuery query){
    query_ = query;
  }

  /**
   * For internal use only.
   */
  protected BGDataModel getDataModel(){
    return bGraphModel_;
  }

  /**
   * Implementation of BFilter interface.
   */
  public SROutput execute(SROutput bo) throws BFilterException{
    BGraph         graph;
    SROutput        result = null;
    Set<HGEResult> rSet;
    
    if (bo==null)
      return null;
    if (rules_.isEmpty())
      return null;
    long tim = System.currentTimeMillis();
    if (query_==null)
      compile();
    if (verbose_){
      System.out.println(String.format("Compile time : %d ms", (System.currentTimeMillis()-tim)));
    }
    try{
      tim = System.currentTimeMillis();
      graph = new BGraph(bo, bGraphModel_);
      if (verbose_){
        System.out.println(String.format("Graph build time : %d ms", (System.currentTimeMillis()-tim)));
      }
      tim = System.currentTimeMillis();
      query_.setVerboseMode(verbose_);
      rSet=query_.execute(bGraphModel_, graph);
      if (verbose_){
        System.out.println(String.format("Execute time : %d ms", (System.currentTimeMillis()-tim)));
      }
      tim = System.currentTimeMillis();
      result = prepareResult(rSet);
      if (verbose_){
        System.out.println(String.format("Prepare result time : %d ms", (System.currentTimeMillis()-tim)));
      }
    }
    catch(Exception ex){
      throw new BFilterException("Unable to filter data: "+ex.getMessage());
    }
    return result;
  }

  /**
   * Evaluate the validity of a rule. If not valid, an exception will be thrown.
   */
  private void evaluateRule(BRule rule) throws BFilterException{
    BAccessorEntry entry;
    String         name, ope;
    String[]       opes;
    int            i;
    boolean        opeOk = false;

    //first of all, check the validity of the rule
    if (rule==null)
      throw new BFilterException("rule is not defined");
    //note: I do not test nullity of rule attributes since a BRule
    //has to be created using its factory which does not allow creating
    //a BRule with null attributes
    name = rule.getAccessor();
    //patch for KoriBlast 2.5 release and above: some names have been updated
    if (name.equals("Feature type")){
      rule.setAccessor("Feature: type");
    }
    else if (name.equals("Qualifier name")){
      rule.setAccessor("Feature: Qualifier name");
    }
    else if (name.equals("Qualifier value")){
      rule.setAccessor("Feature: Qualifier value");
    }
    name = rule.getAccessor();	
    //end of patch
    entry = filterModel_.getAccessorEntry(name);
    if (entry==null)
      throw new BFilterException("rule defines an unknown object accessor: "+name);
    ope=rule.getOperator();
    opes = entry.getOperators();
    for(i=0;i<opes.length;i++){
      if (opes[i].equals(ope)){
        opeOk=true; break;
      }
    }
    if (!opeOk)
      throw new BFilterException("rule defines an invalid operator. Seen: "
          +ope+". Expected, one of: "+Arrays.asList(opes)+".");

    //patch for KoriBlast 2.5 release and above: identity, positive and gap where set as long
    //instead of double. Following code added to reload old filters from disk
    if ((entry.getAccessorName().equals("identity") ||
        entry.getAccessorName().equals("positive") ||
        entry.getAccessorName().equals("gaps")) 
        && rule.getValue() instanceof Long){
      rule.setValue(new Double((Long)rule.getValue()));
    }
    //end of patch

    if (entry.isValidValue(rule.getValue()) == false)
      throw new BFilterException("rule contains an invalid value type. Expected: "+
          DGMAttribute.DT_REPR[entry.getDataType()]);
  }

  /**
   * Implementation of BFilter interface.
   */
  public void add(BRule rule) throws BFilterException{
    evaluateRule(rule);
    rules_.add(rule);
    query_=null;
  }

  /**
   * Implementation of BFilter interface.
   */
  public void remove(BRule rule){
    rules_.remove(rule);
    query_=null;
  }

  /**
   * Implementation of BFilter interface.
   */
  public Iterator<BRule> getRules(){
    return rules_.iterator();
  }

  /**
   * Implementation of BFilter interface.
   */
  public int size(){
    return rules_.size();
  }

  /**
   * Implementation of BFilter interface.
   */
  public String getName(){
    return name_;
  }

  /**
   * Implementation of BFilter interface.
   */
  public void setName(String filterName){
    name_ = filterName;
  }

  /**
   * Implementation of BFilter interface.
   */
  public String getDescription(){
    return description_;
  }

  /**
   * Implementation of BFilter interface.
   */
  public void setDescription(String filterDescription){
    description_ = filterDescription;
  }

  /**
   * Implementation of BFilter interface.
   */
  public boolean getExclusive(){
    return exclusive_;
  }

  /**
   * Implementation of BFilter interface.
   */
  public void setExclusive(boolean val){
    exclusive_ = val;
  }

  /**
   * Implementation of BFilter interface.
   */
  public String getHtmlString(){
    StringBuffer buf;
    int          i, size;

    buf = new StringBuffer("<html><body>");
    size = rules_.size();
    if (size>0){
      for(i=0;i<size;i++){
        buf.append(((BRule)rules_.get(i)).getHtmlString(filterModel_));
        if ((i+1)<size){
          buf.append(exclusive_?" <br><i>and</i> ":" <br><i>or</i> ");
        }
      }
    }
    else{
      buf.append("empty");
    }
    buf.append("</body></html>");
    return buf.toString();
  }
  /**
   * Implementation of BFilter interface.
   */
  public String getTxtString(){
    StringBuffer buf;
    int          i, size;

    buf = new StringBuffer();
    size = rules_.size();
    if (size>0){
      for(i=0;i<size;i++){
        buf.append(((BRule)rules_.get(i)).getTxtString(filterModel_));
        if ((i+1)<size){
          buf.append(exclusive_?" and ":" or ");
        }
      }
    }
    else{
      buf.append("empty");
    }
    return buf.toString();
  }
  /**
   * Implementation of BFilter interface.
   */
  public String toString(){
    StringBuffer buf;
    int          i, size;

    buf = new StringBuffer();
    size = rules_.size();
    if (size>0){
      for(i=0;i<size;i++){
        buf.append(rules_.get(i).toString());
        if ((i+1)<size){
          buf.append(exclusive_?" and ":" or ");
        }
      }
    }
    else{
      buf.append("empty");
    }
    return buf.toString();
  }
  
  public void setVerboseMode(boolean verbose){
    verbose_ = verbose;
  }
}
