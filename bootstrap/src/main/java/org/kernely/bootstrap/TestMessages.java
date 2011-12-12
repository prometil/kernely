package org.kernely.bootstrap;

import static org.kernely.core.template.helpers.I18n.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.kernely.core.template.helpers.I18n;

public class TestMessages {

	List<ResourceBundle> bundles = new ArrayList<ResourceBundle>();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		I18n i18n = new I18n(new Locale("fr", "FR"));
		Object[] messageArguments = { "terre", new Integer(7), new Date() };
		System.out.println(i18n.t("helloworld"));
		System.out.println(i18n.t("helloworld2"));
		System.out.println(i18n.t("template", messageArguments));
	}

}
