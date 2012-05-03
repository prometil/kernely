/**
 * 
 */
package org.kernely.cli;

/**
 * 
 * Progress monitor
 */
public interface ProgressMonitor {
	/**
	 * Start event.
	 */
	void start();
	
	/**
	 * End event.
	 */
	void end();
	/**
	 * Tick event.
	 */
	void tick();

}
