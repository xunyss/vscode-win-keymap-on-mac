package io.xunyss.vscode.wkom;

import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public interface Keymap {

	static boolean outdated() throws IOException {
		FileUtils.copyURLToFile(new URL(Const.defaultWinURL), new File(Const.defaultWin));
		FileUtils.copyURLToFile(new URL(Const.defaultMacURL), new File(Const.defaultMac));

		String version = FileUtils.readLines(new File(Const.defaultWin), "UTF-8").get(0);
		String updated = version.replaceAll(".*(\\d+\\.\\d+\\.\\d+).*", "$1");
		log("#### updated version: " + updated);

		String current = FileUtils.readFileToString(new File(Const.version), StandardCharsets.UTF_8);
		log("#### current version: " + current);

		if (updated.equals(current)) {
			log("#### Already updated.");
//			return false;
			return true;
		}
		FileUtils.write(new File(Const.version), updated, StandardCharsets.UTF_8);
		log("#### start updating..");
		return true;
	}

	static void tempReadable() throws IOException {
		removeComment(Const.defaultWin, Const.win);
		removeComment(Const.defaultMac, Const.mac);
	}

	static void printWinKeys() throws IOException {
		printKeys(Shortcuts.from(Const.win), "Win");
	}

	static void printMacKeys() throws IOException {
		printKeys(Shortcuts.from(Const.mac), "Mac");
	}

	static void writeWinOnMacOn() throws IOException {
		Shortcuts winShortcuts = Shortcuts.from(Const.win);
		Shortcuts womOn = new Shortcuts();
		for (Shortcut ws : winShortcuts) {
			Shortcut to = new Shortcut();
			// todo: default_win, default_mac 에 서로 다른 command/when 조합
			// ref: https://code.visualstudio.com/docs/getstarted/keybindings#_keyboard-layouts
			to.setMac(ws.getKey()
					.replaceAll("alt", "cmd")
					.replaceAll("win", "alt")
					.replaceAll("cmd\\+alt", "alt+cmd")
			);
			to.setCommand(ws.getCommand());
			to.setWhen(ws.getWhen());
			to.setArgs(ws.getArgs());
			womOn.add(to);
		}
		womOn.write(Const.wom_on);
	}

	static void writeWinOnMacOff() throws IOException {
		Shortcuts macShortcuts = Shortcuts.from(Const.mac);
		Shortcuts winShortcuts = Shortcuts.from(Const.win);
		Shortcuts womOff = new Shortcuts();
		for (Shortcut to : macShortcuts) {
			if (winShortcuts.has(to)) {
				continue;
			}
			if (winShortcuts.hasOnlyCommand(to)) {
				to.setMac(to.getKey());
				to.setCommand("-" + to.getCommand());
				to.setKey(null);
				womOff.add(to);
			}
		}
		womOff.write(Const.wom_off);
	}

	static void clean() {
		File[] tmpFiles = new File(Const.targetDir).listFiles(
				file -> file.isFile() && file.getName().endsWith(".tmp.json")
		);
		if (tmpFiles != null) {
			for (File tmp : tmpFiles) {
				//noinspection ResultOfMethodCallIgnored
				tmp.delete();
			}
		}
	}

	static void buildPackage() throws IOException, TemplateException {
		Shortcuts total = new Shortcuts();
		total.append(Shortcuts.from(Const.wom_on));
		total.append(Shortcuts.from(Const.wom_off));
		total.append(Shortcuts.from(Const.custom_eclipse));

		Configuration configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
		configuration.setTemplateLoader(new FileTemplateLoader(new File(Const.projectDir)));
		Template template = configuration.getTemplate("package-template");

		Map<String, String> params = new HashMap<>();
		params.put("package_version", getMavenProjectVersion());
		params.put("package_keybindings", total.toString());

		try (FileWriter fileWriter = new FileWriter(Const.extensionPackage)) {
			template.process(params, fileWriter);
		}
	}


	private static void removeComment(String from, String to) throws IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(from));
			 BufferedWriter fw = new BufferedWriter(new FileWriter(to))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (!line.startsWith("// ") && !line.isBlank()) {
					fw.write(line + StringUtils.LF);
				}
			}
		}
	}

	private static void printKeys(Shortcuts shortcuts, String title) {
		Set<String> keys1 = shortcuts.getKeys();
		Set<String> keys2 = shortcuts.getCombinationKeys();
		log("#### " + title + ": key elements");
		for (String k : keys1) {
			log(k);
		}
		log("#### " + title + ": combination keys");
		for (String k : keys2) {
			log(k);
		}
	}

	private static String getMavenProjectVersion() {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(Const.pomXML);

			NodeList project = doc.getElementsByTagName("project");
			Element projectElement = (Element) project.item(0);
			Element projectVersionElement = (Element) projectElement.getElementsByTagName("version").item(0);

			return projectVersionElement.getTextContent();
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private static void log(Object msg) {
		System.out.println(msg);
	}
}
