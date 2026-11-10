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
import mindustry.world.blocks.campaign.LaunchPad;

public class GierLaunchPad extends LaunchPad{

    public GierLaunchPad(String name){
        super(name);
        itemCapacity = 1000;
        launchTime = 60f * 10f;
        acceptMultipleItems = true;
    }

    public class GierLaunchPadBuild extends LaunchPadBuild{
        public float launchCounter;
        public Sector destination;

        @Override
        public boolean shouldConsume(){
            return destination != null && launchCounter < launchTime;
        }

        @Override
        public void updateTile(){
            if(destination == null) return;

            launchCounter += edelta();

            if(launchCounter >= launchTime && items.total() >= itemCapacity){
                items.each((item, amount) -> {
                    int space = Math.max(0, destination.info.storageCapacity - destination.info.items.get(item));
                    destination.info.items.add(item, Math.min(space, amount));
                });

                destination.info.items.checkNegative();
                destination.saveInfo();

                launchSound.at(x, y, 1f);
                Fx.launchPod.at(this);

                items.clear();
                launchCounter = 0f;
            }
        }

        @Override
        public void buildConfiguration(Table table){
            table.button(Icon.upOpen, Styles.cleari, () -> {
                if(!Vars.state.isCampaign() || Vars.net.client()) return;

                var candidates = Vars.content.planets().select(p ->
                    !p.removed && p.generator != null
                );

                Vars.ui.planet.showPlanetLaunch(
                    Vars.state.rules.sector,
                    candidates,
                    other -> {
                        if(other != null){
                            destination = other;
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
                : "Destination: " + destination.planet.localizedName + " / " + destination.name()
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
            write.i(destination == null ? -1 : destination.id);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            if(revision >= 1){
                launchCounter = read.f();
            }

            if(revision >= 2){
                String planetName = read.str();
                int sectorId = read.i();

                if(planetName != null && !planetName.isEmpty() && sectorId >= 0){
                    Planet planet = (Planet)Vars.content.getByName(ContentType.planet, planetName);

                    if(planet != null && sectorId < planet.sectors.size){
                        destination = planet.sectors.get(sectorId);
                    }
                }
            }
        }
    }
}
