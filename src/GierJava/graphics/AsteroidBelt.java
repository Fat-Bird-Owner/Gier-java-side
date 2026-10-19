package GierJava.graphics;

import arc.graphics.Color; 
import arc.math.Mathf; 
import arc.math.Rand; 
import arc.math.geom.*;
import arc.math.geom.Mat3D; 
import arc.util.Tmp; 
import arc.struct.Seq;

import mindustry.graphics.g3d.*; 
import mindustry.type.Planet;

public class AsteroidBelt{
public static GenericMesh create(Planet planet, int count, float innerRadius, float outerRadius, int seed){
    Seq<GenericMesh> meshes = new Seq<>();
    Rand rand = new Rand(seed);

    Color rock = Color.valueOf("777777");
    Color tint = Color.valueOf("b0a99b");

    for(int i = 0; i < count; i++){
        float angle = Mathf.PI2 * i / count + rand.random(-0.08f, 0.08f);
        float distance = rand.random(innerRadius, outerRadius);

        float x = Mathf.cos(angle) * distance;
        float y = rand.random(-0.08f, 0.08f);
        float z = Mathf.sin(angle) * distance;

        float size = rand.random(0.02f, 0.055f);

        GenericMesh asteroid = new NoiseMesh(
            planet,
            seed + i + 1,
            1,
            size,
            2,
            0.6f,
            0.38f,
            20f,
            rock,
            tint,
            3,
            0.6f,
            0.38f,
            0.5f
        );

        Mat3D transform = new Mat3D()
            .setToTranslation(x, y, z);

        meshes.add(new MatMesh(asteroid, transform));
    }

    return new MultiMesh(meshes.toArray(GenericMesh.class));
}
}