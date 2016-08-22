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
package bzh.plealog.bioinfo.io.filter;

import java.io.File;

import bzh.plealog.bioinfo.api.filter.BFilter;
import bzh.plealog.bioinfo.api.filter.BOperatorAccessors;

/**
 * This interface defines an XML serializer for BFilter and BRule objects. 
 * 
 * @author Patrick G. Durand
 */
public interface FilterSerializer {
	/**
	 * Save a BFilter in a File. If something wrong occurs during the
	 * save operation, this method should thrown a FilterXmlSerializerException.
	 */
	public void save(BFilter filter, File file) throws FilterSerializerException;
	
	/**
	 * Save a BFilterIO in a File. If something wrong occurs during the
	 * save operation, this method should thrown a FilterXmlSerializerException.
	 */
	public void save(BFilterIO filter, File file) throws FilterSerializerException;
	/**
	 * Load a BFilter from a File. If something wrong occurs during the
	 * load operation, this method should thrown a FilterXmlSerializerException.
	 * This method may return null.
	 */
	public BFilter load(BOperatorAccessors fModel, File file) throws FilterSerializerException;
}
