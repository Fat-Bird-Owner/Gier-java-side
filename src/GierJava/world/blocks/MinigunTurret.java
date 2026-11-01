package GierJava.world.blocks;

import arc.util.*;
import mindustry.world.blocks.defense.turrets.*;

public class MinigunTurret extends ItemTurret {

    public float acceleration = 180f;
    public float deceleration = 400f;

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
            reloadCounter += spin * (delta() * ammoReloadMultiplier() * baseReloadSpeed());
        }
    }
}
