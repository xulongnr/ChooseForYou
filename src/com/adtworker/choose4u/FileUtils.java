package com.adtworker.choose4u;

import java.io.File;

import android.util.Log;

public class FileUtils {

	/**
	 * remove cached folds of youmi, domob and so on
	 */
	public static void clearAdCache() {
		delApkFile("/sdcard/Download");
		delFolder("/sdcard/ad");
		delFolder("/sdcard/adwo");
		delFolder("/sdcard/logger");
		delFolder("/sdcard/Tencent/MobWIN");
		delFolder("/sdcard/DomobAppDownload");
		delFolder("/sdcard/youmicache");
		delFolder("/sdcard/logger");
		delFolder("/sdcard/app_dump");
		delFolder("/sdcard/UCDownloads");
	}

	public static void delFolder(String floder) {
		File f = new File(floder);
		File[] fl = f.listFiles();
		if (fl == null) {
			return;
		}
		for (int i = 0; i < fl.length; i++) {
			if (fl[i].isDirectory()) {
				delFolder(fl[i].getAbsolutePath());
			} else {
				fl[i].delete();
			}
		}
	}

	public static void delApkFile(String path) {
		File f = new File(path);
		File[] fl = f.listFiles();
		if (fl == null) {
			return;
		}
		for (int i = 0; i < fl.length; i++) {
			if (fl[i].isDirectory()) {
				delApkFile(fl[i].getAbsolutePath());
			} else {
				if (fl[i].toString().toLowerCase().endsWith("apk")) {
					fl[i].delete();
					Log.e("test", fl[i].toString() + " is del");
				}
			}
		}
	}

	/**
	 * get total size of specified folder
	 * 
	 * @param path
	 * @return long
	 */
	public static long getFolderSize(String path) {
		File file = new File(path);
		if (!file.exists())
			return 0;

		if (!file.isDirectory()) {
			return file.length();
		}
		File[] tempListFiles = file.listFiles();
		long totalSize = 0;
		for (int i = 0; i < tempListFiles.length; i++) {
			totalSize += getFolderSize(tempListFiles[i].getAbsolutePath());
		}
		return totalSize;
	}

	/**
	 * get available size of specified mount partition
	 * 
	 * @param path
	 * @return long
	 */
	public static long getAvailableSize(String path) {
		File pathFile = new File(path);
		android.os.StatFs statfs = new android.os.StatFs(pathFile.getPath());
		long nBlocSize = statfs.getBlockSize();
		long nAvailaBlock = statfs.getAvailableBlocks();
		return nAvailaBlock * nBlocSize;
	}
}
