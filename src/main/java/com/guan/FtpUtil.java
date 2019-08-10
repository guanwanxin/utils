package com.guan;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class FtpUtil {
	private static Log log = LogFactory.getLog(FtpUtil.class);

	public static boolean uploadFile(String url, int port, String username,
			String password, String path, String filename, InputStream input) {
		boolean success = false;
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
			Logger.getLogger(FtpUtil.class.getName()).log(Level.SEVERE,
					"上传文件上FTP服务器失败", e);
		} finally {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException ioe) {
				}
			}
		}
		return success;
	}

	public static File downloadFile(String url, int port, String username,
			String password, String path, String filename,
			String savetoLocalFileName) throws Exception {
		FTPClient fc = null;
		FileOutputStream os = null;
		File localSavedFile = new File(savetoLocalFileName);
		localSavedFile.createNewFile();
		try {
			fc = new FTPClient();
			fc.setConnectTimeout(5000);
			int reply = -123;
			fc.connect(url, port);
			reply = fc.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				throw new Exception("连接FTP服务器[" + url + "]失败, port:" + port);
			}
			boolean ok = fc.login(username, password);

			if (!ok) {
				throw new Exception("登录FTP服务器失败,连接被拒绝, 可能是登录名/密码错误");
			}

			fc.enterLocalActiveMode();
			fc.setFileTransferMode(10);
			fc.setDataTimeout(60000);

			fc.changeWorkingDirectory(path);
			os = new FileOutputStream(localSavedFile);
			boolean isSuccessfully = fc.retrieveFile(filename, os);
			if (!isSuccessfully) {
				throw new Exception("文件[ftp://" + url + ":" + port + "/" + path
						+ "/" + filename + "]没有找到");
			}
		} catch (Exception e) {
			throw new Exception("连接FTP服务器下载文件失败", e);
		} finally {
			if ((fc != null) && (fc.isConnected())) {
				try {
					fc.logout();
					fc.disconnect();
				} catch (IOException e) {
				}
			}
			if (os != null) {
				try {
					os.close();
					os = null;
				} catch (IOException e) {
				}
			}
			fc = null;
		}
		return localSavedFile;
	}

	public static Map<String, String> ftpConnectInfoProcess(String ftpCntInfoStr) {
		Map<String, String> infoMap = new HashMap<String, String>();
		String[] ftpCntInfoArray = ftpCntInfoStr.split("\\|{3}");
		String[] pathInfoArray = ftpCntInfoArray[1].split("/", 2);
		String ftpHost = pathInfoArray[0];
		String ftpPort = ftpCntInfoArray[2];
		String ftpLoginUser = ftpCntInfoArray[3];
		String ftpLoginPwd = ftpCntInfoArray[4];
		String ftpPath = "/" + pathInfoArray[1];
		infoMap.put("host", ftpHost);
		infoMap.put("port", ftpPort);
		infoMap.put("user", ftpLoginUser);
		infoMap.put("pwd", ftpLoginPwd);
		infoMap.put("path", ftpPath);
		return infoMap;
	}

	public static File downLodeSftpFile(String host, int port, String username,
			String password, String path, String fileName,
			String savetoLocalFileName) {
		ChannelSftp sftp = null;
		Channel channel = null;
		Session sshSession = null;
		OutputStream output = null;
		File file = null;
		try {
			file = new File(savetoLocalFileName);
			if (file.isFile()) {
				file.createNewFile();
			}
			output = new FileOutputStream(file);
			// 创建jschclient连接sftp
			JSch jsch = new JSch();
			sshSession = jsch.getSession(username, host, port);
			sshSession.setPassword(password);
			Properties sshConfig = new Properties();
			sshConfig.put("StrictHostKeyChecking", "no");
			sshConfig.put("UseDNS", "no");
			sshSession.setConfig(sshConfig);
			sshSession.connect();
			ChannelSftp openChannel = (ChannelSftp) sshSession
					.openChannel("sftp");
			openChannel.connect();
			// 进入到文件目录
			openChannel.cd(path);
			// 下载文件
			openChannel.get(fileName, output);
		} catch (Exception e) {
			try {
				throw new Exception("连接FTP服务器下载文件失败", e);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			closeChannel(sftp);
			closeChannel(channel);
			closeSession(sshSession);
			try {
				if (output != null) {
					output.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	public static File downloadFileByName(String url, int port,
			String username, String password, String path, String filename,
			String savetoLocalFileName) throws Exception {
		FTPClient fc = null;
		FileOutputStream os = null;
		File localSavedFile = new File(savetoLocalFileName);
		localSavedFile.createNewFile();
		try {
			fc = new FTPClient();
			fc.setConnectTimeout(5000);
			int reply = -123;
			fc.connect(url, port);
			reply = fc.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				throw new Exception("连接FTP服务器[" + url + "]失败, port:" + port);
			}
			boolean ok = fc.login(username, password);

			if (!ok) {
				throw new Exception("登录FTP服务器失败,连接被拒绝, 可能是登录名/密码错误");
			}

			fc.enterLocalActiveMode();
			fc.setFileTransferMode(10);
			fc.setDataTimeout(60000);

			fc.changeWorkingDirectory(path);
			FTPFile[] files = fc.listFiles();
			List<FTPFile> needList = new ArrayList<FTPFile>();
			for (FTPFile file : files) {
				if (file.isFile()) {
					if (file.getName().startsWith(filename)) {
						needList.add(file);
						Calendar c = file.getTimestamp();
						System.out.println("______****************"
								+ DateUtil.convertDateToStr(c.getTime(),
										"yyyy-MM-dd HH:mm:ss"));
					}
				}
			}
			FTPFile downFile = null;

			if (needList.size() == 1) {
				downFile = needList.get(0);
			}
			for (int i = 0; i < needList.size() - 1; i++) {

				long time = needList.get(i).getTimestamp().getTime().getTime();
				long time2 = needList.get(i + 1).getTimestamp().getTime()
						.getTime();
				if (time > time2) {
					downFile = needList.get(i);
				} else {
					downFile = needList.get(i + 1);
				}
			}

			os = new FileOutputStream(localSavedFile);
			boolean isSuccessfully = fc.retrieveFile(downFile.getName(), os);
			if (!isSuccessfully) {
				throw new Exception("文件[ftp://" + url + ":" + port + "/" + path
						+ "/" + filename + "]没有找到");
			}
		} catch (Exception e) {
			throw new Exception("连接FTP服务器下载文件失败", e);
		} finally {
			if ((fc != null) && (fc.isConnected())) {
				try {
					fc.logout();
					fc.disconnect();
				} catch (IOException e) {
				}
			}
			if (os != null) {
				try {
					os.close();
					os = null;
				} catch (IOException e) {
				}
			}
			fc = null;
		}
		return localSavedFile;
	}

	/**
	 * 上传文件
	 * 
	 * @param host
	 *            主机名
	 * @param port
	 *            端口号
	 * @param username
	 *            用户名
	 * @param password
	 *            密码
	 * @param directory
	 *            上传的目录
	 * @param uploadFileName
	 *            要上传的文件名
	 * @param fileInputStream
	 *            要上传的数据流
	 */
	public static boolean uploadSftpFile(String host, int port,
			String username, String password, String directory,
			String uploadFileName, InputStream fileInputStream) {
		ChannelSftp sftp = null;
		Channel channel = null;
		Session sshSession = null;
		OutputStream output = null;
		try {
			// 创建jschclient连接sftp
			JSch jsch = new JSch();
			sshSession = jsch.getSession(username, host, port);
			sshSession.setPassword(password);
			Properties sshConfig = new Properties();
			sshConfig.put("StrictHostKeyChecking", "no");
			sshConfig.put("UseDNS", "no");
			sshSession.setConfig(sshConfig);
			sshSession.connect();
			log.info("SFTP Session connected.");
			ChannelSftp openChannel = (ChannelSftp) sshSession
					.openChannel("sftp");
			openChannel.connect();
			log.info("Connected to " + host);
			// 进入到文件目录
			openChannel.cd(directory);
			// 上传文件
			openChannel.put(fileInputStream, uploadFileName);
			return true;
		} catch (Exception e) {
			log.error("连接FTP服务器上传文件失败,原因：");
			e.printStackTrace();
			return false;
		} finally {
			closeChannel(sftp);
			closeChannel(channel);
			closeSession(sshSession);
			try {
				if (output != null) {
					output.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void closeChannel(Channel channel) {
		if (channel != null) {
			if (channel.isConnected()) {
				channel.disconnect();
			}
		}
	}

	private static void closeSession(Session session) {
		if (session != null) {
			if (session.isConnected()) {
				session.disconnect();
			}
		}
	}
}
