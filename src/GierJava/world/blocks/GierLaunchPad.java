package GierJava.world.blocks;

import arc.Core;
import arc.scene.ui.layout.Table;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.ctype.ContentType;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.type.Planet;
import mindustry.type.Sector;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.PlanetDialog;

import mindustry.world.blocks.campaign.LaunchPad;
import static mindustry.Vars;

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
            return destination != null && launchCounter < launchTime;
        }

        public Sector destination(){
            return state.rules.sector.info.destination;
        }
        
        @Override
        public void updateTile(){
            if(destination == null) return;

            launchCounter += edelta();

            if(launchCounter >= launchTime && items.total() >= itemCapacity){
                items.each((item, amount) -> {
                    int space = Math.max(0, destination().info.storageCapacity - destination().info.items.get(item));
                    destination().info.items.add(item, Math.min(space, amount));
                });

                destination.info.items.checkNegative();
                destination.saveInfo();

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
            write.str(destination == null ? "" : destination.planet.name);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            if(revision >= 1){
                launchCounter = read.f();
            }
            
            }

        
        }
    }
}
