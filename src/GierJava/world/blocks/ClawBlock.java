
package GierJava.world.blocks;

import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.math.geom.Geometry;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.meta.BlockGroup;

import static mindustry.Vars.*;

public class ClawBlock extends Block {

    public int range = 5;
    public float clawSpeed = 0.08f;
    public float payloadSpeed = 0.06f;

    public ClawBlock(String name){
        super(name);

        update = true;
        solid = true;
        rotate = true;
        group = BlockGroup.payloads;

    }

    public class ClawBuild extends Building{

        // 0 = search, 1 = extending, 2 = working,
        // 3 = retracting with payload, 4 = outputting
        public int state = 0;

        public Building target;
        public Payload payload;

        public float clawProgress;
        public float workProgress;
        public float payloadProgress;

        public float targetX, targetY;

        public int dirX(){
            return Geometry.d4(rotation).x;
        }

        public int dirY(){
            return Geometry.d4(rotation).y;
        }

        public float frontX(){
            return x + dirX() * (size * tilesize / 2f);
        }

        public float frontY(){
            return y + dirY() * (size * tilesize / 2f);
        }

        public float backX(){
            return x - dirX() * (size * tilesize / 2f + tilesize);
        }

        public float backY(){
            return y - dirY() * (size * tilesize / 2f + tilesize);
        }

        public void findTarget(){
            target = null;

            for(int i = size / 2 + 1; i <= range; i++){
                Tile other = tile.nearby(dirX() * i, dirY() * i);

                if(other == null) break;

                Building build = other.build;

                if(build == null) continue;

                if(build == this) continue;

                if(build.team != team) continue;

                if(!build.block.canPickup) continue;

                target = build;
                targetX = build.x;
                targetY = build.y;
                state = 1;
                clawProgress = 0f;
                return;
            }
        }

        @Override
        public void updateTile(){
            switch(state){
                case 0 -> {
                    if(payload == null){
                        findTarget();
                    }
                }

                case 1 -> {
                    if(target == null || target.tile == null || target.dead){
                        resetClaw();
                        return;
                    }

                    clawProgress = Mathf.approachDelta(
                        clawProgress, 1f, clawSpeed
                    );

                    if(clawProgress >= 1f){
                        state = 2;
                        workProgress = 0f;
                    }
                }

                case 2 -> {
                    if(target == null || target.dead){
                        resetClaw();
                        return;
                    }

                    // Wait for the target block's build time.
                    workProgress += edelta();
                    if(workProgress >= target.block.buildTime){
                        grabTarget();
                    }
                }

                case 3 -> {
                    payloadProgress = Mathf.approachDelta(
                        payloadProgress, 1f, payloadSpeed
                    );

                    if(payload != null){
                        payload.set(
                            Mathf.lerp(targetX, x, payloadProgress),
                            Mathf.lerp(targetY, y, payloadProgress),
                            rotation * 90f
                        );
                    }

                    if(payloadProgress >= 1f){
                        state = 4;
                        payloadProgress = 0f;
                    }
                }

                case 4 -> {
                    payloadProgress = Mathf.approachDelta(
                        payloadProgress, 1f, payloadSpeed
                    );

                    if(payload != null){
                        payload.set(
                            Mathf.lerp(x, backX(), payloadProgress),
                            Mathf.lerp(y, backY(), payloadProgress),
                            rotation * 90f
                        );
                    }

                    if(payloadProgress >= 1f){
                        Tile out = world.tileWorld(backX(), backY());

                        if(out != null && out.build == null && out.block().isAir()){
                            if(payload instanceof BuildPayload bp){
                                bp.place(out, rotation);
                                payload = null;
                                resetClaw();
                            }
                        }
                    }
                }
            }
        }

        public void grabTarget(){
            if(target == null || target.tile == null){
                resetClaw();
                return;
            }

            payload = new BuildPayload(target);
            Tile targetTile = target.tile;

            targetTile.build.kil();
            targetX = target.x;
            targetY = target.y;

            target = null;
            payloadProgress = 0f;
            state = 3;
        }

        public void resetClaw(){
            target = null;
            state = 0;
            clawProgress = 0f;
            workProgress = 0f;
            payloadProgress = 0f;
        }

        @Override
        public void draw(){
            super.draw();

            float length = range * tilesize * clawProgress;
            if(state == 1 || state == 2){
                Draw.color(team.color);
                Draw.rect(
                    region,
                    x + dirX() * length / 2f,
                    y + dirY() * length / 2f,
                    rotation * 90f
                );
                Draw.color();
            }

            if(payload != null){
                payload.draw();
            }
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.i(state);
            write.f(clawProgress);
            write.f(workProgress);
            write.f(payloadProgress);
            Payload.write(payload, write);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            state = read.i();
            clawProgress = read.f();
            workProgress = read.f();
            payloadProgress = read.f();
            payload = Payload.read(read);
        }
    }
}
