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

    private static final int roomSize = 20;

    private Room[][] rooms;

    private static class Room{
    int x, y;
    Block resource;
    Seq<Asteroid> asteroids = new Seq<>();

    Room(int x, int y, Block resource){
        this.x = x;
        this.y = y;
        this.resource = resource;
    }
    }

    private static class Asteroid{
    int x, y;
    float radius;

    Asteroid(int x, int y, float radius){
        this.x = x;
        this.y = y;
        this.radius = radius;
    }
    }

    private void generateAsteroids(){
    for(int rx = 0; rx < rooms.length; rx++){
        for(int ry = 0; ry < rooms[rx].length; ry++){
            Room room = rooms[rx][ry];

            int count = rand.random(2, 5);

            for(int i = 0; i < count; i++){
                float radius = rand.random(1.5f, 3.5f);

                int x = room.x * roomSize + rand.random(roomSize - 1);
                int y = room.y * roomSize + rand.random(roomSize - 1);

                boolean overlaps = false;

                for(Asteroid other : room.asteroids){
                    float minDistance = radius + other.radius + 2f;

                    if(Mathf.dst2(x, y, other.x, other.y) < minDistance * minDistance){
                        overlaps = true;
                        break;
                    }
                }

                if(!overlaps){
                    room.asteroids.add(new Asteroid(x, y, radius));
                }
            }
        }
    }
    }
    
    private void generateAsteroidOre(int x, int y){
    Room room = getRoom(x, y);

    if(room == null) return;

    for(Asteroid asteroid : room.asteroids){
        float noise = Simplex.noise2d(
            seed + asteroid.x * 31 + asteroid.y * 17,
            2,
            0.6f,
            1f / 2f,
            x,
            y
        );

        float distance = Mathf.dst(x, y, asteroid.x, asteroid.y);

        if(distance < asteroid.radius * 0.75f + noise * 0.8f){
            ore = room.resource;
            return;
        }
    }
    }
    
    private void createRooms(){
        int columns = (width + roomSize - 1) / roomSize;
        int rows = (height + roomSize - 1) / roomSize;

        rooms = new Room[columns][rows];

        Seq<Block> resources = Seq.with(
            Blocks.oreCopper,
            Blocks.oreLead,
            Blocks.wallOreGraphite,
            Blocks.oreTitanium,
            Blocks.oreBeryllium,
            Blocks.oreTungsten,
            Blocks.oreCrystalThorium
        );

        for(int x = 0; x < columns; x++){
            for(int y = 0; y < rows; y++){
                rooms[x][y] = new Room(
                    x,
                    y,
                    resources.get(rand.random(resources.size - 1))
                );
            }
        }
    }

    private Room getRoom(int x, int y){
        int roomX = x / roomSize;
        int roomY = y / roomSize;

        if(roomX < 0 || roomY < 0 ||
           roomX >= rooms.length ||
           roomY >= rooms[0].length){
            return null;
        }

        return rooms[roomX][roomY];
    }

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

    /*
    @Override
    public void postGenerate(Tiles tiles){
        basegen.postGenerate();
    }
    */

    @Override
    protected void generate(){

        lowest = 0f;
        coreX = width / 2;
        coreY = height / 2;

        createRooms();
        generateAsteroids();
        
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

            if(v_n4 > 0.28f){
                floor = Blocks.stone;
            }

            if(floor.asFloor().isLiquid || floor.asFloor().solid) return;

            ore = Blocks.air;
            generateAsteroidOre(x, y);
            
            float sand = Simplex.noise2d(seed + 30, 3, 0.55f, 1f / 30f, x, y);

            if(sand > 0.65f){
                floor = Blocks.darksand;
            }

        });

        float bestDistance = Float.MAX_VALUE;

coreX = width / 2;
coreY = height / 2;

for(int x = 4; x < width - 4; x++){
    for(int y = 4; y < height - 4; y++){
        Tile tile = tiles.get(x, y);

        if(tile == null || tile.floor() != Blocks.stone) continue;

        boolean valid = true;

        for(int rx = -4; rx <= 4 && valid; rx++){
            for(int ry = -4; ry <= 4; ry++){
                Tile nearby = tiles.get(x + rx, y + ry);

                if(nearby == null || nearby.floor() != Blocks.stone){
                    valid = false;
                    break;
                }
            }
        }

        if(!valid) continue;

        float distance = Mathf.dst2(
            x, y,
            width / 2f, height / 2f
        );

        if(distance < bestDistance){
            bestDistance = distance;
            coreX = x;
            coreY = y;
                }
            }
        }
        
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

        /*
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
        */

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
                spacing = 1;
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

        Schematics.placeLoadout(
            Schematics.readBase64("bXNjaAF4nD2KsQ5AMBRFLxUDidlP9CvMJqMYnnqRSrXSvljEv2ORnJzlHCioApUJXthLTwfy60bpaGaXkI8TCk87o+lCZD2QsHNWGPXCyUR7iA0eyNCulqPe6CRtvjP951dfXj1crR12")
            ,coreX, coreY);
    }
}
