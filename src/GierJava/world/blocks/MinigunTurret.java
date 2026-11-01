package GierJava.world.blocks;

import arc.util.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.type.Item;
import arc.math.*;

import static mindustry.Vars.*;

public class MinigunTurret extends ItemTurret {

    public float acceleration = 180f;
    public float deceleration = 400f;
    public float minFlammability = -1f;
    public float overheatDamage = 1f;
    
    public MinigunTurret(String name){
        super(name);
    }

    public class MinigunBuild extends ItemTurretBuild{

        public float spin;

        @Override
        public void updateTile(){

            super.updateTile();

            if(isActive() && hasAmmo() && isShooting()){
                spin = Math.min(spin + (1f / acceleration) * Time.delta, 1f);
            }else{
                spin = Math.max(spin - (1f / deceleration) * Time.delta, 0f);
            }
        }

        @Override
        protected void updateReload(){

            Item item = getAmmoContent();
            
            reloadCounter += spin * (delta() * ammoReloadMultiplier() * baseReloadSpeed());
            if (item != null && minFlammability != -1f){
                if (item.flammability >= minFlammability){
                    
                damage(overheatDamage);
                    
                if(Mathf.chance(0.06 * coolant.amount)){
                    coolEffect.at(x + Mathf.range(size * tilesize / 2f), y + Mathf.range(size * tilesize / 2f));
                }
            }}
            
        }
    }
}
