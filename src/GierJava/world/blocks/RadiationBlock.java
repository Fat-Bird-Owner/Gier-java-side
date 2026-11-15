package GierJava.world.blocks;

import arc.*;
import arc.math.*;
import arc.struct.*;
import arc.util.io.*;
import arc.util.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.ui.*;
import mindustry.world.blocks.production.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;
import mindustry.world.*;
import mindustry.entities.comp.*;
import mindustry.gen.Building;

public class RadiationBlock extends Block {

  public float inputSpeed = 0.1f, maxRadiation = 10f, disperseSpeed = 1f / 60f, productionSpeed = 0.5f, consumeTime = 180f;
  public boolean moveForward = true, canProduce = true, canDisperse = true;
  
  public RadiationBlock(String name){
    super(name);
    update = true;
  }

    @Override
    public void setBars(){
        super.setBars();
        addBar("radiation", (RadiationBuild entity) -> new Bar(Core.bundle.format("bar.heatamount", (int)(entity.radiation + 0.001f)), Pal.reactorPurple, () -> entity.radiationFraction()));
    }
  
  public class RadiationBuild extends Building {
    public float radiation;
    public float useTime;

    public void updateTile(){
      if (front() != null && moveForward == true && canMove(front(), inputSpeed * delta())){
          moveRadiation(front());
      }

      if (canDisperse){
        disperseRadiation();
      }
      
      if (canProduce == true && canConsume() && radiation < maxRadiation){
        useTime += delta();
        radiation += productionSpeed * delta();
        if (useTime >= consumeTime){
          consume();
        }
      }
    }
    
    public void disperseRadiation(){
    radiation = Math.max(0f, radiation - disperseSpeed * delta());
    }

    public float radiationFraction(){
      return radiation / maxRadiation;
    }
    
    public boolean canDisperse(){
      return true;
    }
    
    public boolean canMove(Building build, float amount){
      if (!(build instanceof RadiationBuild radiationBuild)) return false;
      return radiationBuild.radiation + amount <= maxRadiation;
    }

    public void moveRadiation(Building build){
        if (!(build instanceof RadiationBuild radiationBuild)) return;
        if (canMove(radiationBuild, inputSpeed) && radiation > 0f && radiationBuild.radiation < radiationBuild.block.maxRadiation) {
            radiationBuild.radiation += inputSpeed * delta();
            radiation -= inputSpeed * delta();
        }
    }


       @Override
        public void write(Writes write){
            super.write(write);

            write.f(radiation);
            write.f(useTime);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            radiation = read.f();
            useTime = read.f();
        }
  }  
}
