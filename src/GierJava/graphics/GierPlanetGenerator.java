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

import GierJava.content.*;

public class GierPlanetGenerator extends BlankPlanetGenerator{

  private int coreX, coreY;
  private float lowest = 1f;
  
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

        lowest = 1f;
      
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

            if(v_n4 > -0.15f){
                floor = Blocks.stone;

              if (v_n4 < lowest) {
                lowest = v_n4;
                coreX = x;
                coreY = y;
              }
            }

            if(floor.asFloor().isLiquid || floor.asFloor().solid) return;

    float copper = Simplex.noise2d(seed + 10, 3, 0.55f, 1f / 18f, x, y);
    float lead = Simplex.noise2d(seed + 20, 3, 0.55f, 1f / 24f, x, y);
    float sand = Simplex.noise2d(seed + 30, 3, 0.55f, 1f / 30f, x, y);
    float titanium = Simplex.noise2d(seed + 40, 3, 0.55f, 1f / 38f, x, y);
    float beryllium = Simplex.noise2d(seed + 40, 3, 0.55f, 1f / 28f, x, y);
    float tungsten = Simplex.noise2d(seed + 40, 3, 0.55f, 1f / 42f, x, y);
    float thorium = Simplex.noise2d(seed + 50, 3, 0.55f, 1f / 45f, x, y);

    if(copper > 0.72f){
        ore = Blocks.oreCopper;
    }

    if(lead > 0.72f){
        ore = Blocks.oreLead;
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
        Vars.state.rules.winWave = 50;
        Vars.state.rules.spawns = Seq.with(
        new SpawnGroup(GierUnitTypes.drive){{
            begin = 1;
            end = 51;
            spacing = 1;
            unitAmount = 2;
            unitScaling = 1.2f;
            max = 12;
        }},

        new SpawnGroup(GierUnitTypes.drive){{
            begin = 5;
            end = 51;
            spacing = 2;
            unitAmount = 2;
            unitScaling = 0.8f;
            shields = 40f;
            shieldScaling = 5f;
            max = 16;
        }},

        new SpawnGroup(GierUnitTypes.spark){{
            begin = 12;
            spacing = 1;
            unitAmount = 2;
            unitScaling = 0.45f;
            max = 10;
        }},

        new SpawnGroup(GierUnitTypes.ash){{
            begin = 20;
            spacing = 2;
            unitAmount = 1;
            max = 12;
            unitScaling = 0.2f;
        }}
    );
      
        Schematics.placeLoadout(defaultLoadout, coreX, coreY);
    }
}



