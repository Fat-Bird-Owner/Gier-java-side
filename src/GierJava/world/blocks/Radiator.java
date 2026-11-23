package GierJava.world.block;

import arc.math.*;
import arc.struct.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.graphics.*;

import static mindustry.Vars.*;

public class Radiator extends Block{

    public float heat = 10f;
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
  
    public class RadiatorBuild extends Building{

        public Seq<Building> nearby = new Seq<>();
        public int lastChanges = -1;

        public void updateSeq(){
            nearby.clear();

            Vars.indexer.eachBlock(this, radius * Vars.tilesize, other -> {
              
              return true; 
              
              }, other -> {
                nearby.add(other);
            });

            lastChanges = Vars.world.tileChanges;
        }

        @Override
        public void updateTile(){
            if(Vars.world.tileChanges != lastChanges){
                updateSeq();
            }

          if(efficiency > 0f){
            for(Building other : nearby){
              // Placeholder
            }
          }
          
        }
    }
}
