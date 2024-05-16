package io.xunyss.vscode.wkom;

/**
 *
 */
public interface Const {

	String projectDir = System.getProperty("user.dir");
	String version = projectDir + "/keybindings/version";
	String defaultWin = projectDir + "/keybindings/default-win.json";
	String defaultMac = projectDir + "/keybindings/default-mac.json";

	String wom_on = projectDir + "/keybindings/1_win-on-mac_on.json";
	String wom_off = projectDir + "/keybindings/2_win-on-mac_off.json";
	String custom_eclipse = projectDir + "/keybindings/3_custom-eclipse.json";

	String targetDir = projectDir + "/target";
	String win = targetDir + "/default-win.tmp.json";
	String mac = targetDir + "/default-mac.tmp.json";

	String pomXML = projectDir + "/pom.xml";
	String extensionPackage = projectDir + "/win-keymap-on-mac/package.json";

	// https://github.com/codebling/vs-code-default-keybindings
	String defaultKeymapRootURL = "https://raw.githubusercontent.com/codebling/vs-code-default-keybindings/master";
	String defaultWinURL = defaultKeymapRootURL + "/windows.keybindings.json";
	String defaultMacURL = defaultKeymapRootURL + "/macos.keybindings.json";
}
