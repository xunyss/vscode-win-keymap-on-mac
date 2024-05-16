package io.xunyss.vscode.wkom;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;
import java.util.TreeSet;
import java.util.function.Consumer;

/**
 *
 */
public class Shortcuts implements Iterable<Shortcut> {

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
    private static final Type GSON_TYPE = new TypeToken<List<Shortcut>>() {}.getType();

    public static Shortcuts from(String json) throws IOException {
        Shortcuts shortcuts = new Shortcuts();
        shortcuts.read(json);
        return shortcuts;
    }

    private List<Shortcut> list;

    public Shortcuts() {
        this.list = new ArrayList<>();
    }

    public Shortcuts(List<Shortcut> list) {
        this.list = list;
    }


    @Override
    public Iterator<Shortcut> iterator() {
        return list.iterator();
    }

    @Override
    public void forEach(Consumer<? super Shortcut> action) {
        list.forEach(action);
    }

    @Override
    public Spliterator<Shortcut> spliterator() {
        return list.spliterator();
    }


    public void read(String json) throws IOException {
        try (FileReader fileReader = new FileReader(json)) {
            list = GSON.fromJson(fileReader, Shortcuts.GSON_TYPE);
        }
    }

    public void write(String json) throws IOException {
        try (FileWriter fileWriter = new FileWriter(json)) {
            GSON.toJson(list, fileWriter);
        }
    }

    @Override
    public String toString() {
        try (StringWriter stringWriter = new StringWriter()) {
            GSON.toJson(list, stringWriter);
            return stringWriter.toString();
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public boolean add(Shortcut shortcut) {
        return list.add(shortcut);
    }

    public void append(Shortcuts shortcuts) {
        this.list.addAll(shortcuts.list);
    }

    public boolean has(Shortcut shortcut) {
        for (Shortcut sc : list) {
            if (sc.equals(shortcut)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasOnlyCommand(Shortcut shortcut) {
        for (Shortcut sc : list) {
            if (!sc.equalsKey(shortcut) && sc.equalsCommand(shortcut)) {
                return true;
            }
        }
        return false;
    }

    public Set<String> getKeys() {
        Set<String> keys = new TreeSet<>();
        for (Shortcut sc : list) {
            String[] ss = sc.getKey().split("[+\\s]");
            for (String k : ss) {
                if (!k.matches("[a-z0-9]") && !k.matches("^f[0-9]{1,2}")) {
                    keys.add(k);
                }
            }
        }
        return keys;
    }

    public Set<String> getCombinationKeys() {
        Set<String> keys = new TreeSet<>();
        for (Shortcut sc : list) {
            String[] ss = sc.getKey().split("\\s");
            for (String k : ss) {
                if (StringUtils.containsAny(k, "alt", "ctrl", "shift", "win", "cmd", "meta")) {
                    keys.add(k.substring(0, k.lastIndexOf('+')));
                }
            }
        }
        return keys;
    }
}
