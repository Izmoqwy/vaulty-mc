/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.vaulty.config;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

@Getter
public class VaultyConfig {

	private File file;
	private YamlConfiguration yamlConfiguration;

	public VaultyConfig(File file) {
		Preconditions.checkNotNull(file);
		Preconditions.checkArgument(!file.isDirectory());

		this.file = file;
		if (!file.exists()) {
			try {
				if (!file.getParentFile().exists())
					//noinspection ResultOfMethodCallIgnored
					file.getParentFile().mkdirs();
				Preconditions.checkArgument(file.createNewFile());
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.yamlConfiguration = YamlConfiguration.loadConfiguration(file);
	}


	public void save() throws IOException {
		yamlConfiguration.save(file);
	}

	public void handledSave() {
		try {
			save();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}
