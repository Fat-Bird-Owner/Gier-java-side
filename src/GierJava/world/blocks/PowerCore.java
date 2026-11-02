package GierJava.world.blocks;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.graphics.g3d.PlanetParams;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class PowerCore extends CoreBlock{

			public float powerProduction = 2f;

			public PowerCore(String name){
					super(name);
					outputsPower = true;
					consumesPower = false;
					hasPower = true;
			}

			@Override
			public void setStats(){
					super.setStats();
					stats.add(Stat.basePowerGeneration, powerProduction * 60f, StatUnit.powerSecond);
			}

			@Override
			public void setBars(){
					super.setBars();

					if(hasPower && outputsPower){
							addBar("power", (PowerCoreBuild entity) -> new Bar(
									() -> Core.bundle.format(
											"bar.poweroutput",
											Strings.fixed(entity.getPowerProduction() * 60f * entity.timeScale(), 1)
									),
									() -> Pal.powerBar,
									() -> entity.getPowerProduction() / powerProduction * 60f
							));
					}
			}

			public class PowerCoreBuild extends CoreBuild{

					private FrameBuffer landingBuffer;
					private Planet landingPlanet;
					private final PlanetParams landingParams = new PlanetParams();
					private final Vec3 landingDirection = new Vec3();

					@Override
					public float getPowerProduction(){
							return enabled ? powerProduction : 0f;
					}

					@Override
					public void beginLaunch(boolean launching){
							super.beginLaunch(launching);

							thrusterTime = 0f;

							if(headless || launching) return;

							if(landingBuffer != null){
									landingBuffer.dispose();
							}

							Sector sector = state.rules.sector;

							if(sector != null && sector.planet != null){
									landingPlanet = sector.planet;
									landingDirection.set(sector.tile.v).nor();
							}else{
									landingPlanet = state.rules.planetBackground != null
											? state.rules.planetBackground.planet
											: state.rules.planet;

									landingDirection.set(
											Mathf.random(-1f, 1f),
											Mathf.random(-1f, 1f),
											Mathf.random(-1f, 1f)
									).nor();
							}

							landingParams.planet = landingPlanet;
							landingParams.drawSkybox = true;
							landingParams.drawUi = false;
							landingParams.alwaysDrawAtmosphere = true;
							landingParams.uiAlpha = 0f;

							landingBuffer = new FrameBuffer(
									Core.graphics.getWidth(),
									Core.graphics.getHeight()
							);
					}

								@Override
			public void drawLaunch(){

					if(landingBuffer == null || landingPlanet == null) return;

					float fin = renderer.getLandTimeIn();
					float zoom = Interp.pow3In.apply(fin);

					float cloudProgress = Mathf.clamp((fin - 0.48f) / 0.48f);
					cloudProgress = Interp.pow2In.apply(cloudProgress);

					float fade = Mathf.clamp((fin - 0.88f) / 0.12f);

					landingParams.camPos.set(landingDirection);
					landingParams.zoom = Mathf.lerp(1f, -1.8f, zoom);

					renderer.planets.bloom.setThreshold(1.8f);

					landingBuffer.begin(Color.clear);
					renderer.planets.render(landingParams);
					landingBuffer.end();

					Draw.color(Color.white);
					Draw.alpha(1f - fade);

					Draw.rect(
							Draw.wrap(landingBuffer.getTexture()),
							Core.camera.position.x,
							Core.camera.position.y,
							Core.camera.width,
							-Core.camera.height
					);

					float cx = Core.camera.position.x;
					float cy = Core.camera.position.y;
					float w = Core.camera.width;
					float h = Core.camera.height;
					float size = Math.max(w, h);

					if(cloudProgress > 0f){

							float cloudAlpha = cloudProgress * 0.82f;

							Draw.color(Color.valueOf("a9bac4"), cloudAlpha * 0.45f);
							Fill.rect(cx, cy, w, h);

							Draw.color(Color.valueOf("c4d0d6"), cloudAlpha);

							for(int i = 0; i < 9; i++){

									float angle = i * 40f;
									float distance = size * 0.25f * (1f - cloudProgress);

									float cloudX = cx + Mathf.cosDeg(angle) * distance;
									float cloudY = cy + Mathf.sinDeg(angle) * distance;

									float radius = size * 0.38f * cloudProgress;

									Fill.circle(cloudX, cloudY, radius);
							}

							Draw.color(Color.valueOf("e0e6e9"), cloudAlpha * 0.65f);

							for(int i = 0; i < 6; i++){

									float angle = i * 60f + 30f;
									float distance = size * 0.18f * (1f - cloudProgress);

									float cloudX = cx + Mathf.cosDeg(angle) * distance;
									float cloudY = cy + Mathf.sinDeg(angle) * distance;

									Fill.circle(
											cloudX,
											cloudY,
											size * 0.22f * cloudProgress
									);
							}
					}

					Draw.reset();
			}

					@Override
					public void draw(){
							thrusterTime = 0f;

							if(renderer.getLandTime() > 0f && !renderer.isLaunching()){
									float alpha = Mathf.clamp((renderer.getLandTimeIn() - 0.78f) / 0.22f);

									Draw.alpha(alpha);
									super.draw();
									Draw.reset();
							}else{
									super.draw();
							}
					}

					@Override
					public void endLaunch(){
							if(landingBuffer != null){
									renderer.planets.bloom.setThreshold(0.8f);
									landingBuffer.dispose();
									landingBuffer = null;
							}

							landingPlanet = null;
							thrusterTime = 0f;
					}
			}
}
