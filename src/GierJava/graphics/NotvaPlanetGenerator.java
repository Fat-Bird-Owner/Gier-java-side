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
        baseBlock = Blocks.beryllicStone;
        setStarterResource = false;
        replaceLoadout = false;
        completeWave = 30;
    }

    @Override
    public void generateSector(Sector sector){
        sector.setName(Core.bundle.get("notva.sectors-" + sector.id + ".name"));
        sector.threat = (1f / sector.id) * 2f;
    }

    /*
    @Override
    public void addWeather(Sector sector, Rules rules){
        super.addWeather(sector, rules);
        rules.weather.add(new Weather.WeatherEntry(GierWeather.fragStorm));
    }
    */
    
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
        Blocks.oreCrystalThorium
    );

    Seq<Floor> floors = Seq.with(
        Blocks.ferricStone.asFloor(),
        Blocks.beryllicStone.asFloor(),
        Blocks.ferricStone.asFloor(),
        Blocks.beryllicStone.asFloor(),
        Blocks.beryllicStone.asFloor(),
        Blocks.ferricStone.asFloor(),
        Blocks.crystallineStone.asFloor()
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

    // Launch via accelerator instead
    public boolean allowLanding(Sector sector){
        return false;
    }
    
    public void getLockedText(Sector hovered, StringBuilder out){
        out.append("[gray]").append(" ").append(Core.bundle.get("gier-java.asteroidLock"));
    }
    
}
