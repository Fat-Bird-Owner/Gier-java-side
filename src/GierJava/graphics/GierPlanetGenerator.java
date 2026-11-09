package GierJava.graphics;

import arc.math.*;
import arc.util.noise.Simplex;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.game.Schematics;
import mindustry.maps.generators.*;
import mindustry.type.*;
import mindustry.world.*;

public class GierPlanetGenerator extends BlankPlanetGenerator{

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
            }

        });

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

        Schematics.placeLoadout(defaultLoadout, spawnX, spawnY);
    }
}
