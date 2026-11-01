package GierJava.world.blocks;

import arc.util.*;
import mindustry.world.blocks.defense.turrets.*;

public class MinigunTurret extends ItemTurret {

    public float acceleration = 0.15f;
    public float deceleration = 0.3f;

    public MinigunTurret(String name){
        super(name);
    }

    public class MinigunBuild extends ItemTurretBuild{

        public float spin;

        @Override
        public void updateTile(){

            super.updateTile();

            if(isActive() && hasAmmo() && isShooting()){
                spin = Math.min(spin + acceleration * Time.delta, 1f);
            }else{
                spin = Math.max(spin - deceleration * Time.delta, 0f);
            }
        }

        @Override
        protected void updateReload(){
            reloadCounter += spin * (delta() * ammoReloadMultiplier() * baseReloadSpeed());
        }
    }
}
