package com.zitlab.sshd.server;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;

public class SampleSFTPServer {

	public static void main(String[] args) throws Exception {
		
		SftpSubsystemFactory factory = new SftpSubSystemFactory.Builder()
				.withFileSystemAccessor(new MySftpFileSystemAccessor())			
//				.withExecutorServiceProvider(new ThreadPoolSupplier())
				.build();
		
		Path path = Paths.get("hostkey.ser");
		
		SshServer server = SshServer.setUpDefaultServer();
		server.setPort(7021);
		server.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(path));
		
		
		server.setSubsystemFactories(Collections.singletonList(factory));		
		server.setPasswordAuthenticator(new CustomPasswordAuthenticator());
		
		server.start();
	}

}
