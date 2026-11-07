package GierJava.graphics;

import GierJava.content.*;
    
import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.noise.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.fragments.*;
import mindustry.world.*;
import java.lang.reflect.*;
import mindustry.ai.*;
import mindustry.entities.*;
import mindustry.entities.units.*;

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
                    
                    if (Mathf.chance(0.06f)){
                        wall = GierBlocks.massiveStoneBoulder;
                    }
                    
                }else if(distance < -10f){                   
                    floor = Blocks.stone;                    
                }else if(distance < -2f){
                    floor = Blocks.sand;

                    if (distance < -5f && distance > -6f && Mathf.chance(0.64f)){
                        wall = Blocks.darkMetal;
                    }

                    if (distance < -5f && distance > -6f && Mathf.chance(0.87f)){
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
    float x = team == Team.sharded
        ? Mathf.random(3f, width * 0.2f) * tilesize
        : (width - 1f) * tilesize;

    float y = Mathf.random(3f, height - 3f) * tilesize;

    Unit unit = type.spawn(
        team,
        x,
        y,
        team == Team.sharded ? 0f : 180f
    );

    unit.add();
    battleUnits.add(unit);
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

        int shardedCount = battleUnits.count(u -> u.team == Team.sharded);
        int cruxCount = battleUnits.count(u -> u.team == Team.crux);

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

    for(Unit unit : battleUnits){
        if(!unit.isAdded()) continue;

        unit.update();
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

        unit.type.drawShadow(unit);
        unit.type.draw(unit);
    }
    }

    @Override
    public void dispose(){
        for(Unit unit : battleUnits){
            if(unit.isAdded()) unit.remove();
        }

        battleUnits.clear();

        if(batch != null) batch.dispose();
        if(shadows != null) shadows.dispose();

        super.dispose();
    }
}
