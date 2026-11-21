package GierJava.entities.comp;

import ent.anno.Annotations.*;
import mindustry.gen.*;
import mindustry.entities.comp.*;
import mindustry.type.*;
import mindustry.ai.Pathfinder;
import arc.math.geom.Vec2;
import static arc.Core.*;
import mindustry.gen.Unitc;

@EntityComponent
abstract class HybridComp implements Unitc {

  @Import UnitType type 
  
  public void movePref(Vec2 movement){
    if(type.omniMovement || (!isFlying() && floorOn().isLiquid)){
        moveAt(movement);
    }else{
        rotateMove(movement);
    }
  }
  
}
