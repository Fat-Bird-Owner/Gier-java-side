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
import mindustry.graphics.g3d.*;
import arc.math.geom.*;
import mindustry.world.blocks.environment.Floor;
import GierJava.content.*;

import static GierJava.content.GierUnitTypes.*;
import static mindustry.content.UnitTypes.*;

public class GierPlanetGenerator extends BlankPlanetGenerator{

    private int coreX, coreY;
    private float lowest = 1f;

    private final BaseGenerator basegen = new BaseGenerator();

    private static final int roomSize = 20;

    public Room[][] rooms;

    public static class Room{
    int x, y;
    Block resource;
    Floor floor;
    Seq<Asteroid> asteroids = new Seq<>();

        Room(int x, int y, Block resource, Floor floor){
            this.x = x;
            this.y = y;
            this.resource = resource;
            this.floor = floor;
        }
    }

    public static class Asteroid{
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

        if(distance < asteroid.radius + 1.8f){
            floor = room.floor;
        }

        if(distance < asteroid.radius * 0.75f + noise * 0.8f){
            ore = room.resource;
            return;
        }
    }
    }
    
    public void createRooms(){
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
        Blocks.oreCrystalThorium,
        Blocks.oreScrap
    );

    Seq<Floor> floors = Seq.with(
        Blocks.ferricStone.asFloor(),
        Blocks.basalt.asFloor(),
        Blocks.carbonStone.asFloor(),
        Blocks.shale.asFloor(),
        Blocks.beryllicStone.asFloor(),
        Blocks.carbonStone.asFloor(),
        Blocks.crystallineStone.asFloor(),
        Blocks.metalFloorDamaged.asFloor()
    );

    for(int x = 0; x < columns; x++){
        for(int y = 0; y < rows; y++){
            int index = rand.random(resources.size - 1);

                rooms[x][y] = new Room(
                    x,
                    y,
                    resources.get(index),
                    floors.get(index)
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
    public void generateSector(Sector sector){
        int id = sector.id;

        if(id < 10){
            sector.threat = 0.3f;
        }else if(id < 30){
            sector.threat = 0.5f;
        }else if(id < 60){
            sector.threat = 0.7f;
        }else{
            sector.threat = 1f;
        }
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
        return 600;
    }

    @Override
    public boolean allowAcceleratorLanding(Sector sector){
        return true;
    }

    @Override
    public void generate(Tiles tiles, Sector sec, WorldParams params){
        seed = sec.id + params.seedOffset + baseSeed;
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

            floor = Blocks.space;

            if(v_n4 > 0.28f){
                floor = Blocks.stone;
            }

            if(floor.asFloor().isLiquid || floor.asFloor().solid) return;

            ore = Blocks.air;
            float sand = Simplex.noise2d(seed + 30, 3, 0.55f, 1f / 30f, x, y);

            if(sand > 0.65f){
                floor = Blocks.darksand;
            }

            generateAsteroidOre(x, y);

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

        Vars.state.rules.waveTeam = Team.get(5);
        Vars.state.rules.loadout = ItemStack.list(Items.copper, 750, Items.lead, 750, Items.graphite, 500);    
        Vars.state.rules.spawns = generateWave(sector.difficulty, sector.id);

        Schematics.placeLoadout(
            Schematics.readBase64("bXNjaAF4nD2KsQ5AMBRFLxUDidlP9CvMJqMYnnqRSrXSvljEv2ORnJzlHCioApUJXthLTwfy60bpaGaXkI8TCk87o+lCZD2QsHNWGPXCyUR7iA0eyNCulqPe6CRtvjP951dfXj1crR12")
            ,coreX, coreY);

        Vars.state.rules.planetBackground = new PlanetParams(){{
            planet = sector.planet;
            zoom = 1f;
            camPos = new Vec3(1.2388899f, 1.6047299f, 2.4758825f);
        }};
        
    }

    public static void generateWave(float difficulty, int id){
        return generate(difficulty, new Rand(id), false, true);
    }
    
    public static Seq<SpawnGroup> generate(float difficulty, Rand rand, boolean attack, boolean airOnly){
        UnitType[][] species = {
        {occupy, overboard, staticu, scepter, reign},
        {drive, spark, ash, zenith, antumbra}
        };

        if(airOnly){
            species = Structs.filter(UnitType[].class, species, v -> v[0].flying);
        }

        UnitType[][] fspec = species;
        Seq<SpawnGroup> out = new Seq<>();

        //max reasonable wave, after which everything gets boring
        int cap = 30;

        float shieldStart = 30, shieldsPerWave = 20 + difficulty * 30f;
        float[] scaling = {1, 2f, 3f, 4f, 5f};

        Intc createProgression = start -> {
            UnitType[] curSpecies = Structs.random(rand, fspec);
            int curTier = 0;

            for(int i = start; i < cap;){
                int f = i;
                int next = rand.random(8, 16) + (int)Mathf.lerp(5f, 0f, difficulty) + curTier * 4;

                float shieldAmount = Math.max((i - shieldStart) * shieldsPerWave, 0);
                int space = start == 0 ? 1 : rand.random(1, 2);
                int ctier = curTier;

                out.add(new SpawnGroup(curSpecies[Math.min(curTier, curSpecies.length - 1)]){{
                    unitAmount = f == start ? 1 : 6 / (int)scaling[ctier];
                    begin = f;
                    end = f + next >= cap ? never : f + next;
                    max = 13;
                    unitScaling = (difficulty < 0.4f ? rand.random(2.5f, 5f) : rand.random(1f, 4f)) * scaling[ctier];
                    shields = shieldAmount;
                    shieldScaling = shieldsPerWave;
                    spacing = space;
                }});

                out.add(new SpawnGroup(curSpecies[Math.min(curTier, curSpecies.length - 1)]){{
                    unitAmount = 3 / (int)scaling[ctier];
                    begin = f + next - 1;
                    end = f + next + rand.random(6, 10);
                    max = 6;
                    unitScaling = rand.random(2f, 4f);
                    spacing = rand.random(2, 4);
                    shields = shieldAmount/2f;
                    shieldScaling = shieldsPerWave;
                }});

                i += next + 1;
                if(curTier < 3 || (rand.chance(0.05) && difficulty > 0.8)){
                    curTier ++;
                }

                curTier = Math.min(curTier, 3);

                if(rand.chance(0.3)){
                    curSpecies = Structs.random(rand, fspec);
                }
            }
        };

        createProgression.get(0);

        int step = 5 + rand.random(5);

        while(step <= cap){
            createProgression.get(step);
            step += (int)(rand.random(15, 30) * Mathf.lerp(1f, 0.5f, difficulty));
        }

        int bossWave = (int)(rand.random(50, 70) * Mathf.lerp(1f, 0.5f, difficulty));
        int bossSpacing = (int)(rand.random(25, 40) * Mathf.lerp(1f, 0.5f, difficulty));

        int bossTier = difficulty < 0.6 ? 3 : 4;

        out.add(new SpawnGroup(Structs.random(rand, species)[bossTier]){{
            unitAmount = 1;
            begin = bossWave;
            spacing = bossSpacing;
            end = never;
            max = 16;
            unitScaling = bossSpacing;
            shieldScaling = shieldsPerWave;
            effect = StatusEffects.boss;
        }});

        out.add(new SpawnGroup(Structs.random(rand, species)[bossTier]){{
            unitAmount = 1;
            begin = bossWave + rand.random(3, 5) * bossSpacing;
            spacing = bossSpacing;
            end = never;
            max = 16;
            unitScaling = bossSpacing;
            shieldScaling = shieldsPerWave;
            effect = StatusEffects.boss;
        }});

        int finalBossStart = 120 + rand.random(30);

        out.add(new SpawnGroup(Structs.random(rand, species)[bossTier]){{
            unitAmount = 1;
            begin = finalBossStart;
            spacing = bossSpacing/2;
            end = never;
            unitScaling = bossSpacing;
            shields = 500;
            shieldScaling = shieldsPerWave * 4;
            effect = StatusEffects.boss;
        }});

        out.add(new SpawnGroup(Structs.random(rand, species)[bossTier]){{
            unitAmount = 1;
            begin = finalBossStart + 15;
            spacing = bossSpacing/2;
            end = never;
            unitScaling = bossSpacing;
            shields = 500;
            shieldScaling = shieldsPerWave * 4;
            effect = StatusEffects.boss;
        }});

        int shift = Math.max((int)(difficulty * 14 - 5), 0);

        for(SpawnGroup group : out){
            group.begin -= shift;
            group.end -= shift;
        }

        return out;
    }
    
}
