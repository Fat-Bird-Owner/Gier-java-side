package GierJava.content;

import GierJava.type.*;
import arc.func.*;
import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import GierJava.graphics.*;
import mindustry.game.*;
import mindustry.graphics.*;
import mindustry.graphics.g3d.*;
import mindustry.graphics.g3d.PlanetGrid.*;
import mindustry.maps.planet.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.meta.*;
import mindustry.content.Planets;
import mindustry.content.Blocks;

public class GierPlanets {
    public static Planet erekir, gier, sun, innerAsteroidBelt;

    public static void load(){

        sun = new UnpausePlanet("sun", null, 1f, 1){{
            bloom = true;
            accessible = false;

            clipRadius = 11f;
            meshLoader = () -> QuasarMesh.create(this, 14);
            rotateTime = 0.15f;

        }};

        innerAsteroidBelt = new UnpausePlanet("inner-belt", sun, 1f, 1){{
            meshLoader = () ->  new MultiMesh(
            AsteroidBelt.create(this, 222, 50f, 68f, 126),
            AsteroidBelt.create(this, 440, 22f, 150f, 76)
            );
            accessible = false;
            orbitRadius = 0f;
            drawOrbit = false;
            orbitTime = 400f;
            clipRadius = 100f;
            rotateTime = 6000f;

        }};

        erekir = new Planet("erekir", sun, 1f, 2){{
            generator = new ErekirPlanetGenerator();
            meshLoader = () -> new HexMesh(this, 6);
            cloudMeshLoader = () -> new MultiMesh(
                new HexSkyMesh(this, 2, 0.15f, 0.14f, 6, Color.valueOf("eba768").a(0.75f), 2, 0.42f, 1f, 0.43f),
                new HexSkyMesh(this, 3, 0.6f, 0.15f, 6, Color.valueOf("eea293").a(0.75f), 2, 0.42f, 1.2f, 0.45f)
            );
            landCloudColor = Color.valueOf("ed6542");
            atmosphereColor = Color.valueOf("f07218");
            atmosphereRadIn = 0.02f;
            atmosphereRadOut = 0.3f;
            tidalLock = true;
            orbitSpacing = 25f;
            totalRadius += 36f;
            lightSrcTo = 0.5f;
            lightDstFrom = 0.2f;
            clearSectorOnLose = true;
            defaultCore = Blocks.coreBastion;
            iconColor = Color.valueOf("ff9266");
            accessible = false;
            enemyBuildSpeedMultiplier = 0.4f;
            allowLaunchToNumbered = false;
            updateLighting = false;
        }};

        gier = makeAsteroid("gier", erekir, Blocks.ferricStoneWall, Blocks.carbonWall, -5, 0.4f, 24, 3f, gen -> {
            gen.min = 25;
            gen.max = 35;
            gen.carbonChance = 0.6f;
            gen.iceChance = 0f;
            gen.berylChance = 0.1f;
        }, 1);

        gier.alwaysUnlocked = true;
        gier.accessible = true;
        gier.orbitSpacing = 0.1f;
    }
    
    private static Planet makeAsteroid(String name, Planet parent, Block base, Block tint, int seed, float tintThresh, int pieces, float scale, Cons<AsteroidGenerator> cgen, int sectorSize){
        return new Planet(name, parent, 0.12f, sectorSize){{
            hasAtmosphere = false;
            updateLighting = false;
            camRadius = 0.68f * scale;
            minZoom = 0.6f;
            drawOrbit = false;
            accessible = false;
            clipRadius = 2f;
            defaultEnv = Env.space;
            icon = "commandRally";
            generator = new AsteroidGenerator();
            cgen.get((AsteroidGenerator)generator);

            meshLoader = () -> {
                iconColor = tint.mapColor;
                Color tinted = tint.mapColor.cpy().a(1f - tint.mapColor.a);
                Seq<GenericMesh> meshes = new Seq<>();
                Color color = base.mapColor;
                Rand rand = new Rand(id + 2);

                meshes.add(new NoiseMesh(
                    this, seed, 2, radius, 2, 0.55f, 0.45f, 14f,
                    color, tinted, 3, 0.6f, 0.38f, tintThresh
                ));

                for(int j = 0; j < pieces; j++){
                    meshes.add(new MatMesh(
                        new NoiseMesh(this, seed + j + 1, 1, 0.022f + rand.random(0.039f) * scale, 2, 0.6f, 0.38f, 20f,
                        color, tinted, 3, 0.6f, 0.38f, tintThresh),
                        new Mat3D().setToTranslation(Tmp.v31.setToRandomDirection(rand).setLength(rand.random(0.44f, 1.4f) * scale)))
                    );
                }

                return new MultiMesh(meshes.toArray(GenericMesh.class));
            };
        }};
    }

}