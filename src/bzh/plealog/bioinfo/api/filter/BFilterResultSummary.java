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

import java.util.ArrayList;

/**
 * Utility class aims at storing the results of filtering several
 * BFilterResultAtom.  This is a
 * data model for internal use with the Filter UI library.
 * 
 * @author Patrick G. Durand
 */
public class BFilterResultSummary {
	private ArrayList<BFilterResultAtom> _data;
	private String                       filterName;
	private String                       jobName;
	
	public BFilterResultSummary(String jobName, String filterName){
		_data = new ArrayList<BFilterResultAtom>();
		this.filterName = filterName;
		this.jobName = jobName;
	}
	public void addResult(BFilterResultAtom res){
		_data.add(res);
	}
	public BFilterResultAtom getResult(int i){
		return _data.get(i);
	}
	public int size(){
		return _data.size();
	}
	public String getFilterName() {
		return filterName;
	}
	public String getJobName() {
		return jobName;
	}
	
}
