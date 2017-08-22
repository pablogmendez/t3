package ar.fiuba.taller.loadTestConsole;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import ar.fiuba.taller.utils.Pair;


public class Pattern {
	
	public static List<Pair<Integer, Integer>> getPositions(final File file)
	        throws FileNotFoundException, IOException {
	    if (file == null || !file.canRead()) {
	        throw new IllegalArgumentException("file not readable: " + file);
	    }

	    @SuppressWarnings("resource")
		final Scanner s = new Scanner(file).useDelimiter(":\n?");
	    final List<Pair<Integer, Integer>> positions = new ArrayList<Pair<Integer, Integer>>();
	    while (s.hasNext()) {
	        positions.add(new Pair<Integer, Integer>(s.nextInt(), s.nextInt()));
	        s.nextLine();
	    }

	    return positions;
	}
}
