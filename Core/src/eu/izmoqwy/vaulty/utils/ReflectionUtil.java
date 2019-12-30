/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.vaulty.utils;

import com.google.common.collect.Lists;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;

public class ReflectionUtil {

	public static List<Method> getMethodsAnnotatedWith(Class<?> type, Class<? extends Annotation> annotation) {
		List<Method> methods = Lists.newArrayList();
		Class<?> clazz = type;
		while (clazz != Object.class) {
			for (Method method : clazz.getDeclaredMethods()) {
				if (method.isBridge() || method.isSynthetic())
					continue;

				if (method.isAnnotationPresent(annotation))
					methods.add(method);
			}
			clazz = clazz.getSuperclass();
		}
		return methods;
	}

	public static List<Field> getFieldsAnnotatedWith(Class<?> type, Class<? extends Annotation> annotation) {
		// on prend les classes avant pour avoir les fields dans l'ordre inverse (important pour le GUI de config)
		List<Class<?>> classes = Lists.newLinkedList();
		Class<?> clazz = type;
		while (clazz != Object.class) {
			classes.add(clazz);
			clazz = clazz.getSuperclass();
		}
		Collections.reverse(classes);

		List<Field> fields = Lists.newLinkedList();
		for (Class<?> aClass : classes) {
			for (Field field : aClass.getDeclaredFields()) {
				if (field.isSynthetic())
					continue;

				if (field.isAnnotationPresent(annotation))
					fields.add(field);
			}
		}
		return fields;
	}

	public static <T> void setStaticField(Field field, T value) throws IllegalAccessException, NoSuchFieldException {
		if (!field.isAccessible())
			field.setAccessible(true);

		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		if (Modifier.isFinal(field.getModifiers())) {
			modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		}

		field.set(null, value);
	}

	public static Field getField(Class<? extends PacketPlayOutPlayerListHeaderFooter> aClass, String fieldName) {
		Field field = null;
		try {
			field = aClass.getDeclaredField(fieldName);
			field.setAccessible(true);
		}
		catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		return field;
	}
}
