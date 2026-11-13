package mindustry.world.blocks.defense;

import arc.*;
import arc.audio.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.game.EventType.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;


// Placeholder class for custom gimmicks
public class IntegrityShield extends ForceProjector  {

    public float height = 8f, mult = 0.4f; // Placeholder vars
    
    public IntegrityShield(String name){
        super(name);
        targetable = false;
        solid = false;
    }

    public class IntegrityShieldBuild extends ForceBuild {

        /*
        @Override 
        public void updateTile(){
            super.updateTile();
            warmup = 1f;
        }


        public float getxOffset(){
            return Mathf.clamp((x - Core.camera.position.x)*mult, -height, height);
        }

        public float getyOffset(){
            return Mathf.clamp((y - Core.camera.position.y)*mult, -height, height);
        }
        
        public void drawShield(){
            super.drawShield();
            if(!broken){
                float radius = realRadius();

                if(radius > 0.001f){
                        Draw.color(team.color, Color.white, Mathf.clamp(hit));
                        Draw.z(Layer.shields + 1f * hit);
                        Lines.stroke(1.5f);
                        Draw.alpha(0.09f + Mathf.clamp(0.08f * hit));
                        Fill.poly(x + getxOffset(), y + getyOffset(), sides, radius, shieldRotation);
                        Draw.alpha(1f);
                        Lines.poly(x + getxOffset(), y + getyOffset(), sides, radius, shieldRotation);
                        Draw.reset();                
                }
            }

            Draw.reset();
        }
          */

        public float realRadius(){
            return super.realRadius() * (buildup / (shieldHealth + phaseShieldBoost * phaseHeat));
        }
        
    }
}
