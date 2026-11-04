package GierJava.world.blocks;

import arc.util.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.type.*;
import arc.math.*;
import arc.graphics.*;
import arc.scene.ui.layout.*;
import mindustry.ui.*;

import static mindustry.Vars.*;

public class MinigunTurret extends ItemTurret {

    public float acceleration = 180f;
    public float deceleration = 400f;
    public float maxAccel = 1f;
    public float overheatDamage = 1f;
    public float meltPoint = 1f;

    public Color coolColor = Color.valueOf("72d8ff");
    public Color warmColor = Color.valueOf("ffb347");
    public Color hotColor = Color.valueOf("ff3838");
    public Color criticalColor = Color.valueOf("a00020");
    public Color extremeColor = Color.valueOf("51205f");
    
    public MinigunTurret(String name){
        super(name);
    }

    @Override
    public void setBars(){
    super.setBars();

        addBar("spin", (MinigunBuild build) -> new Bar(
            () -> "bar.spin",
            () -> build.getSpinColor(),
            () -> build.spin / maxAccel
        ));
    }
    
    public class MinigunBuild extends ItemTurretBuild{

        public float spin;
        private Liquid liquid;
        
        @Override
        public void updateTile(){

            super.updateTile();

            if(isActive() && hasAmmo() && isShooting()){
                spin = Math.min(spin + (1f / acceleration) * Time.delta, maxAccel);
            }else{
                spin = Math.max(spin - (1f / deceleration) * Time.delta, 0.1f);
            }
        }

        public Color getSpinColor(){
        float progress = spin / maxAccel;

        if(progress < meltPoint){
            return coolColor.cpy().lerp(warmColor, progress / meltPoint);
        }

        float heat = (progress - meltPoint) / (1f - meltPoint);
        if(heat < 0.33f){
            return hotColor.cpy().lerp(criticalColor, heat / 0.33f);
        }

        if(heat < 0.66f){
            return criticalColor.cpy().lerp(extremeColor, (heat - 0.33f) / 0.33f);
        }

        return extremeColor;
        }
        
        @Override
        protected void updateReload(){

            if (liquids != null) {
             liquid = liquids.current();
            }
            
            reloadCounter += spin * (delta() * ammoReloadMultiplier() * baseReloadSpeed());
                if (spin > meltPoint && (liquids == null || liquid == null || liquids.get(liquid) < coolant.amount)){
                    
                damage(overheatDamage * (spin - meltPoint));
                    
                if(coolant != null && Mathf.chance(0.06 * coolant.amount)){
                    coolEffect.at(x + Mathf.range(size * tilesize / 2f), y + Mathf.range(size * tilesize / 2f));
                }
            }
            
        }
    }
}
