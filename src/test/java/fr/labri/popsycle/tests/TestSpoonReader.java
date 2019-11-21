package fr.labri.popsycle.tests;

import fr.labri.popsycle.io.SpoonReader;
import fr.labri.popsycle.model.JClassGroup;
import org.junit.Test;

public class TestSpoonReader {
	@Test
	public void testSpoonReader() {
		SpoonReader r = new SpoonReader(new String[] {"."});
		JClassGroup gp = r.read();
		System.out.println(gp);
	}
}
