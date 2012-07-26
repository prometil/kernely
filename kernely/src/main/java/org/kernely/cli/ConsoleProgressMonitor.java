/**
 * 
 */
package org.kernely.cli;

import java.util.Formatter;
import java.util.Locale;

/**
 *
 */
public class ConsoleProgressMonitor implements ProgressMonitor {

	int counter = 0;
	String taskName = "";

	public ConsoleProgressMonitor(String pTaskName) {
		counter = 0;
		taskName = pTaskName;
	}

	@Override
	public void tick() {
		counter++;
		System.out.print(".");
		if (counter > 50) {
			System.out.flush();
			counter = 0;
		}
	}
	@Override
	public void start() {
		String format = "-> [%1$-15.15s] ";
		StringBuffer sb = new StringBuffer();
		Formatter formatter = new Formatter(sb, Locale.US);
		formatter.format(format, taskName);
		formatter.close();
		System.out.print(sb);
	}

	@Override
	public void end() {
		System.out.println("[Done]");

	}

}
