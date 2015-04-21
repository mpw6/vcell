package cbit.vcell.message.server.sim;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.Set;

import cbit.vcell.tools.PortableCommand;



/**
 * copy files matching beginning of sim name from one directory to another
 * @author gweatherby
 */
public class CopySimFiles implements PortableCommand, FileVisitor<Path> {
	private final String jobName;
	private final String fromDirectory;
	private final String toDirectory;
	/**
	 * transient to avoid capture by PortableCommand
	 */
	private transient FileSystem fs = null; 
	/**
	 * transient to avoid capture by PortableCommand
	 */
	private transient Exception exc = null; 

	public CopySimFiles(String jobName, String fromDirectory, String toDirectory) {
		this.jobName = jobName;
		this.fromDirectory = fromDirectory;
		this.toDirectory = toDirectory;
	}

	@Override
	public int execute() {
		try {
			fs = FileSystems.getDefault( );
			Path from = fs.getPath(fromDirectory);
			File toDir = new File(toDirectory);
			if (!toDir.exists()) {
				toDir.mkdir( );
			}
			Set<FileVisitOption> empty = Collections.emptySet();
			Files.walkFileTree(from, empty, 1, this);
			return 0;
		} catch (IOException e) {
			exc = e;
			return 1;
		}
	}

	@Override
	public Exception exception() {
		return exc;
	}



	/**
	 * @return {@link FileVisitResult#CONTINUE 
	 */
	@Override
	public FileVisitResult postVisitDirectory(Path p, IOException e) throws IOException {
		return FileVisitResult.CONTINUE; 
	}

	/**
	 * @return {@link FileVisitResult#CONTINUE 
	 */
	@Override
	public FileVisitResult preVisitDirectory(Path p, BasicFileAttributes arg1) throws IOException {
		return FileVisitResult.CONTINUE; 
	}

	/**
	 * copy file if matches {@link #jobName}
	 * @return {@link FileVisitResult#CONTINUE 
	 */
	@Override
	public FileVisitResult visitFile(Path p, BasicFileAttributes attr) throws IOException {
		String filename = p.getFileName().toString();
		if (filename.startsWith(jobName)) {
			Path destination = fs.getPath(toDirectory, filename);
			Files.copy(p, destination, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
		}
		return FileVisitResult.CONTINUE; 
	}

	/**
	 * rethrow exception
	 */
	@Override
	public FileVisitResult visitFileFailed(Path p, IOException e) throws IOException {
		throw e;
	}

}