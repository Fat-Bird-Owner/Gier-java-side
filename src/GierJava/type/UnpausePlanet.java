package GierJava.type;

import arc.util.Time; 
import mindustry.type.Planet;
import arc.math.geom.Vec3;
import arc.util.Tmp;

public class UnpausePlanet extends Planet{ 
    
public float realRotateTime = 0.15f; 
public float rotationOffset = 0f;

public UnpausePlanet(String name, Planet parent, float radius, int sectorSize){
    super(name, parent, radius, sectorSize);
}

public Vec3 getLightNormal(){
    return Tmp.v31.set(position)
        .add(0f, 0f, 1000f)
        .sub(position)
        .nor();
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