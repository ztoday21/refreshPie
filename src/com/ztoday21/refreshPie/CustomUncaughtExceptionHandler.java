package com.ztoday21.refreshPie;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * User: hermit
 * Date: 13. 9. 23
 * Time: ¿ÀÈÄ 6:37
 */
public class CustomUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
	private Context mContext;
	private Thread.UncaughtExceptionHandler mDefaultUncaughtExceptionHandler;

	public CustomUncaughtExceptionHandler(Context context) {
		mDefaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		mContext = context;
	}


	private void logToFile(String log) {
		final String filePath = Environment.getExternalStorageDirectory() + "/errorLog.txt";


		try {
			@SuppressWarnings("resource")
			RandomAccessFile file = new RandomAccessFile(filePath, "rw");
			file.seek(file.length());
			file.writeBytes("\r\n" + log);
		}
		catch (IOException e) {
			e.printStackTrace();
		}

	}

	//@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		final String logMsgParams = makeStackTrace(thread, ex);

		//TODO: to process exception for reporting.
		logToFile(logMsgParams);

		callDefaultUncaughtExceptionHandler(thread, ex);
	}

	private static String makeStackTrace(Thread thread, Throwable ex) {
		StringBuilder errLog = new StringBuilder();
		errLog.append("FATAL EXCEPTION: " + thread.getName());
		errLog.append("\n");

		errLog.append(ex.toString());
		errLog.append("\n");

		StackTraceElement[] stack = ex.getStackTrace();
		for (StackTraceElement element : stack) {
			errLog.append("    at " + element);
			errLog.append("\n");
		}

		StackTraceElement[] parentStack = stack;
		Throwable throwable = ex.getCause();
		while (throwable != null) {
			errLog.append("Caused by: ");
			errLog.append(throwable.toString());
			errLog.append("\n");

			StackTraceElement[] currentStack = throwable.getStackTrace();
			int duplicates = countDuplicates(currentStack, parentStack);
			for (int i = 0; i < currentStack.length - duplicates; i++) {
				errLog.append("    at " + currentStack[i].toString());
				errLog.append("\n");
			}
			if (duplicates > 0) {
				errLog.append("    ... " + duplicates + " more");
			}
			parentStack = currentStack;
			throwable = throwable.getCause();
		}

		return errLog.toString();
	}

	private static int countDuplicates(StackTraceElement[] currentStack, StackTraceElement[] parentStack) {
		int duplicates = 0;
		int parentIndex = parentStack.length;
		for (int i = currentStack.length; --i >= 0 && --parentIndex >= 0;) {
			StackTraceElement parentFrame = parentStack[parentIndex];
			if (parentFrame.equals(currentStack[i])) {
				duplicates++;
			} else {
				break;
			}
		}
		return duplicates;
	}

	private void callDefaultUncaughtExceptionHandler(Thread thread, Throwable ex) {
		if (mDefaultUncaughtExceptionHandler != null) {
			mDefaultUncaughtExceptionHandler.uncaughtException(thread, ex);
		}
	}
}
