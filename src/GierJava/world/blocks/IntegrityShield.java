package mindustry.world.blocks.defense;

import arc.*;
import arc.audio.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.game.EventType.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class IntegrityShield extends ForceProjector  {

    public IntegrityShield(String name){
        super(name);
        targetable = false;
        solid = false;
    }

    public class IntegrityShieldBuild extends ForceBuild {

        @Override 
        public void updateTile(){
            super.updateLighting();
            health = buildup;
        }

    }
}