package GierJava.graphics;

import arc.graphics.Color; 
import arc.math.geom.Mat3D; 
import arc.math.geom.Vec3;
import mindustry.graphics.g3d.*; 
import mindustry.type.Planet;

public class QuasarMesh{
public static GenericMesh create(Planet planet, int seed){

    Color deepBlue = Color.valueOf("126bff");
    Color blue = Color.valueOf("36caff");
    Color cyan = Color.valueOf("a8f5ff");
    Color whiteBlue = Color.valueOf("e4ffff");

    float r = planet.radius;

    GenericMesh core = new SunMesh(
        planet, 4,
        5, 0.3, 1.7, 1.2, 1,
        1.1f,
        deepBlue,
        blue,
        cyan,
        cyan,
        whiteBlue,
        Color.white
    );

    GenericMesh[] parts = new GenericMesh[5];
    parts[0] = core;

    int index = 1;

    for(int side : new int[]{-1, 1}){
        for(int layer = 0; layer < 2; layer++){
            float width = layer == 0 ? 0.05f : 0.035f;
            float length = layer == 0 ? 6.8f : 5.55f;

            Color c = layer == 0 ? cyan : whiteBlue;

            GenericMesh jet = new SunMesh(
                planet, 4,
                3, 0.3, 1.7, 1.0, 0.0,
                1f,
                c,
                c,
                c,
                c
            );

            Mat3D transform = new Mat3D()
                .rotate(Vec3.Z, 12f)
                .scale(new Vec3(width, length, width))
                .translate(0f, side * r * 1.05f, 0f);

            parts[index++] = new MatMesh(jet, transform);
        }
    }

    return new MultiMesh(parts);
}
}