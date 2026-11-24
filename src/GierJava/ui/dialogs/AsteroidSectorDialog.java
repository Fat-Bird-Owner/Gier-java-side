package GierJava.ui.dialogs;

import arc.*;
import arc.graphics.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;
import arc.math.*;

public class AsteroidSectorDialog{

    public static Seq<Planet> squareMapPlanets = new Seq<>();

    private final BaseDialog dialog = new BaseDialog("Sector Map");

    private Planet previous;
    private Planet planet;
    private Sector selected;
    private boolean opening;

    public AsteroidSectorDialog(){
        dialog.addCloseButton();
        Events.run(Trigger.update, this::update);
    }

    private Planet getPlanet(){
        if(Vars.ui.planet.isShown()){
            return Vars.ui.planet.state.planet;
        }

        return null;
    }

    private boolean supported(Planet planet){
        return planet != null && squareMapPlanets.contains(planet, true);
    }

    private void update(){
        if(opening) return;
        if(!Vars.ui.planet.isShown()) return;

        Planet current = getPlanet();

        if(!supported(current)) return;

        if(current == previous && dialog.isShown()) return;

        previous = current;
        opening = true;

        rebuild();
        dialog.show();

        Time.runTask(1f, () -> {
            Vars.ui.planet.hide();
            opening = false;
        });
    }

    private void rebuild(){
        dialog.cont.clear();
        planet = getPlanet();

        if(planet == null) planet = previous;

        if(planet == null) return;

        int count = planet.sectors.size;
        int columns = Math.max(1, Mathf.ceil((float)Mathf.sqrt(count)));

        dialog.cont.pane(p -> {
            p.defaults().pad(3f);

            Table grid = new Table();
            grid.defaults().size(100f).pad(3f);

            for(int i = 0; i < count; i++){
                Sector sector = planet.sectors.get(i);

                Button cell = new Button(Styles.squareTogglet);
                cell.clicked(() -> {
                    selected = sector;
                    rebuildInfo();
                });

                cell.add("[accent]" + sector.id).row();

                if(sector.hasBase()){
                    cell.add("[accent]Captured");
                }else if(sector.hasEnemyBase()){
                    cell.add("[salmon]Enemy");
                }else{
                    cell.add("[lightgray]Locked");
                }

                cell.setChecked(selected == sector);

                grid.add(cell);

                if((i + 1) % columns == 0){
                    grid.row();
                }
            }

            p.add(grid).grow().row();
        }).grow();

        dialog.cont.row();

        rebuildInfo();

        dialog.buttons.clear();

        dialog.buttons.button("@back", Icon.left, () -> {
            dialog.hide();

            Vars.ui.planet.show();
            Vars.ui.planet.viewPlanet(planet, false);
        }).size(180f, 54f);
    }

    private void rebuildInfo(){

        dialog.cont.clearChildren();

        dialog.cont.pane(p -> {
            int count = planet.sectors.size;
            int columns = Math.max(1, Mathf.ceil((float)Mathf.sqrt(count)));

            Table grid = new Table();
            grid.defaults().size(100f).pad(3f);

            for(int i = 0; i < count; i++){
                Sector sector = planet.sectors.get(i);

                Button cell = new Button(Styles.squareTogglet);
                cell.clicked(() -> {
                    selected = sector;
                    rebuildInfo();
                });

                cell.add("[accent]" + sector.id).row();

                cell.add(
                    sector.hasBase() ? "[green]Owned" :
                    sector.hasEnemyBase() ? "[scarlet]Enemy" :
                    "[lightgray]Unclaimed"
                );

                cell.setChecked(selected == sector);
                grid.add(cell);

                if((i + 1) % columns == 0) grid.row();
            }

            p.add(grid).growX().row();
            p.add().height(10f).row();

            if(selected == null){
                p.add("[lightgray]Select a sector").row();
                return;
            }

            p.add("[accent]" + selected.name()).row();
            p.add("Sector ID: " + selected.id).row();
            p.add("Threat: " + selected.displayThreat()).row();

            p.button(
                selected.isBeingPlayed() ? "Resume" :
                selected.hasBase() ? "Enter sector" :
                "Land",
                Icon.play,
                () -> land(selected)
            ).width(220f).height(50f)
            .disabled(!canLand(selected));
        }).grow();
    }

    private boolean canLand(Sector sector){
        if(sector == null) return false;
        if(sector.isBeingPlayed() || sector.hasBase()) return true;
        if(sector.locked() || sector.isCaptured()) return false;
        if(sector.planet.generator == null) return false;

        return sector.planet.generator.allowLanding(sector);
    }

    private void land(Sector sector){
        if(!canLand(sector)) return;

        Planet target = planet;

        dialog.hide();

        Vars.ui.planet.show();
        Vars.ui.planet.viewPlanet(target, false);
        Vars.ui.planet.selectSector(sector);

    }
}
