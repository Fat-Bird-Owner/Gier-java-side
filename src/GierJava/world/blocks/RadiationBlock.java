package GierJava.world.blocks;

import arc.*;
import arc.math.*;
import arc.struct.*;
import arc.util.io.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.ui.*;
import mindustry.world.blocks.production.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;
import mindustry.world.*;

public class RadiationBlock extends Block {

  public float inputSpeed = 0.1f, maxRadiation = 10f, disperseSpeed = 1f / 60f;
  public boolean moveForward = true;
  
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

    public void updateTile(){
      if (front() != null && moveForward == true && canMove(front(), inputSpeed)){
          moveRadiation(front(), inputSpeed);
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
        if (canMove(radiationBuild, inputSpeed) && radiation > 0f) {
            radiationBuild.radiation += inputSpeed;
            radiation -= inputSpeed;
        }
    }
    
  }  
}
