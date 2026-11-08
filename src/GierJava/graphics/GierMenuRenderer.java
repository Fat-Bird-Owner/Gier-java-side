package GierJava.graphics;

import GierJava.content.*;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.noise.*;
import mindustry.*;
import mindustry.ai.*;
import mindustry.ai.types.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.fragments.*;
import mindustry.world.*;
import mindustry.world.meta.Env;

import java.lang.reflect.*;

import static mindustry.Vars.*;

public class GierMenuRenderer extends MenuRenderer{
    private static final float darkness = 0.3f;

    private final int width = !mobile ? 100 : 60;
    private final int height = !mobile ? 50 : 40;

    private int cacheFloor, cacheWall;

    private Camera camera = new Camera();
    private Mat mat = new Mat();

    private FrameBuffer shadows;
    private CacheBatch batch;

    private float time;

    private final Seq<Unit> battleUnits = new Seq<>();

    private float reinforcementTimer;

    private final Interval commanderTimer = new Interval(1);

    private static final int maxUnitsPerTeam = 12;
    private static final float reinforcementDelay = 180f;

    private static final UnitType[] shardedUnits = {
        GierUnitTypes.occupy,
        GierUnitTypes.occupy,
        GierUnitTypes.occupy,
        GierUnitTypes.staticu,
        GierUnitTypes.staticu,
        GierUnitTypes.staticu
    };

    private static final UnitType[] cruxUnits = {
        GierUnitTypes.overboard,
        GierUnitTypes.overboard,
        GierUnitTypes.overboard
    };

    public GierMenuRenderer(){
        super();

        Time.mark();

        setupBattleRules();
        generate();
        cache();
        spawnArmy();

        Log.debug("Time to generate battle menu: @", Time.elapsed());
    }

    public static void install(){
        if(Vars.headless || Vars.ui == null) return;

        try{
            Field field = MenuFragment.class.getDeclaredField("renderer");
            field.setAccessible(true);

            MenuRenderer current = (MenuRenderer)field.get(Vars.ui.menufrag);

            if(current instanceof GierMenuRenderer) return;

            GierMenuRenderer replacement = new GierMenuRenderer();

            field.set(Vars.ui.menufrag, replacement);

            if(current != null){
                current.dispose();
            }

            Log.info("[GierMenuRenderer]");
        }catch(Throwable error){
            Log.err("[GierMenuRenderer] Failed to gier...");
            Log.err(error);
        }
    }

    private void setupBattleRules(){
        state.rules.disableUnitCap = true;
        state.rules.env = Env.any;
        state.rules.waves = false;
        state.rules.attackMode = false;
    }

    private void generate(){
        world.setGenerating(true);

        Tiles tiles = world.resize(width, height);

        shadows = new FrameBuffer(width, height);

        int seed = Mathf.random(100000);

        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                float shoreline = width * 0.5f
                    + (float)Simplex.noise2d(
                        seed,
                        2,
                        0.5,
                        1f / 12f,
                        0,
                        y
                    ) * 3f;

                float distance = x - shoreline;

                Block floor;
                Block wall = Blocks.air;

                if(distance < -18f){
                    floor = Blocks.stone;
                    wall = Blocks.stoneWall;

                    if(Mathf.chance(0.06f)){
                        wall = GierBlocks.massiveStoneBoulder;
                    }

                }else if(distance < -10f){
                    floor = Blocks.stone;

                }else if(distance < -2f){
                    floor = Blocks.sand;

                    if(distance < -5f && distance > -6f && Mathf.chance(0.64f)){
                        wall = Blocks.darkMetal;
                    }

                    if(distance < -5f && distance > -6f && Mathf.chance(0.87f)){
                        floor = Blocks.metalFloor;
                    }

                }else if(distance < 1f){
                    floor = Blocks.sandWater;

                }else if(distance < width * 0.12f){
                    floor = Blocks.water;

                }else{
                    floor = Blocks.deepwater;
                }

                Tile tile = new CachedTile();

                tiles.set(x, y, tile);

                tile.x = (short)x;
                tile.y = (short)y;

                tile.setFloor(floor.asFloor());
                tile.setBlock(wall);
                tile.setOverlay(Blocks.air);
            }
        }

        world.setGenerating(false);
    }

    private void spawnUnit(UnitType type, Team team){
    Vec2 position = findSpawnPosition(type, team);

    if(position == null){
        Log.warn("No valid spawn position for @ (@)", type.name, team.name);
        return;
    }

    try{
        Unit unit = type.spawn(team, position.x, position.y);

        unit.controller(new MenuCommandAI());
        battleUnits.add(unit);

        Log.info(
            "Spawned @ for @ at @, @ | Groups: @",
            type.name,
            team.name,
            unit.x,
            unit.y,
            Groups.unit.size()
        );

        Unit enemy = Units.closestEnemy(
            team,
            unit.x,
            unit.y,
            Float.MAX_VALUE,
            e -> !e.dead() &&
                e.checkTarget(type.targetAir, type.targetGround)
        );

        if(enemy != null && unit.controller() instanceof MenuCommandAI ai){
            ai.commandTarget(enemy);
        }

    }catch(Throwable error){
        Log.err("Failed to spawn @ for @", type.name, team.name);
        Log.err(error);
    }
    }

    private Vec2 findSpawnPosition(UnitType type, Team team){
    int minX, maxX;

    if(team == Team.sharded){
        minX = 3;
        maxX = width / 3;
    }else if(type.naval){
        minX = (int)(width * 0.65f);
        maxX = width - 3;
    }else{
        minX = width / 3;
        maxX = (int)(width * 0.48f);
    }

    for(int i = 0; i < 500; i++){
        int x = Mathf.random(minX, maxX);
        int y = Mathf.random(3, height - 4);

        Tile tile = world.tile(x, y);

        if(tile == null || tile.solid()) continue;

        if(type.naval){
            if(!tile.floor().isLiquid) continue;
        }else if(!type.flying){
            if(tile.floor().isDeep()) continue;
            if(type.canDrown && tile.floor().isLiquid) continue;
        }

        return new Vec2(tile.worldx(), tile.worldy());
    }

    return null;
    }
    
    private void cache(){
        Draw.proj().setOrtho(
            0,
            0,
            shadows.getWidth(),
            shadows.getHeight()
        );

        shadows.begin(Color.clear);
        Draw.color(Color.black);

        for(Tile tile : world.tiles){
            if(tile.block() != Blocks.air){
                Fill.rect(
                    tile.x + 0.5f,
                    tile.y + 0.5f,
                    1f,
                    1f
                );
            }
        }

        Draw.color();
        shadows.end();

        Batch previous = Core.batch;

        Core.batch = batch = new CacheBatch(
            new SpriteCache(width * height * 6, false)
        );

        batch.beginCache();

        for(Tile tile : world.tiles){
            tile.floor().drawBase(tile);
        }

        for(Tile tile : world.tiles){
            tile.overlay().drawBase(tile);
        }

        cacheFloor = batch.endCache();

        batch.beginCache();

        for(Tile tile : world.tiles){
            tile.block().drawBase(tile);
        }

        cacheWall = batch.endCache();

        Core.batch = previous;
    }

    private void spawnArmy(){
        for(UnitType type : shardedUnits){
            spawnUnit(type, Team.sharded);
        }

        for(UnitType type : cruxUnits){
            spawnUnit(type, Team.crux);
        }
    }

    private void updateBattle(){
        reinforcementTimer += Time.delta;

        battleUnits.removeAll(unit -> !unit.isAdded() || unit.dead);

        if(reinforcementTimer >= reinforcementDelay){
            reinforcementTimer = 0f;

            int shardedCount = battleUnits.count(
                u -> u.team == Team.sharded
            );

            int cruxCount = battleUnits.count(
                u -> u.team == Team.crux
            );

            if(shardedCount < maxUnitsPerTeam){
                spawnUnit(
                    shardedUnits[Mathf.random(shardedUnits.length - 1)],
                    Team.sharded
                );
            }

            if(cruxCount < maxUnitsPerTeam){
                spawnUnit(
                    cruxUnits[Mathf.random(cruxUnits.length - 1)],
                    Team.crux
                );
            }
        }
        simulateBattle();
    }

    private void simulateBattle(){

        state.teams.updateTeamStats();

        runCommander();

        Groups.updatePooling()
        Groups.unit.update();      
        state.teams.updateTeamStats();
    
        Groups.bullet.updatePhysics();
        Groups.bullet.update();
        Groups.bullet.collide();

        Groups.all.each(e -> {
            if(e instanceof EffectState || e instanceof Decal){
                e.update();
            }
        });
    }

    private void runCommander(){
        if(!commanderTimer.get(20f)) return;

        for(Unit unit : battleUnits){
            if(unit.dead || !unit.isAdded()) continue;

            if(!(unit.controller() instanceof MenuCommandAI ai)) continue;
            if(ai.attackTarget != null &&
                !Units.invalidateTarget(
                    ai.attackTarget,
                    unit.team,
                    unit.x,
                    unit.y
                )){
                continue;
            }

            Unit enemy = Units.closestEnemy(
                unit.team,
                unit.x,
                unit.y,
                Float.MAX_VALUE,
                e -> !e.dead() &&
                    e.checkTarget(
                        unit.type.targetAir,
                        unit.type.targetGround
                    )
            );

            if(enemy != null){
                ai.commandTarget(enemy);
            }
        }
    }

    @Override
    public void render(){
        time += Time.delta;

        updateBattle();

        float scaling = Math.max(
            Scl.scl(4f),
            Math.max(
                Core.graphics.getWidth() / ((width - 1f) * tilesize),
                Core.graphics.getHeight() / ((height - 1f) * tilesize)
            )
        );

        camera.position.set(
            width * tilesize / 2f,
            height * tilesize / 2f
        );

        camera.resize(
            Core.graphics.getWidth() / scaling,
            Core.graphics.getHeight() / scaling
        );

        mat.set(Draw.proj());

        Draw.flush();
        Draw.proj(camera);

        batch.setProjection(camera.mat);

        batch.beginDraw();
        batch.drawCache(cacheFloor);
        batch.endDraw();

        Draw.color();

        Draw.rect(
            Draw.wrap(shadows.getTexture()),
            width * tilesize / 2f - 4f,
            height * tilesize / 2f - 4f,
            width * tilesize,
            -height * tilesize
        );

        Draw.flush();

        batch.beginDraw();
        batch.drawCache(cacheWall);
        batch.endDraw();

        drawUnits();

        Draw.proj(mat);

        Draw.color(0f, 0f, 0f, darkness);

        Fill.crect(
            0,
            0,
            Core.graphics.getWidth(),
            Core.graphics.getHeight()
        );

        Draw.color();
    }

    private void drawUnits(){
        for(Unit unit : battleUnits){
            if(!unit.isAdded()) continue;

            unit.type.draw(unit);
        }

        Draw.flush();
    }

    @Override
    public void dispose(){
        for(Unit unit : battleUnits){
            if(unit.isAdded()){
                unit.remove();
            }
        }

        battleUnits.clear();

        if(batch != null) batch.dispose();
        if(shadows != null) shadows.dispose();

        super.dispose();
    }

    private static class MenuCommandAI extends CommandAI{

        @Override
        public void updateUnit(){
            if(unit == null || !unit.isValid()) return;

            if(attackTarget != null &&
                Units.invalidateTarget(
                    attackTarget,
                    unit.team,
                    unit.x,
                    unit.y
                )){
                attackTarget = null;
                targetPos = null;
            }

            updateTargeting();

            Teamc dst = attackTarget != null
                ? attackTarget
                : target;

            if(dst == null ||
                Units.invalidateTarget(dst, unit.team, unit.x, unit.y)){
                return;
            }

            float engageRange = Math.max(1f, unit.range() - 10f);

            if(unit.within(dst, engageRange)){
                unit.lookAt(dst);
                return;
            }

            Vec2 move = Tmp.v1.set(dst)
                .sub(unit)
                .setLength(unit.speed());

            unit.movePref(move);
            unit.lookAt(dst);
        }
    }
}
