package io.mosip.registration.controller;

import java.net.URL;

import javafx.scene.web.WebEngine;

/**
 * Applies the client theme to server-synced HTML templates (acknowledgement, preview, dashboard, consent)
 * rendered in a {@link javafx.scene.web.WebView}.
 * <p>
 * The templates are master data synced from the server, so they are not modified; instead
 * {@code template-theme.css} is set as the engine's user stylesheet, which WebKit applies on top of the
 * template's own styles. Safe to call repeatedly.
 */
public final class TemplateTheme {

	private static final String STYLESHEET = "/template-theme.css";

	private TemplateTheme() {
	}

	public static void apply(WebEngine engine) {
		if (engine == null) {
			return;
		}
		URL stylesheet = TemplateTheme.class.getResource(STYLESHEET);
		if (stylesheet != null) {
			engine.setUserStyleSheetLocation(stylesheet.toExternalForm());
		}
	}
}
