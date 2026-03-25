package io.github.nanashithenameless.engrok.security;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public final class Log4jMitigationPreLaunch implements PreLaunchEntrypoint {
	private static final String[][] SECURITY_PROPERTIES = {
		{"log4j2.formatMsgNoLookups", "true"},
		{"log4j2.enableJndiLookup", "false"},
		{"log4j2.enableJndiJdbc", "false"},
		{"log4j2.enableJndiJms", "false"},
		{"log4j2.enableJndiContextSelector", "false"}
	};

	@Override
	public void onPreLaunch() {
		for (String[] property : SECURITY_PROPERTIES) {
			System.setProperty(property[0], property[1]);
		}
	}
}
