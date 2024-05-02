package io.xunyss.vscode.wkom;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 *
 */
public class WGET {

    public static void download(String url, File file) throws IOException {
        FileUtils.copyURLToFile(new URL(url), file);
    }

    public static void download(String url, String filepath) throws IOException {
        download(url, new File(filepath));
    }

    public static void main(String[] args) throws IOException {
        WGET.download("https://raw.githubusercontent.com/codebling/vs-code-default-keybindings/master/windows.negative.keybindings.json",
                "/Users/xuny/xdev/work/xunyss/z.json");
    }
}
