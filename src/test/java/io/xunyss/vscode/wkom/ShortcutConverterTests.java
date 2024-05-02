package io.xunyss.vscode.wkom;

import org.junit.Test;

import java.io.IOException;

public class ShortcutConverterTests {

	@Test
	public void generateKeyBinding() throws IOException {
		ShortcutConverter.init();
		ShortcutConverter.convert();
//		ShortcutConverter.clean();
	}
}
