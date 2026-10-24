package GierJava.world.blocks;

import mindustry.world.meta.*; 
import arc.util.io.*; 
import mindustry.gen.*; 
import arc.graphics.g2d.*; 
import mindustry.content.*; 
import arc.util.*; 
import mindustry.type.*; 
import arc.math.*; 
import mindustry.world.*; 
import arc.Core; 
import arc.audio.Sound; 
import mindustry.entities.*;

public class ConfigGate extends Block{ 
    
public TextureRegion topRegion; 
public float speed = 5f; 
public TextureRegion topRegionInverted; 
public Sound toggleSound = Sounds.click; 
public final int timerToggle = timers++;

public static final int underflow = 2;
public static final int router = 0;
public static final int overflow = 1;

public ConfigGate(String name){
    super(name);
    group = BlockGroup.transportation;
    hasItems = true;
    itemCapacity = 1;
    update = true;
    solid = false;
    rotate = true;
    conveyorPlacement = true;
    unloadable = false;
    noUpdateDisabled = true;
    underBullets = true;
    priority = TargetPriority.transport;
    envEnabled = Env.space | Env.terrestrial | Env.underwater;
    regionRotated1 = 1;
    consumesTap = true;

    config(Integer.class, (ConfigGateBuild build, Integer value) -> {
        if(value != null) build.mode = Mathf.clamp(value, 0, 2);
    });
}

@Override
public boolean rotatedOutput(int x, int y){
    return false;
}

@Override
public void load(){
    super.load();
    topRegionInverted = Core.atlas.find(name + "-top-inverted");
    topRegion = Core.atlas.find(name + "-top");
}

@Override
public void setStats(){
    super.setStats();
    stats.add(Stat.itemsMoved, 60f / Math.max(speed, 0.001f), StatUnit.itemsSecond);
}

@Override
public TextureRegion[] icons(){
    return new TextureRegion[]{region, topRegion};
}

public class ConfigGateBuild extends Building{
    public int dumpIndex;
    public int inputDir = -1;
    public int mode = router;
    public float progress;
    public @Nullable Item current;

    @Override
    public void write(Writes write){
        super.write(write);
        write.b(mode);
        write.b(inputDir);
        write.i(dumpIndex);
        write.f(progress);
    }

    @Override
    public void read(Reads read, byte revision){
        super.read(read, revision);
        mode = Mathf.clamp(read.b(), 0, 2);
        inputDir = read.b();
        dumpIndex = read.i();
        progress = read.f();
        current = items.any() ? items.first() : null;
    }

    @Override
    public Object config(){
        return mode;
    }

    @Override
    public void tapped(){
        if(!timer(timerToggle, 15f)) return;
        configure((mode + 1) % 3);
        toggleSound.at(this);
    }

    @Override
    public void draw(){
        Draw.rect(region, x, y);
        Draw.rect(mode == underflow ? topRegionInverted : topRegion, x, y, rotdeg());
        Draw.reset();
    }

    @Override
    public int removeStack(Item item, int amount){
        int removed = super.removeStack(item, amount);
        if(item == current){
            current = null;
            progress = 0f;
            inputDir = -1;
        }
        return removed;
    }

    @Override
    public void handleItem(Building source, Item item){
        if(current != null || items.total() >= itemCapacity) return;
        inputDir = tile.relativeTo(source.tile);
        if(inputDir == -1) return;
        current = item;
        items.add(item, 1);
        progress = 0f;
        noSleep();
    }

    @Override
    public boolean acceptItem(Building source, Item item){
        if(source == null || source.team != team) return false;
        if(current != null || items.total() >= itemCapacity) return false;
        return tile.relativeTo(source.tile) != -1;
    }

    @Override
    public void updateTile(){
        if(current == null && items.any()) current = items.first();

        if(current == null){
            progress = 0f;
            return;
        }

        Building next = target();
        if(next == null) return;

        if(mode == router){
            progress += edelta() / Math.max(speed, 0.001f);
            if(progress < 1f) return;
        }

        next.handleItem(this, current);
        items.remove(current, 1);
        current = null;
        progress = 0f;
        dumpIndex++;
        noSleep();
    }

    private boolean validOutput(@Nullable Building other){
        return other != null
            && other != this
            && other.team == team
            && other.acceptItem(this, current);
    }

    @Nullable
    public Building target(){
        if(current == null || inputDir < 0) return null;

        if(mode == router){
            for(int i = 0; i < 4; i++){
                int dir = Mathf.mod(inputDir + 1 + dumpIndex + i, 4);
                if(dir == inputDir) continue;
                Building other = nearby(dir);
                if(validOutput(other)) return other;
            }
            return null;
        }

        int forward = Mathf.mod(inputDir + 2, 4);
        int left = Mathf.mod(inputDir + 1, 4);
        int right = Mathf.mod(inputDir + 3, 4);

        Building front = nearby(forward);
        Building a = nearby(left);
        Building b = nearby(right);

        boolean frontValid = validOutput(front);
        boolean aValid = validOutput(a);
        boolean bValid = validOutput(b);

        if(mode == overflow){
            if(frontValid) return front;
            if(aValid && bValid) return (dumpIndex & 1) == 0 ? a : b;
            if(aValid) return a;
            if(bValid) return b;
            return null;
        }

        if(aValid && bValid) return (dumpIndex & 1) == 0 ? a : b;
        if(aValid) return a;
        if(bValid) return b;
        return frontValid ? front : null;
    }
}
}