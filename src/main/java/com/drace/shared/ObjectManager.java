package com.drace.shared;

import lombok.Getter;

import java.util.Collection;
import java.util.LinkedHashSet;

public abstract class ObjectManager<T> {
    @Getter protected final LinkedHashSet<T> objects = new LinkedHashSet<>();
    public abstract T get(String name);
    public void add(T t) { objects.add(t); }
    public void addAll(Collection<T> ts) { objects.addAll(ts); }

}
