package com.guan;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * 
 * 连接SFTP，上传和下载文件
 * 
 */
public class SftpUtil {
	private static Log log = LogFactory.getLog(SftpUtil.class);

	private static Session sshSession = null;
	private static ChannelSftp sftp = null;
	private static Channel channel = null;

	/**
	 * 获得SFTP连接
	 * 
	 * @param host
	 *            服务器地址
	 * @param port
	 *            端口号
	 * @param userName
	 *            用户名
	 * @param password
	 *            密码
	 * @param timeout
	 *            超时时间（毫秒）
	 * @return
	 */
	public static ChannelSftp getConnect(String host, int port, String userName, String password, int timeout) {
		JSch jsch = new JSch();
		try {
			// 创建jschclient连接sftp
			if (port > 0) {
				sshSession = jsch.getSession(userName, host, port);
			} else {
				sshSession = jsch.getSession(userName, host);
			}
			sshSession.setPassword(password);
			Properties sshConfig = new Properties();
			sshConfig.put("StrictHostKeyChecking", "no");
			sshSession.setConfig(sshConfig);
			sshSession.setTimeout(timeout);
			sshSession.connect();
			channel = sshSession.openChannel("sftp");
			channel.connect();
			sftp = (ChannelSftp) channel;
		} catch (Exception e) {
			log.info("连接SFTP服务器失败，原因为：");
			e.printStackTrace();
			throw new RuntimeException("连接SFTP服务器失败。");
		}
		return sftp;
	}

	/**
	 * 通过密钥获得SFTP连接
	 * 
	 * @param host
	 *            服务器地址
	 * @param port
	 *            端口号
	 * @param userName
	 *            用户名
	 * @param privateKeyFile
	 *            密钥路径
	 * @param passphrase
	 *            密钥密码
	 * @param timeout
	 *            超时时间（毫秒）
	 * @return
	 */
	public static ChannelSftp getConnectByPrivateKey(String host, int port, String userName, String privateKeyFile,
			String passphrase, int timeout) {
		JSch jsch = new JSch();
		try {
			// 创建jschclient连接sftp
			if (privateKeyFile != null && !"".equals(privateKeyFile)) {
				if (passphrase != null && !"".equals(passphrase)) {
					jsch.addIdentity(privateKeyFile, passphrase);
				} else {
					jsch.addIdentity(privateKeyFile);
				}
			}

			if (port > 0) {
				sshSession = jsch.getSession(userName, host, port);
			} else {
				sshSession = jsch.getSession(userName, host);
			}

			Properties sshConfig = new Properties();
			sshConfig.put("StrictHostKeyChecking", "no");
			sshSession.setConfig(sshConfig);
			sshSession.setTimeout(timeout);
			sshSession.connect();
			channel = sshSession.openChannel("sftp");
			channel.connect();
			sftp = (ChannelSftp) channel;
		} catch (Exception e) {
			log.info("连接SFTP服务器失败，原因为：");
			e.printStackTrace();
			throw new RuntimeException("连接SFTP服务器失败。");
		}
		return sftp;
	}

	/**
	 * 2.打开或者进入指定目录
	 * 
	 * @param directory
	 *            指定目录
	 * @param sftp
	 *            SFTP连接
	 * @return boolean
	 * @author: MSWX
	 */
	public static boolean openDir(String directory, ChannelSftp sftp) {
		try {
			sftp.cd(directory);
			return true;
		} catch (SftpException e) {
			log.error(e + "");
			return false;
		}
	}

	/**
	 * 3. 遍历一个目录，并得到这个路径的集合list（等递归把这个目录下遍历结束后list存放的就是这个目录下的所有文件的路径集合）
	 * 
	 * @param pathName
	 *            指定目录
	 * @param sftp
	 *            SFTP连接
	 * @param MatchingStr
	 *            需要匹配的字符串
	 * @return
	 * @throws SftpException
	 *             List<String>
	 * @author: MSWX
	 */
	@SuppressWarnings("null")
	public static List<String> getSftpPathList(String pathName, ChannelSftp sftp, String MatchingStr)
			throws SftpException {
		List<String> sftpList = new ArrayList<>();
		boolean flag = openDir(pathName, sftp);
		try {
			if (flag) {
				Vector<?> vv = sftp.ls("./");
				if (vv == null && vv.size() == 0) {
					return null;
				} else {
					for (Object object : vv) {
						ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) object;
						String filename = entry.getFilename();
						if (".".equals(filename) || "..".equals(filename)) {
							continue;
						}
						if (openDir(pathName + filename + "/", sftp)) {
							// 能打开，说明是目录，接着遍历
							getSftpPathList(pathName + filename + "/", sftp, MatchingStr);
						} else {
							if (filename.contains(MatchingStr)) {
								sftpList.add(filename);
							}
						}
					}
				}
			} else {
				log.info("对应的目录" + pathName + "不存在！");
			}
		} catch (Exception e) {
			log.info("查找文件失败，原因为：");
			e.printStackTrace();
			return null;
		} finally {
			close();
		}
		return sftpList;
	}

	/**
	 * 4. 遍历一个目录，并得到这个路径的集合list（ 修改时间为对应日期的文件名）
	 * 
	 * @param pathName
	 *            指定目录
	 * @param sftp
	 *            SFTP连接
	 * @param Date
	 *            最后修改日期
	 * @return
	 * @throws SftpException
	 *             List<String>
	 * @author: MSWX
	 */
	@SuppressWarnings("null")
	public static List<String> getSftpPathListByUpdateTime(String pathName, ChannelSftp sftp, Date date)
			throws SftpException {
		List<String> sftpList = new ArrayList<>();
		boolean flag = openDir(pathName, sftp);
		try {
			if (flag) {
				Vector<?> vv = sftp.ls("./");
				if (vv == null && vv.size() == 0) {
					return null;
				} else {
					for (Object object : vv) {
						ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) object;
						String filename = entry.getFilename();
						if (".".equals(filename) || "..".equals(filename)) {
							continue;
						}
						if (openDir(pathName + filename + "/", sftp)) {
							// 能打开，说明是目录，接着遍历
							getSftpPathListByUpdateTime(pathName + filename + "/", sftp, date);
						} else {
							int lastUpdateTime = entry.getAttrs().getATime();
							long diffDay = DateUtil.dateDiffForDay(new Date(Long.valueOf(lastUpdateTime)), date);
							log.info("差异天数为" + diffDay);
							if (diffDay == 0) {
								log.info("当天更新的文件为：" + filename);
								sftpList.add(filename);
							}
						}
					}
				}
			} else {
				log.info("对应的目录" + pathName + "不存在！");
			}
		} catch (Exception e) {
			log.info("查找文件失败，原因为：");
			e.printStackTrace();
			return null;
		} finally {
			close();
		}
		return sftpList;
	}

	/**
	 * 4.关闭资源
	 * 
	 * @author: MSWX
	 */
	public static void close() {
		if (sftp != null) {
			sftp.disconnect();
		}
		if (sshSession != null) {
			sshSession.disconnect();
		}
		if (channel != null) {
			channel.disconnect();
		}
	}

	/**
	 * 下载文件
	 * 
	 * @param host
	 *            服务器地址
	 * @param port
	 *            端口号
	 * @param userName
	 *            用户名
	 * @param password
	 *            密码
	 * @param downloadFilepath
	 *            下载文件的路径
	 * @param fileName
	 *            下载文件的文件名
	 * @param savetoLocalFileName
	 *            保存本地的文件名和路径
	 * @param timeout
	 *            超时时间（毫秒）
	 * @return
	 */
	public static Map<String, String> downLodeSftpFile(String host, int port, String userName, String password,
			String downloadFilepath, List<String> fileNames, String saveFilePath, int timeout) {
		Map<String, String> downLoadFilePaths = new HashMap<String, String>();
		try {
			ChannelSftp sftp = getConnect(host, port, userName, password, timeout);
			// 下载文件
			for (String directory : fileNames) {// 循环文件下载
				File srcFile = new File(saveFilePath + File.separator + directory);
				if (!srcFile.exists()) {
					(new File(srcFile.getParent())).mkdirs();
				}
				sftp.cd(downloadFilepath);// 进入下载地址
				sftp.get(directory, saveFilePath);// 目标文件和保存地址
				downLoadFilePaths.put(directory, srcFile.getPath());
			}
		} catch (Exception e) {
			log.info("下载文件失败，原因为：");
			e.printStackTrace();
			throw new RuntimeException("下载文件失败。");
		} finally {
			close();
		}
		return downLoadFilePaths;
	}

	/**
	 * 下载文件
	 * 
	 * @param host
	 *            服务器地址
	 * @param port
	 *            端口号
	 * @param userName
	 *            用户名
	 * @param privateKeyFile
	 *            密钥路径
	 * @param passphrase
	 *            密钥密码
	 * @param downloadFilepath
	 *            下载文件的路径
	 * @param fileName
	 *            下载文件的文件名
	 * @param saveFilePath
	 *            保存本地的路径
	 * @param timeout
	 *            超时时间（毫秒）
	 * @return
	 */
	public static List<String> downLodeSftpFileByPrivateKey(String host, int port, String userName,
			String privateKeyFile, String passphrase, String downloadFilepath, List<String> fileNames,
			String saveFilePath, int timeout) {
		List<String> downLoadFilePaths = new ArrayList<String>();
		try {
			ChannelSftp sftp = getConnectByPrivateKey(host, port, userName, privateKeyFile, passphrase, timeout);
			sftp.cd(downloadFilepath);// 进入下载地址
			for (String directory : fileNames) {// 循环文件下载
				File srcFile = new File(saveFilePath + File.separator + directory);
				if (!srcFile.exists()) {
					(new File(srcFile.getParent())).mkdirs();
				}
				sftp.get(directory, saveFilePath);// 目标文件和保存地址
				downLoadFilePaths.add(srcFile.getPath());
			}
		} catch (Exception e) {
			log.info("下载文件失败，原因为：");
			e.printStackTrace();
			throw new RuntimeException("下载文件失败。");
		} finally {
			close();
		}
		log.info("下载的文件路径为：" + downLoadFilePaths);
		return downLoadFilePaths;
	}

	/**
	 * 上传文件
	 * 
	 * @param host
	 *            主机名
	 * @param port
	 *            端口号
	 * @param userName
	 *            用户名
	 * @param password
	 *            密码
	 * @param directory
	 *            上传的目录
	 * @param uploadFileName
	 *            要上传的文件名
	 * @param fileInputStream
	 *            要上传的数据流
	 * @param timeout
	 *            超时时间（毫秒）
	 */
	public static boolean uploadSftpFile(String host, int port, String userName, String password, String directory,
			String uploadFileName, InputStream fileInputStream, int timeout) {
		OutputStream output = null;
		try {
			ChannelSftp sftp = getConnect(host, port, userName, password, timeout);
			log.info("Connected to " + host);
			// 进入到文件目录
			sftp.cd(directory);
			// 上传文件
			sftp.put(fileInputStream, uploadFileName);
			return true;
		} catch (Exception e) {
			log.error("连接FTP服务器上传文件失败,原因：");
			e.printStackTrace();
			return false;
		} finally {
			close();
			try {
				if (output != null) {
					output.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
