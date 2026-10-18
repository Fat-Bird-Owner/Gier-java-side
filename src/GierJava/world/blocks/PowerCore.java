package GierJava.world.blocks;

import arc.*;
import arc.audio.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.content.Liquids;

import static mindustry.Vars.*;

public class GeneratorCore extends CoreBlock {

public static float powerProduction = 2f;

    public GeneratorCore(String name){
        super(name);
        outputsPower = true;
        consumesPower = false;
        hasPower = true;
        consumePowerBuffered(300f);
    }

    @Override 
    public void setStats(){
        super.setStats();
        stats.add(Stat.basePowerGeneration, powerProduction* 60f, StatUnit.powerSecond);
    }

    @Override
    public void setBars(){
        super.setBars();

        if(hasPower && outputsPower){
            addBar("power", (GeneratorCoreBuild entity) -> new Bar(() ->
            Core.bundle.format("bar.poweroutput",
            Strings.fixed(entity.getPowerProduction() * 60 * entity.timeScale(), 1)),
            () -> Pal.powerBar,
            () -> entity.getPowerProduction() / powerProduction*60f));
        }
    }

    public class GeneratorCoreBuild extends CoreBuild {

        @Override
        public float getPowerProduction(){
            return enabled ? powerProduction * 60f : 0f;
        }

    }

}