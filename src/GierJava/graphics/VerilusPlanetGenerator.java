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

public class VerilusPlanetGenerator extends GierPlanetGenerator{

    private static final int roomSize = 20;

    {
        baseSeed = 121;
        baseBlock = Blocks.dacite;
        setStarterResource = false;
        replaceLoadout = false;
        completeWave = 25;
    }
    
    @Override
    public void addWeather(Sector sector, Rules rules){
        super.addWeather(sector, rules);
        
        rules.weather.add(new Weather.WeatherEntry(GierWeather.debrisShower){{
            always = true;
        }});
        
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
        Blocks.oreTungsten,
        Blocks.oreCrystalThorium
    );

    Seq<Floor> floors = Seq.with(
        Blocks.stone.asFloor(),
        Blocks.ice.asFloor(),
        Blocks.ice.asFloor(),
        Blocks.beryllicStone.asFloor(),
        Blocks.carbonStone.asFloor(),
        Blocks.dacite.asFloor()
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
