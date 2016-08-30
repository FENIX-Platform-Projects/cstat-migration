package org.fao.ess.cstat.migration.dto;

import java.util.Collection;
import java.util.LinkedList;

public class Groups <T extends Comparable<T>> {
    public Collection<T> insert = new LinkedList<>();
    public Collection<T> update = new LinkedList<>();
    public Collection<T> delete = new LinkedList<>();

    public Groups(Collection<T> source, Collection<T> destination) {
        init(source, destination);
    }
    public void init(Collection<T> source, Collection<T> destination) {
        insert.addAll(source);
        insert.removeAll(destination);

        update.addAll(source);
        update.retainAll(destination);

        delete.addAll(destination);
        delete.removeAll(source);
    }

}
