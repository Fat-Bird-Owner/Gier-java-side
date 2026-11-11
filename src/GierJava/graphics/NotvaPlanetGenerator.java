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
import GierJava.graphics.*;

import GierJava.content.*;

public class NotvaPlanetGenerator extends GierPlanetGenerator{

    private static final int roomSize = 20;

    {
        baseSeed = 121;
    }
    
    @Override
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
        Blocks.beryllicStone.asFloor(),
        Blocks.beryllicStone.asFloor(),
        Blocks.charr.asFloor(),
        Blocks.beryllicStone.asFloor(),
        Blocks.beryllicStone.asFloor(),
        Blocks.crystallineStone.asFloor(),
        Blocks.crystallineStone.asFloor(),
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

}
