package org.kernely.stream;

import org.kernely.core.plugin.AbstractPlugin;
import org.kernely.stream.model.StreamMessage;
import org.kernely.stream.resources.StreamResource;
import org.kernely.stream.service.StreamService;
/**
 * The user plugin
 * @author g.breton
 *
 */
public class StreamPlugin  extends AbstractPlugin {

	/**
	 * Default constructor
	 */
	public StreamPlugin() {
		super("Stream", "/streams");
		registerController(StreamResource.class);
		registerModel(StreamMessage.class);
	}
	
	@Override
	protected void configure() {
		bind(StreamService.class);
	}
}
