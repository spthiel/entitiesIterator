package me.spthiel.entities.main;

import net.minecraft.entity.Entity;

import java.util.List;

class FilterEntry {
    List<EntityTypes> entityTypes;
    String name;
    boolean isInversed;
    Class<? extends Entity> minecraftClass;

    FilterEntry() {
    }

    FilterEntry(List<EntityTypes> entityTypes, String name, boolean isInversed, Class<? extends Entity> minecraftClass) {
        this.entityTypes = entityTypes;
        this.name = name;
        this.isInversed = isInversed;
        this.minecraftClass = minecraftClass;
    }
}
