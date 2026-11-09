package GierJava.graphics;

import arc.math.*;
import arc.util.noise.Simplex;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.game.Schematics;
import mindustry.maps.generators.*;
import mindustry.type.*;
import mindustry.world.*;
import arc.struct.Seq;
import mindustry.Vars;
import arc.Core;
import mindustry.maps.generators.BaseGenerator;

import GierJava.content.*;

public class GierPlanetGenerator extends BlankPlanetGenerator{

  private int coreX, coreY;
  private float lowest = 1f;

  private final BaseGenerator basegen = new BaseGenerator();
  
  @Override
  public void onSectorCaptured(Sector sector){
    Vars.ui.showCustomConfirm(
                Core.bundle.get("gier.capture.title"),
                Core.bundle.get("gier.capture.text"),
                Core.bundle.get("gier.capture.accept"),
                Core.bundle.get("gier.capture.continue"),
                () -> {},
                () -> {
                  
                    Vars.state.rules.winWave += 10;
                    Vars.state.rules.waves = true;
                  
                    sector.info.winWave = Vars.state.rules.winWave;
                    sector.saveInfo();
                  
                }
            );
        }
  
  @Override
  public boolean allowLanding(Sector sector){
      return true;
  }

      @Override
    public int getSectorSize(Sector sector){
        return 400;
    }

    @Override
    public boolean allowAcceleratorLanding(Sector sector){
      return true;
    }
  
    @Override
    public void generate(Tiles tiles, Sector sec, WorldParams params){
        seed = params.seedOffset + baseSeed;
        super.generate(tiles, sec, params);
    }

    @Override
    protected void generate(){      

        lowest = Float.POSITIVE_INFINITY;
        coreX = width / 2;
        coreY = height / 2;  
      
        pass((x, y) -> {
            block = Blocks.air;

            float scale = 1f;
          
            float v_n2 = ((float)Simplex.noise2d(seed + 2, 4, 0.55f, scale / (55f * width / 160f),
                x + 30f * ((float)Simplex.noise2d(seed, 2, 0.50f, scale / (90f * width / 160f), x, y) - 0.5f) * 2f,
                y + 30f * ((float)Simplex.noise2d(seed + 1, 2, 0.50f, scale / (90f * width / 160f), x, y) - 0.5f) * 2f) - 0.5f) * 2f;

            float v_n3 = ((float)Simplex.noise2d(seed + 5, 3, 0.50f, scale / (22f * width / 160f),
                x + 15f * ((float)Simplex.noise2d(seed + 3, 2, 0.50f, scale / (90f * width / 160f), x, y) - 0.5f) * 2f,
                y + 15f * ((float)Simplex.noise2d(seed + 4, 2, 0.50f, scale / (90f * width / 160f), x, y) - 0.5f) * 2f) - 0.5f) * 2f;

            float v_n4 = v_n2 + v_n3;

            floor = Blocks.empty;

            if(v_n4 > -0.18f){
                floor = Blocks.stone;

              if(v_n4 > -0.18f && v_n4 < lowest){
    boolean valid = true;

    for(int rx = -4; rx <= 4; rx++){
        for(int ry = -4; ry <= 4; ry++){
            int tx = x + rx;
            int ty = y + ry;

            if(tx < 0 || ty < 0 || tx >= width || ty >= height){
                valid = false;
                break;
            }

            Tile nearby = tiles.get(tx, ty);

            if(nearby == null || nearby.floor() != Blocks.stone){
                valid = false;
                break;
            }
        }

        if(!valid) break;
    }

    if(valid){
        lowest = v_n4;
        coreX = x;
        coreY = y;
    }
              }
              
            }

            if(floor.asFloor().isLiquid || floor.asFloor().solid) return;

    float copper = Simplex.noise2d(seed + 10, 3, 0.55f, 1f / 18f, x, y);
    float lead = Simplex.noise2d(seed + 20, 3, 0.55f, 1f / 24f, x, y);
    float sand = Simplex.noise2d(seed + 30, 3, 0.55f, 1f / 30f, x, y);
    float graphite = Simplex.noise2d(seed + 59, 3, 0.55f, 1f / 20f, x, y);
    float titanium = Simplex.noise2d(seed + 40, 3, 0.55f, 1f / 38f, x, y);
    float beryllium = Simplex.noise2d(seed + 41, 3, 0.55f, 1f / 28f, x, y);
    float tungsten = Simplex.noise2d(seed + 43, 3, 0.55f, 1f / 42f, x, y);
    float thorium = Simplex.noise2d(seed + 50, 3, 0.55f, 1f / 45f, x, y);
    
    if(copper > 0.72f){
        ore = Blocks.oreCopper;
    }

    if(lead > 0.72f){
        ore = Blocks.oreLead;
    }

    if (graphite > 0.8f){
      ore = Blocks.wallOreGraphite;
    }
            
    if(sand > 0.65f){
        floor = Blocks.darksand;
    }
          
    if(titanium > 0.8f){
        ore = Blocks.oreTitanium;
    }

    if(beryllium > 0.8f){
        ore = Blocks.oreBeryllium;
    }

    if(tungsten > 0.84f){
        ore = Blocks.oreTungsten;
    }
          
    if(thorium > 0.89f){
        ore = Blocks.oreCrystalThorium;
    }
          
        });

/*
        int spawnX = width / 2;
        int spawnY = height / 2;

        int bestWater = Integer.MAX_VALUE;
        int waterCheckRad = 5;

        float length = Math.min(width, height) / 2.55f - rand.random(13f, 23f);
        int offset = rand.random(360);

        for(int i = 0; i < 360; i += 5){
            int angle = offset + i;

            int cx = (int)(width / 2f + Angles.trnsx(angle, length));
            int cy = (int)(height / 2f + Angles.trnsy(angle, length));

            int waterTiles = 0;

            for(int rx = -waterCheckRad; rx <= waterCheckRad; rx++){
                for(int ry = -waterCheckRad; ry <= waterCheckRad; ry++){
                    Tile tile = tiles.get(cx + rx, cy + ry);

                    if(tile == null || tile.floor().isLiquid){
                        waterTiles++;
                    }
                }
            }

            if(waterTiles < bestWater){
                bestWater = waterTiles;
                spawnX = cx;
                spawnY = cy;
            }

            if(waterTiles <= 4) break;
        }
*/
        int margin = 12;

        int[][] spawnPositions = {
            {margin, margin},
            {width - margin - 1, margin},
            {margin, height - margin - 1},
            {width - margin - 1, height - margin - 1}
          };

          for(int[] pos : spawnPositions){
              Tile tile = tiles.get(pos[0], pos[1]);

            if(tile != null){
                tile.setOverlay(Blocks.spawn);
            }
          }

        Vars.state.rules.waves = true;
        Vars.state.rules.winWave = 20;

Tile enemySpawn = null;
float farthest = -1f;

for(int x = 20; x < width - 20; x += 4){
    for(int y = 20; y < height - 20; y += 4){
        Tile tile = tiles.get(x, y);

        if(tile == null || tile.floor().isLiquid || tile.floor().solid) continue;

        float distance = Mathf.dst2(x, y, coreX, coreY);

        if(distance > farthest){
            farthest = distance;
            enemySpawn = tile;
        }
    }
}
      
     if(enemySpawn != null){
    basegen.generate(
        tiles,
        Seq.with(enemySpawn),
        enemySpawn,
        Team.crux,
        sector,
        sector.threat
    );

     }
      
      Vars.state.rules.spawns = Seq.with(
        new SpawnGroup(GierUnitTypes.drive){{
            begin = 1;
            end = 51;
            spacing = 1;
            unitAmount = 2;
            unitScaling = 1.2f;
            max = 3;
        }},

        new SpawnGroup(GierUnitTypes.drive){{
            begin = 5;
            end = 51;
            spacing = 2;
            unitAmount = 2;
            unitScaling = 0.8f;
            shields = 40f;
            shieldScaling = 5f;
            max = 5;
        }},

        new SpawnGroup(GierUnitTypes.spark){{
            begin = 12;
            spacing = 1;
            unitAmount = 2;
            unitScaling = 0.45f;
            max = 4;
        }},

        new SpawnGroup(GierUnitTypes.ash){{
            begin = 20;
            spacing = 1;
            unitAmount = 1;
            max = 2;
            unitScaling = 0.2f;
        }}
    );
      
        Schematics.placeLoadout(defaultLoadout, coreX, coreY);
    }
}



