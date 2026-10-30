
package mindustry.world.blocks.defense.turrets;

import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.io.*;

import mindustry.ctype.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;
import mindustry.type.PayloadSeq;

import static mindustry.Vars.*;

public class PayloadAmmoTurret extends Turret{
    public OrderedMap<UnlockableContent, BulletType> ammoTypes = {};

    public PayloadAmmoTurret(String name){
        super(name);

        maxAmmo = 3;
        acceptsPayload = true;
    }

    public void ammo(Object... objects){
        if(objects.length % 2 != 0){
            throw new IllegalArgumentException("Ammo definitions must be content/bullet pairs.");
        }

        OrderedMap<UnlockableContent, BulletType> map = new OrderedMap<>();

        for(int i = 0; i < objects.length; i += 2){
            if(!(objects[i] instanceof UnlockableContent content)){
                throw new IllegalArgumentException("Ammo entry " + i + " must be UnlockableContent.");
            }

            if(!(objects[i + 1] instanceof BulletType bullet)){
                throw new IllegalArgumentException("Ammo entry " + (i + 1) + " must be BulletType.");
            }

            if(map.containsKey(content)){
                throw new IllegalArgumentException("Duplicate ammo content: " + content.name);
            }

            map.put(content, bullet);
        }

        ammoTypes = map;
    }

    /** Limits all bullet types to this turret's range. */
    public void limitRange(){
        limitRange(1f);
    }

    public void limitRange(float margin){
        for(BulletType bullet : ammoTypes.values()){
            if(bullet.speed > 0f){
                bullet.lifetime = (range + margin) / bullet.speed;
            }
        }
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.remove(Stat.itemCapacity);
        stats.add(Stat.ammo, StatValues.ammo(ammoTypes, true));
    }

    @Override
    public void setBars(){
        super.setBars();

        addBar("ammo", (PayloadTurretBuild build) ->
            new Bar(
                "stat.ammo",
                Pal.ammo,
                build::ammoFraction
            )
        );
    }

    @Override
    public void init(){
        ammoKeys = ammoTypes.keys().toSeq().toArray(UnlockableContent.class);

        consume(new ConsumePayloadFilter(content -> ammoTypes.containsKey(content)){

            @Override
            public void build(Building build, Table table){
                MultiReqImage image = new MultiReqImage();

                for(var block : content.blocks()){
                    displayContent(build, image, block);
                }

                for(var unit : content.units()){
                    displayContent(build, image, unit);
                }

                table.add(image).size(32f);
            }

            void displayContent(Building build, MultiReqImage image, UnlockableContent content){
                if(filter.get(content) && content.unlockedNow()){
                    image.add(new ReqImage(
                        new Image(content.uiIcon),
                        () -> build instanceof PayloadTurretBuild turret
                            && turret.hasAmmo()
                            && turret.currentAmmo() == content
                    ));
                }
            }

            @Override
            public float efficiency(Building build){
                return build instanceof PayloadTurretBuild turret
                    && turret.hasAmmo() ? 1f : 0f;
            }

            @Override
            public void display(Stats stats){

            }
        });

        super.init();
    }

    public class PayloadTurretBuild extends TurretBuild{

        public PayloadSeq payloads = new PayloadSeq();
        public UnlockableContent currentAmmo(){
            for(UnlockableContent content : ammoKeys){
                if(payloads.contains(content)){
                    return content;
                }
            }

            return null;
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload){
            return payload != null
                && payloads.total() < maxAmmo
                && ammoTypes.containsKey(payload.content());
        }

        @Override
        public void handlePayload(Building source, Payload payload){
            if(!acceptPayload(source, payload)) return;

            payloads.add(payload.content());

            totalAmmo = payloads.total();
        }

        @Override
        public boolean hasAmmo(){
            return payloads.total() >= ammoPerShot;
        }

        @Override
        public BulletType peekAmmo(){
            UnlockableContent content = currentAmmo();

            return content == null ? null : ammoTypes.get(content);
        }

        @Override
        public BulletType useAmmo(){
            if(!hasAmmo()) return null;

            UnlockableContent content = currentAmmo();

            if(content == null) return null;

            if(!cheating()){
                for(int i = 0; i < ammoPerShot; i++){
                    payloads.remove(content);
                }
            }

            totalAmmo = payloads.total();

            return ammoTypes.get(content);
        }

        @Override
        public PayloadSeq getPayloads(){
            return payloads;
        }

        @Override
        public UnlockableContent getAmmoContent(){
            return currentAmmo();
        }

        public float ammoFraction(){
            return maxAmmo <= 0 ? 0f :
                (float)payloads.total() / maxAmmo;
        }

        @Override
        public float getAmmoFraction(){
            return ammoFraction();
        }

        @Override
        public void updateTile(){
            totalAmmo = payloads.total();

            super.updateTile();
        }

        @Override
        public void write(Writes write){
            super.write(write);

            payloads.write(write);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            payloads.read(read);
						payloads.removeAll(content -> !ammoTypes.containsKey(content));

            totalAmmo = payloads.total();
        }
    }
}
