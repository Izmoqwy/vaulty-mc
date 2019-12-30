/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.event.registration;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import eu.izmoqwy.uhc.event.CancellableEvent;
import eu.izmoqwy.uhc.event.UHCEvent;
import eu.izmoqwy.vaulty.utils.ReflectionUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class UHCEventManager {

	private static List<UHCListener> listeners = Lists.newArrayList();
	private static Map<Class<? extends UHCEvent>, LinkedList<RegisteredListener>> sorted = Maps.newHashMap();

	public static void register(UHCListener... uhcListeners) {
		listeners.addAll(Arrays.asList(uhcListeners));
		reload();
	}

	public static void unregister(UHCListener... uhcListeners) {
		listeners.removeAll(Arrays.asList(uhcListeners));
		reload();
	}

	private static void reload() {
		Map<Class<? extends UHCEvent>, List<RegisteredListener>> unsorted = Maps.newHashMap();
		for (UHCListener listener : listeners) {
			for (Method method : ReflectionUtil.getMethodsAnnotatedWith(listener.getClass(), UHCEventHandler.class)) {
				if (method.getParameterCount() != 1 || !UHCEvent.class.isAssignableFrom(method.getParameterTypes()[0]))
					continue;

				@SuppressWarnings("unchecked")
				Class<? extends UHCEvent> watchedEvent = (Class<? extends UHCEvent>) method.getParameterTypes()[0];

				List<RegisteredListener> eventMethods = unsorted.getOrDefault(watchedEvent, Lists.newArrayList());
				eventMethods.add(new RegisteredListener(listener, method, method.getAnnotation(UHCEventHandler.class)));
				unsorted.put(watchedEvent, eventMethods);
			}
		}

		Map<Class<? extends UHCEvent>, LinkedList<RegisteredListener>> sorted = Maps.newHashMap();
		for (Class<? extends UHCEvent> event : unsorted.keySet()) {
			sorted.put(event, unsorted.get(event).stream()
					.sorted(Comparator.comparingInt(RegisteredListener::getPriority))
					.collect(Collectors.toCollection(LinkedList::new)));
		}
		UHCEventManager.sorted = sorted;
	}

	public static void fireEvent(UHCEvent event) {
		Class<? extends UHCEvent> eventClass = event.getClass();
		if (sorted.containsKey(eventClass)) {
			for (RegisteredListener listener : sorted.get(eventClass)) {
				if (event instanceof CancellableEvent) {
					if (((CancellableEvent) event).isCancelled() && listener.getEventHandler().ignoreCancelled())
						continue;
				}

				try {
					listener.getMethod().invoke(listener.getInstance(), event);
				}
				catch (InvocationTargetException ex) {
					ex.getTargetException().printStackTrace();
				}
				catch (IllegalAccessException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
}
