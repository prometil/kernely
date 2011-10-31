package org.kernely.user;

import org.kernely.core.plugin.AbstractPlugin;
import org.kernely.user.model.UserModel;
import org.kernely.user.resources.UserController;
/**
 * The user plugin
 * @author g.breton
 *
 */
public class UserPlugin extends AbstractPlugin {

	
	/**
	 * Default constructor
	 */
	public UserPlugin() {
		super("User", "/user");
		registerController(UserController.class);
		registerModel(UserModel.class);
	}
}
