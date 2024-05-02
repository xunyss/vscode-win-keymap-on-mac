package io.xunyss.vscode.wkom;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 */
public class ShortcutConverter {

	static final Map<String, String> ORDER_MAP = new HashMap<String, String>() {{
		put("ctrl+shift+alt", "");
	}};

	static final String projectDir = System.getProperty("user.dir");
	static final String defaultWin = projectDir + "/keymaps/default-win.json";
	static final String defaultMac = projectDir + "/keymaps/default-mac.json";

	static final String targetDir = projectDir + "/target";
	static final String win = targetDir + "/default-win.tmp";
	static final String mac = targetDir + "/default-mac.tmp";
	static final String wom = targetDir + "/win-on-mac.tmp.json";

	public static void init() throws IOException {
		try (	BufferedReader br = new BufferedReader(new FileReader(defaultWin));
				BufferedWriter fw = new BufferedWriter(new FileWriter(win))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (!line.startsWith("// ") && !line.isEmpty()) {
					fw.write(line + "\n");
				}
			}
		}
	}

	public static void clean() {
		File[] tmpFiles = new File(targetDir).listFiles(
				file -> file.isFile() && file.getName().endsWith(".tmp")
		);
		if (tmpFiles != null) {
			for (File tmp : tmpFiles) {
				//noinspection ResultOfMethodCallIgnored
				tmp.delete();
			}
		}
	}

	public static void convert() throws IOException {
		List<Map<String, Object>> from;
		try (FileReader fr = new FileReader(win)) {
            //noinspection unchecked
            from = new Gson().fromJson(fr, List.class);
		}

		// 1. key elems, comb
		Set<String> keyset = new TreeSet<>();
		Set<String> comset = new TreeSet<>();
		for (Map<String, Object> fi : from) {
			String[] ks = fi.get("key").toString().split("[+\\s]");
            keyset.addAll(Arrays.asList(ks));

			String[] cs = fi.get("key").toString().split("\\s");
			for (String k : cs) {
				if (k.contains("alt") || k.contains("ctrl") || k.contains("shift") || k.contains("win")) {
					comset.add(k.substring(0, k.lastIndexOf('+')));
				}
			}
		}
		System.out.println("==== key elems");
		for (String k : keyset) {
			if (k.length() > 1 && !k.matches("^f[0-9]{1,2}")) {
				System.out.println(k);
			}
		}
		System.out.println("==== key comb");		// 조합키를 확인해본 결과 new key 의 "cmd+alt" 만 "alt+cmd"
		for (String k : comset) {
			System.out.println(k);
		}

		// 2. new key
		List<Map<String, Object>> to = new LinkedList<>();
		for (Map<String, Object> fi : from) {
			Map<String, Object> ti = new LinkedHashMap<>();
			ti.put("mac",		fi.get("key").toString()
										.replaceAll("alt", "cmd")
										.replaceAll("win", "alt")
										.replaceAll("cmd\\+alt", "alt+cmd"));
			ti.put("command",	fi.get("command"));
			ti.put("when",		fi.get("when"));
			ti.put("args",		fi.get("args"));
			to.add(ti);
		}
		// vs-code 최신버전 자동 적용
		// default_wind, default_mac 에 서로 다른 command/when 조합
		// custom(eclipse) key-map 추가
		// shortcuts 제거 (negative)
		// keycode>> https://code.visualstudio.com/docs/getstarted/keybindings#_keyboard-layouts

		// 3. win-on-mac
		try (FileWriter fw = new FileWriter(wom)) {
			new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(to, fw);
		}
	}
}
