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

			private TextureRegion cloudRegion;

			public PowerCore(String name){
					super(name);
					outputsPower = true;
					consumesPower = false;
					hasPower = true;
			}

			@Override
			public void load(){
					super.load();
					cloudRegion = Core.atlas.find(name + "-clouds");
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

							if(landingPlanet == null) return;

							landingParams.planet = landingPlanet;
							landingParams.drawSkybox = true;
							landingParams.drawUi = false;
							landingParams.alwaysDrawAtmosphere = true;
							landingParams.uiAlpha = 0f;

							renderer.planets.bloom.setThreshold(2.5f);

							landingBuffer = new FrameBuffer(
									Core.graphics.getWidth(),
									Core.graphics.getHeight()
							);
					}

					@Override
					public void drawLaunch(){

							if(landingBuffer == null || landingPlanet == null) return;

							float fin = Mathf.clamp(renderer.getLandTimeIn());

							float zoom = Interp.pow3In.apply(fin);
							float cloudProgress = Mathf.clamp((fin - 0.52f) / 0.43f);
							float cloudAlpha = Interp.pow2In.apply(cloudProgress);
							float fade = Mathf.clamp((fin - 0.96f) / 0.04f);

							landingParams.camPos.set(landingDirection);
							landingParams.zoom = Mathf.lerp(1f, -0.45f, zoom);

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

							if(cloudProgress > 0f){

									float cx = Core.camera.position.x;
									float cy = Core.camera.position.y;
									float w = Core.camera.width;
									float h = Core.camera.height;

									Draw.color(Color.white, cloudAlpha * 0.85f);

									Draw.rect(
											cloudRegion,
											cx,
											cy,
											w * Mathf.lerp(0.8f, 2.1f, cloudProgress),
											h * Mathf.lerp(0.8f, 2.1f, cloudProgress),
											fin * 8f
									);

									Draw.color(Color.valueOf("b9cbd5"), cloudAlpha * 0.4f);

									Draw.rect(
											cloudRegion,
											cx + w * 0.08f,
											cy - h * 0.06f,
											w * Mathf.lerp(0.9f, 2.3f, cloudProgress),
											h * Mathf.lerp(0.9f, 2.3f, cloudProgress),
											-fin * 5f
									);
							}

							Draw.reset();
					}

					@Override
					public void draw(){
							thrusterTime = 0f;

							if(renderer.getLandTime() > 0f && !renderer.isLaunching()){
									float alpha = Mathf.clamp((renderer.getLandTimeIn() - 0.96f) / 0.04f);

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
									landingBuffer.dispose();
									landingBuffer = null;
							}

							renderer.planets.bloom.setThreshold(0.8f);

							landingPlanet = null;
							thrusterTime = 0f;
					}
			}
}
