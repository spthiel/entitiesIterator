package me.spthiel.entities.main;

import net.minecraft.entity.Entity;

public class EntityWithDistance {
    Entity entity;
    Float distance;

    EntityWithDistance(Entity e, Float distance) {
        this.entity = e;
        this.distance = distance;
    }
}
