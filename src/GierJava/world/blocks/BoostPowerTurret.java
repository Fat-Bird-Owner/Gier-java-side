package GierJava.world.blocks;

import mindustry.content.Items;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.content.*;
import mindustry.type.Item;
import arc.util.Time;
import mindustry.world.meta.*;

import static mindustry.type.ItemStack.with;

public class BoostPowerTurret extends PowerTurret {

    public Item boostItem = null;

    public int boostItemAmount = 1;

    public float boostItemUseTime = 180f;

    public float boostMultiplier = 1.5f;

    public float rangeMultiplier = 1f;

    public BoostPowerTurret(String name){
        super(name);
        consumeItem(boostItem, boostItemAmount).boost();
    }

    @Override
    public void setStats(){
        super.setStats();

            stats.remove(Stat.booster);
            stats.add(Stat.booster, StatValues.itemBoosters(
                "{0}" + StatUnit.timesSpeed.localized(),
                stats.timePeriod, boostMultiplier * 100f, rangeMultiplier * 100f,
                with(boostItem, boostItemAmount))
            );
    }

    public class BoostPowerTurretBuild extends PowerTurretBuild {

        public float boostTimer = 0f;

        @Override 
        public void updateTile(){
            super.updateTile();
            boostTimer = Math.max(0f, boostTimer - delta());
        }

        @Override
        protected void updateReload(){
            float multiplier = 1f;

            if(items.has(boostItem)){

                if(boostTimer <= 0f){
                    items.remove(boostItem, boostItemAmount);
                    boostTimer = boostItemUseTime;
                }
                multiplier += boostMultiplier;
            }

            reloadCounter += delta()
                * ammoReloadMultiplier()
                * baseReloadSpeed()
                * multiplier;
            
        }

    }
}