package io.xunyss.vscode.wkom;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Objects;

/**
 *
 */
public class Shortcut {

	private static final Gson TO_STRING_GSON = new GsonBuilder().disableHtmlEscaping().create();


	private String key;
	private String mac;

	private String command;
	private String when;
	private Object args;


	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getWhen() {
		return when;
	}

	public void setWhen(String when) {
		this.when = when;
	}

	public Object getArgs() {
		return args;
	}

	public void setArgs(Object args) {
		this.args = args;
	}


	public boolean equalsKey(Shortcut sc) {
		return this.getKey().equals(sc.getKey());
	}

	public boolean equalsCommand(Shortcut sc) {
		return  this.getCommand().equals(sc.getCommand())
			&&  Objects.equals(getWhen(), sc.getWhen())
			&&  Objects.equals(getArgs(), sc.getArgs());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Shortcut) {
			Shortcut sc = (Shortcut) obj;
			return equalsKey(sc) && equalsCommand(sc);
		}
		return false;
	}

	@Override
	public String toString() {
		return TO_STRING_GSON.toJson(this);
	}
}
