package GierJava.graphics;

import arc.graphics.Color; 
import arc.math.Rand; 
import arc.math.geom.Mat3D; 
import arc.math.geom.Vec3; 
import arc.struct.Seq;

import mindustry.graphics.g3d.*; 
import mindustry.type.Planet;

public class QuasarMesh{
public static GenericMesh create(Planet planet, int seed){
    Seq<GenericMesh> meshes = new Seq<>();
    Rand rand = new Rand(seed);

    Color deepBlue = Color.valueOf("126bff");
    Color blue = Color.valueOf("36caff");
    Color cyan = Color.valueOf("a8f5ff");
    Color whiteBlue = Color.valueOf("e4ffff");

    float s = planet.radius;

    // Bright, textured central core.
    meshes.add(new SunMesh(
        planet, 4,
        5, 0.3, 1.7, 1.2, 1,
        1.1f,
        deepBlue,
        blue,
        cyan,
        cyan,
        whiteBlue,
        Color.white
    ));

    // Two opposite jets along the Z axis.
    // Each jet is built from overlapping tapered-looking
    // rocky sections, increasing in length away from the core.
    int sections = 12;

    for(int side : new int[]{-1, 1}){
        for(int i = 0; i < sections; i++){
            float t = (i + 0.5f) / sections;

            float y = side * (0.22f + t * 1.25f) * s;

            // Narrow near the core, broader farther out.
            float radius = (0.025f + t * 0.005f) * s;
            float length = 0.12f * s;

            Color jetColor = t < 0.25f ? cyan : blue;

            GenericMesh section = new NoiseMesh(
                planet,
                rand.nextInt(),
                1,
                radius,
                2,
                0.6f,
                0.38f,
                20f,
                jetColor,
                deepBlue,
                3,
                0.6f,
                0.38f,
                0.5f
            );

            meshes.add(new MatMesh(
                section,
                new Mat3D().setToTranslation(
                    new Vec3(
                        rand.random(-0.015f, 0.015f),
                        y,
                        rand.random(-0.015f, 0.015f)
                    )
                )
            ));
        }
    }

    return new MultiMesh(meshes.toArray(GenericMesh.class));
}
}