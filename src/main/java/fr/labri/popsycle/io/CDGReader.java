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

package fr.labri.popsycle.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fr.labri.popsycle.model.JClass;
import fr.labri.popsycle.model.JClassGroup;
import fr.labri.popsycle.model.JDepKind;

public class CDGReader extends JClassGroupReader {

	private String path;

	public CDGReader(String path) {
		super();
		this.path = path;
	}

	@Override
	public JClassGroup read() {
		reset();
		try {
			readCdg();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return new JClassGroup(classes);
	}

	/**
	 * Reads a CDG file. Format:
	 *
	 * <code<SRC;DEST;NB_INH;NB_ITF;NB_REF</code>
	 *
	 * Example:
	 *
	 * <code>fr.labri.popsycle;fr.labri;1;1;0
	 * @throws IOException
	 */
	private void readCdg() throws IOException {
		BufferedReader r = new BufferedReader(new FileReader(path));
		while ( r.ready() ) {
			String line = r.readLine();
			if ( ! "".equals(line) ) {
				String[] tokens = line.split(";");

				String src = tokens[0].trim();
				JClass srcCls = retrieveClass(src);

				String tgt = tokens[1].trim();
				JClass tgtCls = retrieveClass(tgt);

				Map<JDepKind,Integer> deps = new HashMap<>();
				deps.put(JDepKind.INH,Integer.parseInt(tokens[2]));
				deps.put(JDepKind.ITF,Integer.parseInt(tokens[3]));
				deps.put(JDepKind.REF,Integer.parseInt(tokens[4]));

				srcCls.setJClassDep(tgtCls,deps);
			}
		}
		r.close();
	}
}
