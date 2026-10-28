package GierJava.graphics;

import arc.*;
import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.noise.*;
import mindustry.ai.*;
import mindustry.ai.BaseRegistry.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.maps.generators.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import GierJava.content.GierPlanets;
import mindustry.maps.planet.*;

import static mindustry.Vars.*;

// Ripped straight out of serpulo planet gen
public class KelaPlanetGenerator extends TantrosPlanetGenerator {

    static double metalDstScl = 0.15;
    static final Color blueLight = Color.valueOf("4169E1");

    Vec3 basePos = new Vec3(0.9341721, 0.0, 0.3568221);

    @Override
    public boolean isEmissive(){
        return true;
    }

private static final float enemyBaseChance = 0.35f;
private static final int minimumEmptySectors = 3;

public void generateEnemyBases(){
    var planet = GierPlanets.kela;
    var sectors = planet.sectors;

    Rand rand = new Rand(planet.sectorSeed == -1
        ? planet.id
        : planet.sectorSeed);

    int eligible = 0;

    for(Sector sector : sectors){
        if(sector.preset != null) continue;

        if(sector.id == planet.startSector) continue;
        eligible++;
    }

    int target = Math.max(0,
        Math.min(eligible - minimumEmptySectors,
            Math.round(eligible * enemyBaseChance)));

    Seq<Sector> candidates = new Seq<>();

    for(Sector sector : sectors){
        if(sector.preset != null) continue;
        if(sector.id == planet.startSector) continue;

        candidates.add(sector);
    }

    candidates.shuffle(rand);

    for(int i = 0; i < candidates.size; i++){
        Sector sector = candidates.get(i);
        if(sector.hasBase() || sector.hasEnemyBase()) continue;

        sector.generateEnemyBase = i < target;
    }
}


    @Override
    public void getEmissiveColor(Vec3 position, Color out){
        float dst = 999f, captureDst = 999f, lightScl = 0f;

        Object[] sectors = GierPlanets.kela.sectors.items;
        int size = GierPlanets.kela.sectors.size;

        for(int i = 0; i < size; i ++){
            var sector = (Sector)sectors[i];

            if(sector.hasEnemyBase() && !sector.isCaptured()){
                dst = Math.min(dst, position.dst(sector.tile.v) - (sector.preset != null ? sector.preset.difficulty/10f * 0.03f - 0.03f : 0f));
            }else if(sector.hasBase()){
                float cdst = position.dst(sector.tile.v);
                if(cdst < captureDst){
                    captureDst = cdst;
                    lightScl = sector.info.lightCoverage;
                }
            }
        }

        lightScl = Math.min(lightScl / 50000f, 1.3f);
        if(lightScl < 1f) lightScl = Interp.pow5Out.apply(lightScl);

        float freq = 0.05f;
        if(position.dst(basePos) < 0.3f ?

            dst*metalDstScl + Simplex.noise3d(seed + 1, 3, 0.4, 5.5f, position.x, position.y + 200f, position.z)*0.08f + ((basePos.dst(position) + 0.00f) % freq < freq/2f ? 1f : 0f) * 0.07f < 0.08f/* || dst <= 0.0001f*/ :
            dst*metalDstScl + Simplex.noise3d(seed, 3, 0.4, 9f, position.x, position.y + 370f, position.z)*0.06f < 0.045){

        out.set(blueLight)
        .mul(0.2f)
        .mul(0.7f + Simplex.noise3d(seed, 1, 1, 9f, position.x, position.y + 99f, position.z) * 0.4f)
        .lerp(Team.get(5).color, 0.4f);

        }else if(captureDst*metalDstScl + Simplex.noise3d(seed, 3, 0.4, 9f, position.x, position.y + 600f, position.z)*0.07f < 0.05 * lightScl){
            
        out.set(blueLight)
        .mul(0.2f)
        .mul(0.7f + Simplex.noise3d(seed, 1, 1, 9f, position.x, position.y + 99f, position.z) * 0.4f)
        .lerp(Team.sharded.color, 0.4f);

        }
    }

}