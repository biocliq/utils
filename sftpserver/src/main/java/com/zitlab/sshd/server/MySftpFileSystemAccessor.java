package com.zitlab.sshd.server;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.sshd.common.util.GenericUtils;
import org.apache.sshd.common.util.OsUtils;
import org.apache.sshd.common.util.SelectorUtils;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.subsystem.sftp.SftpFileSystemAccessor;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemProxy;

public class MySftpFileSystemAccessor implements SftpFileSystemAccessor {
	public static final String DEFAULT_USERS_HOME = OsUtils.isWin32() ? "C:\\Users\\"
			: OsUtils.isOSX() ? "/Users" : "/home";

	public Path resolveLocalFilePath(ServerSession session, SftpSubsystemProxy subsystem, Path rootDir,
			String remotePath) throws IOException, InvalidPathException {
		{
			Path parent = getHomeDir(session);
			String path = SelectorUtils.translateToLocalFileSystemPath(remotePath, '/', parent.getFileSystem());
			if (path.startsWith("/opt"))
				return parent.resolve(path);
			else
				return parent;
		}
	}
	
	public Map<String, ?> readFileAttributes(
            ServerSession session, SftpSubsystemProxy subsystem,
            Path file, String view, LinkOption... options)
            throws IOException {		
        return Files.readAttributes(file, view, options);
    }

	private Path getHomeDir(ServerSession session) {
		String userName = session.getUsername();
		if (GenericUtils.isEmpty(userName)) {
			return null;
		}

		String homeRoot = getUsersHomeDir();
		if (GenericUtils.isEmpty(homeRoot)) {
			return null;
		}
		return Paths.get("/opt").normalize().toAbsolutePath();
		// return Paths.get(homeRoot, userName).normalize().toAbsolutePath();
	}

	private String getUsersHomeDir() {
		return DEFAULT_USERS_HOME;
	}
	
	public void removeFile(
            ServerSession session, SftpSubsystemProxy subsystem, Path path, boolean isDirectory)
            throws IOException {
		throw new AccessDeniedException("Unsupported operation");
    }
}