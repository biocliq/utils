


In a complex, multi-user environment, There is a need to publish documents accessible the authorized users. The access to the document also needs to be limited based on the user roles and hierarchy in the organization. There can be multiple approaches to share the documtns. 

* Email the documents to the users directly
* Publish the documents in a portal (Sharepoint etc)
* A Filesharing system customized to the meet the organizations need.

While emailing the documents can be the easiest way, the user shall lose the track of the email or the user needs to organize the files in their local system.
A Sharepoint portal may be used to organize the files, within the groups.

A use-case of large-scale publishing for various users based on their access level shall be done by a customized solution. The custom solution shall be a web-based access provisioning or extending one of the file access protocols (such as FTP or SFTP).

Secure File Transfer Protocol (SFTP) uses SSH secure shell protocol to provide file transfer and managmeent functionalities. There are few java libraries available to provide the functionality of SSH Protocol Notably Apache SSHD and Jsch. 

Using Apache SSHD library we can customize multiple controlling aspects such as User authentication, and limiting the files system accessiblity.

### Customizing Authentication
The PasswordAuthenticator interface needs to be implemented using a custom logic user validation
```java
public class CustomPasswordAuthenticator implements PasswordAuthenticator {

	@Override
	public boolean authenticate(String username, String password, ServerSession session)
			throws PasswordChangeRequiredException, AsyncAuthException {
		return "username".equals(username) && "secret".equals(password);
	}

}
```

 The customer Authenticator can be injected while creating the server as below. 

```java
        SshServer server = SshServer.setUpDefaultServer();		
		server.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(path));		
		server.setSubsystemFactories(Collections.singletonList(factory));	
		server.setPasswordAuthenticator(new CustomPasswordAuthenticator());		
		server.start();
```

### Limiting file system access.
By default, the SFTP server can publish all the files that are accessible to the user who runs the SFTP services or daemon. This can be restricted by multiple ways. 

* Custom FileSystem & FileSystemProvider implementation
* Custom SftpFileSystemAccessor implementation

```java
public class MySftpFileSystemAccessor implements SftpFileSystemAccessor {

	public Path resolveLocalFilePath(ServerSession session, SftpSubsystemProxy subsystem, Path rootDir,
			String remotePath) throws IOException, InvalidPathException {
		{
			Path parent = getHomeDir(session);
			String path = SelectorUtils.translateToLocalFileSystemPath(remotePath, '/', parent.getFileSystem());
			if (path.startsWith("/data/username"))
				return parent.resolve(path);
			else
				return parent;
		}
	}
}
```

While Apache SSHD provides the ways to customize to meet the authentication and file access limits, Out of the box, it may not scale to 1000's of users - since Apache uses per thread model for each connection. This behaviour can be changed and fixed thread pool can be introduced by customizing SftpSubsystem implementation. 