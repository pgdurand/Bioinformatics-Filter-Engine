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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;

import bzh.plealog.bioinfo.api.filter.BFilter;
import bzh.plealog.bioinfo.api.filter.BOperatorAccessors;
import bzh.plealog.bioinfo.api.filter.BRule;
import bzh.plealog.bioinfo.api.filter.config.FilterSystemConfigurator;
import bzh.plealog.bioinfo.io.filter.BFilterIO;
import bzh.plealog.bioinfo.io.filter.BRuleIO;
import bzh.plealog.bioinfo.io.filter.FilterSerializer;
import bzh.plealog.bioinfo.io.filter.FilterSerializerException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * This class implements a default FilterXmlSerializer. It uses the XStream
 * framework. For internal use only.
 * 
 * @author Patrick G. Durand
 */
public class FilterSerializerImplem implements FilterSerializer{
	private static XStream streamer;
	
	static {
		streamer = new XStream(new DomDriver("ISO-8859-1"));
		streamer.alias("BFilter", BFilterIO.class); 
		streamer.alias("BRule", BRuleIO.class); 
	}
	
	/**
	 * Implementation of FilterXmlSerializer interface.
	 */
	public void save(BFilter filter, File file) throws FilterSerializerException{
		try (FileOutputStream fos = new FileOutputStream(file);){
			streamer.toXML(new BFilterIO(filter), fos);
			fos.flush();
		} catch (Exception e) {
			throw new FilterSerializerException("Unable to save BFilter in: "+
					file.getAbsolutePath()+": "+e);
		}
	}
	
	/**
	 * Implementation of FilterXmlSerializer interface.
	 */
	public BFilter load(BOperatorAccessors fModel, File file) throws FilterSerializerException{
		BFilter         filter=null;
		
		try(FileInputStream fis = new FileInputStream(file);) {
		  filter = FilterSystemConfigurator.getFilterFactory().createFilter(fModel, (BFilterIO) streamer.fromXML(fis));
			handleAccessorRenaming(filter);
		} catch (Exception e) {
			throw new FilterSerializerException("Unable to load BFilter from: "+
					file.getAbsolutePath()+": "+e);
		}
		return filter;
	}
	
	private void handleAccessorRenaming(BFilter filter){
		Iterator<BRule> rules;
		BRule           rule;
		
		rules = filter.getRules();
		while(rules.hasNext()){
			rule = rules.next();
			if(BAccessorsBOutput.accessorRenaming_.containsKey(rule.getAccessor())){
				rule.setAccessor(BAccessorsBOutput.accessorRenaming_.get(rule.getAccessor()));
			}
		}
	}

	public void save(BFilterIO filter, File file)
			throws FilterSerializerException {
		try (FileOutputStream fos = new FileOutputStream(file);){
			streamer.toXML(filter, fos);
			fos.flush();
		} catch (Exception e) {
			throw new FilterSerializerException("Unable to save BFilter in: "+
					file.getAbsolutePath()+": "+e);
		}
	}
}
