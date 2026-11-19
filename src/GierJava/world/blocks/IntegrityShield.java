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

    public float damageMultiplier = 0.25f; // Placeholder vars
    public Color damagedColor = Pal.remove;
    
    protected static final Cons<Bullet> shieldConsumer = bullet -> {
        if(bullet.team != paramEntity.team && bullet.type.absorbable && !bullet.absorbed &&
            Intersector.isInRegularPolygon(paramBlock.sides, paramEntity.x, paramEntity.y, paramEntity.realRadius(), paramBlock.shieldRotation, bullet.x, bullet.y)){

            bullet.absorb();
            paramBlock.hitSound.at(bullet.x, bullet.y, 1f + Mathf.range(0.1f), paramBlock.hitSoundVolume);
            paramBlock.absorbEffect.at(bullet);
            paramBlock.absorbEffect.at(paramEntity);
            paramEntity.hit = 1f;
            paramEntity.damage(bullet.type.shieldDamage(bullet) * paramBlock.damageMultiplier);
            paramEntity.buildup += bullet.type.shieldDamage(bullet);
        }
    };
    
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
        
          */

        public Color getColor(){
            return damagedColor.cpy().lerp(team.color, Mathf.clamp(health / maxHealth));
        }
        
        @Override
        public void drawShield(){
            if(!broken){
                float radius = realRadius();

                if(radius > 0.001f){
                    Draw.color(getColor(), Color.white, Mathf.clamp(hit));

                    if(renderer.animateShields){
                        Draw.z(Layer.shields + 0.001f * hit);
                        Fill.poly(x, y, sides, radius, shieldRotation);
                    }else{
                        Draw.z(Layer.shields);
                        Lines.stroke(1.5f);
                        Draw.alpha(0.09f + Mathf.clamp(0.08f * hit));
                        Fill.poly(x, y, sides, radius, shieldRotation);
                        Draw.alpha(1f);
                        Lines.poly(x, y, sides, radius, shieldRotation);
                        Draw.reset();
                    }
                }
            }

            Draw.reset();
        }
        
        @Override
        public void deflectBullets(){
            float radius = realRadius();

            if(radius > 0 && !broken){
                paramBlock = IntegrityShield.this;
                paramEntity = this;

                Groups.bullet.intersect( x - radius, y - radius, radius * 2f, radius * 2f, shieldConsumer);
            }
        }

        @Override
        public float realRadius(){
            return super.realRadius() * (health / (maxHealth + phaseShieldBoost * phaseHeat));
        }
        
    }
}
