package me.spthiel.entities.main.variableprovider;

import me.spthiel.entities.main.EntityVariableProvider;
import me.spthiel.entities.main.ScriptedIteratorEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;

public class ItemEntityProvider extends EntityVariableProvider {

	public ItemEntityProvider() {
		super(EntityItem.class);
	}

	@Override
	public void addVariable(ScriptedIteratorEntities iterator, String key, Object object) {
		iterator.addVar("ENTITYITEM" + key.toUpperCase(),object);
	}

	@Override
	public void addVariables(ScriptedIteratorEntities iterator, Entity entity) {
		EntityItem item = (EntityItem)entity;

		iterator.addVar("ENTITYISITEM", true);
		addVariable(iterator, "age", item.getAge());
		addVariable(iterator, "owner", item.getOwner());
		addVariable(iterator, "thrower", item.getThrower());

		ItemStack tem = item.getItem();
		addVariable(iterator, "name", translate(tem.getUnlocalizedName()));
		addVariable(iterator, "count", tem.getCount());
		addVariable(iterator, "displayname", tem.getDisplayName());
		addVariable(iterator, "damage", tem.getItemDamage());
		addVariable(iterator, "maxdamage", tem.getMaxDamage());
		addVariable(iterator, "metadata", tem.getMetadata());
		addVariable(iterator, "enchanted", tem.isItemEnchanted());
		addVariable(iterator, "stackable", tem.isStackable());

		Item i = tem.getItem();
		addVariable(iterator, "unlocalizedname", i.getUnlocalizedName());

	}

	private String translate(String unlocalizedname) {
		return I18n.translateToLocal(unlocalizedname + ".name");
	}
}
