package io.xunyss.vscode.wkom;

import freemarker.template.TemplateException;
import org.junit.Test;

import java.io.IOException;

public class KeymapTests {

	@Test
	public void build() throws IOException, TemplateException {
		if (Keymap.outdated()) {
			Keymap.tempReadable();
			Keymap.printWinKeys();
			Keymap.printMacKeys();
			Keymap.writeWinOnMacOn();
			Keymap.writeWinOnMacOff();
//			Keymap.clean();
			Keymap.buildPackage();
		}
	}
}
