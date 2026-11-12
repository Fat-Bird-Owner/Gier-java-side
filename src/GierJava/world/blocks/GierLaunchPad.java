package GierJava.world.blocks;

import arc.Core;
import arc.scene.ui.layout.Table;
import arc.util.io.Reads;
import arc.util.io.Writes;
import arc.struct.*;
import arc.util.*;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.ctype.ContentType;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.type.Planet;
import mindustry.type.Sector;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.PlanetDialog;
import ent.anno.Annotations.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.entities.comp.*;

import mindustry.world.blocks.campaign.LaunchPad;
import static mindustry.Vars.*;

public class GierLaunchPad extends LaunchPad{

    public GierLaunchPad(String name){
        super(name);
        itemCapacity = 1000;
        launchTime = 60f * 10f;
        acceptMultipleItems = true;
    }

    public class GierLaunchPadBuild extends LaunchPadBuild{
        public float launchCounter;

        @Override
        public boolean shouldConsume(){
            return destination() != null && launchCounter < launchTime;
        }

        public Sector destination(){
            return state.rules.sector.info.destination;
        }
        
        @Override
        public void updateTile(){
            if(destination() == null) return;

            launchCounter += edelta();

            if(launchCounter >= launchTime && items.total() >= itemCapacity){
                items.each((item, amount) -> {
                    int space = Math.max(0, destination().info.storageCapacity - destination().info.items.get(item));
                    destination().info.items.add(item, Math.min(space, amount));
                });

                destination().info.items.checkNegative();
                destination().saveInfo();

                launchSound.at(x, y, 1f);
                Fx.launchPod.at(this);

                LaunchPayload entity = LaunchPayload.create();
                items.each((item, amount) -> entity.stacks.add(new ItemStack(item, amount)));
                entity.set(this);
                entity.lifetime(120f);
                entity.team(team);
                entity.add();

                items.clear();
                launchCounter = 0f;
            }
        }

        @Override
public void buildConfiguration(Table table){
    if(!Vars.state.isCampaign() || Vars.net.client()){
        deselect();
        return;
    }

    table.button(Icon.upOpen, Styles.cleari, () -> {
        PlanetDialog.debugSelect = true;

        Vars.ui.planet.hidden(() -> PlanetDialog.debugSelect = false);

        Vars.ui.planet.showPlanetLaunch(
            Vars.state.rules.sector,
            Vars.state.rules.sector.planet.launchCandidates,
            other -> {
                PlanetDialog.debugSelect = false;

                if(other != null && other.hasBase() && other.isFrozen() == false){
                    state.rules.sector.info.destination = other;
                }
            }
        );

        deselect();
    }).size(40f);
}

        @Override
        public void display(Table table){
            super.display(table);

            table.row();
            table.add(destination == null
                ? "No destination selected"
                : "Destination: " + destination().planet.localizedName + " / " + destination.name()
            ).wrap().width(200f).left();
        }

        @Override
        public byte version(){
            return 2;
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.f(launchCounter);
            write.str(destination() == null ? "" : destination().planet.name);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            if(revision >= 1){
                launchCounter = read.f();
            }
            
            }


    @EntityDef(LaunchPayloadc.class)
    static abstract class LaunchPayloadComp implements Drawc, Timedc, Teamc{
        @Import float x,y;

        Seq<ItemStack> stacks = new Seq<>();
        transient Interval in = new Interval();

        @Override
        public void draw(){
            float alpha = fout(Interp.pow5Out);
            float scale = (1f - alpha) * 1.3f + 1f;
            float cx = cx(), cy = cy();
            float rotation = fin() * (130f + Mathf.randomSeedRange(id(), 50f));

            Draw.z(Layer.effect + 0.001f);

            Draw.color(Pal.engine);

            float rad = 0.2f + fslope();

            Fill.light(cx, cy, 10, 25f * (rad + scale-1f), Tmp.c2.set(Pal.engine).a(alpha), Tmp.c1.set(Pal.engine).a(0f));

            Draw.alpha(alpha);
            for(int i = 0; i < 4; i++){
                Drawf.tri(cx, cy, 6f, 40f * (rad + scale-1f), i * 90f + rotation);
            }

            Draw.color();

            Draw.z(Layer.weather - 1);

            TextureRegion region = blockOn() instanceof mindustry.world.blocks.campaign.LaunchPad p ? p.podRegion : Core.atlas.find("launchpod");
            scale *= region.scl();
            float rw = region.width * scale, rh = region.height * scale;

            Draw.alpha(alpha);
            Draw.rect(region, cx, cy, rw, rh, rotation);

            Tmp.v1.trns(225f, fin(Interp.pow3In) * 250f);

            Draw.z(Layer.flyingUnit + 1);
            Draw.color(0, 0, 0, 0.22f * alpha);
            Draw.rect(region, cx + Tmp.v1.x, cy + Tmp.v1.y, rw, rh, rotation);

            Draw.reset();
        }

        float cx(){
            return x + fin(Interp.pow2In) * (12f + Mathf.randomSeedRange(id() + 3, 4f));
        }

        float cy(){
            return y + fin(Interp.pow5In) * (100f + Mathf.randomSeedRange(id() + 2, 30f));
        }

        @Override
        public void update(){
            float r = 3f;
            if(in.get(4f - fin()*2f)){
                Fx.rocketSmoke.at(cx() + Mathf.range(r), cy() + Mathf.range(r), fin());
            }
        }

        @Override
        public void remove(){
            if(!state.isCampaign() || net.client()) return;

            Sector destsec = state.rules.sector.info.destination;

            //actually launch the items upon removal
            if(team() == state.rules.defaultTeam && destsec != null && destsec != state.rules.sector){
                ItemSeq dest = new ItemSeq();

                for(ItemStack stack : stacks){
                    dest.add(stack);
                    state.rules.sector.info.handleItemExport(stack);
                    Events.fire(new LaunchItemEvent(stack));
                }

                if(state.getPlanet().campaignRules.legacyLaunchPads){
                    destsec.addItems(dest);
                }
            }
        }

        
        }
    }
}
