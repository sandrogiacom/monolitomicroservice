package com.monolitomicroservice.teste.wildfly.security.common;

import java.security.Principal;
import java.security.acl.Group;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

public class CustomGroup extends CustomPrincipal implements Group, Cloneable {
    private static final long serialVersionUID = 1L;

    private HashMap members = new HashMap();

    public CustomGroup(String name) {
        super(name);
    }

    @Override
    public boolean addMember(Principal user) {
        boolean isMember = members.containsKey(user);
        if (isMember == false)
            members.put(user, user);
        return isMember == false;
    }

    @Override
    public boolean removeMember(Principal user) {
        Object prev = members.remove(user);
        return prev != null;
    }

    @Override
    public boolean isMember(Principal member) {
        boolean isMember = members.containsKey(member);
        if (isMember == false) {   // Check any Groups for membership
            Collection values = members.values();
            Iterator iter = values.iterator();
            while (isMember == false && iter.hasNext()) {
                Object next = iter.next();
                if (next instanceof Group) {
                    Group group = (Group) next;
                    isMember = group.isMember(member);
                }
            }
        }
        return isMember;
    }

    @Override
    public Enumeration<? extends Principal> members() {
        return Collections.enumeration(members.values());
    }

    public synchronized Object clone() throws CloneNotSupportedException {
        CustomGroup clone = (CustomGroup) super.clone();
        if (clone != null)
            clone.members = (HashMap) this.members.clone();
        return clone;
    }

    @Override
    public String toString() {
        return "CustomGroup{" +
                "name=" + getName() +
                ", members=" + members +
                '}';
    }
}
