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

import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.StringTokenizer;

import java.util.logging.Logger;

/**
 * This is a utility class aimed at accessing Rich Search Result objects data
 * using Java Reflection.
 * 
 * @author Patrick G. Durand
 */
public class BGUtils {

  private static Hashtable<String, String> _attrAccessors;
  private static final Logger _logger = Logger.getLogger(BGUtils.class.getName());

  private static final Class<?>[] EMPLY_CLASS_ARG = new Class[0];
  private static final Object[] EMPLY_OBJ_ARG = new Object[0];

  static {
    //  *** IMPORTANT NOTICE ***
    //when modifying this Map, also modify 
    //com.plealog.bioinfo.filter.implem.datamodel.BGDataModel#initialize()
    //com.plealog.bioinfo.filter.api.BAccessors
    _attrAccessors = new Hashtable<String, String>();
    //accessors for BHit
    _attrAccessors.put("accession", "getHitAccession");
    _attrAccessors.put("countHsp", "countHsp");
    _attrAccessors.put("definition", "getHitDef");
    _attrAccessors.put("id", "getHitId");
    _attrAccessors.put("length", "getHitLen");
    _attrAccessors.put("numi", "getHitNum");
    _attrAccessors.put("qgCover","getQueryGlobalCoverage");
    _attrAccessors.put("hgCover","getHitGlobalCoverage");

    //accessors for BHsp
    _attrAccessors.put("nums", "getHspNum");
    _attrAccessors.put("qCover","getQueryCoverage");
    _attrAccessors.put("hCover","getHitCoverage");

    //accessors for BHsp.Scores
    _attrAccessors.put("bitScore","getScores.getBitScore");
    _attrAccessors.put("score","getScores.getScore");
    _attrAccessors.put("evalue","getScores.getEvalue");
    _attrAccessors.put("identity","getScores.getIdentityP");
    _attrAccessors.put("positive","getScores.getPositiveP");
    _attrAccessors.put("gaps","getScores.getGapsP");
    _attrAccessors.put("alignLen","getScores.getAlignLen");
    //_attrAccessors.put("density","getScores.getDensity");

    //accessors for BHsp.QuerySequence
    _attrAccessors.put("qFrom","getQuery.getFrom");
    _attrAccessors.put("qTo","getQuery.getTo");
    _attrAccessors.put("qGaps","getQuery.getGaps");
    _attrAccessors.put("qFrame","getQuery.getFrame");
    _attrAccessors.put("qSequence","getQuery.getSequence");

    //accessors for BHsp.HitSequence
    _attrAccessors.put("hFrom","getHit.getFrom");
    _attrAccessors.put("hTo","getHit.getTo");
    _attrAccessors.put("hGaps","getHit.getGaps");
    _attrAccessors.put("hFrame","getHit.getFrame");
    _attrAccessors.put("hSequence","getHit.getSequence");

    //accessors for Features
    _attrAccessors.put("key", "getKey");
    _attrAccessors.put("qualName", "getName");
    _attrAccessors.put("qualValue", "getValue");

    //accessors for SequenceInfo
    _attrAccessors.put("siType", "getSequenceInfo.getMoltype");
    _attrAccessors.put("siTopo", "getSequenceInfo.getTopology");
    _attrAccessors.put("siDiv", "getSequenceInfo.getDivision");
    _attrAccessors.put("siOrg", "getSequenceInfo.getOrganism");
    _attrAccessors.put("siTax", "getSequenceInfo.getTaxonomy");
    _attrAccessors.put("siCDate", "getSequenceInfo.getCreationDate");
    _attrAccessors.put("siUDate", "getSequenceInfo.getUpdateDate");
  }

  private static Object getValue(StringTokenizer attrPath, Object data) throws Exception{
    Object ret = null;
    String token;
    Method method;

    token = attrPath.nextToken();
    method = data.getClass().getMethod(token, EMPLY_CLASS_ARG);
    ret = method.invoke(data, EMPLY_OBJ_ARG);
    if (ret==null)
      return null;
    if (attrPath.hasMoreTokens())
      ret = getValue(attrPath, ret);
    return ret;
  }

  public static synchronized Object getValue(Object data, String objectType, String attrName){
    StringTokenizer tokenizer;
    Object          ret = null;
    String          methodName;

    if (data==null || objectType==null || attrName==null)
      return null;
    methodName = (String) _attrAccessors.get(attrName);
    if (methodName==null){
      _logger.warning("Unable to find method "+methodName+
          " for "+objectType+": not found in attribute accessors map.");
      return null;
    }

    try {
      tokenizer = new StringTokenizer(methodName,".");
      ret = getValue(tokenizer, data);
      if (ret!=null){
        if (ret instanceof Integer || ret instanceof Byte || ret instanceof Short){
          ret = new Long(((Number)ret).intValue());
        }
        else if (ret instanceof Float){
          ret = new Double(((Number)ret).doubleValue());
        }
      }
    } catch (Exception e) {
      _logger.warning("Unable to invoke method "+methodName+" for "+objectType+":"+e);
    }
    return ret;

  }
}
