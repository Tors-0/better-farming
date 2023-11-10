package io.github.Tors_0.dotwarden.common.item;

import io.github.Tors_0.dotwarden.common.extensions.PlayerExtensions;
import io.github.Tors_0.dotwarden.common.networking.DOTWNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.mob.warden.WardenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import java.util.function.Predicate;

public class HarmonicStaffItem extends Item {
    private static final Predicate<Entity> VALID_ENTITY = (entity -> (entity instanceof LivingEntity && !(entity instanceof WardenEntity)));
    private static final int POWER_LEVEL_COST = 3;
    private static final float BOOM_DAMAGE = 10.0F;

    public HarmonicStaffItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (!world.isClient()) {
            if (!(((PlayerExtensions)player).dotwarden$getPowerLevel() >= POWER_LEVEL_COST)) return TypedActionResult.fail(player.getStackInHand(hand));
            double maxDistance = 20d;
            float tickDelta = 1f;
            Vec3d vec3d = player.getCameraPosVec(tickDelta);
            Vec3d vec3d5 = player.getRotationVec(tickDelta);
            Vec3d vec3d6 = vec3d.add(vec3d5.x * maxDistance, vec3d5.y * maxDistance, vec3d5.z * maxDistance);
            HitResult blockResult = world
                    .raycast(new RaycastContext(
                                    vec3d, vec3d6, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player
                            ));
            Vec3d startPoint = player.getPos().add(0.0, 1.6F, 0.0);
            double distanceToBlockSq = blockResult != null ? blockResult.getPos().squaredDistanceTo(startPoint) : Double.POSITIVE_INFINITY;
            Vec3d rotationVec = player.getRotationVec(1.0F);
            double range = 20;
            double effectiveRangeSq = Math.min(range * range, distanceToBlockSq);
            Vec3d endPoint = startPoint.add(rotationVec.x * range, rotationVec.y * range, rotationVec.z * range);
            Box box = player.getBoundingBox().stretch(rotationVec.multiply(range)).expand(1.0D,1.0D,1.0D);
            EntityHitResult entityHitResult = ProjectileUtil.raycast(
                    player,
                    startPoint,
                    endPoint,
                    box,
                    VALID_ENTITY,
                    effectiveRangeSq
            );
            if (entityHitResult != null
                    && entityHitResult.getEntity() instanceof LivingEntity livingEntity
            ) {
                if (!player.isCreative()) {
                    player.getItemCooldownManager().set(this, 200);
                    ((PlayerExtensions)player).dotwarden$addPowerLevel( - POWER_LEVEL_COST);
                }
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeInt(((PlayerExtensions)player).dotwarden$getPowerLevel());
                buf.writeInt(((PlayerExtensions)player).dotwarden$getPower());
                ServerPlayNetworking.send((ServerPlayerEntity)player, DOTWNetworking.POWERLEVEL_PACKET_ID, buf);
                Vec3d vec3d2 = livingEntity.getEyePos().subtract(startPoint);
                Vec3d vec3d3 = vec3d2.normalize();
                for (int i = 1; i < MathHelper.floor(vec3d2.length()) + 7; ++i) {
                    Vec3d vec3d4 = startPoint.add(vec3d3.multiply((double) i));
                    ((ServerWorld) world).spawnParticles(ParticleTypes.SONIC_BOOM, vec3d4.x, vec3d4.y, vec3d4.z, 1, 0.0, 0.0, 0.0, 0.0);
                }
                player.playSound(SoundEvents.ENTITY_WARDEN_SONIC_BOOM, SoundCategory.PLAYERS, 3.0F, 1.0F);
                livingEntity.damage(new EntityDamageSource("sonic_boom", player).setUsesMagic(), BOOM_DAMAGE);
                double d = 0.5 * (1.0 - livingEntity.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
                double e = 2.5 * (1.0 - livingEntity.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
                livingEntity.addVelocity(vec3d3.getX() * e, vec3d3.getY() * d, vec3d3.getZ() * e);
                return TypedActionResult.success(player.getStackInHand(hand),true);
            }
        }
        return TypedActionResult.fail(player.getStackInHand(hand));
    }
}
