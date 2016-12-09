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
package bzh.plealog.bioinfo.api.filter.config;

import bzh.plealog.bioinfo.api.data.searchresult.utils.SRFactory;
import bzh.plealog.bioinfo.api.filter.BDataAccessors;
import bzh.plealog.bioinfo.api.filter.BFilterFactory;
import bzh.plealog.bioinfo.api.filter.BRuleFactory;
import bzh.plealog.bioinfo.data.searchresult.ISRFactory;
import bzh.plealog.bioinfo.filter.implem.BAccessorsBOutput;
import bzh.plealog.bioinfo.filter.implem.BFilterFactoryImplem;
import bzh.plealog.bioinfo.filter.implem.BRuleFactoryImplem;
import bzh.plealog.bioinfo.filter.implem.FilterSerializerImplem;
import bzh.plealog.bioinfo.io.filter.FilterSerializer;

/**
 * This is the SROutput filtering system. Always call method initializeSystem
 * defined here before using the filtering system.
 * 
 * @author Patrick G. Durand
 */
public class FilterSystemConfigurator {
	public static BDataAccessors filterableModel_;
	//provides the default implementation for BFilterFactory
	public static BFilterFactory filterFactory_;
	//provides the default implementation for BRuleFactory
	public static BRuleFactory   ruleFactory_;
	//provides the default implementation for FilterXmlSerializer
	public static FilterSerializer serializer_;
	//provides the default implementation for BFactory
	public static SRFactory _boFactory;
	private static boolean _bInited = false;

	static{
		initializeSystem();
	}

	/**
	 * Setup the Filtering system. Always call this method at the beginning of your
	 * code and ALWAYS before using the FIltering system.
	 */
	public static final void initializeSystem(){
		if (_bInited)
			return;

		FilterSystemConfigurator.setFilterableModel(new BAccessorsBOutput());
		FilterSystemConfigurator.setSRFactory(new ISRFactory());
		FilterSystemConfigurator.setFilterFactory(new BFilterFactoryImplem());
		FilterSystemConfigurator.setRuleFactory(new BRuleFactoryImplem());
		FilterSystemConfigurator.setSerializer(new FilterSerializerImplem());

		_bInited = true;
	}
	/**
	 * Return the filterable data model on which the Filtering system can be applied.
	 * @return a BDataAccessors instance
	 * */
	public static BDataAccessors getFilterableModel() {
		return filterableModel_;
	}
  /**
   * Set the filterable data model on which the Filtering system can be applied.
   * @param filterableModel a BDataAccessors instance
   * */
	public static void setFilterableModel(BDataAccessors filterableModel) {
		filterableModel_ = filterableModel;
	}
	/**
	 * Set the filter factory to use to create new filters.
	 * @param bff a BFilterFactory instance
	 * */
	public static void setFilterFactory(BFilterFactory bff){
		if (bff==null)
			return;
		filterFactory_ = bff;
	}
  /**
   * Return the filter factory to use to create new filters.
   * @return a BFilterFactory instance
   * */
	public static BFilterFactory getFilterFactory(){
		return filterFactory_;
	}

  /**
   * Return the rule factory to use to create new rules.
   * @param rff a BRuleFactory instance
   * */
	public static void setRuleFactory(BRuleFactory rff){
		if (rff==null)
			return;
		ruleFactory_ = rff;
	}
  /**
   * Return the rule factory to use to create new rules.
   * @return a BRuleFactory instance
   * */
	public static BRuleFactory getRuleFactory(){
		return ruleFactory_;
	}
  /**
   * Return the IO to use to save and load filters.
   * @return a FilterSerializer instance
   * */
	public static FilterSerializer getSerializer(){
		return serializer_;
	}
  /**
   * Set the IO to use to save and load filters.
   * @param s a FilterSerializer instance
   * */
	public static void setSerializer(FilterSerializer s){
		if (s==null)
			return;
		serializer_ = s;
	}
  /**
   * Return the Rich SearchResult factory to use to create SROuptut objects.
   * @return a SRFactory instance
   * */
	public static SRFactory getSRFactory(){
		return _boFactory;
	}
  /**
   * Set the Rich SearchResult factory to use to create SROuptut objects.
   * @param bf a SRFactory instance
   * */
	public static void setSRFactory(SRFactory bf){
		if (bf==null)
			return;
		_boFactory = bf;
	}

}
