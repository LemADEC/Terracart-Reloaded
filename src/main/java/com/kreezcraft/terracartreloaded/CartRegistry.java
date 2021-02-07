package com.kreezcraft.terracartreloaded;

import com.kreezcraft.terracartreloaded.entity.TerraCartEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class CartRegistry {
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, TerraCart.MODID);

	public static final RegistryObject<EntityType<TerraCartEntity>> TERRA_CART = ENTITIES.register("terra_cart",
			() -> register("player_statue", EntityType.Builder.<TerraCartEntity>create(TerraCartEntity::new, EntityClassification.MISC)
					.size(0.98F, 0.7F).trackingRange(8)
					.setCustomClientFactory(TerraCartEntity::new)));

	private static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> builder) {
		return builder.build(id);
	}
}
