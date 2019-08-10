package com.guan;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class FtpHelper {
	public static Log log = LogFactory.getLog(FtpHelper.class);

	public FtpHelper() {
		this.ftpClient = new FTPClient();
	}

	public boolean connect(String hostname, int port, String username,
			String password) throws IOException {
		this.ftpClient.connect(hostname, port);
		this.ftpClient.setControlEncoding("GBK");
		if ((FTPReply.isPositiveCompletion(this.ftpClient.getReplyCode()))
				&& (this.ftpClient.login(username, password))) {
			return true;
		}

		disconnect();
		return false;
	}

	public DownloadStatus download(String remote, String local)
			throws IOException {
		this.ftpClient.enterLocalPassiveMode();

		this.ftpClient.setFileType(2);

		FTPFile[] files = this.ftpClient.listFiles(new String(remote
				.getBytes("GBK"), "iso-8859-1"));
		if (files.length != 1) {
			System.out.println("远程文件不存在");
			return DownloadStatus.Remote_File_Noexist;
		}

		long lRemoteSize = files[0].getSize();
		File f = new File(local);
		DownloadStatus result;
		if (f.exists()) {
			long localSize = f.length();

			if (localSize >= lRemoteSize) {
				System.out.println("本地文件大于远程文件，下载中止");
				return DownloadStatus.Local_Bigger_Remote;
			}

			FileOutputStream out = new FileOutputStream(f, true);
			this.ftpClient.setRestartOffset(localSize);
			InputStream in = this.ftpClient.retrieveFileStream(new String(
					remote.getBytes("GBK"), "iso-8859-1"));
			byte[] bytes = new byte['Ѐ'];
			long step = lRemoteSize / 100L;
			long process = localSize / step;
			int c;
			while ((c = in.read(bytes)) != -1) {
				out.write(bytes, 0, c);
				localSize += c;
				long nowProcess = localSize / step;
				if (nowProcess > process) {
					process = nowProcess;
					if (process % 10L == 0L) {
						System.out.println("下载进度：" + process);
					}
				}
			}
			in.close();
			out.close();
			boolean isDo = this.ftpClient.completePendingCommand();
			if (isDo) {
				result = DownloadStatus.Download_From_Break_Success;
			} else {
				result = DownloadStatus.Download_From_Break_Failed;
			}
		} else {
			OutputStream out = new FileOutputStream(f);
			InputStream in = this.ftpClient.retrieveFileStream(new String(
					remote.getBytes("GBK"), "iso-8859-1"));
			byte[] bytes = new byte['Ѐ'];
			long step = lRemoteSize / 100L;
			long process = 0L;
			long localSize = 0L;
			int c;
			while ((c = in.read(bytes)) != -1) {
				out.write(bytes, 0, c);
				localSize += c;
				long nowProcess = localSize / step;
				if (nowProcess > process) {
					process = nowProcess;
					if (process % 10L == 0L) {
						System.out.println("下载进度：" + process);
					}
				}
			}
			in.close();
			out.close();
			boolean upNewStatus = this.ftpClient.completePendingCommand();
			if (upNewStatus) {
				result = DownloadStatus.Download_New_Success;
			} else {
				result = DownloadStatus.Download_New_Failed;
			}
		}
		return result;
	}

	public UploadStatus upload(String local, String remote) throws IOException {
		this.ftpClient.enterLocalPassiveMode();

		this.ftpClient.setFileType(2);
		this.ftpClient.setControlEncoding("GBK");

		String remoteFileName = remote;
		if (remote.contains("/")) {
			remoteFileName = remote.substring(remote.lastIndexOf("/") + 1);

			if (createDirecroty(remote, this.ftpClient) == UploadStatus.Create_Directory_Fail) {
				return UploadStatus.Create_Directory_Fail;
			}
		}

		FTPFile[] files = this.ftpClient.listFiles(new String(remoteFileName
				.getBytes("GBK"), "iso-8859-1"));
		UploadStatus result;
		if (files.length == 1) {
			long remoteSize = files[0].getSize();
			File f = new File(local);
			long localSize = f.length();
			if (remoteSize == localSize)
				return UploadStatus.File_Exits;
			if (remoteSize > localSize) {
				return UploadStatus.Remote_Bigger_Local;
			}

			result = uploadFile(remoteFileName, f, this.ftpClient, remoteSize);

			if (result == UploadStatus.Upload_From_Break_Failed) {
				if (!this.ftpClient.deleteFile(remoteFileName)) {
					return UploadStatus.Delete_Remote_Faild;
				}
				result = uploadFile(remoteFileName, f, this.ftpClient, 0L);
			}
		} else {
			result = uploadFile(remoteFileName, new File(local),
					this.ftpClient, 0L);
		}
		return result;
	}

	public void disconnect() throws IOException {
		if (this.ftpClient.isConnected()) {
			this.ftpClient.disconnect();
		}
	}

	public UploadStatus createDirecroty(String remote, FTPClient ftpClient)
			throws IOException {
		UploadStatus status = UploadStatus.Create_Directory_Success;
		String directory = remote.substring(0, remote.lastIndexOf("/") + 1);
		if ((!directory.equalsIgnoreCase("/"))
				&& (!ftpClient.changeWorkingDirectory(new String(directory
						.getBytes("GBK"), "iso-8859-1")))) {
			int start = 0;
			int end = 0;
			if (directory.startsWith("/")) {
				start = 1;
			} else {
				start = 0;
			}
			end = directory.indexOf("/", start);
			for (;;) {
				String subDirectory = new String(remote.substring(start, end)
						.getBytes("GBK"), "iso-8859-1");
				if (!ftpClient.changeWorkingDirectory(subDirectory)) {
					if (ftpClient.makeDirectory(subDirectory)) {
						ftpClient.changeWorkingDirectory(subDirectory);
					} else {
						System.out.println("创建目录失败");
						return UploadStatus.Create_Directory_Fail;
					}
				}

				start = end + 1;
				end = directory.indexOf("/", start);

				if (end <= start) {
					break;
				}
			}
		}
		return status;
	}

	private FTPClient ftpClient;

	public UploadStatus uploadFile(String remoteFile, File localFile,
			FTPClient ftpClient, long remoteSize) throws IOException {
		long step = localFile.length() / 100L;
		long process = 0L;
		long localreadbytes = 0L;
		RandomAccessFile raf = new RandomAccessFile(localFile, "r");
		OutputStream out = ftpClient.appendFileStream(new String(remoteFile
				.getBytes("GBK"), "iso-8859-1"));

		if (remoteSize > 0L) {
			ftpClient.setRestartOffset(remoteSize);
			process = remoteSize / step;
			raf.seek(remoteSize);
			localreadbytes = remoteSize;
		}
		byte[] bytes = new byte['Ѐ'];
		int c;
		while ((c = raf.read(bytes)) != -1) {
			out.write(bytes, 0, c);
			localreadbytes += c;
			if (localreadbytes / step != process) {
				process = localreadbytes / step;
				System.out.println("上传进度:" + process);
			}
		}

		out.flush();
		raf.close();
		out.close();
		boolean result = ftpClient.completePendingCommand();
		UploadStatus status;
		if (remoteSize > 0L) {
			status = result ? UploadStatus.Upload_From_Break_Success
					: UploadStatus.Upload_From_Break_Failed;
		} else {
			status = result ? UploadStatus.Upload_New_File_Success
					: UploadStatus.Upload_New_File_Failed;
		}
		return status;
	}

	public boolean deleteFile(String pathName) {
		boolean flag = false;
		try {
			flag = this.ftpClient.deleteFile(pathName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	public void deleteFile(List<String> fileList) {
		if ((fileList != null) && (fileList.size() > 0)) {
			for (String fileName : fileList) {
				try {
					this.ftpClient.deleteFile(fileName);
					log.info("删除七天前文件：" + fileName);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public List<String> getFileList(String path) throws IOException {
		this.ftpClient.enterLocalPassiveMode();
		FTPFile[] ftpFiles = this.ftpClient.listFiles(path);
		List<String> retList = new ArrayList<String>();
		if ((ftpFiles == null) || (ftpFiles.length == 0)) {
			return retList;
		}
		for (FTPFile ftpFile : ftpFiles) {
			if (ftpFile.isFile()) {
				retList.add(ftpFile.getName());
			}
		}
		return retList;
	}

	public static enum UploadStatus {
		Create_Directory_Fail, Create_Directory_Success, Upload_New_File_Success, Upload_New_File_Failed, File_Exits, Remote_Bigger_Local, Upload_From_Break_Success, Upload_From_Break_Failed, Delete_Remote_Faild;

		private UploadStatus() {
		}
	}

	public static enum DownloadStatus {
		Remote_File_Noexist, Download_New_Success, Download_New_Failed, Local_Bigger_Remote, Download_From_Break_Success, Download_From_Break_Failed;

		private DownloadStatus() {
		}
	}
}
