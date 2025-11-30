package hunternif.mc.atlas.client.gui;

import hunternif.mc.atlas.AntiqueAtlasItem;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.client.*;
import hunternif.mc.atlas.client.gui.core.*;
import hunternif.mc.atlas.core.DimensionData;
import hunternif.mc.atlas.marker.DimensionMarkersData;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.marker.MarkerTextureMap;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.util.*;
import net.minecraft.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class GuiAtlas extends GuiComponent {
    public static final int WIDTH = 310;
    public static final int HEIGHT = 218;
    private static final int CONTENT_X = 17;
    private static final int CONTENT_Y = 11;
    private static final int MAP_WIDTH = 276;
    private static final int MAP_HEIGHT = 194;
    private static final float PLAYER_ROTATION_STEPS = 16.0f;
    private static final int PLAYER_ICON_WIDTH = 7;
    private static final int PLAYER_ICON_HEIGHT = 8;
    public static final int MARKER_SIZE = 32;
    private static final int MARKER_RADIUS = 7;
    private static final double MAX_SCALE = 4.0d;
    private static final double MIN_SCALE = 0.03125d;
    private final GuiArrowButton btnUp;
    private final GuiArrowButton btnDown;
    private final GuiArrowButton btnLeft;
    private final GuiArrowButton btnRight;
    private final GuiBookmarkButton btnExportPng;
    private final GuiBookmarkButton btnMarker;
    private final GuiBookmarkButton btnDelMarker;
    /** Button for showing/hiding all markers. */
    private final GuiBookmarkButton btnShowMarkers;
    private final GuiPositionButton btnPosition;
    private static final int BUTTON_PAUSE = 8;
    private int dragMouseX;
    private int dragMouseY;
    private int dragMapOffsetX;
    private int dragMapOffsetY;
    private int mapOffsetX;
    private int mapOffsetY;
    private boolean followPlayer;
    private int tileHalfSize;
    private int tile2ChunkScale;
    private DimensionMarkersData localMarkersData;
    private DimensionMarkersData globalMarkersData;
    private Marker toDelete;
    private EntityPlayer player;
    private ItemStack stack;
    private DimensionData biomeData;
    private int screenScale;
    public static int navigateStep = 24;
    private static final double MIN_SCALE_THRESHOLD = 0.5d;
    private static double mapScale = MIN_SCALE_THRESHOLD;
    private boolean DEBUG_RENDERING = false;
    private long[] renderTimes = new long[30];
    private int renderTimesIndex = 0;
    private final GuiStates state = new GuiStates();
    private final GuiStates.IState NORMAL = new GuiStates.SimpleState();
    /** If on, all markers as well as the player icon are hidden. */
    private final GuiStates.IState HIDING_MARKERS = new GuiStates.IState() {
        @Override
        public void onEnterState() {
            // Set the button as not selected so that it can be clicked again:
            btnShowMarkers.setSelected(false);
            btnShowMarkers.setTitle(I18n.getString("gui.antiqueatlas.showMarkers"));
            btnShowMarkers.setIconTexture(Textures.ICON_SHOW_MARKERS);
        }
        @Override
        public void onExitState() {
            btnShowMarkers.setSelected(false);
            btnShowMarkers.setTitle(I18n.getString("gui.antiqueatlas.hideMarkers"));
            btnShowMarkers.setIconTexture(Textures.ICON_HIDE_MARKERS);
        }
    };
    private final GuiStates.IState PLACING_MARKER = new GuiStates.IState() {
        @Override
        public void onEnterState() {
            GuiAtlas.this.btnMarker.setSelected(true);
        }

        @Override
        public void onExitState() {
            GuiAtlas.this.btnMarker.setSelected(false);
        }
    };
    private final GuiStates.IState DELETING_MARKER = new GuiStates.IState() {
        @Override
        public void onEnterState() {
            GuiAtlas.this.mc.mouseHelper.grabMouseCursor();
            GuiAtlas.this.addChild(GuiAtlas.this.eraser);
            GuiAtlas.this.btnDelMarker.setSelected(true);
        }

        @Override
        public void onExitState() {
            GuiAtlas.this.mc.mouseHelper.ungrabMouseCursor();
            GuiAtlas.this.removeChild(GuiAtlas.this.eraser);
            GuiAtlas.this.btnDelMarker.setSelected(false);
        }
    };
    private final GuiCursor eraser = new GuiCursor();
    private final GuiStates.IState EXPORTING_IMAGE = new GuiStates.IState() {
        @Override
        public void onEnterState() {
            GuiAtlas.this.btnExportPng.setSelected(true);
        }

        @Override
        public void onExitState() {
            GuiAtlas.this.btnExportPng.setSelected(false);
        }
    };
    private GuiComponentButton selectedButton = null;
    private long timeButtonPressed = 0;
    private boolean isDragging = false;
    private GuiScaleBar scaleBar = new GuiScaleBar();
    private GuiMarkerFinalizer markerFinalizer = new GuiMarkerFinalizer();
    private GuiBlinkingMarker blinkingIcon = new GuiBlinkingMarker();
    private ProgressBarOverlay progressBar = new ProgressBarOverlay(100, 2);

    @SuppressWarnings("rawtypes")
    public GuiAtlas() {
        setSize(WIDTH, HEIGHT);
        setMapScale(0.5);
        followPlayer = true;
        setInterceptKeyboard(false);

        btnUp = GuiArrowButton.up();
        addChild(btnUp).offsetGuiCoords(148, 10);
        btnDown = GuiArrowButton.down();
        addChild(btnDown).offsetGuiCoords(148, 194);
        btnLeft = GuiArrowButton.left();
        addChild(btnLeft).offsetGuiCoords(15, 100);
        btnRight = GuiArrowButton.right();
        addChild(btnRight).offsetGuiCoords(283, 100);
        btnPosition = new GuiPositionButton();
        btnPosition.setEnabled(!followPlayer);
        addChild(btnPosition).offsetGuiCoords(283, 194);
        IButtonListener positionListener = new IButtonListener() {
            @Override
            public void onClick(GuiComponentButton button) {
                selectedButton = button;
                if (button.equals(btnPosition)) {
                    followPlayer = true;
                    btnPosition.setEnabled(false);
                } else {
                    // Navigate once, before enabling pause:
                    navigateByButton(selectedButton);
                    timeButtonPressed = player.worldObj.getTotalWorldTime();
                }
            }
        };
        btnUp.addListener(positionListener);
        btnDown.addListener(positionListener);
        btnLeft.addListener(positionListener);
        btnRight.addListener(positionListener);
        btnPosition.addListener(positionListener);

        btnExportPng = new GuiBookmarkButton(1, Textures.ICON_EXPORT, I18n.getString("gui.antiqueatlas.exportImage"));
        addChild(btnExportPng).offsetGuiCoords(300, 75);
        btnExportPng.addListener((IButtonListener<GuiBookmarkButton>) button -> {
            progressBar.reset();
            if (stack != null) {
                new Thread(() -> exportImage(stack.copy())).start();
            }
        });

        btnMarker = new GuiBookmarkButton(0, Textures.ICON_ADD_MARKER, I18n.getString("gui.antiqueatlas.addMarker"));
        addChild(btnMarker).offsetGuiCoords(300, 14);
        btnMarker.addListener(button -> {
            if (stack != null) {
                if (state.is(PLACING_MARKER)) {
                    selectedButton = null;
                    state.switchTo(NORMAL);
                } else {
                    selectedButton = button;
                    state.switchTo(PLACING_MARKER);
                }
            }
        });
        btnDelMarker = new GuiBookmarkButton(2, Textures.ICON_DELETE_MARKER, I18n.getString("gui.antiqueatlas.delMarker"));
        addChild(btnDelMarker).offsetGuiCoords(300, 33);
        btnDelMarker.addListener(button -> {
            if (stack != null) {
                if (state.is(DELETING_MARKER)) {
                    selectedButton = null;
                    state.switchTo(NORMAL);
                } else {
                    selectedButton = button;
                    state.switchTo(DELETING_MARKER);
                }
            }
        });
        btnShowMarkers = new GuiBookmarkButton(3, Textures.ICON_HIDE_MARKERS, I18n.getString("gui.antiqueatlas.hideMarkers"));
        addChild(btnShowMarkers).offsetGuiCoords(300, 52);
        btnShowMarkers.addListener(button -> {
            if (stack != null) {
                selectedButton = null;
                state.switchTo(state.is(HIDING_MARKERS) ? NORMAL : HIDING_MARKERS);
            }
        });

        addChild(scaleBar).offsetGuiCoords(20, 198);
        scaleBar.setMapScale(1);

        markerFinalizer.addListener(blinkingIcon);

        eraser.setTexture(Textures.ERASER, 12, 14, 2, 11);
    }

    public GuiAtlas setAtlasItemStack(ItemStack stack) {
        this.player = Minecraft.getMinecraft().thePlayer;
        this.stack = stack;
        updateAtlasData();
        return this;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.state.switchTo(this.NORMAL);
        Keyboard.enableRepeatEvents(true);
        this.screenScale = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight).getScaleFactor();
        setCentered();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseState) {
        super.mouseClicked(mouseX, mouseY, mouseState);
        if (state.is(EXPORTING_IMAGE)) {
            return; // Don't remove the progress bar.
        }

        // If clicked on the map, start dragging
        int mapX = (width - MAP_WIDTH)/2;
        int mapY = (height - MAP_HEIGHT)/2;
        boolean isMouseOverMap = mouseX >= mapX && mouseX <= mapX + MAP_WIDTH &&
                mouseY >= mapY && mouseY <= mapY + MAP_HEIGHT;
        if (!state.is(NORMAL) && !state.is(HIDING_MARKERS)) {
            if (state.is(PLACING_MARKER) // If clicked on the map, place marker:
                    && isMouseOverMap && mouseState == 0 /* left click */) {
                markerFinalizer.setMarkerData(player.worldObj,
                        stack.getItemDamage(), player.dimension,
                        screenXToWorldX(mouseX), screenYToWorldZ(mouseY));
                addChild(markerFinalizer);

                blinkingIcon.setTexture(MarkerTextureMap.instance()
                                .getTexture(markerFinalizer.selectedType),
                        MARKER_SIZE, MARKER_SIZE);
                addChildBehind(markerFinalizer, blinkingIcon)
                        .setRelativeCoords(mouseX - getGuiX() - MARKER_SIZE/2,
                                mouseY - getGuiY() - MARKER_SIZE/2);

                // Need to intercept keyboard events to type in the label:
                setInterceptKeyboard(true);

                // Un-press all keys to prevent player from walking infinitely:
                KeyBinding.unPressAllKeys();

            } else if (state.is(DELETING_MARKER) // If clicked on a marker, delete it:
                    && toDelete != null && isMouseOverMap && mouseState == 0) {
                AtlasAPI.getMarkerAPI().deleteMarker(player.worldObj,
                        stack.getItemDamage(), toDelete.getId());
            }
            state.switchTo(NORMAL);
        } else if (isMouseOverMap && selectedButton == null) {
            isDragging = true;
            dragMouseX = mouseX;
            dragMouseY = mouseY;
            dragMapOffsetX = mapOffsetX;
            dragMapOffsetY = mapOffsetY;
        }
    }

    /** Opens a dialog window to select which file to save to, then performs
     * rendering of the map of current dimension into a PNG image. */
    private void exportImage(ItemStack stack) {
        boolean showMarkers = !state.is(HIDING_MARKERS);
        state.switchTo(EXPORTING_IMAGE);
        // Default file name is "Atlas <N>.png"
        File file = ExportImageUtil.selectPngFileToSave("Atlas " + stack.getItemDamage(), progressBar);
        if (file != null) {
            try {
                Log.info("Exporting image from Atlas #%d to file %s", stack.getItemDamage(), file.getAbsolutePath());
                ExportImageUtil.exportPngImage(biomeData, globalMarkersData, localMarkersData, file, progressBar, showMarkers);
                Log.info("Finished exporting image");
            } catch (OutOfMemoryError e) {
                Log.error(e, "Image is too large");
                progressBar.setStatusString(I18n.getString("gui.antiqueatlas.export.tooLarge"));
                return; //Don't switch to normal state yet so that the error message can be read.
            }
        }
        state.switchTo(showMarkers ? NORMAL : HIDING_MARKERS);
    }

    @Override
    public void handleKeyboardInput() {
        super.handleKeyboardInput();
        if (Keyboard.getEventKeyState()) {
            int key = Keyboard.getEventKey();
            if (key == 200) {
                navigateMap(0, navigateStep);
                return;
            }
            if (key == 208) {
                navigateMap(0, -navigateStep);
                return;
            }
            if (key == 203) {
                navigateMap(navigateStep, 0);
                return;
            }
            if (key == 205) {
                navigateMap(-navigateStep, 0);
                return;
            }
            if (key == 78 || key == 13) {
                setMapScale(mapScale * 2.0d);
            } else if (key == 74 || key == 12) {
                setMapScale(mapScale / 2.0d);
            }
        }
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        int wheelMove = Mouse.getEventDWheel();
        if (wheelMove != 0) {
            setMapScale(mapScale * Math.pow(2.0d, wheelMove > 0 ? 1 : -1));
        }
    }

    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int eventButton) {
        super.mouseMovedOrUp(mouseX, mouseY, eventButton);
        if (eventButton != -1) {
            this.selectedButton = null;
            this.isDragging = false;
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int lastMouseButton, long timeSinceMouseClick) {
        super.mouseClickMove(mouseX, mouseY, lastMouseButton, timeSinceMouseClick);
        if (this.isDragging) {
            this.followPlayer = false;
            this.btnPosition.setEnabled(true);
            this.mapOffsetX = (this.dragMapOffsetX + mouseX) - this.dragMouseX;
            this.mapOffsetY = (this.dragMapOffsetY + mouseY) - this.dragMouseY;
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (this.player == null) {
            return;
        }
        if (this.followPlayer) {
            this.mapOffsetX = (int) ((-this.player.posX) * mapScale);
            this.mapOffsetY = (int) ((-this.player.posZ) * mapScale);
        }
        if (this.player.worldObj.getTotalWorldTime() > this.timeButtonPressed + 8) {
            navigateByButton(this.selectedButton);
        }
        updateAtlasData();
    }

    private void updateAtlasData() {
        this.biomeData = AntiqueAtlasItem.itemAtlas.getAtlasData(this.stack, this.player.worldObj).getDimensionData(this.player.dimension);
        this.globalMarkersData = AntiqueAtlasMod.globalMarkersData.getData().getMarkersDataInDimension(this.player.dimension);
        MarkersData markersData = AntiqueAtlasItem.itemAtlas.getMarkersData(this.stack, this.player.worldObj);
        if (markersData != null) {
            this.localMarkersData = markersData.getMarkersDataInDimension(this.player.dimension);
        } else {
            this.localMarkersData = null;
        }
    }

    public void navigateByButton(GuiComponentButton btn) {
        if (btn == null) {
            return;
        }
        if (btn.equals(this.btnUp)) {
            navigateMap(0, navigateStep);
            return;
        }
        if (btn.equals(this.btnDown)) {
            navigateMap(0, -navigateStep);
        } else if (btn.equals(this.btnLeft)) {
            navigateMap(navigateStep, 0);
        } else if (btn.equals(this.btnRight)) {
            navigateMap(-navigateStep, 0);
        }
    }

    public void navigateMap(int dx, int dy) {
        this.mapOffsetX += dx;
        this.mapOffsetY += dy;
        this.followPlayer = false;
        this.btnPosition.setEnabled(true);
    }

    public void setMapScale(double scale) {
        double oldScale = mapScale;
        mapScale = scale;
        if (mapScale < MIN_SCALE) {
            mapScale = MIN_SCALE;
        }
        if (mapScale > MAX_SCALE) {
            mapScale = MAX_SCALE;
        }
        if (mapScale >= MIN_SCALE_THRESHOLD) {
            this.tileHalfSize = (int) Math.round(8.0d * mapScale);
            this.tile2ChunkScale = 1;
        } else {
            this.tileHalfSize = (int) Math.round(MAX_SCALE);
            this.tile2ChunkScale = (int) Math.round(MIN_SCALE_THRESHOLD / mapScale);
        }
        this.scaleBar.setMapScale(mapScale * 2.0d);
        this.mapOffsetX = (int) (this.mapOffsetX * (mapScale / oldScale));
        this.mapOffsetY = (int) (this.mapOffsetY * (mapScale / oldScale));
        this.dragMapOffsetX = (int) (this.dragMapOffsetX * (mapScale / oldScale));
        this.dragMapOffsetY = (int) (this.dragMapOffsetY * (mapScale / oldScale));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float par3) {
        if (DEBUG_RENDERING) {
            renderTimes[renderTimesIndex++] = System.currentTimeMillis();
            if (renderTimesIndex == renderTimes.length) {
                renderTimesIndex = 0;
                double elapsed = 0;
                for (int i = 0; i < renderTimes.length - 1; i++) {
                    elapsed += renderTimes[i + 1] - renderTimes[i];
                }
                Log.info("GuiAtlas avg. render time: %.3f", elapsed / renderTimes.length);
            }
        }

        GL11.glColor4f(1, 1, 1, 1);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0); // So light detail on tiles is visible
        AtlasRenderHelper.drawFullTexture(Textures.BOOK, getGuiX(), getGuiY(), WIDTH, HEIGHT);

        if (stack == null || biomeData == null) return;


        if (state.is(DELETING_MARKER)) {
            GL11.glColor4f(1, 1, 1, 0.5f);
        }
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((getGuiX() + CONTENT_X)*screenScale,
                mc.displayHeight - (getGuiY() + CONTENT_Y + MAP_HEIGHT)*screenScale,
                MAP_WIDTH*screenScale, MAP_HEIGHT*screenScale);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        // Find chunk coordinates of the top left corner of the map.
        // The 'roundToBase' is required so that when the map scales below the
        // threshold the tiles don't change when map position changes slightly.
        // The +-2 at the end provide margin so that tiles at the edges of
        // the page have their stitched texture correct.
        int mapStartX = MathUtil.roundToBase((int)Math.floor(-((double)MAP_WIDTH/2d + mapOffsetX + 2*tileHalfSize) / mapScale / 16d), tile2ChunkScale);
        int mapStartZ = MathUtil.roundToBase((int)Math.floor(-((double)MAP_HEIGHT/2d + mapOffsetY + 2*tileHalfSize) / mapScale / 16d), tile2ChunkScale);
        int mapEndX = MathUtil.roundToBase((int)Math.ceil(((double)MAP_WIDTH/2d - mapOffsetX + 2*tileHalfSize) / mapScale / 16d), tile2ChunkScale);
        int mapEndZ = MathUtil.roundToBase((int)Math.ceil(((double)MAP_HEIGHT/2d - mapOffsetY + 2*tileHalfSize) / mapScale / 16d), tile2ChunkScale);
        int mapStartScreenX = getGuiX() + WIDTH/2 + (int)((mapStartX << 4) * mapScale) + mapOffsetX;
        int mapStartScreenY = getGuiY() + HEIGHT/2 + (int)((mapStartZ << 4) * mapScale) + mapOffsetY;

        TileRenderIterator iter = new TileRenderIterator(biomeData);
        iter.setScope(new Rect().setOrigin(mapStartX, mapStartZ).
                set(mapStartX, mapStartZ, mapEndX, mapEndZ));
        iter.setStep(tile2ChunkScale);
        while (iter.hasNext()) {
            SubTileQuartet subtiles = iter.next();
            for (SubTile subtile : subtiles) {
                if (subtile == null || subtile.tile == null) continue;
                AtlasRenderHelper.drawAutotileCorner(
                        BiomeTextureMap.instance().getTexture(subtile.tile),
                        mapStartScreenX + subtile.x * tileHalfSize,
                        mapStartScreenY + subtile.y * tileHalfSize,
                        subtile.getTextureU(), subtile.getTextureV(), tileHalfSize);
            }
        }

        if (!state.is(HIDING_MARKERS)) {
            int markersStartX = MathUtil.roundToBase(mapStartX, MarkersData.CHUNK_STEP) / MarkersData.CHUNK_STEP - 1;
            int markersStartZ = MathUtil.roundToBase(mapStartZ, MarkersData.CHUNK_STEP) / MarkersData.CHUNK_STEP - 1;
            int markersEndX = MathUtil.roundToBase(mapEndX, MarkersData.CHUNK_STEP) / MarkersData.CHUNK_STEP + 1;
            int markersEndZ = MathUtil.roundToBase(mapEndZ, MarkersData.CHUNK_STEP) / MarkersData.CHUNK_STEP + 1;
            double iconScale = getIconScale();

            // Draw global markers:
            for (int x = markersStartX; x <= markersEndX; x++) {
                for (int z = markersStartZ; z <= markersEndZ; z++) {
                    List<Marker> markers = globalMarkersData.getMarkersAtChunk(x, z);
                    if (markers == null) continue;
                    for (Marker marker : markers) {
                        renderMarker(marker, iconScale);
                    }
                }
            }

            // Draw local markers:
            if (localMarkersData != null) {
                for (int x = markersStartX; x <= markersEndX; x++) {
                    for (int z = markersStartZ; z <= markersEndZ; z++) {
                        List<Marker> markers = localMarkersData.getMarkersAtChunk(x, z);
                        if (markers == null) continue;
                        for (Marker marker : markers) {
                            renderMarker(marker, iconScale);
                        }
                    }
                }
            }
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        // Overlay the frame so that edges of the map are smooth:
        GL11.glColor4f(1, 1, 1, 1);
        AtlasRenderHelper.drawFullTexture(Textures.BOOK_FRAME, getGuiX(), getGuiY(), WIDTH, HEIGHT);
        double iconScale = getIconScale();

        // Draw player icon:
        if (!state.is(HIDING_MARKERS)) {
            // How much the player has moved from the top left corner of the map, in pixels:
            int playerOffsetX = (int)(player.posX * mapScale) + mapOffsetX;
            int playerOffsetZ = (int)(player.posZ * mapScale) + mapOffsetY;
            if (playerOffsetX < -MAP_WIDTH/2) playerOffsetX = -MAP_WIDTH/2;
            if (playerOffsetX > MAP_WIDTH/2) playerOffsetX = MAP_WIDTH/2;
            if (playerOffsetZ < -MAP_HEIGHT/2) playerOffsetZ = -MAP_HEIGHT/2;
            if (playerOffsetZ > MAP_HEIGHT/2 - 2) playerOffsetZ = MAP_HEIGHT/2 - 2;
            // Draw the icon:
            GL11.glColor4f(1, 1, 1, state.is(PLACING_MARKER) ? 0.5f : 1);
            GL11.glPushMatrix();
            GL11.glTranslated(getGuiX() + WIDTH/2 + playerOffsetX, getGuiY() + HEIGHT/2 + playerOffsetZ, 0);
            float playerRotation = (float) Math.round(player.rotationYaw / 360f * PLAYER_ROTATION_STEPS) / PLAYER_ROTATION_STEPS * 360f;
            GL11.glRotatef(180 + playerRotation, 0, 0, 1);
            GL11.glTranslated(-PLAYER_ICON_WIDTH/2*iconScale, -PLAYER_ICON_HEIGHT/2*iconScale, 0);
            AtlasRenderHelper.drawFullTexture(Textures.PLAYER, 0, 0,
                    (int)Math.round(PLAYER_ICON_WIDTH*iconScale), (int)Math.round(PLAYER_ICON_HEIGHT*iconScale));
            GL11.glPopMatrix();
            GL11.glColor4f(1, 1, 1, 1);
        }

        // Draw buttons:
        super.drawScreen(mouseX, mouseY, par3);

        // Draw the semi-transparent marker attached to the cursor when placing a new marker:
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        if (state.is(PLACING_MARKER)) {
            GL11.glColor4f(1, 1, 1, 0.5f);
            AtlasRenderHelper.drawFullTexture(
                    MarkerTextureMap.instance().getTexture(markerFinalizer.selectedType),
                    mouseX - MARKER_SIZE/2*iconScale, mouseY - MARKER_SIZE/2*iconScale,
                    (int)Math.round(MARKER_SIZE*iconScale), (int)Math.round(MARKER_SIZE*iconScale));
            GL11.glColor4f(1, 1, 1, 1);
        }

        // Draw progress overlay:
        if (state.is(EXPORTING_IMAGE)) {
            drawDefaultBackground();
            progressBar.draw((width - 100)/2, height/2 - 34);
        }
    }

    private void renderMarker(Marker marker, double scale) {
        int markerX = worldXToScreenX(marker.getX());
        int markerY = worldZToScreenY(marker.getZ());
        if (!marker.isVisibleAhead() &&
                !biomeData.hasTileAt(marker.getChunkX(), marker.getChunkZ())) {
            return;
        }
        boolean mouseIsOverMarker = isMouseInRadius(markerX, markerY, (int)Math.ceil(MARKER_RADIUS*scale));
        if (state.is(PLACING_MARKER)) {
            GL11.glColor4f(1, 1, 1, 0.5f);
        } else if (state.is(DELETING_MARKER)) {
            if (marker.isGlobal()) {
                GL11.glColor4f(1, 1, 1, 0.5f);
            } else {
                if (mouseIsOverMarker) {
                    GL11.glColor4f(0.5f, 0.5f, 0.5f, 1);
                    toDelete = marker;
                } else {
                    GL11.glColor4f(1, 1, 1, 1);
                    if (toDelete == marker) {
                        toDelete = null;
                    }
                }
            }
        } else {
            GL11.glColor4f(1, 1, 1, 1);
        }
        AtlasRenderHelper.drawFullTexture(
                MarkerTextureMap.instance().getTexture(marker.getType()),
                markerX - (double)MARKER_SIZE/2*scale,
                markerY - (double)MARKER_SIZE/2*scale,
                (int)Math.round(MARKER_SIZE*scale), (int)Math.round(MARKER_SIZE*scale));
        if (isMouseOver && mouseIsOverMarker && marker.getLabel().length() > 0) {
            drawTooltip(Arrays.asList(marker.getLocalizedLabel()), mc.fontRenderer);
        }
    }

    @Override // net.minecraft.client.gui.GuiScreen
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override // net.minecraft.plugin.antiqueatlas.client.gui.core.GuiComponent, net.minecraft.client.gui.GuiScreen
    public void onGuiClosed() {
        super.onGuiClosed();
        removeChild(this.markerFinalizer);
        removeChild(this.blinkingIcon);
        Keyboard.enableRepeatEvents(false);
    }

    private int screenXToWorldX(int mouseX) {
        return (int) Math.round(((mouseX - (this.width / 2)) - this.mapOffsetX) / mapScale);
    }

    private int screenYToWorldZ(int mouseY) {
        return (int) Math.round(((mouseY - (this.height / 2)) - this.mapOffsetY) / mapScale);
    }

    private int worldXToScreenX(int x) {
        return (int) Math.round((x * mapScale) + (this.width / 2) + this.mapOffsetX);
    }

    private int worldZToScreenY(int z) {
        return (int) Math.round((z * mapScale) + (this.height / 2) + this.mapOffsetY);
    }

    @Override
    protected void onChildClosed(GuiComponent child) {
        if (child.equals(this.markerFinalizer)) {
            removeChild(this.blinkingIcon);
        }
    }

    /** Update all text labels to current localization. */
    public void updateL18n() {
        btnExportPng.setTitle(I18n.getString("gui.antiqueatlas.exportImage"));
        btnMarker.setTitle(I18n.getString("gui.antiqueatlas.addMarker"));
    }


    /** Returns the scale of markers and player icon at given mapScale. */
    private double getIconScale() {
        return AntiqueAtlasMod.settings.doScaleMarkers ? (mapScale < 0.5 ? 0.5 : mapScale > 1 ? 2 : 1) : 1;
    }
}
