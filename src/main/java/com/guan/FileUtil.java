package com.guan;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class FileUtil {
	public static boolean deleteFolder(String sPath) {
		boolean flag = false;
		File file = new File(sPath);

		if (!file.exists()) {
			return flag;
		}

		if (file.isFile()) {
			return deleteFile(sPath);
		}
		return deleteDirectory(sPath);
	}

	public static boolean deleteDirectory(String sPath) {
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);

		if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
			return false;
		}
		boolean flag = true;

		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag)
					break;
			} else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if (!flag) {
			return false;
		}
		if (dirFile.delete()) {
			return true;
		}
		return false;
	}

	public static boolean deleteFolderFiles(String sPath) {
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);

		if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
			return false;
		}
		boolean flag = true;

		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag)
					break;
			} else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}
		return flag;
	}

	public static boolean deleteFile(String sPath) {
		boolean flag = false;
		File file = new File(sPath);

		if ((file.isFile()) && (file.exists())) {
			file.delete();
			flag = true;
		}
		return flag;
	}

	public static void createFile(String fileDir, String textStr)
			throws FileNotFoundException {
		File ctlFile2 = new File(fileDir);
		PrintStream printStream2 = new PrintStream(new FileOutputStream(
				ctlFile2));
		printStream2.println(textStr);
		printStream2.close();
	}
}
