package GierJava.world;

import arc.math.*;
import arc.math.geom.*;
import arc.util.noise.*;

import mindustry.content.*;
import mindustry.game.*;
import mindustry.graphics.g3d.*;
import mindustry.maps.generators.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;

import static mindustry.Vars.*;

public class GierAsteroidGenerator extends BlankPlanetGenerator{

    public int mapSize = 500;

    public int minAsteroids = 35;
    public int maxAsteroids = 55;

    public int minRadius = 25;
    public int maxRadius = 100;

    public float noiseScale = 35f;
    public float noiseStrength = 0.18f;

    @Override
    public int getSectorSize(Sector sector){
        return mapSize;
    }

    @Override
    public boolean allowLanding(Sector sector){
        return true;
    }

    public GierAsteroidGenerator(){
        defaultLoadout = Loadouts.basicNucleus;
    }

    @Override
    protected void generate(){

        int sx = width / 2;
        int sy = height / 2;

        seed = sector.id + baseSeed;

        Floor space = Blocks.empty.asFloor();

        tiles.eachTile(tile -> tile.setFloor(space));

        asteroid(sx, sy, 150, Blocks.ferricStone);

        int amount = rand.random(minAsteroids, maxAsteroids);

        for(int i = 0; i < amount; i++){

            int radius = rand.random(minRadius, maxRadius);

            int x = rand.random(radius, width - radius);
            int y = rand.random(radius, height - radius);

            Block[] terrain = {
                Blocks.ferricStone,
                Blocks.carbonStone,
                Blocks.beryllicStone,
                Blocks.stone
            };

            Block selected = terrain[rand.random(terrain.length - 1)];

            asteroid(x, y, radius, selected);
        }

        for(int i = 0; i < amount * 3; i++){

            int radius = rand.random(3, 15);

            int x = rand.random(radius, width - radius);
            int y = rand.random(radius, height - radius);

            asteroid(x, y, radius, Blocks.ferricStone);
        }

        tiles.eachTile(tile -> {

            if(tile.floor() == Blocks.ferricStone &&
                rand.chance(0.025f)){

                tile.setFloor(Blocks.ferricCraters);
            }

            if(tile.floor() == Blocks.stone &&
                rand.chance(0.02f)){

                tile.setFloor(Blocks.craters);
            }
        });

        tiles.eachTile(tile -> {

            if(tile.floor() == space) return;

            boolean edge = false;

            for(Point2 p : Geometry.d8){

                Tile other = tiles.get(
                    tile.x + p.x,
                    tile.y + p.y
                );

                if(other == null || other.floor() == space){
                    edge = true;
                    break;
                }
            }

            if(edge){
                tile.setBlock(tile.floor().wall);
            }
        });

        ore(
            Blocks.oreCopper,
            Blocks.ferricStone,
            5f,
            0.8f
        );

        oreAround(
            Blocks.oreLead,
            Blocks.stoneWall,
            3,
            70f,
            0.6f
        );

        wallOre(
            Blocks.carbonWall,
            Blocks.graphiticWall,
            35f,
            0.57f
        );

        wallOre(
            Blocks.beryllicStoneWall,
            Blocks.wallOreBeryllium,
            50f,
            0.62f
        );

        ore(
            Blocks.oreTitanium,
            Blocks.stone,
            4f,
            0.9f
        );

        ore(
            Blocks.oreThorium,
            Blocks.beryllicStone,
            4f,
            0.9f
        );

        ore(
            Blocks.oreThorium,
            Blocks.carbonStone,
            4f,
            0.9f
        );

        Tile spawn = tiles.getn(sx, sy);
        spawn.setBlock(Blocks.air);
        spawn.setOverlay(Blocks.spawn);

        Schematics.placeLaunchLoadout(sx, sy);

        state.rules.planetBackground = new PlanetParams(){{
            planet = sector.planet;
            zoom = 1f;
            camPos = new Vec3(
                1.2388899f,
                1.6047299f,
                2.4758825f
            );
        }};

        state.rules.dragMultiplier = 0.7f;
        state.rules.borderDarkness = false;
        state.rules.waves = true;
        state.rules.hideSpawns = false;

        state.rules.spawns = Waves.generate(
            0.5f,
            rand,
            false,
            true,
            false
        );
    }

    private void asteroid(
        int cx,
        int cy,
        int radius,
        Block terrain
    ){

        for(int x = cx - radius; x <= cx + radius; x++){
            for(int y = cy - radius; y <= cy + radius; y++){

                if(!tiles.in(x, y)) continue;

                float distance = Mathf.dst(
                    x, y,
                    cx, cy
                ) / radius;

                float noise = Simplex.noise2d(
                    seed,
                    3,
                    0.5f,
                    1f / noiseScale,
                    x,
                    y
                ) * noiseStrength;

                if(distance + noise < 1f){
                    tiles.getn(x, y).setFloor(terrain);
                }
            }
        }
    }
}
