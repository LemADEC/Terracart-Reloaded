package com.kreezcraft.terracartreloaded;

import com.kreezcraft.terracartreloaded.entity.TerraCartEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CartRegistry {
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, TerraCart.MODID);

	public static final RegistryObject<EntityType<TerraCartEntity>> TERRA_CART = ENTITIES
			.register("terra_cart",
			          () -> register(
							  "player_statue",
							  EntityType.Builder.<TerraCartEntity>of(TerraCartEntity::new, MobCategory.MISC)
							                    .sized(0.98F, 0.7F)
							                    .setTrackingRange(8)
							                    .setCustomClientFactory(TerraCartEntity::new) ));

	private static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> builder) {
		return builder.build(id);
	}
}
