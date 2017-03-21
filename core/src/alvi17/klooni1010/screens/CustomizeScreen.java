package alvi17.klooni1010.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import alvi17.klooni1010.Klooni;
import alvi17.klooni1010.Theme;
import alvi17.klooni1010.actors.MoneyBuyBand;
import alvi17.klooni1010.actors.SoftButton;
import alvi17.klooni1010.actors.ThemeCard;
import alvi17.klooni1010.game.GameLayout;

// Screen where the user can customize the look and feel of the game
class CustomizeScreen implements Screen {

    //region Members

    private Klooni game;
    private Stage stage;

    private final Screen lastScreen;

    //endregion

    //region Static members

    // As the examples show on the LibGdx wiki
    private static final float minDelta = 1/30f;

    //endregion

    //region Constructor

    CustomizeScreen(Klooni game, final Screen lastScreen) {
        final GameLayout layout = new GameLayout();

        this.game = game;
        this.lastScreen = lastScreen;
        stage = new Stage();

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        HorizontalGroup optionsGroup = new HorizontalGroup();
        optionsGroup.space(12);

        // Back to the previous screen
        final SoftButton backButton = new SoftButton(1, "back_texture");
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                goBack();
            }
        });
        optionsGroup.addActor(backButton);

        // Turn sound on/off
        final SoftButton soundButton = new SoftButton(
                0, Klooni.soundsEnabled() ? "sound_on_texture" : "sound_off_texture");

        soundButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                final boolean enabled = Klooni.toggleSound();
                soundButton.image = CustomizeScreen.this.game.skin.getDrawable(
                        enabled ? "sound_on_texture" : "sound_off_texture");
            }
        });
        optionsGroup.addActor(soundButton);

        // Snap to grid on/off
        final SoftButton snapButton = new SoftButton(
                2, Klooni.shouldSnapToGrid() ? "snap_on_texture" : "snap_off_texture");

        snapButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                final boolean shouldSnap = Klooni.toggleSnapToGrid();
                snapButton.image = CustomizeScreen.this.game.skin.getDrawable(
                        shouldSnap ? "snap_on_texture" : "snap_off_texture");
            }
        });
        optionsGroup.addActor(snapButton);

        // Issues
//        final SoftButton issuesButton = new SoftButton(3, "issues_texture");
//        issuesButton.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                Gdx.net.openURI("https://github.com/LonamiWebs/Klooni1010/issues");
//            }
//        });
//        optionsGroup.addActor(issuesButton);

        // Website
        final SoftButton webButton = new SoftButton(2, "web_texture");
        webButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.net.openURI("https://play.google.com/store/apps/developer?id=MRA17");
            }
        });
        optionsGroup.addActor(webButton);

        table.add(new ScrollPane(optionsGroup)).pad(20, 4, 12, 4);

        // Load all the available themes
        final MoneyBuyBand buyBand = new MoneyBuyBand(game);

        table.row();
        final VerticalGroup themesGroup = new VerticalGroup();
        for (Theme theme : Theme.getThemes()) {
            final ThemeCard card = new ThemeCard(game, layout, theme);
            card.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if (Klooni.isThemeBought(card.theme))
                        card.use();
                    else
                        buyBand.askBuy(card);

                    for (Actor a : themesGroup.getChildren()) {
                        ThemeCard c = (ThemeCard)a;
                        c.usedThemeUpdated();
                    }
                    return true;
                }
            });
            themesGroup.addActor(card);
        }

        themesGroup.space(8);
        table.add(new ScrollPane(themesGroup)).expand().fill();

        // Show the current money row
        table.row();
        table.add(buyBand).expandX().fillX();
    }

    //endregion

    //region Private methods

    private void goBack() {
        CustomizeScreen.this.game.transitionTo(lastScreen);
    }

    //endregion

    //region Public methods

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Klooni.theme.glClearBackground();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), minDelta));
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            goBack();
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    //endregion

    //region Empty methods

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    //endregion
}
