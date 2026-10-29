
package GierJava.world.blocks;

import arc.graphics.g2d.Draw;
import mindustry.content.Fx;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Building;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.PayloadBlock;

public class PayloadProjectileType extends BulletType{

    public PayloadProjectileType(){
        speed = 4.5f;
        lifetime = 60f;
        damage = 30f;
        hitSize = 12f;

        collides = true;
        collidesTiles = true;
        collidesGround = true;
        collidesAir = true;
        collidesTeam = false;
        keepVelocity = false;

        splashDamage = 60f;
        splashDamageRadius = 24f;

        hitEffect = Fx.massiveExplosion;
        despawnEffect = Fx.massiveExplosion;
        despawnHit = true;
        drawSize = 100f;
    }

    @Override
    public void draw(Bullet bullet){
        super.draw(bullet);

        if(bullet.data instanceof BuildPayload payload){
            payload.set(bullet.x, bullet.y, bullet.rotation());

            Draw.z(mindustry.graphics.Layer.bullet);
            payload.draw();
        }

        Draw.reset();
    }

    @Override
    public void hitTile(Bullet bullet, Building build, float x, float y, float initialHealth, boolean direct){
        if(bullet.data instanceof BuildPayload payload
            && build instanceof PayloadBlock.PayloadBlockBuild<?> payloadBuild
            && bullet.owner instanceof Building source
            && payloadBuild.acceptPayload(source, payload)){

            payloadBuild.handlePayload(source, payload);
            bullet.data = null;

            hitEffect.at(x, y);
            bullet.remove();
            return;
        }

        super.hitTile(bullet, build, x, y, initialHealth, direct);
    }

    @Override
    public void hit(Bullet bullet, float x, float y, boolean createFrags){
        Object carried = bullet.data;

        super.hit(bullet, x, y, createFrags);
        if(carried instanceof Payload payload && bullet.data == carried){
            bullet.data = null;
            payload.destroyed();
        }
    }

    @Override
    public void despawned(Bullet bullet){
        Object carried = bullet.data;

        super.despawned(bullet);
        if(carried instanceof Payload payload && bullet.data == carried){
            bullet.data = null;
            payload.destroyed();
        }
    }

@Override
public void update(Bullet bullet){
    super.update(bullet);

    if(bullet.data instanceof BuildPayload payload && !bullet.isAdded()){
        return;
    }

    if(bullet.data instanceof BuildPayload payload){
        payload.set(bullet.x, bullet.y, bullet.rotation());
    }
}
    
}
