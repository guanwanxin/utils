package com.guan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.Reader;

public class ReadFileUtil2 {
	public static void readFileByRandomAccess(String fileName) {
		RandomAccessFile randomFile = null;
		try {
			System.out.println("随机读取一段文件内容：");

			randomFile = new RandomAccessFile(fileName, "r");

			long fileLength = randomFile.length();

			int beginIndex = fileLength > 4L ? 0 : 0;

			randomFile.seek(beginIndex);
			byte[] bytes = new byte[10];
			int byteread = 0;
			while ((byteread = randomFile.read(bytes)) != -1) {
				System.out.write(bytes, 0, byteread);
			}
			return;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (randomFile != null) {
				try {
					randomFile.close();
				} catch (IOException localIOException3) {
				}
			}
		}
	}

	public static void readFileByLines(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		try {
			System.out.println("以行为单位读取文件内容，一次读一整行：");
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			int line = 1;
			while ((tempString = reader.readLine()) != null) {
				System.out.println("line " + line + ": " + tempString);
				line++;
			}
			reader.close();
			return;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException localIOException3) {
				}
			}
		}
	}

	public static void readFileByChars(String fileName) {
		File file = new File(fileName);
		Reader reader = null;
		try {
			System.out.println("以字符为单位读取文件内容，一次读一个字节：");

			reader = new InputStreamReader(new FileInputStream(file));
			int tempchar;
			while ((tempchar = reader.read()) != -1) {
				if ((char) tempchar != '\r') {
					System.out.print((char) tempchar);
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			System.out.println("\n以字符为单位读取文件内容，一次读多个字节：");

			char[] tempchars = new char[30];
			int charread = 0;
			reader = new InputStreamReader(new FileInputStream(fileName));
			while ((charread = reader.read(tempchars)) != -1) {
				if ((charread == tempchars.length)
						&& (tempchars[(tempchars.length - 1)] != '\r')) {
					System.out.print(tempchars);
				} else {
					for (int i = 0; i < charread; i++) {
						if (tempchars[i] != '\r') {
							System.out.print(tempchars[i]);
						}
					}
				}
			}
			return;
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException localIOException2) {
				}
			}
		}
	}

	public static void readFileByBytes(String fileName) {
		File file = new File(fileName);
		InputStream in = null;
		try {
			System.out.println("以字节为单位读取文件内容，一次读一个字节：");

			in = new FileInputStream(file);
			int tempbyte;
			while ((tempbyte = in.read()) != -1) {
				System.out.println(tempbyte);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			System.out.println("以字节为单位读取文件内容，一次读多个字节：");

			byte[] tempbytes = new byte[100];
			int byteread = 0;
			in = new FileInputStream(fileName);
			showAvailableBytes(in);
			while ((byteread = in.read(tempbytes)) != -1) {
				System.out.write(tempbytes, 0, byteread);
			}
			return;
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException localIOException2) {
				}
			}
		}
	}

	private static void showAvailableBytes(InputStream in) {
		try {
			System.out.println("当前字节输入流中的字节数为:" + in.available());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
