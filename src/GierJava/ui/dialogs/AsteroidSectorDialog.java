
package GierJava.ui.dialogs;

import arc.*;
import arc.math.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;

import GierJava.content.*;

public class AsteroidSectorDialog{

    public static Seq<Planet> squareMapPlanets = new Seq<>();

    private final BaseDialog dialog = new BaseDialog("Sector Map");

    private Planet planet;
    private Sector selected;

    private final Table grid = new Table();
    private final Table info = new Table();

    public AsteroidSectorDialog(){

        if(!squareMapPlanets.contains(GierPlanets.notva, true))
            squareMapPlanets.add(GierPlanets.notva);

        if(!squareMapPlanets.contains(GierPlanets.verilus, true))
            squareMapPlanets.add(GierPlanets.verilus);

        if(!squareMapPlanets.contains(GierPlanets.gier, true))
            squareMapPlanets.add(GierPlanets.gier);

        dialog.addCloseButton();

        dialog.cont.pane(p -> {
            p.add(grid).grow().row();
            p.add(info).growX().padTop(10f);
        }).grow();

        dialog.buttons.button("@back", Icon.left, () -> {
            Planet target = planet;

            dialog.hide();

            Vars.ui.planet.show();
            Vars.ui.planet.viewPlanet(target, false);
        }).size(180f, 54f);

        addButton();
    }

    public static boolean usesSquareMap(Planet planet){
        return planet != null &&
            squareMapPlanets.contains(planet, true);
    }

    private void addButton(){

        Vars.ui.planet.buttons.button(
            "Square Sectors",
            Icon.grid,
            () -> {
                Planet target = Vars.ui.planet.state.planet;

                if(usesSquareMap(target)){
                    show(target);
                }
            }
        ).size(180f, 54f);
    }

    private void show(Planet planet){
        this.planet = planet;
        this.selected = null;

        rebuildGrid();
        rebuildInfo();

        dialog.show();
    }

    private void rebuildGrid(){

        grid.clear();

        int count = planet.sectors.size;
        int columns = Math.max(1, Mathf.ceil(Mathf.sqrt(count)));

        grid.defaults().size(100f).pad(3f);

        for(int i = 0; i < count; i++){

            Sector sector = planet.sectors.get(i);

            Button cell = new Button(Styles.squareTogglet);

            cell.clicked(() -> {
                selected = sector;
                rebuildInfo();
                rebuildGrid();
            });

            cell.add("[accent]" + sector.id).row();

            if(sector.hasBase()){
                cell.add("[green]Captured");
            }else if(sector.hasEnemyBase()){
                cell.add("[scarlet]Enemy");
            }else if(isLocked(sector)){
                cell.add("[lightgray]Locked");
            }else{
                cell.add("[lightgray]Unclaimed");
            }

            cell.setChecked(selected == sector);

            grid.add(cell);

            if((i + 1) % columns == 0){
                grid.row();
            }
        }
    }

    private void rebuildInfo(){

        info.clear();

        if(selected == null){
            info.add("[lightgray]Select a sector").row();
            return;
        }

        Sector sector = selected;

        info.add("[accent]" + sector.name()).row();
        info.add("Sector ID: " + sector.id).row();
        info.add("Threat: " + sector.displayThreat()).row();

        if(sector.hasBase()){
            info.add("[green]Has a friendly base").row();
        }else if(sector.hasEnemyBase()){
            info.add("[scarlet]Enemy base detected").row();
        }

        info.button(
            sector.isBeingPlayed() ? "Resume" :
            sector.hasBase() ? "Enter sector" :
            "Land",
            Icon.play,
            () -> land(sector)
        ).width(220f).height(50f)
        .disabled(!canLand(sector));
    }

    private boolean isLocked(Sector sector){

        return sector.preset != null &&
            sector.preset.requireUnlock &&
            sector.preset.locked() &&
            !sector.hasBase();
    }

    private boolean canLand(Sector sector){

        if(sector == null) return false;

        if(sector.isBeingPlayed() || sector.hasBase())
            return true;

        if(isLocked(sector)) return false;
        if(sector.isShielded()) return false;
        if(sector.planet.generator == null) return false;

        if(sector.id == sector.planet.startSector)
            return true;

        return sector.planet.generator.allowLanding(sector);
    }

    private void land(Sector sector){

        if(!canLand(sector)) return;

        Planet target = planet;

        dialog.hide();

        PlanetDialog planetDialog = Vars.ui.planet;

        planetDialog.show();
        planetDialog.viewPlanet(target, false);
        planetDialog.selectSector(sector);

        try{
            java.lang.reflect.Method method =
                PlanetDialog.class.getDeclaredMethod("playSelected");

            method.setAccessible(true);
            method.invoke(planetDialog);

        }catch(Exception e){
            Vars.ui.showException(e);
        }
    }
}
