package com.herobrine.mod.entities;

import com.herobrine.mod.util.entities.EntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.AbstractIllagerEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class HerobrineMageEntity extends AbstractHerobrineEntity {
    protected HerobrineMageEntity(EntityType<? extends HerobrineMageEntity> type, World worldIn) {
        super(type, worldIn);
        experienceValue = 5;
    }

    public HerobrineMageEntity(World worldIn) {
        this(EntityRegistry.HEROBRINE_MAGE_ENTITY, worldIn);
    }

    private int illusionCastingTime = 400;
    private int effectsCastingTime = 250;
    private int teleportCastingTime = 500;

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 0.6D, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AbstractIllagerEntity.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractSurvivorEntity.class, true));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, GolemEntity.class, true));
        this.targetSelector.addGoal(6, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomWalkingGoal(this, 0.4D));
        this.goalSelector.addGoal(8, new LookAtGoal(this, AbstractIllagerEntity.class, 8.0F));
        this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(10, new LookAtGoal(this, AbstractSurvivorEntity.class, 8.0F));
        this.goalSelector.addGoal(11, new LookAtGoal(this, GolemEntity.class, 8.0F));
        this.goalSelector.addGoal(12, new LookRandomlyGoal(this));
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40.0D);
        this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
        this.getAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(1.0D);
        this.getAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).setBaseValue(1.0D);
        this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.6D);
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
    }

    @Override
    public void writeAdditional(@NotNull CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("IllusionCastingInterval", this.illusionCastingTime);
        compound.putInt("WeakenCastingInterval", this.effectsCastingTime);
        compound.putInt("WarpCastingInterval", this.teleportCastingTime);
    }

    @Override
    public void readAdditional(@NotNull CompoundNBT compound) {
        super.readAdditional(compound);
        this.illusionCastingTime = compound.getInt("IllusionCastingInterval");
        this.effectsCastingTime = compound.getInt("WeakenCastingInterval");
        this.teleportCastingTime = compound.getInt("WarpCastingInterval");
    }

    @Override
    public void livingTick() {
        if(this.isAlive()) {
            //Again, this was 2 if statements with the same result. (GJ)
            if (this.illusionCastingTime <= 0 || this.illusionCastingTime > 400) {
                this.illusionCastingTime = 400;
            }

            --this.illusionCastingTime;

            //Same situation as before.
            if (this.effectsCastingTime <= 0 || this.effectsCastingTime > 250) {
                this.effectsCastingTime = 250;
            }

            --this.effectsCastingTime;

            //Again.
            if (this.teleportCastingTime <= 0 || this.teleportCastingTime > 500) {
                this.teleportCastingTime = 500;
            }
            --this.teleportCastingTime;

            if (this.illusionCastingTime == 0 && this.isAggressive()) {
                int x = (int) this.getPosX();
                int y = (int) this.getPosY();
                int z = (int) this.getPosZ();

                if (!world.isRemote) {
                    Entity entity1 = new FakeHerobrineMageEntity(EntityRegistry.FAKE_HEROBRINE_MAGE_ENTITY, world);
                    Entity entity2 = new FakeHerobrineMageEntity(EntityRegistry.FAKE_HEROBRINE_MAGE_ENTITY, world);
                    Entity entity3 = new FakeHerobrineMageEntity(EntityRegistry.FAKE_HEROBRINE_MAGE_ENTITY, world);
                    Entity entity4 = new FakeHerobrineMageEntity(EntityRegistry.FAKE_HEROBRINE_MAGE_ENTITY, world);
                    entity1.setLocationAndAngles(x, y, z, world.rand.nextFloat() * 360F, 0);
                    entity2.setLocationAndAngles(x, y, z, world.rand.nextFloat() * 360F, 0);
                    entity3.setLocationAndAngles(x, y, z, world.rand.nextFloat() * 360F, 0);
                    entity4.setLocationAndAngles(x, y, z, world.rand.nextFloat() * 360F, 0);
                    world.addEntity(entity1);
                    world.addEntity(entity2);
                    world.addEntity(entity3);
                    world.addEntity(entity4);
                }
                //This was "if (world.isRemote)" directly below the if above, i changed it because
                //to use the result of the if above (GJ)
                else {
                    if (!this.isSilent()) {
                        this.world.playSound(this.getPosX() + 0.5D, this.getPosY() + 0.5D, this.getPosZ() + 0.5D, SoundEvents.ENTITY_ILLUSIONER_CAST_SPELL, this.getSoundCategory(), 1.0F + this.rand.nextFloat(), this.rand.nextFloat() * 0.7F + 0.3F, false);
                    }

                    for (int i = 0; i < 20; ++i) {
                        double d0 = this.rand.nextGaussian() * 0.02D;
                        double d1 = this.rand.nextGaussian() * 0.02D;
                        double d2 = this.rand.nextGaussian() * 0.02D;
                        this.world.addParticle(ParticleTypes.EFFECT, this.getPosXWidth(1.0D) - d0 * 10.0D, this.getPosYRandom() - d1 * 10.0D, this.getPosZRandom(1.0D) - d2 * 10.0D, d0, d1, d2);
                    }
                }
            }

            if (this.effectsCastingTime == 0 && this.isAggressive()) {
                LivingEntity entity = HerobrineMageEntity.this.getAttackTarget();
                if (entity != null) {
                    entity.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 400, 1));
                    entity.addPotionEffect(new EffectInstance(Effects.WEAKNESS, 400));
                }

                if (this.world.isRemote) {
                    if (!this.isSilent()) {
                        this.world.playSound(this.getPosX() + 0.5D, this.getPosY() + 0.5D, this.getPosZ() + 0.5D, SoundEvents.ENTITY_ILLUSIONER_CAST_SPELL, this.getSoundCategory(), 1.0F + this.rand.nextFloat(), this.rand.nextFloat() * 0.7F + 0.3F, false);
                    }

                    for (int i = 0; i < 20; ++i) {
                        double d0 = this.rand.nextGaussian() * 0.02D;
                        double d1 = this.rand.nextGaussian() * 0.02D;
                        double d2 = this.rand.nextGaussian() * 0.02D;
                        this.world.addParticle(ParticleTypes.EFFECT, this.getPosXWidth(1.0D) - d0 * 10.0D, this.getPosYRandom() - d1 * 10.0D, this.getPosZRandom(1.0D) - d2 * 10.0D, d0, d1, d2);
                    }
                }
            }

            if (this.teleportCastingTime == 0 && this.isAggressive()) {
                LivingEntity entity = HerobrineMageEntity.this.getAttackTarget();
                if (entity != null) {
                    int x = (int) entity.getPosX();
                    int y = (int) entity.getPosY();
                    int z = (int) entity.getPosZ();
                    BlockState block = world.getBlockState(new BlockPos(x, y + 3, z));
                    BlockState blockAt = world.getBlockState(new BlockPos(x, y + 4, z));
                    if (blockAt.getBlock() == Blocks.AIR.getDefaultState().getBlock() && block.getBlock() == Blocks.AIR.getDefaultState().getBlock() || blockAt.getBlock() == Blocks.CAVE_AIR.getDefaultState().getBlock() && block.getBlock() == Blocks.CAVE_AIR.getDefaultState().getBlock() || blockAt.getBlock() == Blocks.CAVE_AIR.getDefaultState().getBlock() && block.getBlock() == Blocks.AIR.getDefaultState().getBlock() || blockAt.getBlock() == Blocks.AIR.getDefaultState().getBlock() && block.getBlock() == Blocks.CAVE_AIR.getDefaultState().getBlock()) {
                        entity.setPositionAndUpdate(x, y + 4, z);
                        if (this.world.isRemote) {
                            if (!this.isSilent()) {
                                this.world.playSound(this.getPosX() + 0.5D, this.getPosY() + 0.5D, this.getPosZ() + 0.5D, SoundEvents.ENTITY_ILLUSIONER_CAST_SPELL, this.getSoundCategory(), 1.0F + this.rand.nextFloat(), this.rand.nextFloat() * 0.7F + 0.3F, false);
                            }
                            for (int i = 0; i < 20; ++i) {
                                double d0 = this.rand.nextGaussian() * 0.02D;
                                double d1 = this.rand.nextGaussian() * 0.02D;
                                double d2 = this.rand.nextGaussian() * 0.02D;
                                this.world.addParticle(ParticleTypes.EFFECT, this.getPosXWidth(1.0D) - d0 * 10.0D, this.getPosYRandom() - d1 * 10.0D, this.getPosZRandom(1.0D) - d2 * 10.0D, d0, d1, d2);
                            }
                        }
                    }
                }
            }
        }
        super.livingTick();
    }
}