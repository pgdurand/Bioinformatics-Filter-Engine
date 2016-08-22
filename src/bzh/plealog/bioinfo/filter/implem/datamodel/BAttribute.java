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
package bzh.plealog.bioinfo.filter.implem.datamodel;

import bzh.plealog.hge.api.datamodel.DGMAttribute;
import bzh.plealog.hge.api.datamodel.DGMAttributeException;

/**
 * This class implements DGMAttribute. 
 * 
 * @author Patrick G. Durand
 */
public class BAttribute implements DGMAttribute {
    private String _repr;
    private String _name;
    private int    _dataType;
    private int    _containerType;

    public BAttribute(String attrName, String dataType){
        StringBuffer szBuf;
        
        _name = attrName;
        _containerType = DGMAttribute.CT_ATOMIC;
        
        //get data type
        if (dataType.indexOf("string") != -1){
            _dataType = DGMAttribute.DT_STRING;
        }
        else if (dataType.indexOf("integer") != -1){
            _dataType = DGMAttribute.DT_LONG;
        }
        else if (dataType.indexOf("byte") != -1){
            _dataType = DGMAttribute.DT_LONG;
        }
        else if (dataType.indexOf("short") != -1){
            _dataType = DGMAttribute.DT_LONG;
        }
        else if (dataType.indexOf("long") != -1){
            _dataType = DGMAttribute.DT_LONG;
        }
        else if (dataType.indexOf("double") != -1){
            _dataType = DGMAttribute.DT_DOUBLE;
        }
        else if (dataType.indexOf("float") != -1){
            _dataType = DGMAttribute.DT_DOUBLE;
        }
        else if (dataType.indexOf("char") != -1){
            _dataType = DGMAttribute.DT_CHARACTER;
        }
        else if (dataType.indexOf("boolean") != -1){
            _dataType = DGMAttribute.DT_BOOLEAN;
        }
        else{
            throw new DGMAttributeException("Unsupported data type: "+dataType);
        }
        
        szBuf = new StringBuffer(_name);
        szBuf.append(": ");
        szBuf.append(DGMAttribute.CT_REPR[_containerType]);
        szBuf.append(" ");
        szBuf.append(DGMAttribute.DT_REPR[_dataType]);
        _repr = szBuf.toString();
    }

	/**
	 * Implementation of DGMAttribute interface.
	 */ 
    public String getName(){
        return _name;
    }
    
	/**
	 * Implementation of DGMAttribute interface.
	 */ 
    public int getJavaType(){
        return _dataType;
    }
    
	/**
	 * Implementation of DGMAttribute interface.
	 */ 
    public int getContainerType(){
        return _containerType;
    }
    
    public String toString(){
        return _repr;
    }
    

}
