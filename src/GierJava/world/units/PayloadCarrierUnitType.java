package GierJava.world.units;

import arc.*;
import arc.audio.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.g2d.TextureAtlas.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.ai.*;
import mindustry.ai.Pathfinder.*;
import mindustry.ai.types.*;
import mindustry.content.*;
import mindustry.type.*;
import mindustry.core.*;
import mindustry.ctype.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.entities.part.*;
import mindustry.entities.units.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.graphics.MultiPacker.*;
import mindustry.logic.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.liquid.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.blocks.storage.*;
import static arc.graphics.g2d.Draw.*;
import static mindustry.Vars.*;

public class PayloadCarrierUnitType extends UnitType{

  public PayloadCarrierUnitType(String name){
        super(name);
  }
  
private void updateHeldPayloads(Unit unit){
      if(!(unit instanceof Payloadc payloadc)) return;

    for(Payload payload : payloadc.payloads()){
        if(payload instanceof UnitPayload unitPayload){
            unitPayload.unit.update();
        }else if(payload instanceof BuildPayload buildPayload){
            buildPayload.build.update();
          
          if (unit.stack.item != null && buildPayload.build.acceptItem(buildPayload.build, unit.stack.item)){
            buildPayload.build.handleItem(buildPayload.build, unit.stack.item);
            unit.stack.amount -= 1;
          }

          if(buildPayload.build instanceof LiquidRouter.LiquidRouterBuild liqBuild){
    for(Payload payloadIn : payloadc.payloads()){
        if(payloadIn instanceof BuildPayload otherPayload){
            Building other = otherPayload.build;

            if(other == liqBuild || other.liquids == null) continue;

            Liquid liquid = liqBuild.liquids.current();

            if(liquid != null && liqBuild.liquids.get(liquid) > 0f && other.acceptLiquid(liqBuild, liquid)){
                liqBuild.transferLiquid(other, 2f, liquid);
            }
        }
    }
}

if(buildPayload.build instanceof StorageBlock.StorageBuild storBuild){
    for(Payload payloadIn : payloadc.payloads()){
        if(payloadIn instanceof BuildPayload otherPayload){
            Building other = otherPayload.build;

            if(other == storBuild || other.items == null) continue;

            for(Item item : content.items()){
                if(storBuild.items.get(item) <= 0) continue;

                if(other.acceptItem(storBuild, item)){
                    other.handleItem(storBuild, item);
                    storBuild.items.remove(item, 1);
                }
            }
        }
    }
}
          
        }
    }
}

@Override
public void update(Unit unit){
    updateHeldPayloads(unit);
}

@Override
public void updatePayload(Unit unit, @Nullable Unit unitHolder, @Nullable Building buildingHolder){
    updateHeldPayloads(unit);
}
  
  @Override
public <T extends Unit & Payloadc> void drawPayload(T unit){
    if(!unit.hasPayload()) return;

    float layer = flying ? flyingLayer : groundLayer;
    
    float prev = Draw.z();
    Draw.z(layer + 0.02f);

    for(Payload payload : unit.payloads()){
        payload.set(unit.x, unit.y, unit.rotation);
        payload.draw();
    }

    Draw.z(prev);
}
  
}
