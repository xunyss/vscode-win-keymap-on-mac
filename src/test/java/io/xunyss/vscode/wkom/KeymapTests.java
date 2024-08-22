package io.xunyss.vscode.wkom;

import com.google.gson.Gson;
import freemarker.template.TemplateException;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class KeymapTests {

	@Test
	public void build() throws IOException, TemplateException {
		if (Keymap.outdated()) {
			Keymap.tempReadable();
			Keymap.printWinKeys();
			Keymap.printMacKeys();
			Keymap.writeWinOnMacOn();
			Keymap.writeWinOnMacOff();
			Keymap.clean();
			Keymap.buildPackage();
		}
	}

    @Test
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void extensionKeybindings() throws IOException {
		// Project Manager for Java
		// https://raw.githubusercontent.com/microsoft/vscode-java-dependency/main/package.json
		FileUtils.copyURLToFile(
				new URL("https://raw.githubusercontent.com/microsoft/vscode-java-dependency/main/package.json"),
				new File(Const.targetDir + "/vscode-java-dependency.tmp.json"));
		Map<String, Object> map;
		try (FileReader reader = new FileReader(Const.targetDir + "/vscode-java-dependency.tmp.json")) {
			map = new Gson().fromJson(reader, Map.class);
		}

		List list = (List) ((Map) map.get("contributes")).get("keybindings");
		Shortcuts shortcuts = new Shortcuts(list);
		System.out.println(shortcuts);
	}
}
