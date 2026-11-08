package GierJava.world.blocks;

import arc.math.*;
import arc.math.geom.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.defense.*;
import mindustry.content.*;
import arc.util.Interval;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.*;

import static mindustry.Vars.*;

public class SlimeMold extends Wall{

    public float growthChance = 0.02f, depriveDuration = 180f, deadDuration = 400f, depriveDamage = 0.45f, blockDamage = 15f, transmitCooldown = 5f;
    public int maxNeighbors = 3, signalProduct = 15;
    
    public SlimeMold(String name){
        super(name);
        update = true;
        drawCached = false;
        baseShake = 0f;
        underBullets = true;
        drawCracks = false;
        drawTeamOverlay = false;
        createRubble = false;
        rebuildable = false;
        drawDynamic = true;
        drawCached = false;
    }

    public static class Signal {
        public int strength;

        public Signal(int strength){
            this.strength = strength;
        }
    }
    
    public class SlimeMoldBuild extends WallBuild{

        public Signal signal;
        public float deprive;
        public float drawScale = 1f;
        public float transmitInterval;
        public int dist = 0;
        
        public int getNearbyBlocks(Tile near){
            int found = 0;

            for(int dx = -1; dx <= 1; dx++){
                for(int dy = -1; dy <= 1; dy++){
                    if(dx == 0 && dy == 0) continue;

                    Tile nearby = world.tile(near.x + dx, near.y + dy);

                    if(nearby != null && nearby.block() == SlimeMold.this){
                        found++;
                    }
                }
            }

            return found;
        }

        @Override
        public void draw(){
            if(autotile){
                TextureRegion region = autotileRegions[TileBitmask.values[autotileBits]];
                float drawSize = tilesize * size * drawScale;
                Draw.rect(region, x, y, drawSize, drawSize);
            }else{
                super.draw();
            }
        }
        
        @Override
        public void updateTile(){

            transmitInterval -= Time.delta;
            
            drawScale = Mathf.lerpDelta(
                drawScale,
                signal != null ? 1.3f : 1f,
                0.08f
            );
            
            if (signal == null && deprive >= depriveDuration){
                deprive += 1f;
                damage(depriveDamage);
                if (deprive >= deadDuration) kill();
            } else if (signal == null) {
                deprive += Time.delta;
            } else {
                deprive = 0f;
            }
            
            if(signal != null && transmitInterval <= 0f){
    transmitInterval = transmitCooldown;

    SlimeMoldBuild best = null;
    int lowestDist = Integer.MAX_VALUE;

    for(int i = 0; i < 4; i++){
        Point2 dir = Geometry.d4[i];

        Tile nearby = world.tile(
            tile.x + dir.x,
            tile.y + dir.y
        );

        if(nearby == null || !(nearby.build instanceof SlimeMoldBuild mold)) {
            if (nearby.team() != team) continue;
                nearby.build.damage(blockDamage);
            continue;
        }
        if(mold.team != team || mold.signal != null) continue;

        if(mold.dist == 0 || mold.dist > dist){
            if(mold.dist < lowestDist){
                lowestDist = mold.dist;
                best = mold;
            }
        }
    }

    if(best != null){
        signal.strength--;

        if(signal.strength <= 0){
            signal = null;
            return;
        }

        best.dist = dist + 1;
        best.signal = signal;
        signal = null;
    }
            }

            if (signal == null && transmitInterval <= 0f){
                if (tile.overlay() == null || tile.overlay().itemDrop == null) return;
                signal = new Signal(signalProduct);
                dist = 0;
            }
            
            if(!Mathf.chanceDelta(growthChance) || signal == null) return;

            int direction = Mathf.random(3);
            Point2 dir = Geometry.d4[direction];

            Tile nearby = world.tile(
                tile.x + dir.x,
                tile.y + dir.y
            );

            if(nearby == null) return;
            if(nearby.block() != Blocks.air) return;
            if(getNearbyBlocks(nearby) >= maxNeighbors) return;
            
            nearby.setBlock(block, team);
        }
    }
}
