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

					float impactTime = 0.68f;
					float revealTime = 0.84f;

					float fall = Mathf.clamp(fin / impactTime);
					float explosion = Mathf.clamp((fin - impactTime) / 0.16f);
					float reveal = Mathf.clamp((fin - revealTime) / (1f - revealTime));

					float podHeight = 190f * (1f - Interp.pow3In.apply(fall));

					float impactX = x;
					float impactY = y;

					Draw.z(Layer.effect);

					Draw.color(Color.black, 0.22f * (1f - reveal));
					Fill.circle(x, y, 34f + fall * 12f);

					if(fin < impactTime){

							float rotation = (1f - fall) * 8f;

							Draw.color(Color.valueOf("252b32"));
							Draw.rect("white", x, y + podHeight, 66f, 54f, rotation);

							Draw.color(Color.valueOf("59636b"));

							for(int i = 0; i < 4; i++){
									float angle = i * 90f + rotation;
									float px = x + Mathf.cosDeg(angle) * 25f;
									float py = y + podHeight + Mathf.sinDeg(angle) * 20f;

									Draw.rect("white", px, py, 13f, 49f, angle);
							}

							Draw.color(Color.valueOf("87939a"));
							Draw.rect("white", x, y + podHeight + 27f, 57f, 8f, rotation);

							Draw.color(Color.valueOf("e69b36"));
							Draw.rect("white", x - 17f, y + podHeight, 4f, 42f, rotation);
							Draw.rect("white", x + 17f, y + podHeight, 4f, 42f, rotation);
					}

					if(fin >= impactTime){

							float radius = 18f + explosion * 85f;
							float alpha = 1f - explosion;

							Draw.color(Color.white, alpha * 0.85f);
							Fill.circle(impactX, impactY, radius * 0.35f);

							Draw.color(Color.valueOf("ffad45"), alpha * 0.65f);
							Fill.circle(impactX, impactY, radius * 0.65f);

							Draw.color(Color.valueOf("d6dce0"), alpha);
							Lines.stroke(4f * alpha);
							Lines.circle(impactX, impactY, radius);

							for(int i = 0; i < 16; i++){
									float angle = i * 22.5f;
									float distance = explosion * (35f + (i % 4) * 13f);

									float sx = impactX + Mathf.cosDeg(angle) * distance;
									float sy = impactY + Mathf.sinDeg(angle) * distance;

									Draw.color(i % 2 == 0 ? Color.orange : Color.white, alpha);
									Fill.circle(sx, sy, 1.5f + alpha);
							}

							for(int i = 0; i < 8; i++){
									float angle = i * 45f + 12f;
									float distance = explosion * (25f + (i % 3) * 18f);

									float px = x + Mathf.cosDeg(angle) * distance;
									float py = y + Mathf.sinDeg(angle) * distance;

									float rotation = angle + explosion * (i % 2 == 0 ? 220f : -220f);
									float panelAlpha = 1f - Mathf.clamp(explosion * 0.9f);

									Draw.color(Color.valueOf(i % 2 == 0 ? "59636b" : "87939a"), panelAlpha);
									Draw.rect("white", px, py, 22f, 9f, rotation);
							}
					}

					if(fin >= revealTime){

							float coreAlpha = Interp.pow2Out.apply(reveal);

							Draw.color(Color.white);
							Draw.alpha(coreAlpha);

							Draw.rect(
									region,
									x,
									y,
									region.width * region.scl(),
									region.height * region.scl()
							);

							if(teamRegions[team.id] != teamRegion){
									Draw.color(team.color);
									Draw.alpha(coreAlpha);

									Draw.rect(
											teamRegions[team.id],
											x,
											y,
											region.width * region.scl(),
											region.height * region.scl()
									);
							}
					}

					Draw.reset();
            }
        
    }

}
