package GierJava.world.blocks;

import arc.math.*;
import arc.math.geom.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.*;
import mindustry.content.*;
import arc.util.Interval;

import static mindustry.Vars.*;

public class SlimeMold extends Wall{

    public float growthChance = 0.02f, depriveDuration = 180f, deadDuration = 400f, depriveDamage = 0.45f;
    public int maxNeighbors = 4;
    
    public SlimeMold(String name){
        super(name);
        update = true;
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
        public void updateTile(){
            if (signal == null && deprive >= depriveDuration){
                deprive += 1f;
                damage(depriveDamage);
                if (deprive >= deadDuration) kill();
            } else if (signal == null) {
                deprive += 1f;
            } else {
                deprive = 0f;
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
