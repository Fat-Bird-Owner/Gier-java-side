package GierJava.entities.comp;

import ent.anno.Annotations.*;
import mindustry.gen.*;
import mindustry.content.StatusEffects;

import static arc.Core.*;

@EntityComponent
abstract class RageComp implements Healthc{
    private boolean enraged;

    @Override
    public void update(){
        super.update();
    if (!enraged && isValid() && health <= maxHealth / 2f){
        apply(StatusEffects.boss);
        enraged = true;
    }}

}
