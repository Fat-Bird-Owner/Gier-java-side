package GierJava.world.blocks;

import arc.graphics.Color;
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
import mindustry.content.Liquids;

import static mindustry.Vars.*;

public class PowerCore extends CoreBlock {

    public float powerProduction = 2f;
    public Color landingColor = Color.valueOf("8ebdffff");
    
    public PowerCore(String name){
        super(name);
        outputsPower = true;
        consumesPower = false;
        hasPower = true;
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
            addBar("power", (PowerCoreBuild entity) -> new Bar(() ->
            Core.bundle.format("bar.poweroutput",
            Strings.fixed(entity.getPowerProduction() * 60 * entity.timeScale(), 1)),
            () -> Pal.powerBar,
            () -> entity.getPowerProduction() / powerProduction * 60f));
        }
    }

    public class PowerCoreBuild extends CoreBuild {

        @Override
        public float getPowerProduction(){
            return enabled ? powerProduction : 0f;
        }

        @Override
        public void drawLanding(float x, float y){

        float fin = renderer.getLandTimeIn();
        float fout = 1f - fin;

        float progress = Interp.pow3Out.apply(fin);

        float height = 18f * fout;
        float scale = Mathf.lerp(0.82f, 1f, progress);
        float rotation = 5f * fout;

        float coreRadius = size * tilesize * 0.55f;

        Draw.z(Layer.effect);

        Draw.color(Color.black, 0.18f * fout);
        Fill.circle(x, y, coreRadius * 0.85f);

        Draw.color(landingColor, 0.08f * fout);
        Fill.circle(x, y, coreRadius * 1.4f);

        Draw.color(Color.white);
        Draw.rect(
            region,
            x,
            y + height,
            region.width * region.scl() * scale,
            region.height * region.scl() * scale,
            rotation
            );

            if(teamRegions[team.id] != teamRegion){
                Draw.color(team.color);
                Draw.rect(
                    teamRegions[team.id],
                    x,
                    y + height,
                    region.width * region.scl() * scale,
                    region.height * region.scl() * scale,
                    rotation
                );
            }

            Draw.reset();
        }
        
    }

}
