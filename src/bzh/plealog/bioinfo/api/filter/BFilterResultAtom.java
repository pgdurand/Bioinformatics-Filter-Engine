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

import bzh.plealog.bioinfo.api.data.searchresult.SRHit;
import bzh.plealog.bioinfo.api.data.searchresult.utils.SREntry;

/**
 * Utility class aims at storing the result of BlastEntry filtering. This is a
 * data model for internal use with the Filter UI library.
 * 
 * @author Patrick G. Durand
 */
public class BFilterResultAtom {
	private SREntry _entry;
	
	//data related to the Blast query
	private String _blastQueryName;
	private String _blastQueryCode;
	
	//data related to the query sequence
	private String  _seqQueryName;
	private Integer _entryOrderNum;
	//date related to the first HSP
	private SRHit   _hit;
	
	public void setEntry(SREntry entry){
		_entry = entry;
	}
	public SREntry getEntry(){
		return _entry;
	}
	public String getBlastQueryCode() {
		return _blastQueryCode;
	}
	public void setBlastQueryCode(String blastQueryCode) {
		this._blastQueryCode = blastQueryCode;
	}
	public String getBlastQueryName() {
		return _blastQueryName;
	}
	public void setBlastQueryName(String blastQueryName) {
		this._blastQueryName = blastQueryName;
	}
	public String getSeqQueryName() {
		return _seqQueryName;
	}
	public void setSeqQueryName(String seqQueryName) {
		this._seqQueryName = seqQueryName;
	}
    public Integer getEntryOrderNum() {
		return _entryOrderNum;
	}
	public void setEntryOrderNum(Integer orderNum) {
		_entryOrderNum = orderNum;
	}
	public void setHit(SRHit hit){
		_hit = hit;
	}
	public SRHit getHit(){
		return _hit;
	}
}
