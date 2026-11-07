package GierJava.world.blocks;

import arc.math.*;
import arc.math.geom.*;
import mindustry.world.*;
import mindustry.world.block.defense.*;
import mindustry.content;

import static mindustry.Vars.*;

public class SlimeMold extends Wall{

    public float growthChance = 0.02f;
    public int maxNeighbors = 3;

    public SlimeMold(String name){
        super(name);
        update = true;
    }

    public class SlimeMoldBuild extends WallBuild{

        public int getNearbyBlocks(){
            int found = 0;

            for(int dx = -1; dx <= 1; dx++){
                for(int dy = -1; dy <= 1; dy++){
                    if(dx == 0 && dy == 0) continue;

                    Tile nearby = world.tile(tile.x + dx, tile.y + dy);

                    if(nearby != null && nearby.block() == SlimeMold.this){
                        found++;
                    }
                }
            }

            return found;
        }

        @Override
        public void updateTile(){
            if(getNearbyBlocks() >= maxNeighbors) return;
            if(!Mathf.chanceDelta(growthChance)) return;

            int direction = Mathf.random(3);
            Point2 dir = Geometry.d4[direction];

            Tile nearby = world.tile(
                tile.x + dir.x,
                tile.y + dir.y
            );

            if(nearby == null) return;
            if(nearby.block() != Blocks.air) return;

            nearby.setBlock(block, team);
        }
    }
}
