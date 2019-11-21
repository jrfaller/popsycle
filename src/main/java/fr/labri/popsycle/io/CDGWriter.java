package fr.labri.popsycle.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import fr.labri.popsycle.model.JClass;
import fr.labri.popsycle.model.JClassDep;
import fr.labri.popsycle.model.JClassGroup;
import fr.labri.popsycle.model.JDepKind;

public class CDGWriter {

	private JClassGroup grp;

	public CDGWriter(JClassGroup grp) {
		this.grp = grp;
	}

	public void write(File f) {
		try {
			FileWriter fw = new FileWriter(f);
			for( JClass cls : grp.getJClasses() ) {
				for( JClassDep dep : cls.getJClassDeps() ) {
					fw.append(cls.getName() + ";" + dep.getTarget().getName() + ";");
					if ( dep.getKindMap().containsKey(JDepKind.INH) )
						fw.append(Integer.toString(dep.getKindMap().get(JDepKind.INH)));
					else
						fw.append("0");
					fw.append(";");
					if ( dep.getKindMap().containsKey(JDepKind.ITF) )
						fw.append(Integer.toString(dep.getKindMap().get(JDepKind.ITF)));
					else
						fw.append("0");
					fw.append(";");
					if ( dep.getKindMap().containsKey(JDepKind.REF) )
						fw.append(Integer.toString(dep.getKindMap().get(JDepKind.REF)));
					else
						fw.append("0");
					fw.append("\n");
				}
			}
			fw.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

}
