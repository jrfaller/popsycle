/*
 * Copyright 2009-2011 Jean-Rémy Falleri
 * 
 * This file is part of Popsycle.
 * Popsycle is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Popsycle is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Popsycle.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.labri.popsycle.model;

public enum JDepKind {
	
	INH,
	
	ITF,
	
	REF;
	
	public static JDepKind getWorseDepKind(JDepKind first, JDepKind second) {
		if ( first == JDepKind.INH || second == JDepKind.INH )
			return JDepKind.INH;
		else if ( first == JDepKind.ITF || second == JDepKind.ITF )
			return JDepKind.ITF;
		else 
			return REF;
	}

}
