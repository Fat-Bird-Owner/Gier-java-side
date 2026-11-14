package GierJava.world.blocks;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.annotations.Annotations.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.distribution.Conveyor.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

// Stackconveyor that can accept items even if its not the start
public class OpenStackConveyor extends StackConveyor {
  public class OpenStackConveyorBuild extends StackConveyorBuild{
    
        @Override
        public boolean acceptItem(Building source, Item item){
            if(this == source) return items.total() < itemCapacity && (!items.any() || items.has(item)); //player threw items
            if(cooldown > recharge - 1f) return false; //still cooling down
            return !((state != stateLoad) //not a loading dock
            ||  (items.any() && !items.has(item)) //incompatible items
            ||  (items.total() >= getMaximumAccepted(item)); //filled to capacity
        }

  }
}
