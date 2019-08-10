package com.guan;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class FtpUtil2 {
	public static boolean uploadFile(String url, int port, String username,
			String password, String path, String filename, InputStream input) {
		Boolean success = false;
		FTPClient ftp = new FTPClient();
		try {
			ftp.connect(url, port);

			ftp.login(username, password);
			int reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				return success;
			}
			ftp.changeWorkingDirectory(path);
			ftp.storeFile(filename, input);

			input.close();
			ftp.logout();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException localIOException4) {
				}
			}
		}
		return success;
	}

	public static boolean downFile(String url, int port, String username,
			String password, String remotePath, String filename,
			String localPath) {
		Boolean success = false;
		FTPClient ftp = new FTPClient();
		try {
			ftp.connect(url, port);

			ftp.login(username, password);
			int reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				return success;
			}
			ftp.changeWorkingDirectory(remotePath);
			FTPFile[] fs = ftp.listFiles();
			for (FTPFile ff : fs) {
				if (ff.getName().equals(filename)) {
					File localFile = new File(localPath + "/" + ff.getName());

					OutputStream is = new FileOutputStream(localFile);
					ftp.retrieveFile(ff.getName(), is);
					is.close();
				}
			}
			ftp.logout();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException localIOException4) {
				}
			}
		}
		return success;
	}
}
