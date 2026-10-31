package GierJava.graphics;

import mindustry.maps.planet.AsteroidGenerator;

public class GierAsteroidGenerator extends AsteroidGenerator{

    public GierAsteroidGenerator(){
        min = 25;
        max = 40;

        radMin = 10f;
        radMax = 65f;

        ferricChance = 0.5f;
        carbonChance = 0.25f;
        berylChance = 0.25f;
        iceChance = 0f;

        copperScale = 1.2f;
        leadScale = 1.2f;
        graphiteScale = 1.2f;
        berylliumScale = 1f;
        thoriumScl = 0.8f;
    }

    @Override
    public boolean allowLanding(Sector sector){
        return true;
    }
    
}
