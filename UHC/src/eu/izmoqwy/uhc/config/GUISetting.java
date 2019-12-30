/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.config;

import org.bukkit.Material;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GUISetting {

	String group() default "default";

	String name() default "";

	Material icon() default Material.BOOK;

	byte iconData() default 0;

	boolean duration() default false;

	int step() default 1;

	int min() default 1;

	int max() default 100;

	float stepf() default 1;

	float minf() default 1;

	float maxf() default 100;

}
