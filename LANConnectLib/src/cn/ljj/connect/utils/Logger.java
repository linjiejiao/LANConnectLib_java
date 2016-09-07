package cn.ljj.connect.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
	/**
	 * Priority constant for the println method; use Log.v.
	 */
	public static final int VERBOSE = 2;

	/**
	 * Priority constant for the println method; use Log.d.
	 */
	public static final int DEBUG = 3;

	/**
	 * Priority constant for the println method; use Log.i.
	 */
	public static final int INFO = 4;

	/**
	 * Priority constant for the println method; use Log.w.
	 */
	public static final int WARN = 5;

	/**
	 * Priority constant for the println method; use Log.e.
	 */
	public static final int ERROR = 6;

	/**
	 * Priority constant for the println method.
	 */
	public static final int ASSERT = 7;

	private Logger() {
	}

	/**
	 * Send a {@link #VERBOSE} log message.
	 * 
	 * @param tag
	 *            Used to identify the source of a log message. It usually
	 *            identifies the class or activity where the log call occurs.
	 * @param msg
	 *            The message you would like logged.
	 */
	public static int v(String tag, String msg) {
		return println(VERBOSE, tag, msg);
	}

	/**
	 * Send a {@link #VERBOSE} log message and log the exception.
	 * 
	 * @param tag
	 *            Used to identify the source of a log message. It usually
	 *            identifies the class or activity where the log call occurs.
	 * @param msg
	 *            The message you would like logged.
	 * @param tr
	 *            An exception to log
	 */
	public static int v(String tag, String msg, Throwable tr) {
		return println(VERBOSE, tag, msg + '\n' + getStackTraceString(tr));
	}

	/**
	 * Send a {@link #DEBUG} log message.
	 * 
	 * @param tag
	 *            Used to identify the source of a log message. It usually
	 *            identifies the class or activity where the log call occurs.
	 * @param msg
	 *            The message you would like logged.
	 */
	public static int d(String tag, String msg) {
		return println(DEBUG, tag, msg);
	}

	/**
	 * Send a {@link #DEBUG} log message and log the exception.
	 * 
	 * @param tag
	 *            Used to identify the source of a log message. It usually
	 *            identifies the class or activity where the log call occurs.
	 * @param msg
	 *            The message you would like logged.
	 * @param tr
	 *            An exception to log
	 */
	public static int d(String tag, String msg, Throwable tr) {
		return println(DEBUG, tag, msg + '\n' + getStackTraceString(tr));
	}

	/**
	 * Send an {@link #INFO} log message.
	 * 
	 * @param tag
	 *            Used to identify the source of a log message. It usually
	 *            identifies the class or activity where the log call occurs.
	 * @param msg
	 *            The message you would like logged.
	 */
	public static int i(String tag, String msg) {
		return println(INFO, tag, msg);
	}

	/**
	 * Send a {@link #INFO} log message and log the exception.
	 * 
	 * @param tag
	 *            Used to identify the source of a log message. It usually
	 *            identifies the class or activity where the log call occurs.
	 * @param msg
	 *            The message you would like logged.
	 * @param tr
	 *            An exception to log
	 */
	public static int i(String tag, String msg, Throwable tr) {
		return println(INFO, tag, msg + '\n' + getStackTraceString(tr));
	}

	/**
	 * Send a {@link #WARN} log message.
	 * 
	 * @param tag
	 *            Used to identify the source of a log message. It usually
	 *            identifies the class or activity where the log call occurs.
	 * @param msg
	 *            The message you would like logged.
	 */
	public static int w(String tag, String msg) {
		return println(WARN, tag, msg);
	}

	/**
	 * Send a {@link #WARN} log message and log the exception.
	 * 
	 * @param tag
	 *            Used to identify the source of a log message. It usually
	 *            identifies the class or activity where the log call occurs.
	 * @param msg
	 *            The message you would like logged.
	 * @param tr
	 *            An exception to log
	 */
	public static int w(String tag, String msg, Throwable tr) {
		return println(WARN, tag, msg + '\n' + getStackTraceString(tr));
	}

	/*
	 * Send a {@link #WARN} log message and log the exception.
	 * 
	 * @param tag Used to identify the source of a log message. It usually
	 * identifies the class or activity where the log call occurs.
	 * 
	 * @param tr An exception to log
	 */
	public static int w(String tag, Throwable tr) {
		return println(WARN, tag, getStackTraceString(tr));
	}

	/**
	 * Send an {@link #ERROR} log message.
	 * 
	 * @param tag
	 *            Used to identify the source of a log message. It usually
	 *            identifies the class or activity where the log call occurs.
	 * @param msg
	 *            The message you would like logged.
	 */
	public static int e(String tag, String msg) {
		return println(ERROR, tag, msg);
	}

	/**
	 * Send a {@link #ERROR} log message and log the exception.
	 * 
	 * @param tag
	 *            Used to identify the source of a log message. It usually
	 *            identifies the class or activity where the log call occurs.
	 * @param msg
	 *            The message you would like logged.
	 * @param tr
	 *            An exception to log
	 */
	public static int e(String tag, String msg, Throwable tr) {
		return println(ERROR, tag, msg + '\n' + getStackTraceString(tr));
	}

	/**
	 * Handy function to get a loggable stack trace from a Throwable
	 * 
	 * @param tr
	 *            An exception to log
	 */
	public static String getStackTraceString(Throwable tr) {
		if (tr == null) {
			return "";
		}

		// This is to reduce the amount of log spew that apps do in the
		// non-error
		// condition of the network being unavailable.
		Throwable t = tr;
		while (t != null) {
			if (t instanceof UnknownHostException) {
				return "";
			}
			t = t.getCause();
		}

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		tr.printStackTrace(pw);
		pw.flush();
		return sw.toString();
	}

	public static int println(int priority, String tag, String msg) {
		System.out.println(getTimeInfo() + getThreadInfo() + "[" + tag + "] " + getPriorityTag(priority) + ":" + msg);
		return 0;
	}

	private static String getPriorityTag(int priority) {
		switch (priority) {
		case VERBOSE:
			return "[V]";
		case DEBUG:
			return "[D]";
		case INFO:
			return "[I]";
		case WARN:
			return "[W]";
		case ERROR:
			return "[E]";
		case ASSERT:
			return "[A]";
		}
		return "";
	}

	private static String getThreadInfo() {
		Thread t = Thread.currentThread();
		return "[" + t.getName() + "(" + Thread.currentThread().getId() + ")] ";
	}
	
	private static String getTimeInfo() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS ");
		Date now = new Date();
		return dateFormat.format(now);
	}
}
