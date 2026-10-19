package GierJava.type;

import arc.util.Time; 
import mindustry.type.Planet;

public class SpinningPlanet extends Planet{ 
    
public float realRotateTime = 0.15f; 
public float rotationOffset = 0f;

public SpinningPlanet(String name, Planet parent, float radius, int sectorSize){
    super(name, parent, radius, sectorSize);
}

@Override
public float getRotation(){
    if(tidalLock){
        return super.getRotation();
    }

    return (rotationOffset + Time.globalTime / 60f
        * (360f / realRotateTime)) % 360f;
}

}