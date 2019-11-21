package fr.labri.popsycle.tests.benchmark;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.labri.popsycle.io.CDGReader;
import fr.labri.popsycle.model.JClassGroup;
import fr.labri.popsycle.model.JDepImportance;
import fr.labri.popsycle.model.JPackageGroup;

public class BenchmarkUtils {

	public static Map<String,Set<String>> getJPackageDeps(String file,JDepImportance importance) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		Map<String,Set<String>> map = new HashMap<String, Set<String>>();
		while( br.ready() ) {
			String line = br.readLine();
			String[] tokens = line.split(";");
			if ( tokens[2].equals(importance.toString()) ) {
				if ( !map.containsKey(tokens[0]) )
					map.put(tokens[0],new HashSet<String>());
				map.get(tokens[0]).add(tokens[1]);
			}
		}
		return map;
	}
	
	public static JPackageGroup loadJPackageGroup(String file) {
		CDGReader r = new CDGReader(file);
		JClassGroup cg = r.read();
		JPackageGroup pg = cg.extractJPackageGroup();
		return pg;
	}
	
}
