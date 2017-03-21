package alvi17.klooni1010.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import alvi17.klooni1010.Klooni;
import alvi17.klooni1010.actors.Band;
import alvi17.klooni1010.actors.SoftButton;
import alvi17.klooni1010.game.BaseScorer;
import alvi17.klooni1010.game.GameLayout;

// The pause stage is not a whole screen but rather a menu
// which can be overlaid on top of another screen
class PauseMenuStage extends Stage {

    //region Members

    private InputProcessor lastInputProcessor;
    private boolean shown;
    private boolean hiding;

    private final ShapeRenderer shapeRenderer;

    private final Band band;
    private final BaseScorer scorer;

    //endregion

    //region Constructor

    // We need the score to save the maximum score if a new record was beaten
    PauseMenuStage(final GameLayout layout, final Klooni game, final BaseScorer scorer, final int gameMode) {
        this.scorer = scorer;

        shapeRenderer = new ShapeRenderer(20); // 20 vertex seems to be enough for a rectangle

        Table table = new Table();
        table.setFillParent(true);
        addActor(table);

        // Current and maximum score band.
        // Do not add it to the table not to over-complicate things.
        band = new Band(game, layout, this.scorer);
        addActor(band);

        // Home screen button
        final SoftButton homeButton = new SoftButton(3, "home_texture");
        table.add(homeButton).space(16);

        homeButton.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                game.transitionTo(new MainMenuScreen(game));
            }
        });

        // Replay button
        final SoftButton replayButton = new SoftButton(0, "replay_texture");
        table.add(replayButton).space(16);

        replayButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // false, don't load the saved game state; we do want to replay
                game.transitionTo(new GameScreen(game, gameMode, false));
            }
        });

        table.row();

        // Palette button (buy colors)
        final SoftButton paletteButton = new SoftButton(1, "palette_texture");
        table.add(paletteButton).space(16);

        paletteButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Don't dispose because then it needs to take us to the previous screen
                game.transitionTo(new CustomizeScreen(game, game.getScreen()), false);
            }
        });

        // Continue playing OR share (if game over) button
        // TODO Enable both actions for this button? Or leave play?
        final SoftButton playButton = new SoftButton(2, "play_texture");
        table.add(playButton).space(16);

        playButton.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                hide();
            }
        });
    }

    //endregion

    //region Private methods

    // Hides the pause menu, setting back the previous input processor
    private void hide() {
        shown = false;
        hiding = true;
        Gdx.input.setInputProcessor(lastInputProcessor);

        addAction(Actions.sequence(
                Actions.moveTo(0, Gdx.graphics.getHeight(), 0.5f, Interpolation.swingIn),
                new RunnableAction() {
                    @Override
                    public void run() {
                        hiding = false;
                    }
                }
        ));
        scorer.resume();
    }

    //endregion

    //region Package local methods

    // Shows the pause menu, indicating whether it's game over or not
    void show() {
        scorer.pause();
        scorer.saveScore();

        // Save the last input processor so then we can return the handle to it
        lastInputProcessor = Gdx.input.getInputProcessor();
        Gdx.input.setInputProcessor(this);
        shown = true;
        hiding = false;

        addAction(Actions.moveTo(0, Gdx.graphics.getHeight()));
        addAction(Actions.moveTo(0, 0, 0.75f, Interpolation.swingOut));
    }

    void showGameOver(final String gameOverReason) {
        band.setMessage(gameOverReason);
        show();
    }

    boolean isShown() {
        return shown;
    }

    boolean isHiding() {
        return hiding;
    }

    //endregion

    //region Public methods

    @Override
    public void draw() {
        if (shown) {
            // Draw an overlay rectangle with not all the opacity
            // This is the only place where ShapeRenderer is OK because the batch hasn't started
            Gdx.gl.glEnable(GL20.GL_BLEND);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(1f, 1f, 1f, 0.3f);
            shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            shapeRenderer.end();
        }

        super.draw();
    }

    @Override
    public boolean keyUp(int keyCode) {
        if (keyCode == Input.Keys.P || keyCode == Input.Keys.BACK) // Pause
            hide();

        return super.keyUp(keyCode);
    }

    //endregion
}
