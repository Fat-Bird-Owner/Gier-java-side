package GierJava.graphics;

import arc.graphics.Color; 
import arc.math.geom.Mat3D; 
import arc.math.geom.Vec3;
import mindustry.graphics.g3d.*; 
import mindustry.type.Planet;

public class QuasarMesh{
public static GenericMesh create(Planet planet, int seed){
    Color deepBlue = Color.valueOf("164cff");
    Color blue = Color.valueOf("478cff");
    Color cyan = Color.valueOf("a8faff");
    Color whiteBlue = Color.valueOf("eafcff");

    float r = planet.radius;

    GenericMesh core = new SunMesh(
        planet, 5,
        5, 0.3, 1.7, 1.2, 0.25,
        1.15f,
        deepBlue,
        blue,
        cyan,
        whiteBlue,
        Color.white
    );

    GenericMesh[] parts = new GenericMesh[5];
    parts[0] = core;

    int index = 1;

    for(int side : new int[]{-1, 1}){
        for(int layer = 0; layer < 2; layer++){
            float width = layer == 0 ? 0.11f : 0.045f;
            float length = layer == 0 ? 1.8f : 1.55f;

            Color c = layer == 0 ? blue : whiteBlue;

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
                .scale(new Vec3(width, length, width))
                .translate(0f, side * r * 1.05f, 0f);

            parts[index++] = new MatMesh(jet, transform);
        }
    }

    return new MultiMesh(parts);
}
}