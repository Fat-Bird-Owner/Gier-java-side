package GierJava.world.blocks;

import arc.math.*;
import arc.struct.*;
import arc.util.*;

import mindustry.gen.*;
import mindustry.world.*;
import mindustry.type.*;
import mindustry.content.*;
import mindustry.graphics.*;
import mindustry.world.draw.*;

public class StatusProjector extends Block{

    public int radius = 10;
    public float statusDuration = 15f;
    public StatusEffect status = StatusEffects.overclock;

    public DrawBlock drawer = new DrawDefault();
  
    public StatusProjector(String name){
        super(name);
        update = true;
    }

    @Override
    public void load(){
        super.load();
        drawer.load(this);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        x *= tilesize;
        y *= tilesize;
        x += offset;
        y += offset;

        Drawf.dashSquare(status.color, x, y, range * tilesize);

    }
  
    public class StatusProjectorBuild extends Building{

        @Override
        public float warmup(){
            return warmup;
        }

        @Override
        public float totalProgress(){
            return totalTime;
        }

        @Override
        public void draw(){
            drawer.draw(this);
        }
      
        @Override
        public void updateTile(){

            float range = radius * Vars.tilesize;
            boolean found = false;
          
            Groups.unit.intersect(
                x - range,
                y - range,
                range * 2f,
                range * 2f,
                unit -> {
                  unit.applyStatus(status, statusDuration);
                  found = true;
                }
            );

            if (found){
              warmup = Mathf.approachDelta(warmup, found ? 1f : 0f, 1f / 70f);
              totalTime += warmup * Time.delta;
            }
          
        }
    }
}
