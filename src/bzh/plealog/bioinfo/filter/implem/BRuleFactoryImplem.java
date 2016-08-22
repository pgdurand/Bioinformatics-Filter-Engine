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

import bzh.plealog.bioinfo.api.filter.BRule;
import bzh.plealog.bioinfo.api.filter.BRuleException;
import bzh.plealog.bioinfo.api.filter.BRuleFactory;

/**
 * This is a default implementation of interface BRuleFactory. 
 * 
 * @author Patrick G. Durand
 */
public class BRuleFactoryImplem implements BRuleFactory {

	public BRule createRule(String accessor, String operator, Object value)
			throws BRuleException {
		if (accessor==null)
			throw new BRuleException("accessor cannot be null");
		if (operator==null)
			throw new BRuleException("operator cannot be null");
		if (value==null)
			throw new BRuleException("value cannot be null");
		return new BRuleImplem(accessor, operator, value);
	}

}
