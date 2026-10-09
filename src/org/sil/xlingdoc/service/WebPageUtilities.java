/**
 * Copyright (c) 2026 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */

package org.sil.xlingdoc.service;

import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;

/**
 * 
 */
public class WebPageUtilities {

	public static String getCssVariableValue(WebEngine webEngine, String variableName) {
        String script = String.format(
            "window.getComputedStyle(document.documentElement).getPropertyValue('%s').trim();",
            variableName
        );
        Object result = webEngine.executeScript(script);
        return result != null ? result.toString() : "";
    }

	public static WebEngine allowConsoleLogViaJavaScript(WebEngine webEngine, WebPageInteractor webPageInteractor) {
		// Allow use of console.log and console.error in JS
		JSObject window = (JSObject) webEngine.executeScript("window");
		window.setMember("javaOut", System.out);
		window.setMember("javaErr", System.err);
		String loggingRedirectScript =
		    "console.log = function(message) { javaOut.println('[JS Log] ' + message); };\n" +
		    "console.error = function(message) { javaErr.println('[JS Error] ' + message); };";
		webEngine.executeScript(loggingRedirectScript);
		window.setMember("xLingDocApp", webPageInteractor);
		return webEngine;
	}

	// Following based on Gemini answer on 2026.10.08
	public static int obtainCurrentCursorPosition(WebEngine webEngine) {
		String script = "(function() {" + "    var sel = window.getSelection();"
				+ "    if (sel && sel.rangeCount > 0) {" + "        var range = sel.getRangeAt(0);"
				+ "        var node = range.startContainer;" + "        var offset = range.startOffset;"
				+ "        var span = (node.nodeType === 3) ? node.parentNode : node;"
				+ "            return (span.id || 'NO_ID') + ':' + offset;" + "        };" + "})();";
		int position = -1;
		Object result = webEngine.executeScript(script);
		if (result != null) {
			String[] details = result.toString().split(":");
//			String spanId = details[0];
			position = Integer.parseInt(details[1]);
		}
		return position;
	}

}
