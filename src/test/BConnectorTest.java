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
package test;

import bzh.plealog.bioinfo.filter.implem.datamodel.BGDataModel;

/**
 * This class illustrates the use of the Filtering System using directly the
 * Hyper-Graph Explorer API. See source code of TestFilterSystem to see how
 * to use the filtering system through Filters that hide the direct use of HGE.
 * 
 * @author Patrick G. Durand
 */
public class BConnectorTest {

  /**
   * @param args
   */
  public static void main(String[] args) {
    BGDataModel conn;

    //display the data model
    conn = new BGDataModel();
    System.out.println(conn);
  }

}
