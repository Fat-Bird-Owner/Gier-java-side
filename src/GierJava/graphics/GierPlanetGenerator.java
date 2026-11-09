package GierJava.graphics;

import arc.math.Mathf;
import arc.math.geom.*;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.game.Team;
import mindustry.maps.generators.BlankPlanetGenerator;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.Floor;
import mindustry.maps.generators.*;
import mindustry.game.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.meta.*;
import mindustry.content.*;
import mindustry.game.Schematic;
import mindustry.game.Schematics;
import mindustry.game.Loadouts;
import mindustry.maps.generators.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.meta.*;
import mindustry.content.*;
import arc.math.*;
import arc.struct.*;

public class GierPlanetGenerator extends BlankPlanetGenerator{

    private int coreX, coreY;
    private float lowest;

    private static final int roomRadius = 10;
    private static final int targetRooms = 32;

    private static class Room{
        int x, y;
        int radius;
        Block resource;

        Room(int x, int y, int radius, Block resource){
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.resource = resource;
        }
    }

    @Override
    public int getSectorSize(Sector sector){
        return 400;
    }

    @Override
    public boolean allowLanding(Sector sector){
        return true;
    }

    @Override
    public boolean allowAcceleratorLanding(Sector sector){
        return true;
    }

    @Override
    public void generate(Tiles tiles, Sector sec, WorldParams params){
        this.tiles = tiles;
        this.sector = sec;
        this.width = tiles.width;
        this.height = tiles.height;
        this.seed = params.seedOffset + baseSeed;
        this.rand.setSeed(sec.id + params.seedOffset + baseSeed);

        tiles.fill();

        generate();

        generateResourceRooms();

        findCorePosition();

        Schematics.placeLoadout(defaultLoadout, coreX, coreY);

        placeSpawnPoints();
    }

    @Override
    protected void generate(){
        pass((x, y) -> {
            float n1 = Simplex.noise2d(seed, 3, 0.55f, 1f / 35f, x, y);
            float n2 = Simplex.noise2d(seed + 1, 3, 0.55f, 1f / 20f, x, y);
            float n3 = Simplex.noise2d(seed + 2, 3, 0.55f, 1f / 28f, x, y);
            float n4 = Simplex.noise2d(seed + 3, 3, 0.55f, 1f / 45f, x, y);

            floor = Blocks.empty;
            ore = Blocks.air;
            block = Blocks.air;

            if(n4 > -0.18f){
                floor = Blocks.stone;
            }

            if(n2 > 0.25f){
                floor = Blocks.sand;
            }

            if(n3 > 0.35f){
                floor = Blocks.darksand;
            }

            if(floor == Blocks.empty){
                floor = Blocks.water;
            }

            tiles.getn(x, y).setFloor((Floor)floor);
            tiles.getn(x, y).setBlock(block);
        });
    }

    private Seq<Block> getResources(){
        return Seq.with(
            Blocks.oreCopper,
            Blocks.oreLead,
            Blocks.oreGraphite,
            Blocks.oreTitanium,
            Blocks.oreBeryllium,
            Blocks.oreTungsten,
            Blocks.oreCrystalThorium
        );
    }

    private void generateResourceRooms(){
        Seq<Room> rooms = new Seq<>();
        Seq<Block> resources = getResources();

        int attempts = 1000;

        for(int i = 0; i < attempts && rooms.size < targetRooms; i++){
            int x = rand.random(roomRadius + 5, width - roomRadius - 6);
            int y = rand.random(roomRadius + 5, height - roomRadius - 6);

            if(Mathf.dst2(x, y, coreX, coreY) < 35f * 35f) continue;

            Tile center = tiles.get(x, y);

            if(center == null || center.floor().isLiquid || center.floor().solid) continue;

            boolean overlaps = false;

            for(Room room : rooms){
                float minDistance = roomRadius + room.radius + 5f;

                if(Mathf.dst2(x, y, room.x, room.y) < minDistance * minDistance){
                    overlaps = true;
                    break;
                }
            }

            if(overlaps) continue;

            rooms.add(new Room(x, y, roomRadius, resources.random(rand)));
        }

        for(Room room : rooms){
            for(int x = room.x - room.radius; x <= room.x + room.radius; x++){
                for(int y = room.y - room.radius; y <= room.y + room.radius; y++){
                    if(Mathf.dst2(x, y, room.x, room.y) > room.radius * room.radius) continue;

                    Tile tile = tiles.get(x, y);

                    if(tile == null || tile.floor().isLiquid || tile.floor().solid) continue;

                    tile.setOverlay(room.resource);
                }
            }
        }
    }

    private void findCorePosition(){
        lowest = Float.POSITIVE_INFINITY;
        coreX = width / 2;
        coreY = height / 2;

        for(int x = 5; x < width - 5; x++){
            for(int y = 5; y < height - 5; y++){
                Tile tile = tiles.get(x, y);

                if(tile == null || tile.floor() != Blocks.stone) continue;

                boolean valid = true;

                for(int tx = x - 4; tx <= x + 4 && valid; tx++){
                    for(int ty = y - 4; ty <= y + 4; ty++){
                        Tile other = tiles.get(tx, ty);

                        if(other == null || other.floor() != Blocks.stone){
                            valid = false;
                            break;
                        }
                    }
                }

                if(!valid) continue;

                float value = Simplex.noise2d(seed + 3, 3, 0.55f, 1f / 45f, x, y);

                if(value < lowest){
                    lowest = value;
                    coreX = x;
                    coreY = y;
                }
            }
        }
    }

    private void placeSpawnPoints(){
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
    }
}
