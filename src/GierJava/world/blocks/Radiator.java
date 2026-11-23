package GierJava.world.block;

import arc.math.*;
import arc.struct.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.graphics.*;
import mindustry.world.blocks.heat.*;

import static mindustry.Vars.*;

public class Radiator extends Block{

    public float heatOutput = 10f;
    public int radius = 5;
  
    public Radiator(String name){
        super(name);
        update = true;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);       
        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range, Pal.placing);      
    }
  
    public class RadiatorBuild extends Building implements HeatBlock{

        public Seq<Building> nearby = new Seq<>();
        public int lastChanges = -1;
        public float heat = 0f;

        @Override
        public float heatFrac(){
            return heat / heatOutput;
        }

        @Override
        public float heat(){
            return heat;
        }
        
        public void updateSeq(){
            nearby.clear();

            Vars.indexer.eachBlock(this, radius * Vars.tilesize, other ->
                other instanceof HeatConsumer,
                other -> nearby.add(other)
            );

            lastChanges = Vars.world.tileChanges;
        }

        @Override
        public void updateTile(){
            if(Vars.world.tileChanges != lastChanges){
                updateSeq();
            }

            heat = Mathf.approachDelta(heat, heatOutput * efficiency, warmupRate * delta());

            for(Building other : nearby){
                HeatConsumer consumer = (HeatConsumer)other;
                float[] sideHeat = consumer.sideHeat();

                for(int i = 0; i < sideHeat.length; i++){
                    sideHeat[i] = Math.max(sideHeat[i], heat);
                }
            }
        }

        
    }
}
