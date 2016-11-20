package fileHandlers;

import static java.nio.file.FileVisitResult.CONTINUE;

/**
 * Sample code that finds files that match the specified glob pattern.
 * For more information on what constitutes a glob pattern, see
 * https://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob
 *
 * The file or directories that match the pattern are printed to
 * standard out.  The number of matches is also printed.
 *
 * When executing this application, you must put the glob pattern
 * in quotes, so the shell will not expand any wild cards:
 *              java Find . -name "*.java"
 */
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Vector;


public class PathFinder extends SimpleFileVisitor<Path> {

	private final PathMatcher matcher;
	private int numMatches = 0;
	private Vector<String> foundPaths;

	public PathFinder(String pattern) {
		matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
		foundPaths = new Vector<String>();
	}

	// Compares the glob pattern against
	// the file or directory name.
	public void find(Path fileName) {
		Path name = fileName.getFileName();
		if (name != null && matcher.matches(name)) {
			numMatches++;
			foundPaths.add(fileName.toString());
		}
	}

	// Prints the total number of matches to standard out.
	public void done(String fileName) {
		//System.out.println("PathFinder Matched " + numMatches + " with " + fileName);
	}

	public Vector<String> getPathsAsArray() {
		return this.foundPaths;
	}
	
	// Invoke the pattern matching
	// method on each file.
	@Override
	public FileVisitResult visitFile(Path file,
			BasicFileAttributes attrs) {
		find(file);
		return CONTINUE;
	}

	// Invoke the pattern matching
	// method on each directory.
	@Override
	public FileVisitResult preVisitDirectory(Path dir,
			BasicFileAttributes attrs) {
		find(dir);
		return CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file,
			IOException exc) {
		System.err.println(exc);
		return CONTINUE;
	}
}
