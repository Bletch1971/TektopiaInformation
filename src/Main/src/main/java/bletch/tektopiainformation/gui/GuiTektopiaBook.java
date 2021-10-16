package bletch.tektopiainformation.gui;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import org.lwjgl.input.Mouse;
import bletch.common.utils.Font;
import bletch.common.utils.LoggerUtils;
import bletch.common.utils.RenderUtils;
import bletch.common.utils.StringUtils;
import bletch.common.utils.TextUtils;
import bletch.tektopiainformation.core.ModDetails;
import bletch.tektopiainformation.core.ModSounds;
import bletch.tektopiainformation.enums.GuiMapMarkerType;
import bletch.tektopiainformation.enums.GuiMapQuadrant;
import bletch.tektopiainformation.enums.GuiPageType;
import bletch.tektopiainformation.network.data.EconomyData;
import bletch.tektopiainformation.network.data.EnemiesData;
import bletch.tektopiainformation.network.data.EnemyData;
import bletch.tektopiainformation.network.data.HomeData;
import bletch.tektopiainformation.network.data.HomesData;
import bletch.tektopiainformation.network.data.VisitorData;
import bletch.tektopiainformation.network.data.VisitorsData;
import bletch.tektopiainformation.network.data.ResidentData;
import bletch.tektopiainformation.network.data.ResidentsData;
import bletch.tektopiainformation.network.data.StructureData;
import bletch.tektopiainformation.network.data.StructuresData;
import bletch.tektopiainformation.network.data.VillageData;
import bletch.tektopiainformation.utils.TektopiaUtils;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.structures.VillageStructureType;

public class GuiTektopiaBook extends GuiScreen {

	private static final String BUTTON_KEY_BACK = "back";
	private static final String BUTTON_KEY_PREVIOUSPAGE = "previouspage";
	private static final String BUTTON_KEY_NEXTPAGE = "nextpage";
	private static final String BUTTON_KEY_STARTBOOK = "startbook";
	private static final String BUTTON_KEY_ENDBOOK = "endbook";
	private static final String BUTTON_KEY_CLOSE = "close";
	private static final String BUTTON_KEY_AIFILTER = "aifilter";

	private static final String BUTTON_KEY_PREVIOUSSUBPAGE = "previoussubpage";
	private static final String BUTTON_KEY_NEXTSUBPAGE = "nextsubpage";

	private static final String BUTTON_KEY_MAPLINK = "mapLink";
	private static final String BUTTON_KEY_HOMELINK = "homeLink";
	private static final String BUTTON_KEY_PROFESSIONLINK = "professionLink";
	private static final String BUTTON_KEY_RESIDENTLINK = "residentLink";
	private static final String BUTTON_KEY_STATISTICLINK = "statisticLink";
	private static final String BUTTON_KEY_STRUCTURELINK = "structureLink";
	private static final String BUTTON_KEY_VISITORLINK = "visitorLink";

	private static final String BUTTON_KEY_SHOWMAPBOUNDARIES = "showmapboundaries";
	private static final String BUTTON_KEY_SHOWMAPENEMIES = "showmapenemies";
	private static final String BUTTON_KEY_SHOWMAPHOMES = "showmaphomes";
	private static final String BUTTON_KEY_SHOWMAPVISITORS = "showmapvisitors";
	private static final String BUTTON_KEY_SHOWMAPPLAYER = "showmapplayer";
	private static final String BUTTON_KEY_SHOWMAPRESIDENTS = "showmapresidents";
	private static final String BUTTON_KEY_SHOWMAPSTRUCTURES = "showmapstructures";
	private static final String BUTTON_KEY_SHOWMAPTOWNHALL = "showmaptownhall";

	private static final String BOOKMARK_KEY_ECONOMY = "economy";
	private static final String BOOKMARK_KEY_ENEMIES = "enemies";
	private static final String BOOKMARK_KEY_HOMES = "homes";
	private static final String BOOKMARK_KEY_MAP = "map";
	private static final String BOOKMARK_KEY_PROFESSIONS = "professions";
	private static final String BOOKMARK_KEY_RESIDENTS = "residents";
	private static final String BOOKMARK_KEY_STATISTICS = "statistics";
	private static final String BOOKMARK_KEY_STRUCTURES = "structures";
	private static final String BOOKMARK_KEY_VILLAGE = "village";
	private static final String BOOKMARK_KEY_VISITORS = "visitors";

	private static final String SUBPAGE_KEY_AIFILTER = "aifilter";

	private static final int BOOK_WIDTH = 512;
	private static final int BOOK_HEIGHT = 400;

	private static final int BOOKMARK_LEFT_X = -17;
	private static final int BOOKMARK_RIGHT_X = 488;
	private static final int BOOKMARK_TOP_Y = 16;	

	private static final int BOOKMARK_WIDTH = 35;
	private static final int BOOKMARK_HEIGHT = 26;
	private static final int BOOKMARK_SPACE_Y = 2;

	private static final int PAGE_LANDSCAPE_WIDTH = 480;
	private static final int PAGE_LANDSCAPE_HEIGHT = 384;
	private static final int PAGE_PORTRAIT_WIDTH = 384;
	private static final int PAGE_PORTRAIT_HEIGHT = 384;	

	private static final int MAP_MARKER_SIZE = 16;
	private static final float MAP_MARKER_SELECTED_SCALE = 1.5F;
	private static final int MAP_TOOLTIP_SIZE_STRUCTURE = 10;
	private static final int MAP_TOOLTIP_SIZE_ENTITY = 5;

	private static final int PROFESSION_WIDTH = 56;
	private static final int PROFESSION_HEIGHT = 90;

	private static final int PAGE_LEFTPAGE_LEFTMARGIN_X = 35;
	private static final int PAGE_LEFTPAGE_RIGHTMARGIN_X = 247;
	private static final int PAGE_LEFTPAGE_WIDTH = PAGE_LEFTPAGE_RIGHTMARGIN_X - PAGE_LEFTPAGE_LEFTMARGIN_X;
	private static final int PAGE_LEFTPAGE_CENTER_X = PAGE_LEFTPAGE_LEFTMARGIN_X + (PAGE_LEFTPAGE_WIDTH / 2);
	private static final int PAGE_LEFTPAGE_LEFTCENTER_X = PAGE_LEFTPAGE_LEFTMARGIN_X + (PAGE_LEFTPAGE_WIDTH / 4);

	private static final int PAGE_RIGHTPAGE_LEFTMARGIN_X = 268;
	private static final int PAGE_RIGHTPAGE_RIGHTMARGIN_X = 480;
	private static final int PAGE_RIGHTPAGE_WIDTH = PAGE_RIGHTPAGE_RIGHTMARGIN_X - PAGE_RIGHTPAGE_LEFTMARGIN_X;
	private static final int PAGE_RIGHTPAGE_CENTER_X = PAGE_RIGHTPAGE_LEFTMARGIN_X + (PAGE_RIGHTPAGE_WIDTH / 2);
	private static final int PAGE_RIGHTPAGE_LEFTCENTER_X = PAGE_RIGHTPAGE_LEFTMARGIN_X + (PAGE_RIGHTPAGE_WIDTH / 4);

	private static final int PAGE_HEADER_Y = 20;
	private static final int PAGE_BODY_Y = 35;
	private static final int PAGE_FOOTER_Y = 335;

	private static final int MAP_PAGE_LEFT_X = 55;
	private static final int MAP_PAGE_TOP_Y = 70;
	private static final int MAP_PAGE_RIGHT_X = 455;
	private static final int MAP_PAGE_BOTTOM_Y = 310;
	private static final int MAP_PAGE_MIDDLE_X = MAP_PAGE_LEFT_X + (((MAP_PAGE_RIGHT_X - MAP_PAGE_LEFT_X) / 3) * 2);
	private static final int MAP_PAGE_MIDDLE_Y = MAP_PAGE_TOP_Y + ((MAP_PAGE_BOTTOM_Y - MAP_PAGE_TOP_Y) / 2);

	private static final int MAP_HEADER_Y = 45;
	//private static final int MAP_BODY_Y = 60;
	//private static final int MAP_FOOTER_Y = 310;

	private static final int MAP_AXIS_LENGTH_X = 120;
	private static final int MAP_AXIS_LENGTH_Y = 120;
	private static final int MAP_AXIS_INTERVAL_X = 10;
	private static final int MAP_AXIS_INTERVAL_Y = 10;
	private static final int MAP_AXIS_INTERVAL_X_COUNT = MAP_AXIS_LENGTH_X / MAP_AXIS_INTERVAL_X;
	private static final int MAP_AXIS_INTERVAL_Y_COUNT = MAP_AXIS_LENGTH_Y / MAP_AXIS_INTERVAL_Y;

	private static final int SUBPAGE_LEFT_X = 65;
	private static final int SUBPAGE_TOP_Y = 25;
	private static final int SUBPAGE_RIGHT_X = 320;
	private static final int SUBPAGE_BOTTOM_Y = 350;
	private static final int SUBPAGE_WIDTH = SUBPAGE_RIGHT_X - SUBPAGE_LEFT_X;
	private static final int SUBPAGE_CENTER_X = SUBPAGE_LEFT_X + (SUBPAGE_WIDTH / 2);
	//private static final int SUBPAGE_LEFTCENTER_X = SUBPAGE_LEFT_X + (SUBPAGE_WIDTH / 4);

	private static final int LABEL_TRAILINGSPACE_X = 10;

	private static final int LINE_SPACE_Y = 2;
	private static final int LINE_SPACE_Y_HEADER = 5;
	private static final int LINE_SPACE_Y_IMAGE = 6;

	private static final int STRUCTURETYPES_PER_PAGE = 24;
	private static final int STRUCTURES_PER_PAGE = 22;
	private static final int STRUCTUREOCCUPANTS_PER_PAGE = 15;
	private static final int HOMETYPES_PER_PAGE = 24;
	private static final int HOMES_PER_PAGE = 21;
	private static final int HOMEBEDS_PER_PAGE = 6;
	private static final int PROFESSIONTYPES_PER_PAGE = 24;
	private static final int PROFESSIONS_PER_PAGE = 22;
	private static final int RESIDENTS_PER_PAGE = 24;
	private static final int VISITORS_PER_PAGE = 24;
	private static final int VISITORVENDORLIST0_PER_PAGE = 8;
	private static final int VISITORVENDORLIST_PER_PAGE = 15;
	private static final int ENEMIES_PER_PAGE = 24;
	private static final int STATRESIDENTS_PER_PAGE = 20;
	private static final int STATSTRUCTURES_PER_PAGE = 20;
	private static final int RESIDENTVENDORLIST_PER_PAGE = 15;
	private static final int SALESHISTORY_PER_PAGE = 16;
	private static final int ADDITIONALPROFESSIONS_PER_PAGE = 4;
	private static final int AIFILTERLIST_PER_PAGE = 24;

	private static final ResourceLocation book = new ResourceLocation(ModDetails.MOD_ID, "textures/gui/gui_book.png");
	private static final ResourceLocation bookmarkLeft = new ResourceLocation(ModDetails.MOD_ID, "textures/gui/bookmark_left.png");
	private static final ResourceLocation bookmarkRight = new ResourceLocation(ModDetails.MOD_ID, "textures/gui/bookmark_right.png");
	private static final ResourceLocation buttonBack = new ResourceLocation(ModDetails.MOD_ID, "textures/gui/button_back.png");
	private static final ResourceLocation buttonPreviousPage = new ResourceLocation(ModDetails.MOD_ID, "textures/gui/button_previous.png");
	private static final ResourceLocation buttonNextPage = new ResourceLocation(ModDetails.MOD_ID, "textures/gui/button_next.png");
	private static final ResourceLocation buttonStartBook = new ResourceLocation(ModDetails.MOD_ID, "textures/gui/button_start.png");
	private static final ResourceLocation buttonEndBook = new ResourceLocation(ModDetails.MOD_ID, "textures/gui/button_end.png");
	private static final ResourceLocation buttonClose = new ResourceLocation(ModDetails.MOD_ID, "textures/gui/button_close.png");
	private static final ResourceLocation buttonAiFilter = new ResourceLocation(ModDetails.MOD_ID, "textures/gui/button_aifilter.png");
	private static final ResourceLocation page_landscape = new ResourceLocation(ModDetails.MOD_ID, "textures/gui/gui_paper_landscape.png");
	private static final ResourceLocation page_portrait = new ResourceLocation(ModDetails.MOD_ID, "textures/gui/gui_paper_portrait.png");
	private static final ResourceLocation mapMarkerTownHall = new ResourceLocation(ModDetails.MOD_ID, "textures/gui/map_marker_townhall.png");
	private static final ResourceLocation mapMarkerStructure = new ResourceLocation(ModDetails.MOD_ID, "textures/gui/map_marker_structure.png");
	private static final ResourceLocation mapMarkerHome = new ResourceLocation(ModDetails.MOD_ID, "textures/gui/map_marker_home.png");
	private static final ResourceLocation mapMarkerResidentFemale = new ResourceLocation(ModDetails.MOD_ID, "textures/gui/map_marker_resident_female.png");
	private static final ResourceLocation mapMarkerResidentMale = new ResourceLocation(ModDetails.MOD_ID, "textures/gui/map_marker_resident_male.png");
	private static final ResourceLocation mapMarkerVisitor = new ResourceLocation(ModDetails.MOD_ID, "textures/gui/map_marker_visitor.png");
	private static final ResourceLocation mapMarkerEnemy = new ResourceLocation(ModDetails.MOD_ID, "textures/gui/map_marker_enemy.png");
	private static final ResourceLocation mapMarkerPlayer = new ResourceLocation(ModDetails.MOD_ID, "textures/gui/map_marker_player.png");
	private static final ResourceLocation mapCheckmarkTick = new ResourceLocation(ModDetails.MOD_ID, "textures/gui/checkmark_tick.png");
	private static final ResourceLocation mapCheckmarkCross = new ResourceLocation(ModDetails.MOD_ID, "textures/gui/checkmark_cross.png");

	private static Boolean showMapBoundaries = true;
	private static Boolean showMapTownHall = true;
	private static Boolean showMapStructures = true; 
	private static Boolean showMapHomes = true; 
	private static Boolean showMapResidents = true; 
	private static Boolean showMapPlayer = true;  
	private static Boolean showMapVisitors = true;  
	private static Boolean showMapEnemies = true; 

	private VillageData villageData;
	private HashMap<String, ResourceLocation> bookmarkResources;
	private ArrayList<GuiPage> pages;
	private ArrayList<GuiBookmark> bookmarks;
	private ArrayList<GuiButton> buttons;
	private ArrayList<GuiTooltip> tooltips;
	private Stack<Integer> pageHistory;

	private int leftPageIndex;
	private String subPageKey;
	private int subPageIndex;
	private int subPageCount;

	private float scale;
	private int x;
	private int y; 
	private int xPageLandscape;
	private int yPageLandscape; 
	private int xPagePortrait;
	private int yPagePortrait; 

	public GuiTektopiaBook(VillageData villageData) {
		this.villageData = villageData;
		this.pages = new ArrayList<GuiPage>(); 
		this.bookmarks = new ArrayList<GuiBookmark>();
		this.buttons = new ArrayList<GuiButton>();
		this.tooltips = new ArrayList<GuiTooltip>();
		this.pageHistory = new Stack<>();

		this.scale = 1.0F;

		setLeftPageIndex(0);
		createBookmarkResources();
		createPages();
		setSubPage(null);
	}

	public VillageData getVillageData() {
		return this.villageData;
	}	

	public int getLeftPageIndex() {
		return this.leftPageIndex;
	}	

	public int getRightPageIndex() {
		return this.leftPageIndex + 1;
	}

	public boolean isStartOfBook() {
		return this.getLeftPageIndex() <= this.pages.stream().mapToInt(p -> p.getPageIndex()).min().orElse(0);
	}

	public boolean isEndOfBook() {
		return this.getRightPageIndex() >= this.pages.stream().mapToInt(p -> p.getPageIndex()).max().orElse(0);
	}

	/**
	 * Returns true if this GUI should pause the game when it is displayed in single-player
	 */
	@Override
	public boolean doesGuiPauseGame() {
		return true;
	}

	/**
	 * Draws the screen and all the components in it.
	 */
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawDefaultBackground();

		// calculate display scale
		this.scale = 10.0F;
		this.scale = Math.min(this.scale, (float)this.width / (float)BOOK_WIDTH);
		this.scale = Math.min(this.scale, (float)this.height / (float)BOOK_HEIGHT);

		// calculate background display area 
		this.x = (int) Math.max(1, ((this.width / this.scale) - BOOK_WIDTH) / 2); 
		this.y = (int) Math.max(1, ((this.height / this.scale) - BOOK_HEIGHT) / 2) + 10;

		this.xPageLandscape = this.x + (BOOK_WIDTH / 2) - (PAGE_LANDSCAPE_WIDTH / 2);
		this.yPageLandscape = 0;
		this.xPagePortrait = this.x + (BOOK_WIDTH / 2) - (PAGE_PORTRAIT_WIDTH / 2);
		this.yPagePortrait = 0;

		this.tooltips.clear();

		createBookmarks();
		createButtons();

		{
			GlStateManager.pushMatrix();
			GlStateManager.scale(this.scale, this.scale, 1.0F);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

			// draw book background
			mc.getTextureManager().bindTexture(book);
			super.drawModalRectWithCustomSizedTexture(this.x, this.y, 0, 0, BOOK_WIDTH, BOOK_HEIGHT, BOOK_WIDTH, 512);

			// draw the bookmarks
			for (GuiBookmark bookmark : this.bookmarks) {
				if (bookmark.getBackground() != null && bookmark.getBackground().getTexture() != null) {
					mc.getTextureManager().bindTexture(bookmark.getBackground().getTexture());
					super.drawModalRectWithCustomSizedTexture(bookmark.getBackground().getLeft(), bookmark.getBackground().getTop(), bookmark.getBackground().getTextureLeft(), bookmark.getBackground().getTextureTop(), bookmark.getBackground().getWidth(), bookmark.getBackground().getHeight(), bookmark.getBackground().getTextureWidth(), bookmark.getBackground().getTextureHeight());
				}

				if (bookmark.getIcon() != null && bookmark.getIcon().getTexture() != null) {
					mc.getTextureManager().bindTexture(bookmark.getIcon().getTexture());
					super.drawModalRectWithCustomSizedTexture(bookmark.getIcon().getLeft(), bookmark.getIcon().getTop(), bookmark.getIcon().getTextureLeft(), bookmark.getIcon().getTextureTop(), bookmark.getIcon().getWidth(), bookmark.getIcon().getHeight(), bookmark.getIcon().getTextureWidth(), bookmark.getIcon().getTextureHeight());
				}
			}

			// this is to force something to the screen
			Font.small.printLeft("", this.x, this.y);

			drawPages(mouseX, mouseY, partialTicks);
			drawSubPages(mouseX, mouseY, partialTicks);

			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

			// draw the buttons
			for (GuiButton button : this.buttons) {
				if (button.getIcon() != null && button.getIcon().getTexture() != null) {
					mc.getTextureManager().bindTexture(button.getIcon().getTexture());
					super.drawModalRectWithCustomSizedTexture(button.getIcon().getLeft(), button.getIcon().getTop(), button.getIcon().getTextureLeft(), button.getIcon().getTextureTop(), button.getIcon().getWidth(), button.getIcon().getHeight(), button.getIcon().getTextureWidth(), button.getIcon().getTextureHeight());
				}
			}

			GlStateManager.popMatrix();

			// display any tooltips
			for (GuiTooltip tooltip : this.tooltips) {
				if (tooltip != null && tooltip.withinBounds(mouseX, mouseY, this.scale)) {
					super.drawHoveringText(tooltip.getTooltip(), mouseX, mouseY);
					break;
				}
			}	        
		}

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	@Override
	public void initGui() {
		super.initGui();
	}

	public void handleMouseInput() throws IOException{
		super.handleMouseInput();

		// note to self(signum returns +1 for positive numbers and -1 for negative numbers, 0 for 0..)
		int pageScroll = Integer.signum(Mouse.getEventDWheel());
		if (pageScroll != 0) {
			
			if (this.isSubPageOpen()) {
				setSubPageIndex(this.subPageIndex + -pageScroll);
			} else {
				setLeftPageIndex(this.leftPageIndex + -(pageScroll * 2));
			}
			
			this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(ModSounds.BOOK_PAGE_TURN, 1.0F));
			
		}
	}

	/**
	 * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
	 */
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if (mouseButton == 0) {
			for (GuiBookmark bookmark : this.bookmarks) {
				if (bookmark == null) {
					continue;
				}

				// check if we have clicked on one of the bookmarks
				if (bookmark != null && bookmark.withinBounds(mouseX, mouseY, this.scale)) {
					actionPerformed(bookmark);
					break;
				}
				else if (bookmark != null && bookmark.withinBounds(mouseX, mouseY, this.scale)) {
					actionPerformed(bookmark);
					break;
				}
			}

			// process the buttons in reverse order as the buttons at the top of the z-order will be the last in the list.
			for (int i = this.buttons.size() - 1; i >= 0; i--) {
				GuiButton button = this.buttons.get(i);

				if (button == null) {
					continue;
				}

				// check if we have clicked on one of the buttons
				if (button.withinBounds(mouseX, mouseY, this.scale) ) {
					actionPerformed(button);
					break;
				}
			}
		}

		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	/**
	 * Called when the screen is unloaded. Used to disable keyboard repeat events
	 */
	@Override
	public void onGuiClosed() {
		clearPageHistory();
		this.pageHistory = null;

		this.tooltips.clear();
		this.tooltips = null;

		this.bookmarks.clear();
		this.bookmarks = null;

		this.pages.clear();
		this.pages = null;

		this.bookmarkResources.clear();
		this.bookmarkResources = null;

		this.villageData = null;
	}

	private void addPage(GuiPage page) {
		if (page == null)
			return;

		this.pages.add(page);

		LoggerUtils.info("Adding page " + page.getBookmarkKey() + "::" + page.getGuiPageType() + "::" + page.getDataKey(), true);
	}

	private void createBookmarkResources() {
		this.bookmarkResources = new HashMap<String, ResourceLocation>();

		this.bookmarkResources.put(BOOKMARK_KEY_ECONOMY, new ResourceLocation(ModDetails.MOD_ID, "textures/gui/icon_economy.png"));
		this.bookmarkResources.put(BOOKMARK_KEY_ENEMIES, new ResourceLocation(ModDetails.MOD_ID, "textures/gui/icon_enemy.png"));
		this.bookmarkResources.put(BOOKMARK_KEY_HOMES, new ResourceLocation(ModDetails.MOD_ID, "textures/gui/icon_home.png"));
		this.bookmarkResources.put(BOOKMARK_KEY_MAP, new ResourceLocation(ModDetails.MOD_ID, "textures/gui/icon_map.png"));
		this.bookmarkResources.put(BOOKMARK_KEY_PROFESSIONS, new ResourceLocation(ModDetails.MOD_ID, "textures/gui/icon_profession.png"));
		this.bookmarkResources.put(BOOKMARK_KEY_RESIDENTS, new ResourceLocation(ModDetails.MOD_ID, "textures/gui/icon_resident.png"));
		this.bookmarkResources.put(BOOKMARK_KEY_STATISTICS, new ResourceLocation(ModDetails.MOD_ID, "textures/gui/icon_statistics.png"));
		this.bookmarkResources.put(BOOKMARK_KEY_STRUCTURES, new ResourceLocation(ModDetails.MOD_ID, "textures/gui/icon_structure.png"));
		this.bookmarkResources.put(BOOKMARK_KEY_VILLAGE, new ResourceLocation(ModDetails.MOD_ID, "textures/gui/icon_summary.png"));
		this.bookmarkResources.put(BOOKMARK_KEY_VISITORS, new ResourceLocation(ModDetails.MOD_ID, "textures/gui/icon_visitor.png"));
	}

	private void createBookmarks() {
		final int[] tXL = new int[] { this.x + BOOKMARK_LEFT_X };
		final int[] tXR = new int[] { this.x + BOOKMARK_RIGHT_X };
		final int[] tY = new int[] { this.y + BOOKMARK_TOP_Y };

		this.bookmarks.clear();

		this.pages.stream()
		.filter(p -> p.getBookmarkKey() != "")
		.forEach(p -> {
			GuiBookmark bookmark = new GuiBookmark(p.getBookmarkKey(), p.getPageIndex());

			ResourceLocation icon = null;
			if (this.bookmarkResources.containsKey(p.getBookmarkKey())) {
				icon = this.bookmarkResources.get(p.getBookmarkKey());
			}

			if (p.getPageIndex() <= this.getLeftPageIndex()) {
				bookmark.setBackground(bookmarkLeft, tXL[0], tY[0], BOOKMARK_WIDTH, BOOKMARK_HEIGHT, 0, 0, 75, 75);
				if (icon != null) {
					bookmark.setIcon(icon, tXL[0] + 12, tY[0] + 5, 16, 16, 0, 0, 16, 16);
					tXL[0] += 1;
				}
			} else {
				bookmark.setBackground(bookmarkRight, tXR[0], tY[0], BOOKMARK_WIDTH, BOOKMARK_HEIGHT, 0, 0, 75, 75);
				if (icon != null) {
					bookmark.setIcon(icon, tXR[0] + 7, tY[0] + 5, 16, 16, 0, 0, 16, 16);
					tXR[0] += 1;
				}
			}
			this.bookmarks.add(bookmark);

			String tooltipText = bookmark.getDisplayName();
			if (!StringUtils.isNullOrWhitespace(tooltipText)) {
				this.tooltips.add(new GuiTooltip(bookmark.getBackground().getLeft(), bookmark.getBackground().getTop(), bookmark.getBackground().getWidth(), bookmark.getBackground().getHeight(), tooltipText));
			}

			tY[0] += BOOKMARK_HEIGHT + BOOKMARK_SPACE_Y;
		});
	}

	private void createButtons() {
		this.buttons.clear();

		GuiButton button = null;
		String tooltipText = null;

		if (!this.isSubPageOpen() && !this.pageHistory.empty()) {
			int tX = this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X - 16;
			int tY = this.y + PAGE_FOOTER_Y - 5;

			button = new GuiButton(BUTTON_KEY_BACK);
			button.setIcon(buttonBack, tX, tY, 16, 16, 0, 0, 16, 16);
			this.buttons.add(button);

			tooltipText = button.getDisplayName();
			if (!StringUtils.isNullOrWhitespace(tooltipText)) {
				this.tooltips.add(new GuiTooltip(button.getIcon().getLeft(), button.getIcon().getTop(), button.getIcon().getWidth(), button.getIcon().getHeight(), tooltipText));
			}
		}

		if (!this.isStartOfBook()) {
			int tX = this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + 4;
			int tY = this.y + PAGE_FOOTER_Y - 5;

			button = new GuiButton(BUTTON_KEY_STARTBOOK);
			button.setIcon(buttonStartBook, tX, tY, 16, 16, 0, 0, 16, 16);
			this.buttons.add(button);

			tooltipText = button.getDisplayName();
			if (!StringUtils.isNullOrWhitespace(tooltipText)) {
				this.tooltips.add(new GuiTooltip(button.getIcon().getLeft(), button.getIcon().getTop(), button.getIcon().getWidth(), button.getIcon().getHeight(), tooltipText));
			}

			tX += button.getIcon().getWidth() + 4;

			button = new GuiButton(BUTTON_KEY_PREVIOUSPAGE);
			button.setIcon(buttonPreviousPage, tX, tY, 16, 16, 0, 0, 16, 16);
			this.buttons.add(button);

			tooltipText = button.getDisplayName();
			if (!StringUtils.isNullOrWhitespace(tooltipText)) {
				this.tooltips.add(new GuiTooltip(button.getIcon().getLeft(), button.getIcon().getTop(), button.getIcon().getWidth(), button.getIcon().getHeight(), tooltipText));
			}

			tX += button.getIcon().getWidth() + 4;
		}

		if (!this.isEndOfBook()) {
			int tX = this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X - 20;
			int tY = this.y + PAGE_FOOTER_Y - 5;

			button = new GuiButton(BUTTON_KEY_ENDBOOK);
			button.setIcon(buttonEndBook, tX, tY, 16, 16, 0, 0, 16, 16);
			this.buttons.add(button);

			tooltipText = button.getDisplayName();
			if (!StringUtils.isNullOrWhitespace(tooltipText)) {
				this.tooltips.add(new GuiTooltip(button.getIcon().getLeft(), button.getIcon().getTop(), button.getIcon().getWidth(), button.getIcon().getHeight(), tooltipText));
			}

			tX -= button.getIcon().getWidth() + 4;

			button = new GuiButton(BUTTON_KEY_NEXTPAGE);
			button.setIcon(buttonNextPage, tX, tY, 16, 16, 0, 0, 16, 16);
			this.buttons.add(button);

			tooltipText = button.getDisplayName();
			if (!StringUtils.isNullOrWhitespace(tooltipText)) {
				this.tooltips.add(new GuiTooltip(button.getIcon().getLeft(), button.getIcon().getTop(), button.getIcon().getWidth(), button.getIcon().getHeight(), tooltipText));
			}

			tX -= button.getIcon().getWidth() + 4;
		}
	}

	private void createPages() {

		this.pages.clear();

		if (this.villageData == null) {
			return;
		}

		EconomyData economyData = this.villageData.getEconomyData();
		HomesData homesData = this.villageData.getHomesData();
		ResidentsData residentsData = this.villageData.getResidentsData();
		StructuresData structuresData = this.villageData.getStructuresData();
		VisitorsData visitorsData = this.villageData.getVisitorsData();
		EnemiesData enemiesData = this.villageData.getEnemiesData();

		int startPageIndex = 0;
		int pageIndex = 0;

		// inside cover page page
		addPage(new GuiPage(GuiPageType.INSIDECOVER, pageIndex++, getPageKey("", 0)));

		// title page
		addPage(new GuiPage(GuiPageType.TITLE, pageIndex++, getPageKey("", 0)));

		// village title page
		addPage(new GuiPage(GuiPageType.SUMMARY, pageIndex++, getPageKey(BOOKMARK_KEY_VILLAGE, 0), BOOKMARK_KEY_VILLAGE));

		// village pages
		addPage(new GuiPage(GuiPageType.VILLAGE, pageIndex++, getPageKey("", 0)));

		if (structuresData != null) {
			if (pageIndex % 2 != 0)
				addPage(new GuiPage(GuiPageType.BLANK, pageIndex++, getPageKey("", 0)));

			// title page
			addPage(new GuiPage(GuiPageType.SUMMARY, pageIndex++, getPageKey(BOOKMARK_KEY_STRUCTURES, 0), BOOKMARK_KEY_STRUCTURES));

			List<VillageStructureType> structureTypes = TektopiaUtils.getVillageStructureTypes();

			// structure type summary
			if (structuresData.getStructureTypeCounts() != null) {
				int count = structureTypes.size();

				if (count > 0) {
					int pages = count / STRUCTURETYPES_PER_PAGE;
					if (count % STRUCTURETYPES_PER_PAGE > 0) {
						pages++;
					}

					for (int page = 0; page < pages; page++) {
						addPage(new GuiPage(GuiPageType.STRUCTURE, pageIndex++, getPageKey("", page)));
					}
				}
			}

			if (structuresData.getStructures() != null) {
				// structure type pages
				for (VillageStructureType structureType : structureTypes) {
					List<StructureData> structures = structuresData.getStructuresByType(structureType);

					int count = structures == null || structures.size() == 0 ? 0 : structures.size();
					if (count > 0) {
						int pages = count / STRUCTURES_PER_PAGE;
						if (count % STRUCTURES_PER_PAGE > 0) {
							pages++;
						}

						for (int page = 0; page < pages; page++) {    	        			
							addPage(new GuiPage(GuiPageType.STRUCTURETYPE, pageIndex++, getStructureTypePageKey(structureType, page)));
						}
					}
				}

				// structure pages
				for (StructureData structure : structuresData.getStructures()) {
					int count = structure.getOccupantCount();
					int pages = count / STRUCTUREOCCUPANTS_PER_PAGE;
					if (count % STRUCTUREOCCUPANTS_PER_PAGE > 0) {
						pages++;
					}
					pages = Math.max(1, pages);

					for (int page = 0; page < pages; page++) {
						// check if the structure owns the frame position
						if (page == 0 && this.villageData.getStructureId() > 0 && this.villageData.getStructureId() == structure.getStructureId()) {
							startPageIndex = pageIndex;
						}
						else if (page == 0 && this.villageData.getFramePosition() != null && structure.getFramePosition() != null && this.villageData.getFramePosition().equals(structure.getFramePosition())) {
							startPageIndex = pageIndex;
						} 
						addPage(new GuiPage(GuiPageType.STRUCTURE, pageIndex++, getStructureDetailPageKey(structure.getStructureId(), page)));
					}
				}
			}
		}

		if (homesData != null) {
			if (pageIndex % 2 != 0)
				addPage(new GuiPage(GuiPageType.BLANK, pageIndex++, getPageKey("", 0)));

			// title page
			addPage(new GuiPage(GuiPageType.SUMMARY, pageIndex++, getPageKey(BOOKMARK_KEY_HOMES, 0), BOOKMARK_KEY_HOMES));

			List<VillageStructureType> homeTypes = TektopiaUtils.getHomeStructureTypes();

			// home type summary
			if (homesData.getHomeTypeCounts() != null) {
				int count = homeTypes.size();

				if (count > 0) {
					int pages = count / HOMETYPES_PER_PAGE;
					if (count % HOMETYPES_PER_PAGE > 0) {
						pages++;
					}

					for (int page = 0; page < pages; page++) {
						addPage(new GuiPage(GuiPageType.HOME, pageIndex++, getPageKey("", page)));
					}
				}
			}

			if (homesData.getHomes() != null) {
				// home type pages
				for (VillageStructureType homeType : homeTypes) {
					List<HomeData> homes = homesData.getHomesByType(homeType);

					int count = homes == null || homes.size() == 0 ? 0 : homes.size();
					if (count > 0) {
						int pages = count / HOMES_PER_PAGE;
						if (count % HOMES_PER_PAGE > 0) {
							pages++;
						}

						for (int page = 0; page < pages; page++) {
							addPage(new GuiPage(GuiPageType.HOMETYPE, pageIndex++, getStructureTypePageKey(homeType, page)));
						}
					}
				}

				// home pages
				for (HomeData homeData : homesData.getHomes()) {
					int count = homeData.getMaxBeds();

					if (count > 0) {
						int pages = count / HOMEBEDS_PER_PAGE;
						if (count % HOMEBEDS_PER_PAGE > 0) {
							pages++;
						}

						for (int page = 0; page < pages; page++) {
							// check if the home owns the frame position
							if (this.villageData.getStructureId() > 0 && this.villageData.getStructureId() == homeData.getHomeId()) {
								startPageIndex = pageIndex;
							}
							else if (page == 0 && this.villageData.getFramePosition() != null && homeData.getFramePosition() != null && this.villageData.getFramePosition().equals(homeData.getFramePosition())) {
								startPageIndex = pageIndex;
							} 

							addPage(new GuiPage(GuiPageType.HOME, pageIndex++, getStructureDetailPageKey(homeData.getHomeId(), page)));
						}
					}
				}
			}
		}

		if (TektopiaUtils.getProfessionTypeNames() != null) {
			if (pageIndex % 2 != 0)
				addPage(new GuiPage(GuiPageType.BLANK, pageIndex++, getPageKey("", 0)));

			// title page
			addPage(new GuiPage(GuiPageType.SUMMARY, pageIndex++, getPageKey(BOOKMARK_KEY_PROFESSIONS, 0), BOOKMARK_KEY_PROFESSIONS));

			// profession type list
			int count = TektopiaUtils.getProfessionTypeNames().size();
			int pages = count / PROFESSIONTYPES_PER_PAGE;
			if (count % PROFESSIONTYPES_PER_PAGE > 0) {
				pages++;
			}
			pages = Math.max(1, pages);

			for (int page = 0; page < pages; page++) {
				addPage(new GuiPage(GuiPageType.PROFESSION, pageIndex++, getPageKey("", page)));
			}

			// profession pages
			if (residentsData != null) {
				for (String professionType : TektopiaUtils.getProfessionTypeNames()) {
					count = residentsData.getProfessionTypeCount(professionType);
					if (count > 0) {
						pages = count / PROFESSIONS_PER_PAGE;
						if (count % PROFESSIONS_PER_PAGE > 0) {
							pages++;
						}

						for (int page = 0; page < pages; page++) {
							addPage(new GuiPage(GuiPageType.PROFESSIONTYPE, pageIndex++, getProfessionDetailPageKey(professionType, page)));
						}
					}
				}
			}
		}

		if (residentsData != null) {
			if (pageIndex % 2 != 0)
				addPage(new GuiPage(GuiPageType.BLANK, pageIndex++, getPageKey("", 0)));

			// title page
			addPage(new GuiPage(GuiPageType.SUMMARY, pageIndex++, getPageKey(BOOKMARK_KEY_RESIDENTS, 0), BOOKMARK_KEY_RESIDENTS));

			// resident list
			int count = residentsData.getResidentsCountAll();
			int pages = count / RESIDENTS_PER_PAGE;
			if (count % RESIDENTS_PER_PAGE > 0) {
				pages++;
			}
			pages = Math.max(1, pages);

			for (int page = 0; page < pages; page++) {
				addPage(new GuiPage(GuiPageType.RESIDENT, pageIndex++, getPageKey("", page)));
			}

			// resident pages
			if (residentsData.getResidents() != null) {
				for (ResidentData residentData : residentsData.getResidents()) {
					// check for the resident
					if (this.villageData.getEntityId() > 0 && this.villageData.getEntityId() == residentData.getId()) {
						startPageIndex = pageIndex;
					}
					// check if the resident owns the bed position
					if (this.villageData.getBedPosition() != null && residentData.getBedPosition() != null && this.villageData.getBedPosition().equals(residentData.getBedPosition())) {
						startPageIndex = pageIndex;
					}    	        	

					addPage(new GuiPage(GuiPageType.RESIDENT, pageIndex++, getResidentDetailPageKey(residentData.getId())));
				}
			}
		}

		if (visitorsData != null) {
			if (pageIndex % 2 != 0)
				addPage(new GuiPage(GuiPageType.BLANK, pageIndex++, getPageKey("", 0)));

			// title page
			addPage(new GuiPage(GuiPageType.SUMMARY, pageIndex++, getPageKey(BOOKMARK_KEY_VISITORS, 0), BOOKMARK_KEY_VISITORS));

			// visitor list
			int count = visitorsData.getVisitorsCount();
			int pages = count / VISITORS_PER_PAGE;
			if (count % VISITORS_PER_PAGE > 0) {
				pages++;
			}
			pages = Math.max(1, pages);

			for (int page = 0; page < pages; page++) {
				addPage(new GuiPage(GuiPageType.VISITOR, pageIndex++, getPageKey("", page)));
			}

			// visitor pages
			if (visitorsData.getVisitors() != null) {
				for (VisitorData visitorData : visitorsData.getVisitors()) {
					// check for the visitor
					if (this.villageData.getEntityId() > 0 && this.villageData.getEntityId() == visitorData.getId()) {
						startPageIndex = pageIndex;
					}  	        	

					addPage(new GuiPage(GuiPageType.VISITOR, pageIndex++, getPageKey("" + visitorData.getId(), 0)));

					// if visitor is a vendor, then make sure to add additional pages for items, if needed.
					if (visitorData.isVendor()) {
						MerchantRecipeList recipeList = visitorData.getRecipeList();
						if (recipeList != null && !recipeList.isEmpty()) {
							count = recipeList.size() - VISITORVENDORLIST0_PER_PAGE;
							if (count > 0) {
								pages = count / VISITORVENDORLIST_PER_PAGE;
								if (count % VISITORVENDORLIST_PER_PAGE > 0) {
									pages++;
								}

								for (int page = 0; page < pages; page++) {
									addPage(new GuiPage(GuiPageType.VISITOR, pageIndex++, getResidentDetailPageKey(visitorData.getId(), page + 1)));
								}
							}
						}
					}
				}
			}
		}

		if (enemiesData != null) {
			if (pageIndex % 2 != 0)
				addPage(new GuiPage(GuiPageType.BLANK, pageIndex++, getPageKey("", 0)));

			// title page
			addPage(new GuiPage(GuiPageType.SUMMARY, pageIndex++, getPageKey(BOOKMARK_KEY_ENEMIES, 0), BOOKMARK_KEY_ENEMIES));

			// enemy list
			int count = enemiesData.getEnemiesCount();
			int pages = count / ENEMIES_PER_PAGE;
			if (count % ENEMIES_PER_PAGE > 0) {
				pages++;
			}
			pages = Math.max(1, pages);

			for (int page = 0; page < pages; page++) {
				addPage(new GuiPage(GuiPageType.ENEMY, pageIndex++, getPageKey("", page)));
			}
		}

		if (residentsData != null || structuresData != null) {
			if (pageIndex % 2 != 0)
				addPage(new GuiPage(GuiPageType.BLANK, pageIndex++, getPageKey("", 0)));

			// title page
			addPage(new GuiPage(GuiPageType.SUMMARY, pageIndex++, getPageKey(BOOKMARK_KEY_STATISTICS, 0), BOOKMARK_KEY_STATISTICS));

			// summary pages
			addPage(new GuiPage(GuiPageType.STATS, pageIndex++, getPageKey("", 0)));

			// happiness
			Map<Integer, List<ResidentData>> happinessMap = residentsData != null ? residentsData.getResidentHappinessStatistics() : null;

			if (happinessMap != null) {
				for (Entry<Integer, List<ResidentData>> happinessEntry : happinessMap.entrySet()) {
					int count = happinessEntry.getValue().size();
					int pages = count / STATRESIDENTS_PER_PAGE;
					if (count % STATRESIDENTS_PER_PAGE > 0) {
						pages++;
					}
					pages = Math.max(1, pages);

					for (int page = 0; page < pages; page++) {
						addPage(new GuiPage(GuiPageType.STATS, pageIndex++, getStatisticsPageKey("happiness", happinessEntry.getKey(), page)));
					}
				}        		
			}

			// hunger
			Map<Integer, List<ResidentData>> hungerMap = residentsData != null ? residentsData.getResidentHungerStatistics() : null;

			if (hungerMap != null) {
				for (Entry<Integer, List<ResidentData>> hungerEntry : hungerMap.entrySet()) {
					int count = hungerEntry.getValue().size();
					int pages = count / STATRESIDENTS_PER_PAGE;
					if (count % STATRESIDENTS_PER_PAGE > 0) {
						pages++;
					}
					pages = Math.max(1, pages);

					for (int page = 0; page < pages; page++) {
						addPage(new GuiPage(GuiPageType.STATS, pageIndex++, getStatisticsPageKey("hunger", hungerEntry.getKey(), page)));
					}
				}
			}

			// overcrowding
			List<StructureData> overcrowdingList = structuresData != null ? structuresData.getStructuresOvercrowded() : null;

			if (overcrowdingList != null) {
				int count = overcrowdingList.size();
				int pages = count / STATSTRUCTURES_PER_PAGE;
				if (count % STATSTRUCTURES_PER_PAGE > 0) {
					pages++;
				}
				pages = Math.max(1, pages);

				for (int page = 0; page < pages; page++) {
					addPage(new GuiPage(GuiPageType.STATS, pageIndex++, getStatisticsPageKey("overcrowding", 0, page)));
				}
			}
		}

		if (economyData != null) {
			if (pageIndex % 2 != 0)
				addPage(new GuiPage(GuiPageType.BLANK, pageIndex++, getPageKey("", 0)));

			// title page
			addPage(new GuiPage(GuiPageType.SUMMARY, pageIndex++, getPageKey(BOOKMARK_KEY_ECONOMY, 0), BOOKMARK_KEY_ECONOMY));

			int count;
			int pages;

			// Architect Items
			ResidentData architect = residentsData.getArchitect();
			if (architect != null) {
				MerchantRecipeList recipeList = architect.getRecipeList();
				if (recipeList != null && !recipeList.isEmpty()) {
					count = recipeList.size();
					if (count > 0) {
						pages = count / RESIDENTVENDORLIST_PER_PAGE;
						if (count % RESIDENTVENDORLIST_PER_PAGE > 0) {
							pages++;
						}

						for (int page = 0; page < pages; page++) {
							addPage(new GuiPage(GuiPageType.ECONOMY, pageIndex++, getPageKey("architectitems", page)));
						}
					}
				}
			}

			// Tradesman Items
			ResidentData tradesman = residentsData.getTradesman();
			if (tradesman != null) {
				MerchantRecipeList recipeList = tradesman.getRecipeList();
				if (recipeList != null && !recipeList.isEmpty()) {
					count = recipeList.size();
					if (count > 0) {
						pages = count / RESIDENTVENDORLIST_PER_PAGE;
						if (count % RESIDENTVENDORLIST_PER_PAGE > 0) {
							pages++;
						}

						for (int page = 0; page < pages; page++) {
							addPage(new GuiPage(GuiPageType.ECONOMY, pageIndex++, getPageKey("tradesmanitems", page)));
						}
					}
				}
			}

			// sales history pages
			count = economyData.getMerchantSales();
			pages = count / (SALESHISTORY_PER_PAGE * 3);
			if (count % (SALESHISTORY_PER_PAGE * 3) > 0) {
				pages++;
			}
			pages = Math.max(1, pages);

			for (int page = 0; page < pages; page++) {
				addPage(new GuiPage(GuiPageType.ECONOMY, pageIndex++, getPageKey("saleshistory", page)));
			}
		}

		if (pageIndex % 2 != 0)
			addPage(new GuiPage(GuiPageType.BLANK, pageIndex++, getPageKey("", 0)));

		// map pages
		addPage(new GuiPage(GuiPageType.MAP, pageIndex++, getPageKey(GuiMapQuadrant.ALL.name(), 0), BOOKMARK_KEY_MAP));
		addPage(new GuiPage(GuiPageType.MAP, pageIndex++, getPageKey("", 0)));
		addPage(new GuiPage(GuiPageType.MAP, pageIndex++, getPageKey(GuiMapQuadrant.NORTHWEST.name(), 0)));
		addPage(new GuiPage(GuiPageType.MAP, pageIndex++, getPageKey("", 0)));
		addPage(new GuiPage(GuiPageType.MAP, pageIndex++, getPageKey(GuiMapQuadrant.NORTHEAST.name(), 0)));
		addPage(new GuiPage(GuiPageType.MAP, pageIndex++, getPageKey("", 0)));
		addPage(new GuiPage(GuiPageType.MAP, pageIndex++, getPageKey(GuiMapQuadrant.SOUTHWEST.name(), 0)));
		addPage(new GuiPage(GuiPageType.MAP, pageIndex++, getPageKey("", 0)));
		addPage(new GuiPage(GuiPageType.MAP, pageIndex++, getPageKey(GuiMapQuadrant.SOUTHEAST.name(), 0)));
		addPage(new GuiPage(GuiPageType.MAP, pageIndex++, getPageKey("", 0)));

		setLeftPageIndex(startPageIndex);
	}

	private void drawPageHeader(int mouseX, int mouseY, float partialTicks, GuiPage guiPage) {
		String headerText = TextUtils.translate("tektopiaBook.name");

		if (!StringUtils.isNullOrWhitespace(headerText)) {
			if (guiPage.isLeftPage()) {
				Font.small.printLeft(headerText, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, this.y + PAGE_HEADER_Y);
			}

			if (guiPage.isRightPage()) {
				Font.small.printRight(headerText, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, this.y + PAGE_HEADER_Y);
			}
		}
	}

	private void drawPageFooter(int mouseX, int mouseY, float partialTicks, GuiPage guiPage) {
		if (guiPage.isLeftPage()) {
			Font.small.printCentered(this.leftPageIndex, this.x + PAGE_LEFTPAGE_CENTER_X, this.y + PAGE_FOOTER_Y); 
		}

		if (guiPage.isRightPage()) {
			Font.small.printCentered(this.leftPageIndex + 1, this.x + PAGE_RIGHTPAGE_CENTER_X, this.y + PAGE_FOOTER_Y); 
		}
	}

	private void drawPages(int mouseX, int mouseY, float partialTicks) {
		final int[] pageIndex = { this.leftPageIndex };

		for (int index = 0; index < 2; index++) {
			GuiPage guiPage = this.pages.stream()
					.filter(p -> p.getPageIndex() == pageIndex[0])
					.findFirst().orElse(null);

			if (guiPage != null) {
				switch (guiPage.getGuiPageType()) {
				case BLANK:
					drawPageBlank(mouseX, mouseY, partialTicks, guiPage);
					break;
				case ECONOMY:
					drawPageEconomy(mouseX, mouseY, partialTicks, guiPage);
					break;
				case ENEMY:
					drawPageEnemy(mouseX, mouseY, partialTicks, guiPage);
					break;
				case HOME:
					drawPageHome(mouseX, mouseY, partialTicks, guiPage);
					break;
				case HOMETYPE:
					drawPageHomeType(mouseX, mouseY, partialTicks, guiPage);
					break;
				case MAP:
					drawPageMap(mouseX, mouseY, partialTicks, guiPage);
					break;
				case PROFESSION:
					drawPageProfession(mouseX, mouseY, partialTicks, guiPage);
					break;
				case PROFESSIONTYPE:
					drawPageProfessionType(mouseX, mouseY, partialTicks, guiPage);
					break;
				case RESIDENT:
					drawPageResident(mouseX, mouseY, partialTicks, guiPage);
					break;
				case STATS:
					drawPageStatistics(mouseX, mouseY, partialTicks, guiPage);
					break;
				case STRUCTURE:
					drawPageStructure(mouseX, mouseY, partialTicks, guiPage);
					break;
				case STRUCTURETYPE:
					drawPageStructureType(mouseX, mouseY, partialTicks, guiPage);
					break;
				case SUMMARY:
					drawPageSummary(mouseX, mouseY, partialTicks, guiPage);
					break;
				case TITLE:
					drawPageTitle(mouseX, mouseY, partialTicks, guiPage);
					break;
				case VILLAGE:
					drawPageVillage(mouseX, mouseY, partialTicks, guiPage);
					break;
				case VISITOR:
					drawPageVisitor(mouseX, mouseY, partialTicks, guiPage);
					break;
				case INSIDECOVER:
				default:
					break;
				}
			}

			pageIndex[0]++;
		}
	}

	private void drawPageBlank(int mouseX, int mouseY, float partialTicks, GuiPage guiPage) {
		drawPageHeader(mouseX, mouseY, partialTicks, guiPage);
		drawPageFooter(mouseX, mouseY, partialTicks, guiPage);
	}

	private void drawPageEconomy(int mouseX, int mouseY, float partialTicks, GuiPage guiPage) {
		drawPageHeader(mouseX, mouseY, partialTicks, guiPage);
		drawPageFooter(mouseX, mouseY, partialTicks, guiPage);

		int y = this.y + PAGE_BODY_Y;
		int indentX = 10;

		String[] dataKey = getPageKeyParts(guiPage.getDataKey());
		String continued = TextUtils.translate("tektopiaBook.continued");

		if (dataKey[0].equals("architectitems") || dataKey[0].equals("tradesmanitems")) {

			ResidentsData residentsData = this.villageData.getResidentsData();
			MerchantRecipeList recipeList = null;

			if (dataKey[0].equals("architectitems")) {
				// architect items
				ResidentData architect = residentsData != null ? residentsData.getArchitect() : null;
				recipeList = architect != null ? architect.getRecipeList() : null;
			}

			if (dataKey[0].equals("tradesmanitems")) {
				// tradesman items
				ResidentData tradesman = residentsData != null ? residentsData.getTradesman() : null;
				recipeList = tradesman != null ? tradesman.getRecipeList() : null;
			}

			String header = TextUtils.translate("tektopiaBook.economy." + dataKey[0]);

			if (!StringUtils.isNullOrWhitespace(header)) {
				if (!dataKey[1].equals("0") && !StringUtils.isNullOrWhitespace(continued)) {
					header += " " + continued;
				}
				header = TextFormatting.DARK_BLUE + header;

				if (guiPage.isLeftPage()) {
					Font.normal.printLeft(header, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
				}

				if (guiPage.isRightPage()) {
					Font.normal.printLeft(header, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
				}
			}

			y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

			String sellHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.sell");
			String buyHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.buy");

			if (guiPage.isLeftPage()) {
				Font.small.printLeft(sellHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
				Font.small.printLeft(buyHeader, this.x + PAGE_LEFTPAGE_CENTER_X, y); 
			}

			if (guiPage.isRightPage()) {
				Font.small.printLeft(sellHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
				Font.small.printLeft(buyHeader, this.x + PAGE_RIGHTPAGE_CENTER_X, y); 
			}

			y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y_IMAGE;

			if (recipeList != null && recipeList.size() > 0) {
				int page = 0;
				try {
					page = Integer.parseInt(dataKey[1]);
				}
				catch (NumberFormatException e) {
					page = 0;
				}
				int startIndex = page * RESIDENTVENDORLIST_PER_PAGE;
				int endIndex = Math.min(recipeList.size(), startIndex + RESIDENTVENDORLIST_PER_PAGE);

				List<MerchantRecipe> recipes = recipeList.subList(startIndex, endIndex);

				for (MerchantRecipe recipe : recipes) {
					ItemStack buyItem1Stack = recipe.getItemToBuy();
					ItemStack buyItem2Stack = recipe.getSecondItemToBuy();
					ItemStack sellItemStack = recipe.getItemToSell();

					List<String> buyItem1Tooltip = buyItem1Stack.getTooltip(null, TooltipFlags.NORMAL);
					if (buyItem1Stack.isItemEnchanted() && buyItem1Tooltip != null && buyItem1Tooltip.size() > 0) {
						buyItem1Tooltip.set(0, TextFormatting.AQUA + buyItem1Tooltip.get(0));
					}
					List<String> buyItem2Tooltip = buyItem2Stack.getTooltip(null, TooltipFlags.NORMAL);
					if (buyItem2Stack.isItemEnchanted() && buyItem2Tooltip != null && buyItem2Tooltip.size() > 0) {
						buyItem2Tooltip.set(0, TextFormatting.AQUA + buyItem2Tooltip.get(0));
					}
					List<String> sellTooltip = sellItemStack.getTooltip(null, TooltipFlags.NORMAL);
					if (sellItemStack.isItemEnchanted() && sellTooltip != null && sellTooltip.size() > 0) {
						sellTooltip.set(0, TextFormatting.AQUA + sellTooltip.get(0));
					}

					if (guiPage.isLeftPage()) {

						if (!buyItem1Stack.isEmpty()) {
							RenderUtils.renderItemIntoGUI(super.itemRender, buyItem1Stack, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y - 5);
							RenderUtils.renderItemOverlayIntoGUI(super.itemRender, Font.normal.fontRenderer, buyItem1Stack, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y - 5, null);
							if (buyItem1Tooltip != null && buyItem1Tooltip.size() > 0) {
								this.tooltips.add(new GuiTooltip(this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y - 5, 16, 16, buyItem1Tooltip));
							}
						}

						if (!buyItem2Stack.isEmpty()) {
							RenderUtils.renderItemIntoGUI(super.itemRender, buyItem2Stack, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + 20, y - 5);
							RenderUtils.renderItemOverlayIntoGUI(super.itemRender, Font.normal.fontRenderer, buyItem2Stack, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + 20, y - 5, null);
							if (buyItem2Tooltip != null && buyItem2Tooltip.size() > 0) {
								this.tooltips.add(new GuiTooltip(this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + 20, y - 5, 16, 16, buyItem2Tooltip));
							}
						}

						if (!sellItemStack.isEmpty()) {
							RenderUtils.renderItemIntoGUI(super.itemRender, sellItemStack, this.x + PAGE_LEFTPAGE_CENTER_X, y - 5);
							RenderUtils.renderItemOverlayIntoGUI(super.itemRender, Font.normal.fontRenderer, sellItemStack, this.x + PAGE_LEFTPAGE_CENTER_X, y - 5, null);
							if (sellTooltip != null && sellTooltip.size() > 0) {
								this.tooltips.add(new GuiTooltip(this.x + PAGE_LEFTPAGE_CENTER_X, y - 5, 16, 16, sellTooltip));
							}
						}
					}

					if (guiPage.isRightPage()) {

						if (!buyItem1Stack.isEmpty()) {
							RenderUtils.renderItemIntoGUI(super.itemRender, buyItem1Stack, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y - 5);
							RenderUtils.renderItemOverlayIntoGUI(super.itemRender, Font.normal.fontRenderer, buyItem1Stack, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y - 5, null);
							if (buyItem1Tooltip != null && buyItem1Tooltip.size() > 0) {
								this.tooltips.add(new GuiTooltip(this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y - 5, 16, 16, buyItem1Tooltip));
							}
						}

						if (!buyItem2Stack.isEmpty()) {
							RenderUtils.renderItemIntoGUI(super.itemRender, buyItem2Stack, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + 20, y - 5);
							RenderUtils.renderItemOverlayIntoGUI(super.itemRender, Font.normal.fontRenderer, buyItem2Stack, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + 20, y - 5, null);
							if (buyItem2Tooltip != null && buyItem2Tooltip.size() > 0) {
								this.tooltips.add(new GuiTooltip(this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + 20, y - 5, 16, 16, buyItem2Tooltip));
							}
						}

						if (!sellItemStack.isEmpty()) {
							RenderUtils.renderItemIntoGUI(super.itemRender, sellItemStack, this.x + PAGE_RIGHTPAGE_CENTER_X, y - 5);
							RenderUtils.renderItemOverlayIntoGUI(super.itemRender, Font.normal.fontRenderer, sellItemStack, this.x + PAGE_RIGHTPAGE_CENTER_X, y - 5, null);
							if (sellTooltip != null && sellTooltip.size() > 0) {
								this.tooltips.add(new GuiTooltip(this.x + PAGE_RIGHTPAGE_CENTER_X, y - 5, 16, 16, sellTooltip));
							}
						}
					}

					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y + LINE_SPACE_Y_IMAGE;
				}
			}
		}


		if (dataKey[0].equals("saleshistory")) {
			// sales history

			EconomyData economyData = this.villageData.getEconomyData();

			String header = TextUtils.translate("tektopiaBook.economy.salesHistory");

			if (!StringUtils.isNullOrWhitespace(header)) {
				if (!dataKey[1].equals("0") && !StringUtils.isNullOrWhitespace(continued)) {
					header += " " + continued;
				}
				header = TextFormatting.DARK_BLUE + header;

				if (guiPage.isLeftPage()) {
					Font.normal.printLeft(header, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
				}

				if (guiPage.isRightPage()) {
					Font.normal.printLeft(header, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
				}
			}

			y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y_IMAGE;

			if (economyData != null) {
				List<ItemStack> salesHistory = economyData.getSalesHistory();

				if (salesHistory != null && salesHistory.size() > 0) {
					int page = 0;
					try {
						page = Integer.parseInt(dataKey[1]);
					}
					catch (NumberFormatException e) {
						page = 0;
					}
					int startIndex = page * SALESHISTORY_PER_PAGE;
					int endIndex = Math.min(salesHistory.size(), startIndex + SALESHISTORY_PER_PAGE);

					//y += LINE_SPACE_Y;
					int yTop = y;

					for (int i = 0; i < 3; i++) {
						if (startIndex >= salesHistory.size())
							break;

						int xL = this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX;
						if (i == 1)
							xL = this.x + PAGE_LEFTPAGE_CENTER_X - 8;
						else if (i == 2)
							xL = this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X - 16 - indentX;

						int xR = this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX;
						if (i == 1)
							xR = this.x + PAGE_RIGHTPAGE_CENTER_X - 8;
						else if (i == 2)
							xR = this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X - 16 - indentX;

						List<ItemStack> sales = salesHistory.subList(startIndex, endIndex);

						for (ItemStack itemStack : sales) {

							List<String> itemStackTooltip = itemStack.getTooltip(null, TooltipFlags.NORMAL);
							if (itemStack.isItemEnchanted() && itemStackTooltip != null && itemStackTooltip.size() > 0) {
								itemStackTooltip.set(0, TextFormatting.AQUA + itemStackTooltip.get(0));
							}

							if (guiPage.isLeftPage()) {
								RenderUtils.renderItemIntoGUI(super.itemRender, itemStack, xL, y - 5);
								RenderUtils.renderItemOverlayIntoGUI(super.itemRender, Font.normal.fontRenderer, itemStack, xL, y - 5, null);
								if (itemStackTooltip != null && itemStackTooltip.size() > 0) {
									this.tooltips.add(new GuiTooltip(xL, y - 5, 16, 16, itemStackTooltip));
								}
							}

							if (guiPage.isRightPage()) {
								RenderUtils.renderItemIntoGUI(super.itemRender, itemStack, xR, y - 5);
								RenderUtils.renderItemOverlayIntoGUI(super.itemRender, Font.normal.fontRenderer, itemStack, xR, y - 5, null);
								if (itemStackTooltip != null && itemStackTooltip.size() > 0) {
									this.tooltips.add(new GuiTooltip(xR, y - 5, 16, 16, itemStackTooltip));
								}
							}                   	

							y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y + LINE_SPACE_Y_IMAGE;
						}

						y = yTop;
						startIndex += SALESHISTORY_PER_PAGE;
						endIndex = Math.min(salesHistory.size(), startIndex + SALESHISTORY_PER_PAGE);
					}
				}
			}
		}
	}

	private void drawPageEnemy(int mouseX, int mouseY, float partialTicks, GuiPage guiPage) {
		drawPageHeader(mouseX, mouseY, partialTicks, guiPage);
		drawPageFooter(mouseX, mouseY, partialTicks, guiPage);

		int y = this.y + PAGE_BODY_Y;
		int indentX = 10;

		GuiHyperlink button = null;
		GuiTooltip toolTip = null;
		int x1 = 0;
		int x2 = 0;

		String[] dataKey = getPageKeyParts(guiPage.getDataKey());
		EnemiesData enemiesData = this.villageData.getEnemiesData();

		if (dataKey[0].equals("")) {
			// enemy list

			String pageHeader = TextUtils.translate("tektopiaBook.enemies.enemies");

			if (!StringUtils.isNullOrWhitespace(pageHeader)) {
				pageHeader = TextFormatting.DARK_BLUE + pageHeader;

				if (guiPage.isLeftPage()) {
					Font.normal.printLeft(pageHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
				}

				if (guiPage.isRightPage()) {
					Font.normal.printLeft(pageHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
				}
			} 

			y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

			List<EnemyData> enemies = enemiesData.getEnemies();

			if (enemies != null) {            	
				int page = 0;
				try {
					page = Integer.parseInt(dataKey[1]);
				}
				catch (NumberFormatException e) {
					page = 0;
				}
				int startIndex = page * ENEMIES_PER_PAGE;
				int index = 0;

				String enemyHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.enemy");
				String positionHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.position");

				if (guiPage.isLeftPage()) {
					Font.small.printLeft(enemyHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
					Font.small.printRight(positionHeader, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y);
				}

				if (guiPage.isRightPage()) {
					Font.small.printLeft(enemyHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
					Font.small.printRight(positionHeader, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y);
				}

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y; 

				for (EnemyData enemy : enemies) {
					if (index >= startIndex && index < startIndex + ENEMIES_PER_PAGE) {
						String enemyName = "";
						String enemyPosition = "";

						enemyName += enemy.getName();
						enemyPosition += formatBlockPos(enemy.getCurrentPosition());

						if (guiPage.isLeftPage()) {
							Font.small.printLeft(enemyName, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
							Font.small.printRight(enemyPosition, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y);
						}

						if (guiPage.isRightPage()) {
							Font.small.printLeft(enemyName, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
							Font.small.printRight(enemyPosition, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y);

							x1 = this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX;
							x2 = x1 + Font.small.getStringWidth(enemyName);
						}

						if (!this.isSubPageOpen()) {
							button = new GuiHyperlink(BUTTON_KEY_MAPLINK, getHyperlinkData(GuiPageType.ENEMY, getResidentDetailPageKey(enemy.getId())));
							button.setIcon(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT);
							this.buttons.add(button);

							toolTip = new GuiTooltip(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT, TextUtils.translate("tektopiaBook.links.mapdetails"));
							this.tooltips.add(toolTip);
						}

						y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
					}  
					index++;
				}
			}
		} else {
			// enemy

			int enemyId = 0;
			try {
				enemyId = Integer.parseInt(dataKey[0]);
			}
			catch (NumberFormatException e) {
				enemyId = 0;
			}

			EnemyData enemy = enemyId > 0 ? enemiesData.getEnemyById(enemyId) : null;

			if (enemy != null) {

				String header = stripTextFormatting(enemy.getName());

				if (!StringUtils.isNullOrWhitespace(header)) {
					// check if this is the villager we clicked on
					if (this.villageData.getEntityId() > 0 && this.villageData.getEntityId() == enemyId) {
						header = TextFormatting.UNDERLINE + header;
					}

					if (guiPage.isLeftPage()) {
						Font.normal.printLeft(header, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
					}

					if (guiPage.isRightPage()) {
						Font.normal.printLeft(header, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
					}
				} 

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

				{
					int tXL = this.x + (PAGE_LEFTPAGE_RIGHTMARGIN_X - PROFESSION_WIDTH);
					int tXR = this.x + (PAGE_RIGHTPAGE_RIGHTMARGIN_X - PROFESSION_WIDTH);
					int tY = y;

					String modId = enemy.getModId();
					if (modId.equals(ModDetails.MOD_ID_TEKTOPIA))
						modId = ModDetails.MOD_ID;
					String className = enemy.getClassName();
					int level = enemy.getLevel();

					ResourceLocation visitorResource = new ResourceLocation(modId, "textures/enemies/" + className + "_" + level + ".png");

					if (visitorResource != null) {
						GlStateManager.pushMatrix();
						GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

						mc.getTextureManager().bindTexture(visitorResource);

						if (guiPage.isLeftPage()) {
							super.drawModalRectWithCustomSizedTexture(tXL, tY, 0, 0, PROFESSION_WIDTH, PROFESSION_HEIGHT, 56, 90);
						}

						if (guiPage.isRightPage()) {
							super.drawModalRectWithCustomSizedTexture(tXR, tY, 0, 0, PROFESSION_WIDTH, PROFESSION_HEIGHT, 56, 90);
						}	             

						GlStateManager.popMatrix();   	
					}
				}
			}
		}
	}

	private void drawPageHome(int mouseX, int mouseY, float partialTicks, GuiPage guiPage) {
		drawPageHeader(mouseX, mouseY, partialTicks, guiPage);
		drawPageFooter(mouseX, mouseY, partialTicks, guiPage); 

		int y = this.y + PAGE_BODY_Y;
		int indentX = 10;

		GuiHyperlink button = null;
		GuiTooltip toolTip = null;
		int x1 = 0;
		int x2 = 0;
		int x3 = 0;
		int x4 = 0;

		String[] dataKey = getPageKeyParts(guiPage.getDataKey());
		HomesData homesData = this.villageData.getHomesData();
		String continued = TextUtils.translate("tektopiaBook.continued");

		if (dataKey[0].equals("")) {
			// home type

			String typeHeader = TextUtils.translate("tektopiaBook.homes.hometypes");

			if (!StringUtils.isNullOrWhitespace(typeHeader)) {
				typeHeader = TextFormatting.DARK_BLUE + typeHeader;

				if (guiPage.isLeftPage()) {
					Font.normal.printLeft(typeHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
				}

				if (guiPage.isRightPage()) {
					Font.normal.printLeft(typeHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
				}
			} 

			y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

			Map<VillageStructureType, Integer> homeTypeCounts = homesData != null ? homesData.getHomeTypeCounts() : null;

			if (homeTypeCounts != null) {
				final int[] maxLength = { 0 };

				int page = 0;
				try {
					page = Integer.parseInt(dataKey[1]);
				}
				catch (NumberFormatException e) {
					page = 0;
				}
				int startIndex = page * HOMETYPES_PER_PAGE;
				int index = 0;

				String nameHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.name");
				String countHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.count");

				maxLength[0] += Font.small.getStringWidth(countHeader);

				if (guiPage.isLeftPage()) {
					Font.small.printLeft(nameHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
					Font.small.printLeft(countHeader, this.x + PAGE_LEFTPAGE_CENTER_X, y); 
				}

				if (guiPage.isRightPage()) {
					Font.small.printLeft(nameHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
					Font.small.printLeft(countHeader, this.x + PAGE_RIGHTPAGE_CENTER_X, y); 
				}

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

				for (VillageStructureType homeType : TektopiaUtils.getHomeStructureTypes()) {
					if (index >= startIndex && index < startIndex + HOMETYPES_PER_PAGE) {
						String typeName = getStructureTypeName(homeType);
						int typeCount = 0;

						if (homeTypeCounts.containsKey(homeType)) {
							typeCount = homeTypeCounts.get(homeType);
						}

						if (guiPage.isLeftPage()) {
							Font.small.printLeft(typeName, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
							Font.small.printRight(typeCount, this.x + PAGE_LEFTPAGE_CENTER_X + maxLength[0], y); 

							x1 = this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX;
							x2 = x1 + Font.small.getStringWidth(typeName);
						}

						if (guiPage.isRightPage()) {
							Font.small.printLeft(typeName, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
							Font.small.printRight(typeCount, this.x + PAGE_RIGHTPAGE_CENTER_X + maxLength[0], y); 

							x1 = this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX;
							x2 = x1 + Font.small.getStringWidth(typeName);
						}

						if (!this.isSubPageOpen() && typeCount > 0) {
							button = new GuiHyperlink(BUTTON_KEY_HOMELINK, getHyperlinkData(GuiPageType.HOMETYPE, getStructureTypePageKey(homeType)));
							button.setIcon(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT);
							this.buttons.add(button);

							toolTip = new GuiTooltip(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT, TextUtils.translate("tektopiaBook.links.homedetails"));
							this.tooltips.add(toolTip);
						}

						y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
					}  
					index++;
				}
			}

		} else {
			// home

			int homeId = 0;
			try {
				homeId = Integer.parseInt(dataKey[0]);
			}
			catch (NumberFormatException e) {
				homeId = 0;
			}

			HomeData homeData = homesData.getHomeById(homeId);
			if (homeData != null) {

				String header = homeData.getStructureTypeName();

				if (!StringUtils.isNullOrWhitespace(header)) {
					// check if this is the villager we clicked on
					if (this.villageData.getStructureId() > 0 && this.villageData.getStructureId() == homeData.getHomeId()) {
						header = TextFormatting.UNDERLINE + header;
					}
					else if (this.villageData.getFramePosition() != null && this.villageData.getFramePosition().equals(homeData.getFramePosition())) {
						header = TextFormatting.UNDERLINE + header;
					}      
					if (!dataKey[1].equals("0") && !StringUtils.isNullOrWhitespace(continued)) {
						header += " " + continued;
					}
					header = TextFormatting.DARK_BLUE + header;

					if (guiPage.isLeftPage()) {
						Font.normal.printLeft(header, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
					}

					if (guiPage.isRightPage()) {
						Font.normal.printLeft(header, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
					}
				} 

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

				String framePositionLabel = TextUtils.translate("tektopiaBook.homes.frameposition");
				String framePositionText = "";

				if (!StringUtils.isNullOrWhitespace(framePositionLabel)) {
					framePositionText += formatBlockPos(homeData.getFramePosition());

					if (guiPage.isLeftPage()) {
						Font.small.printLeft(framePositionLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
						Font.small.printLeft(framePositionText, this.x + PAGE_LEFTPAGE_CENTER_X, y); 
						
						x1 = this.x + PAGE_LEFTPAGE_CENTER_X;
						x2 = x1 + Font.small.getStringWidth(framePositionText);
					}

					if (guiPage.isRightPage()) {
						Font.small.printLeft(framePositionLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
						Font.small.printLeft(framePositionText, this.x + PAGE_RIGHTPAGE_CENTER_X, y); 
						
						x1 = this.x + PAGE_RIGHTPAGE_CENTER_X;
						x2 = x1 + Font.small.getStringWidth(framePositionText);
					}

					if (!this.isSubPageOpen()) {
						button = new GuiHyperlink(BUTTON_KEY_MAPLINK, getHyperlinkData(GuiPageType.HOME, getStructureDetailPageKey(homeData.getHomeId())));
						button.setIcon(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT);
						this.buttons.add(button);

						toolTip = new GuiTooltip(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT, TextUtils.translate("tektopiaBook.links.mapdetails"));
						this.tooltips.add(toolTip);
					}
				} 

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

				String residentsLabel = TextUtils.translate("tektopiaBook.homes.totalresidents");
				String residentsText = "";

				if (!StringUtils.isNullOrWhitespace(residentsLabel)) {
					residentsText += "" + homeData.getResidentsCount();

					if (guiPage.isLeftPage()) {
						Font.small.printLeft(residentsLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
						Font.small.printRight(residentsText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y);
					}

					if (guiPage.isRightPage()) {
						Font.small.printLeft(residentsLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
						Font.small.printRight(residentsText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y);
					}
				}

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

				if (dataKey[1].equals("0")) {
					String adultLabel = TextUtils.translate("tektopiaBook.homes.adults");
					String adultText = "";

					if (!StringUtils.isNullOrWhitespace(adultLabel)) {
						adultText += "" + homeData.getAdultCount();

						if (guiPage.isLeftPage()) {
							Font.small.printLeft(adultLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
							Font.small.printRight(adultText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y);
						}

						if (guiPage.isRightPage()) {
							Font.small.printLeft(adultLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
							Font.small.printRight(adultText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y);
						}
					}

					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

					String childLabel = TextUtils.translate("tektopiaBook.homes.children");
					String childText = "";

					if (!StringUtils.isNullOrWhitespace(childLabel)) {
						childText += "" + homeData.getChildCount();

						if (guiPage.isLeftPage()) {
							Font.small.printLeft(childLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
							Font.small.printRight(childText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y);
						}

						if (guiPage.isRightPage()) {
							Font.small.printLeft(childLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
							Font.small.printRight(childText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y);
						}
					}

					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

					String maleLabel = TextUtils.translate("tektopiaBook.homes.males");
					String maleText = "";

					if (!StringUtils.isNullOrWhitespace(maleLabel)) {
						maleText += "" + homeData.getMaleCount();

						if (guiPage.isLeftPage()) {
							Font.small.printLeft(maleLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
							Font.small.printRight(maleText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y);
						}

						if (guiPage.isRightPage()) {
							Font.small.printLeft(maleLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
							Font.small.printRight(maleText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y);
						}
					}

					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

					String femaleLabel = TextUtils.translate("tektopiaBook.homes.females");
					String femaleText = "";

					if (!StringUtils.isNullOrWhitespace(femaleLabel)) {
						femaleText += "" + homeData.getFemaleCount();

						if (guiPage.isLeftPage()) {
							Font.small.printLeft(femaleLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
							Font.small.printRight(femaleText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y); 
						}

						if (guiPage.isRightPage()) {
							Font.small.printLeft(femaleLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
							Font.small.printRight(femaleText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y);
						}
					}

					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y + LINE_SPACE_Y_HEADER;

					String tilesperoccupantLabel = TextUtils.translate("tektopiaBook.homes.tileinformation");

					if (!StringUtils.isNullOrWhitespace(tilesperoccupantLabel)) {
						tilesperoccupantLabel = TextFormatting.DARK_BLUE + tilesperoccupantLabel;

						if (guiPage.isLeftPage()) {
							Font.small.printLeft(tilesperoccupantLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y);
						}

						if (guiPage.isRightPage()) {
							Font.small.printLeft(tilesperoccupantLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
						}
					}

					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

					String currentBedsLabel = TextUtils.translate("tektopiaBook.homes.currentbeds");
					String currentBedsText = "";
					String maxBedsText = "";

					if (!StringUtils.isNullOrWhitespace(currentBedsLabel)) {
						currentBedsText += "" + homeData.getBedCount();
						maxBedsText += " / " + homeData.getMaxBeds();
						
						if (guiPage.isLeftPage()) {
							Font.small.printLeft(currentBedsLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
							Font.small.printRight(currentBedsText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y); 
							Font.small.printLeft(maxBedsText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y); 
						}

						if (guiPage.isRightPage()) {
							Font.small.printLeft(currentBedsLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
							Font.small.printRight(currentBedsText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y);  
							Font.small.printLeft(maxBedsText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y); 
						}
					}

					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

					String floorTilesLabel = TextUtils.translate("tektopiaBook.homes.tilesperbed");
					String floorTilesText = "";

					if (!StringUtils.isNullOrWhitespace(floorTilesLabel)) {
						floorTilesText += "" + homeData.getTilesPerVillager();

						if (guiPage.isLeftPage()) {
							Font.small.printLeft(floorTilesLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
							Font.small.printRight(floorTilesText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y); 
						}

						if (guiPage.isRightPage()) {
							Font.small.printLeft(floorTilesLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
							Font.small.printRight(floorTilesText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y); 
						}
					}

					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

					floorTilesLabel = TextUtils.translate("tektopiaBook.homes.requiredtiles");
					floorTilesText = "";

					if (!StringUtils.isNullOrWhitespace(floorTilesLabel)) {
						floorTilesText += "" + homeData.getTilesPerVillager() * homeData.getBedCount();

						if (guiPage.isLeftPage()) {
							Font.small.printLeft(floorTilesLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
							Font.small.printRight(floorTilesText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y); 
						}

						if (guiPage.isRightPage()) {
							Font.small.printLeft(floorTilesLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
							Font.small.printRight(floorTilesText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y); 
						}
					}

					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

					floorTilesLabel = TextUtils.translate("tektopiaBook.homes.currenttiles");
					floorTilesText = "";

					if (!StringUtils.isNullOrWhitespace(floorTilesLabel)) {
						floorTilesText += "" + homeData.getFloorTileCount();
						if (homeData.getFloorTileCount() < homeData.getTilesPerVillager() * homeData.getBedCount()) {
							floorTilesText = TextFormatting.DARK_RED + floorTilesText;
						} else {
							floorTilesText = TextFormatting.DARK_GREEN + floorTilesText;
						}

						if (guiPage.isLeftPage()) {
							Font.small.printLeft(floorTilesLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
							Font.small.printRight(floorTilesText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y); 
						}

						if (guiPage.isRightPage()) {
							Font.small.printLeft(floorTilesLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
							Font.small.printRight(floorTilesText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y); 
						}
					}
				} else {

					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;  
					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;  
					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y + LINE_SPACE_Y_HEADER; 
					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;  
					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;  
					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;  
					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;             
				}

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y + LINE_SPACE_Y_HEADER;

				String bedsText = TextUtils.translate("tektopiaBook.homes.beds");

				if (!StringUtils.isNullOrWhitespace(bedsText)) {
					bedsText = TextFormatting.DARK_BLUE + bedsText;

					if (guiPage.isLeftPage()) {
						Font.small.printLeft(bedsText, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
					}

					if (guiPage.isRightPage()) {
						Font.small.printLeft(bedsText, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
					}
				} 

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

				List<BlockPos> bedPositions = homeData.getBedPositions(); 

				final int[] maxLength = { 0 };
				maxLength[0] += LABEL_TRAILINGSPACE_X;

				int page = 0;
				try {
					page = Integer.parseInt(dataKey[1]);
				}
				catch (NumberFormatException e) {
					page = 0;
				}
				int startIndex = page * HOMEBEDS_PER_PAGE;

				String indexHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.index");
				String positionHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.position");
				String residentHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.resident");
				String levelHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.level");

				if (guiPage.isLeftPage()) {
					Font.small.printRight(indexHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
					Font.small.printLeft(positionHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y); 
					Font.small.printLeft(residentHeader, this.x + PAGE_LEFTPAGE_CENTER_X, y);
					Font.small.printRight(levelHeader, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y);
				}

				if (guiPage.isRightPage()) {
					Font.small.printRight(indexHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
					Font.small.printLeft(positionHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y); 
					Font.small.printLeft(residentHeader, this.x + PAGE_RIGHTPAGE_CENTER_X, y);
					Font.small.printRight(levelHeader, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y);
				}

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y; 

				for (int bedIndex = 0; bedIndex < homeData.getMaxBeds(); bedIndex++) {
					if (bedIndex >= startIndex && bedIndex < startIndex + HOMEBEDS_PER_PAGE) {
						String bedText = "" + (bedIndex + 1);
						String bedPositionText = "";

						String residentName = "";
						String residentProfession = "";
						String residentLevel = "";

						BlockPos bedPosition = null;
						ResidentData residentData = null;

						int y1 = 0;
						int y2 = 0;

						bedPosition = bedIndex < bedPositions.size() ? bedPositions.get(bedIndex) : null;
						if (bedPosition == null) {
							bedPositionText += TextFormatting.DARK_RED + TextUtils.translate("tektopiaBook.missing");

						} else {
							bedPositionText += "" + formatBlockPos(bedPosition);

							residentData = homeData.getResidentByBedPosition(bedPosition);

							if (residentData != null) {
								residentName += formatResidentName(residentData.isMale(), residentData.getName(), true);
								residentProfession += getTypeName(residentData.getProfessionType());

								ProfessionType professionType = TektopiaUtils.getProfessionType(residentData.getProfessionType());
								if (professionType != null) {
									switch (professionType) {
									case CHILD:
									case NITWIT:
										break;
									default:                       			
										residentLevel = formatResidentLevel(residentData.getLevel(), residentData.getBaseLevel(), false, false);
										break;
									}
								}
							} else {
								residentName = TextFormatting.GOLD + TextUtils.translate("tektopiaBook.empty");
							}
						}   

						if (guiPage.isLeftPage()) {
							Font.small.printRight(bedText, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
							Font.small.printLeft(bedPositionText, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y);
							Font.small.printLeft(residentName, this.x + PAGE_LEFTPAGE_CENTER_X, y);
							Font.small.printRight(residentLevel, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y);

							x1 = this.x + PAGE_LEFTPAGE_CENTER_X;
							x2 = x1 + Font.small.getStringWidth(residentName);
							y1 = y;

							y += Font.small.fontRenderer.FONT_HEIGHT;

							Font.small.printLeft(residentProfession, this.x + PAGE_LEFTPAGE_CENTER_X, y);

							x3 = this.x + PAGE_LEFTPAGE_CENTER_X;
							x4 = x3 + Font.small.getStringWidth(residentProfession);
							y2 = y;
						}

						if (guiPage.isRightPage()) {
							Font.small.printRight(bedText, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
							Font.small.printLeft(bedPositionText, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y); 
							Font.small.printLeft(residentName, this.x + PAGE_RIGHTPAGE_CENTER_X, y);
							Font.small.printRight(residentLevel, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y);

							x1 = this.x + PAGE_RIGHTPAGE_CENTER_X;
							x2 = x1 + Font.small.getStringWidth(residentName);
							y1 = y;

							y += Font.small.fontRenderer.FONT_HEIGHT;

							Font.small.printLeft(residentProfession, this.x + PAGE_RIGHTPAGE_CENTER_X, y);

							x3 = this.x + PAGE_RIGHTPAGE_CENTER_X;
							x4 = x3 + Font.small.getStringWidth(residentProfession);
							y2 = y;
						}

						if (!this.isSubPageOpen() && residentData != null) {
							button = new GuiHyperlink(BUTTON_KEY_RESIDENTLINK, getHyperlinkData(GuiPageType.RESIDENT, getResidentDetailPageKey(residentData.getId())));
							button.setIcon(x1, y1, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT);
							this.buttons.add(button);

							toolTip = new GuiTooltip(x1, y1, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT, TextUtils.translate("tektopiaBook.links.residentdetails"));
							this.tooltips.add(toolTip);

							button = new GuiHyperlink(BUTTON_KEY_PROFESSIONLINK, getHyperlinkData(GuiPageType.PROFESSIONTYPE, getProfessionDetailPageKey(residentProfession)));
							button.setIcon(x3, y2, x4 - x3, Font.small.fontRenderer.FONT_HEIGHT);
							this.buttons.add(button);

							toolTip = new GuiTooltip(x3, y2, x4 - x3, Font.small.fontRenderer.FONT_HEIGHT, TextUtils.translate("tektopiaBook.links.professiondetails"));
							this.tooltips.add(toolTip); 
						}

						y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
					}
				}
			}
		}
	}

	private void drawPageHomeType(int mouseX, int mouseY, float partialTicks, GuiPage guiPage) {
		drawPageHeader(mouseX, mouseY, partialTicks, guiPage);
		drawPageFooter(mouseX, mouseY, partialTicks, guiPage); 

		int y = this.y + PAGE_BODY_Y;
		int indentX = 10;

		GuiHyperlink button = null;
		GuiTooltip toolTip = null;
		int x1 = 0;
		int x2 = 0;
		int x3 = 0;
		int x4 = 0;

		String[] dataKey = getPageKeyParts(guiPage.getDataKey());
		VillageStructureType homeType = VillageStructureType.valueOf(dataKey[0]);

		HomesData homesData = this.villageData.getHomesData();
		Map<VillageStructureType, Integer> homeTypeCounts = homesData != null ? homesData.getHomeTypeCounts() : null;
		List<HomeData> homes = homesData != null ? homesData.getHomesByType(homeType) : null;

		String typeName = getStructureTypeName(homeType);
		String continued = TextUtils.translate("tektopiaBook.continued");
		String summary = TextUtils.translate("tektopiaBook.summary");

		if (!StringUtils.isNullOrWhitespace(typeName)) {
			typeName += " " + summary;
			if (!dataKey[1].equals("0") && !StringUtils.isNullOrWhitespace(continued)) {
				typeName += " " + continued;
			}
			typeName = TextFormatting.DARK_BLUE + typeName;

			if (guiPage.isLeftPage()) {
				Font.normal.printLeft(typeName, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
			}

			if (guiPage.isRightPage()) {
				Font.normal.printLeft(typeName, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
			}
		} 

		y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

		if (dataKey[1].equals("0")) {
			String totalLabel = TextUtils.translate("tektopiaBook.hometypes.totalhomes");
			String totalText = "";

			if (!StringUtils.isNullOrWhitespace(totalLabel)) {
				if (homeTypeCounts != null && homeTypeCounts.containsKey(homeType)) {
					totalText += "" + homeTypeCounts.get(homeType);
				}

				if (guiPage.isLeftPage()) {
					Font.small.printLeft(totalLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
					Font.small.printRight(totalText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y); 
				}

				if (guiPage.isRightPage()) {
					Font.small.printLeft(totalLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
					Font.small.printRight(totalText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y);
				}
			}

			y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

			String residentsLabel = TextUtils.translate("tektopiaBook.homes.totalresidents");
			String residentsText = "";

			if (!StringUtils.isNullOrWhitespace(residentsLabel)) {
				if (this.villageData.getResidentsData() != null)
					residentsText += "" + homesData.getResidentCountByType(homeType);

				if (guiPage.isLeftPage()) {
					Font.small.printLeft(residentsLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
					Font.small.printRight(residentsText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y);
				}

				if (guiPage.isRightPage()) {
					Font.small.printLeft(residentsLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
					Font.small.printRight(residentsText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y); 
				}
			}           
		}

		y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y + LINE_SPACE_Y_HEADER;

		String header = TextUtils.translate("tektopiaBook.hometypes.frameposition");

		if (!StringUtils.isNullOrWhitespace(header)) {
			header = TextFormatting.DARK_BLUE + header;

			if (guiPage.isLeftPage()) {
				Font.small.printLeft(header, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
			}

			if (guiPage.isRightPage()) {
				Font.small.printLeft(header, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
			}
		} 

		y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

		if (homes != null) {
			final int[] maxLength = { 0 , 0 };
			maxLength[0] += LABEL_TRAILINGSPACE_X;

			int page = 0;
			try {
				page = Integer.parseInt(dataKey[1]);
			}
			catch (NumberFormatException e) {
				page = 0;
			}
			int startIndex = page * HOMES_PER_PAGE;
			int index = 0;

			String indexHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.index");
			String positionHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.position");
			String residentsHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.hometypes.residents");
			String validTextHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.valid");

			maxLength[1] += Font.small.getStringWidth(residentsHeader);

			if (guiPage.isLeftPage()) {
				Font.small.printRight(indexHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
				Font.small.printLeft(positionHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y); 
				Font.small.printLeft(residentsHeader, this.x + PAGE_LEFTPAGE_CENTER_X, y);
				Font.small.printRight(validTextHeader, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y);
			}

			if (guiPage.isRightPage()) {
				Font.small.printRight(indexHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
				Font.small.printLeft(positionHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y); 
				Font.small.printLeft(residentsHeader, this.x + PAGE_RIGHTPAGE_CENTER_X, y);
				Font.small.printRight(validTextHeader, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y);
			}

			y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y; 

			for (HomeData home : homes) {
				if (index >= startIndex && index < startIndex + HOMES_PER_PAGE) {

					String indexName = "" + (index + 1);
					String framePosition = formatBlockPos(home.getFramePosition());
					if (this.villageData.getStructureId() > 0 && this.villageData.getStructureId() == home.getHomeId()) {
						framePosition = TextFormatting.UNDERLINE + framePosition;
					}
					else if (this.villageData.getFramePosition() != null && home.getFramePosition() != null && this.villageData.getFramePosition().equals(home.getFramePosition())) {
						framePosition = TextFormatting.UNDERLINE + framePosition;
					}
					String residentText = "" + (home.getResidentsCount() >= 0 ? home.getResidentsCount() : "0");
					String validText = "" + (home.isValid() ? TextUtils.SYMBOL_GREENTICK : TextUtils.SYMBOL_REDCROSS);

					if (guiPage.isLeftPage()) {
						Font.small.printRight(indexName, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
						Font.small.printLeft(framePosition, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y); 
						Font.small.printRight(residentText, this.x + PAGE_LEFTPAGE_CENTER_X + maxLength[1], y); 
						Font.small.printRight(validText, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y); 

						x2 = this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX;
						x1 = x2 - Font.small.getStringWidth(indexName);
						x3 = this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0];
						x4 = x3 + Font.small.getStringWidth(framePosition);
					}

					if (guiPage.isRightPage()) {
						Font.small.printRight(indexName, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
						Font.small.printLeft(framePosition, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y); 
						Font.small.printRight(residentText, this.x + PAGE_RIGHTPAGE_CENTER_X + maxLength[1], y);
						Font.small.printRight(validText, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y);

						x2 = this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX;
						x1 = x2 - Font.small.getStringWidth(indexName);
						x3 = this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0];
						x4 = x3 + Font.small.getStringWidth(framePosition);
					}

					if (!this.isSubPageOpen()) {
						button = new GuiHyperlink(BUTTON_KEY_HOMELINK, getHyperlinkData(GuiPageType.HOME, getStructureDetailPageKey(home.getHomeId())));
						button.setIcon(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT);
						this.buttons.add(button);

						toolTip = new GuiTooltip(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT, TextUtils.translate("tektopiaBook.links.homedetails"));
						this.tooltips.add(toolTip);
						
						button = new GuiHyperlink(BUTTON_KEY_MAPLINK, getHyperlinkData(GuiPageType.HOME, getStructureDetailPageKey(home.getHomeId())));
						button.setIcon(x3, y, x4 - x3, Font.small.fontRenderer.FONT_HEIGHT);
						this.buttons.add(button);

						toolTip = new GuiTooltip(x3, y, x4 - x3, Font.small.fontRenderer.FONT_HEIGHT, TextUtils.translate("tektopiaBook.links.mapdetails"));
						this.tooltips.add(toolTip);
					}

					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;           			
				}
				index++;
			}
		}
	}

	private void drawPageMap(int mouseX, int mouseY, float partialTicks, GuiPage guiPage) {
		drawPageHeader(mouseX, mouseY, partialTicks, guiPage);
		drawPageFooter(mouseX, mouseY, partialTicks, guiPage);

		String[] dataKey = getPageKeyParts(guiPage.getDataKey());

		// do not process page if the datakey is blank
		if (dataKey[0].equals(""))
			return;

		drawSubPageLandscapeBackground(mouseX, mouseY, partialTicks);

		StructureData townHallStructure = this.villageData == null ? null : this.villageData.getTownHall();
		if (townHallStructure == null) 
			return;

		BlockPos villageOrigin = townHallStructure.getFramePosition();

		List<GuiMapMarker> mapMarkers = new ArrayList<GuiMapMarker>();

		// create all the map markers
		if (showMapTownHall) {

			BlockPos structurePoint = townHallStructure.getFramePosition();
			BlockPos villageOffset = structurePoint.subtract(villageOrigin);

			String name = TextFormatting.DARK_GREEN + getStructureTypeName(townHallStructure.getStructureType());
			String position = TextUtils.translate("tektopiaBook.homes.frameposition") + " " + formatBlockPos(structurePoint);

			List<String> tooltips = new ArrayList<String>();
			tooltips.add(name);
			tooltips.add(position);

			int markerSize = MAP_MARKER_SIZE;
			int markerOffset = 0;
			int tooltipSize = MAP_TOOLTIP_SIZE_STRUCTURE;
			int tooltipOffset = 0;
			int priority = 1;
			
			// check if this structure has been selected
			if (this.villageData.getStructureId() > 0 && this.villageData.getStructureId() == townHallStructure.getStructureId()) {
				markerSize *= MAP_MARKER_SELECTED_SCALE;
				markerOffset = markerSize - MAP_MARKER_SIZE;
				tooltipSize *= MAP_MARKER_SELECTED_SCALE;
				tooltipOffset = tooltipSize - MAP_TOOLTIP_SIZE_STRUCTURE;
				priority = 2;
			}
			else if (this.villageData.getFramePosition() != null && this.villageData.getFramePosition().equals(townHallStructure.getFramePosition())) {
				markerSize *= MAP_MARKER_SELECTED_SCALE;
				markerOffset = markerSize - MAP_MARKER_SIZE;
				tooltipSize *= MAP_MARKER_SELECTED_SCALE;
				tooltipOffset = tooltipSize - MAP_TOOLTIP_SIZE_STRUCTURE;
				priority = 2;
			}

			ResourceLocation texture = mapMarkerTownHall;

			GuiTexture icon = new GuiTexture(texture, 
					villageOffset.getX() - (markerOffset / 2), villageOffset.getZ() - markerOffset, markerSize, markerSize, 
					0, 0, markerSize, markerSize);

			GuiTooltip tooltip = new GuiTooltip(villageOffset.getX() - (tooltipOffset / 2), villageOffset.getZ() - markerOffset, 2, 1, tooltipSize, tooltipSize, tooltips);

			GuiMapMarker mapMarker = new GuiMapMarker(getStructureTypePageKey(townHallStructure.getStructureType()), 
					GuiMapMarkerType.TOWNHALL,
					icon,
					villageOffset,
					tooltip);
			mapMarker.setPriority(priority);

			mapMarkers.add(mapMarker);

		}

		if (showMapHomes) {
			HomesData homesData = this.villageData.getHomesData();

			for (HomeData home : homesData.getHomes()) {

				BlockPos structurePoint = home.getFramePosition();
				BlockPos villageOffset = structurePoint.subtract(villageOrigin);

				String name = TextFormatting.DARK_GREEN + getStructureTypeName(home.getStructureType());
				String position = TextUtils.translate("tektopiaBook.homes.frameposition") + " " + formatBlockPos(structurePoint);

				List<String> tooltips = new ArrayList<String>();
				tooltips.add(name);
				tooltips.add(position);

				int markerSize = MAP_MARKER_SIZE;
				int markerOffset = 0;
				int tooltipSize = MAP_TOOLTIP_SIZE_STRUCTURE;
				int tooltipOffset = 0;
				int priority = 1;
				
				// check if this structure has been selected
				if (this.villageData.getStructureId() > 0 && this.villageData.getStructureId() == home.getHomeId()) {
					markerSize *= MAP_MARKER_SELECTED_SCALE;
					markerOffset = markerSize - MAP_MARKER_SIZE;
					tooltipSize *= MAP_MARKER_SELECTED_SCALE;
					tooltipOffset = tooltipSize - MAP_TOOLTIP_SIZE_STRUCTURE;
					priority = 2;
				}
				else if (this.villageData.getFramePosition() != null && this.villageData.getFramePosition().equals(home.getFramePosition())) {
					markerSize *= MAP_MARKER_SELECTED_SCALE;
					markerOffset = markerSize - MAP_MARKER_SIZE;
					tooltipSize *= MAP_MARKER_SELECTED_SCALE;
					tooltipOffset = tooltipSize - MAP_TOOLTIP_SIZE_STRUCTURE;
					priority = 2;
				}

				ResourceLocation texture = mapMarkerHome;

				GuiTexture icon = new GuiTexture(texture, 
						villageOffset.getX() - (markerOffset / 2), villageOffset.getZ() - markerOffset, markerSize, markerSize, 
						0, 0, markerSize, markerSize);

				GuiTooltip tooltip = new GuiTooltip(villageOffset.getX() - (tooltipOffset / 2), villageOffset.getZ() - markerOffset, 2, 1, tooltipSize, tooltipSize, tooltips);

				GuiMapMarker mapMarker = new GuiMapMarker(getStructureDetailPageKey(home.getHomeId()), 
						GuiMapMarkerType.HOME,
						icon,
						villageOffset,
						tooltip);
				mapMarker.setPriority(priority);

				mapMarkers.add(mapMarker);
			}
		}

		if (showMapStructures) {
			StructuresData structuresData = this.villageData.getStructuresData();
			List<VillageStructureType> homeTypes = TektopiaUtils.getHomeStructureTypes();

			for (StructureData structure : structuresData.getStructures()) {
				if (structure.getStructureType() == VillageStructureType.TOWNHALL)
					continue;
				if (homeTypes.contains(structure.getStructureType())) 
					continue;

				BlockPos structurePoint = structure.getFramePosition();
				BlockPos villageOffset = structurePoint.subtract(villageOrigin);

				String name = TextFormatting.DARK_GREEN + getStructureTypeName(structure.getStructureType());
				String position = TextUtils.translate("tektopiaBook.homes.frameposition") + " " + formatBlockPos(structurePoint);

				List<String> tooltips = new ArrayList<String>();
				tooltips.add(name);
				tooltips.add(position);

				int markerSize = MAP_MARKER_SIZE;
				int markerOffset = 0;
				int tooltipSize = MAP_TOOLTIP_SIZE_STRUCTURE;
				int tooltipOffset = 0;
				int priority = 1;
				
				// check if this structure has been selected
				if (this.villageData.getStructureId() > 0 && this.villageData.getStructureId() == structure.getStructureId()) {
					markerSize *= MAP_MARKER_SELECTED_SCALE;
					markerOffset = markerSize - MAP_MARKER_SIZE;
					tooltipSize *= MAP_MARKER_SELECTED_SCALE;
					tooltipOffset = tooltipSize - MAP_TOOLTIP_SIZE_STRUCTURE;
					priority = 2;
				}
				else if (this.villageData.getFramePosition() != null && this.villageData.getFramePosition().equals(structure.getFramePosition())) {
					markerSize *= MAP_MARKER_SELECTED_SCALE;
					markerOffset = markerSize - MAP_MARKER_SIZE;
					tooltipSize *= MAP_MARKER_SELECTED_SCALE;
					tooltipOffset = tooltipSize - MAP_TOOLTIP_SIZE_STRUCTURE;
					priority = 2;
				}

				ResourceLocation texture = mapMarkerStructure;

				GuiTexture icon = new GuiTexture(texture, 
						villageOffset.getX() - (markerOffset / 2), villageOffset.getZ() - markerOffset, markerSize, markerSize, 
						0, 0, markerSize, markerSize);

				GuiTooltip tooltip = new GuiTooltip(villageOffset.getX() - (tooltipOffset / 2), villageOffset.getZ() - markerOffset, 2, 1, tooltipSize, tooltipSize, tooltips);

				GuiMapMarker mapMarker = new GuiMapMarker(getStructureTypePageKey(structure.getStructureType()), 
						GuiMapMarkerType.STRUCTURE,
						icon,
						villageOffset,
						tooltip);
				mapMarker.setPriority(priority);

				mapMarkers.add(mapMarker);
			}
		}

		if (showMapResidents) {
			ResidentsData residentsData = this.villageData.getResidentsData();

			for (ResidentData resident : residentsData.getResidents()) {
				BlockPos residentPoint = resident.getCurrentPosition();
				BlockPos villageOffset = residentPoint.subtract(villageOrigin);

				String name = formatResidentName(resident.isMale(), resident.getName(), true);
				String profession = getTypeName(resident.getProfessionType());
				String level = "";
				String health = TextUtils.translate("tektopiaBook.residents.health") + " " + formatResidentStatistic(resident.getHealth(), resident.getMaxHealth(), true);
				String position = TextUtils.translate("tektopiaBook.residents.position") + " " + formatBlockPos(resident.getCurrentPosition());

				ProfessionType professionType = TektopiaUtils.getProfessionType(resident.getProfessionType());
				if (professionType != null) {
					switch (professionType) {
					case CHILD:
					case NITWIT:
						break;
					default:                       			
						level = formatResidentLevel(resident.getLevel(), resident.getBaseLevel(), false, true);
						break;
					}
				}

				List<String> tooltips = new ArrayList<String>();
				tooltips.add(name);
				tooltips.add(profession);
				tooltips.add(level);
				tooltips.add(health);
				tooltips.add(position);

				int markerSize = MAP_MARKER_SIZE;
				int markerOffset = 0;
				int tooltipSize = MAP_TOOLTIP_SIZE_ENTITY;
				int tooltipOffset = 0;
				int priority = 1;
				
				// check if this entity has been selected
				if (this.villageData.getEntityId() > 0 && this.villageData.getEntityId() == resident.getId()) {
					markerSize *= MAP_MARKER_SELECTED_SCALE;
					markerOffset = markerSize - MAP_MARKER_SIZE;
					tooltipSize *= MAP_MARKER_SELECTED_SCALE;
					tooltipOffset = tooltipSize - MAP_TOOLTIP_SIZE_ENTITY;
					priority = 2;
				}

				ResourceLocation texture = resident.isMale() ? mapMarkerResidentMale : mapMarkerResidentFemale;

				GuiTexture icon = new GuiTexture(texture, 
						villageOffset.getX() - (markerOffset / 2), villageOffset.getZ() - markerOffset, markerSize, markerSize, 
						0, 0, markerSize, markerSize);

				GuiTooltip tooltip = new GuiTooltip(villageOffset.getX() - (tooltipOffset / 2), villageOffset.getZ() - markerOffset, 5, 1, tooltipSize, tooltipSize, tooltips);

				GuiMapMarker mapMarker = new GuiMapMarker(getResidentDetailPageKey(resident.getId()), 
						GuiMapMarkerType.RESIDENT,
						icon,
						villageOffset,
						tooltip);
				mapMarker.setPriority(priority);

				mapMarkers.add(mapMarker);
			}
		}

		if (showMapVisitors) {
			VisitorsData visitorsData = this.villageData.getVisitorsData();

			for (VisitorData visitor : visitorsData.getVisitors()) {
				BlockPos visitorPoint = visitor.getCurrentPosition();
				BlockPos villageOffset = visitorPoint.subtract(villageOrigin);

				String name = formatResidentName(visitor.isMale(), visitor.getName(), true);
				String profession = getTypeName(visitor.getProfessionType());
				String professionTypes = TextUtils.translate("tektopiaBook.professions.professiontypes");
				String position = TextUtils.translate("tektopiaBook.residents.position") + " " + formatBlockPos(visitor.getCurrentPosition());

				List<String> tooltips = new ArrayList<String>();
				tooltips.add(name);
				if (!StringUtils.isNullOrWhitespace(profession)) {
					tooltips.add(profession);
				}

				if (visitor.getAdditionalProfessionsCount() > 0) {
					tooltips.add(professionTypes);

					for (Entry<String, Integer> additionalProfessionType : visitor.getAdditionalProfessions().entrySet()) {
						String additionalProfessionText = getTypeName(additionalProfessionType.getKey());
						String additionalProfessionLevel = formatResidentLevel(additionalProfessionType.getValue(), additionalProfessionType.getValue(), false, true);

						tooltips.add(TextFormatting.AQUA + TextUtils.SYMBOL_BULLET + " " + additionalProfessionText + TextUtils.SEPARATOR_DASH + additionalProfessionLevel);
					}
				}

				tooltips.add(position);

				int markerSize = MAP_MARKER_SIZE;
				int markerOffset = 0;
				int tooltipSize = MAP_TOOLTIP_SIZE_ENTITY;
				int tooltipOffset = 0;
				int priority = 1;
				
				// check if this entity has been selected
				if (this.villageData.getEntityId() > 0 && this.villageData.getEntityId() == visitor.getId()) {
					markerSize *= MAP_MARKER_SELECTED_SCALE;
					markerOffset = markerSize - MAP_MARKER_SIZE;
					tooltipSize *= MAP_MARKER_SELECTED_SCALE;
					tooltipOffset = tooltipSize - MAP_TOOLTIP_SIZE_ENTITY;
					priority = 2;
				}

				ResourceLocation texture = mapMarkerVisitor;

				GuiTexture icon = new GuiTexture(texture, 
						villageOffset.getX() - (markerOffset / 2), villageOffset.getZ() - markerOffset, markerSize, markerSize, 
						0, 0, markerSize, markerSize);

				GuiTooltip tooltip = new GuiTooltip(villageOffset.getX() - (tooltipOffset / 2), villageOffset.getZ() - markerOffset, 5, 1, tooltipSize, tooltipSize, tooltips);

				GuiMapMarker mapMarker = new GuiMapMarker(getResidentDetailPageKey(visitor.getId()), 
						GuiMapMarkerType.VISITOR,
						icon,
						villageOffset,
						tooltip);
				mapMarker.setPriority(priority);

				mapMarkers.add(mapMarker);
			}
		}

		if (showMapEnemies) {
			EnemiesData enemiesData = this.villageData.getEnemiesData();

			for (EnemyData enemy : enemiesData.getEnemies()) {
				BlockPos enemyPoint = enemy.getCurrentPosition();
				BlockPos villageOffset = enemyPoint.subtract(villageOrigin);

				String name = TextFormatting.AQUA + enemy.getName();
				String position = TextUtils.translate("tektopiaBook.residents.position") + " " + formatBlockPos(enemy.getCurrentPosition());

				List<String> tooltips = new ArrayList<String>();
				tooltips.add(name);
				tooltips.add(position);

				int markerSize = MAP_MARKER_SIZE;
				int markerOffset = 0;
				int tooltipSize = MAP_TOOLTIP_SIZE_ENTITY;
				int tooltipOffset = 0;
				int priority = 1;
				
				// check if this entity has been selected
				if (this.villageData.getEntityId() > 0 && this.villageData.getEntityId() == enemy.getId()) {
					markerSize *= MAP_MARKER_SELECTED_SCALE;
					markerOffset = markerSize - MAP_MARKER_SIZE;
					tooltipSize *= MAP_MARKER_SELECTED_SCALE;
					tooltipOffset = tooltipSize - MAP_TOOLTIP_SIZE_ENTITY;
					priority = 2;
				}

				ResourceLocation texture = mapMarkerEnemy;

				GuiTexture icon = new GuiTexture(texture, 
						villageOffset.getX() - (markerOffset / 2), villageOffset.getZ() - markerOffset, markerSize, markerSize, 
						0, 0, markerSize, markerSize);

				GuiTooltip tooltip = new GuiTooltip(villageOffset.getX() - (tooltipOffset / 2), villageOffset.getZ() - markerOffset, 5, 1, tooltipSize, tooltipSize, tooltips);

				GuiMapMarker mapMarker = new GuiMapMarker(getResidentDetailPageKey(enemy.getId()), 
						GuiMapMarkerType.ENEMY,
						icon,
						villageOffset,
						tooltip);
				mapMarker.setPriority(priority);

				mapMarkers.add(mapMarker);
			}
		}

		if (showMapPlayer && this.villageData.getPlayerPosition() != null) {
			BlockPos playerPoint = this.villageData.getPlayerPosition();
			BlockPos villageOffset = playerPoint.subtract(villageOrigin);

			String name = TextFormatting.AQUA + "Player";
			String position = TextUtils.translate("tektopiaBook.residents.position") + " " + formatBlockPos(this.villageData.getPlayerPosition());

			List<String> tooltips = new ArrayList<String>();
			tooltips.add(name);
			tooltips.add(position);

			int markerSize = MAP_MARKER_SIZE;
			int markerOffset = 0;
			int tooltipSize = MAP_TOOLTIP_SIZE_ENTITY;
			int tooltipOffset = 0;

			ResourceLocation texture = mapMarkerPlayer;

			GuiTexture icon = new GuiTexture(texture, 
					villageOffset.getX() - (markerOffset / 2), villageOffset.getZ() - markerOffset, markerSize, markerSize, 
					0, 0, markerSize, markerSize);

			GuiTooltip tooltip = new GuiTooltip(villageOffset.getX() - (tooltipOffset / 2), villageOffset.getZ() - markerOffset, 5, 1, tooltipSize, tooltipSize, tooltips);

			GuiMapMarker mapMarker = new GuiMapMarker(getPageKey("player", 0), 
					GuiMapMarkerType.PLAYER,
					icon,
					villageOffset,
					tooltip);

			mapMarkers.add(mapMarker);	
		}

		// Sort the map markers so that the markers are ordered
		Collections.sort(mapMarkers);

		int x = this.x + MAP_PAGE_LEFT_X;
		int y = this.y + MAP_PAGE_TOP_Y;
		int indentX = 0;

		// display village details
		indentX = 120;

		String originLabel = TextUtils.translate("tektopiaBook.village.origin");
		String originText = "";

		if (!StringUtils.isNullOrWhitespace(originLabel)) {
			originText += formatBlockPos(this.villageData.getVillageOrigin());

			Font.small.printLeft(originLabel, x, y); 
			Font.small.printRight(originText, x + indentX, y);
		}

		y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y + LINE_SPACE_Y_HEADER;

		String boundaryLabel = TextUtils.translate("tektopiaBook.village.boundarynorthwest");
		String boundaryText = "";

		if (!StringUtils.isNullOrWhitespace(boundaryLabel)) {
			boundaryText += formatBlockPos(this.villageData.getVillageNorthWestCorner());

			Font.small.printLeft(boundaryLabel, x, y); 
			Font.small.printRight(boundaryText, x + indentX, y);
		} 

		y += Font.small.fontRenderer.FONT_HEIGHT;

		boundaryLabel = TextUtils.translate("tektopiaBook.village.boundarynortheast");
		boundaryText = "";

		if (boundaryLabel != null &&boundaryLabel.trim() != "") {
			boundaryText += formatBlockPos(this.villageData.getVillageNorthEastCorner());

			Font.small.printLeft(boundaryLabel, x, y); 
			Font.small.printRight(boundaryText, x + indentX, y);
		}

		y += Font.small.fontRenderer.FONT_HEIGHT;

		boundaryLabel = TextUtils.translate("tektopiaBook.village.boundarysouthwest");
		boundaryText = "";

		if (!StringUtils.isNullOrWhitespace(boundaryLabel)) {
			boundaryText += formatBlockPos(this.villageData.getVillageSouthWestCorner());

			Font.small.printLeft(boundaryLabel, x, y); 
			Font.small.printRight(boundaryText, x + indentX, y); 
		}

		y += Font.small.fontRenderer.FONT_HEIGHT;

		boundaryLabel = TextUtils.translate("tektopiaBook.village.boundarysoutheast");
		boundaryText = "";

		if (!StringUtils.isNullOrWhitespace(boundaryLabel)) {
			boundaryText += formatBlockPos(this.villageData.getVillageSouthEastCorner());

			Font.small.printLeft(boundaryLabel, x, y); 
			Font.small.printRight(boundaryText, x + indentX, y); 
		}

		y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y + LINE_SPACE_Y_HEADER;

		String structuresLabel = TextUtils.translate("tektopiaBook.structuretypes.total");
		String structuresText = "";

		if (!StringUtils.isNullOrWhitespace(structuresLabel)) {
			if (this.villageData.getStructuresData() != null)
				structuresText += "" + this.villageData.getStructuresData().getStructuresCount();

			Font.small.printLeft(structuresLabel, x, y);
			Font.small.printRight(structuresText, x + indentX, y);
		}

		y += Font.small.fontRenderer.FONT_HEIGHT;

		String homesLabel = TextUtils.translate("tektopiaBook.hometypes.totalhomes");
		String homesText = "";

		if (!StringUtils.isNullOrWhitespace(homesLabel)) {
			if (this.villageData.getStructuresData() != null)
				homesText += "" + this.villageData.getHomesData().getHomesCount();

			Font.small.printLeft(homesLabel, x, y);
			Font.small.printRight(homesText, x + indentX, y);
		}

		y += Font.small.fontRenderer.FONT_HEIGHT;

		String residentsLabel = TextUtils.translate("tektopiaBook.residents.total");
		String residentsText = "";

		if (!StringUtils.isNullOrWhitespace(residentsLabel)) {
			if (this.villageData.getResidentsData() != null)
				residentsText += "" + this.villageData.getResidentsData().getResidentsCount();

			Font.small.printLeft(residentsLabel, x, y);
			Font.small.printRight(residentsText, x + indentX, y);
		}

		y += Font.small.fontRenderer.FONT_HEIGHT;

		String visitorsLabel = TextUtils.translate("tektopiaBook.visitors.total");
		String visitorsText = "";

		if (!StringUtils.isNullOrWhitespace(visitorsLabel)) {
			if (this.villageData.getResidentsData() != null)
				visitorsText += "" + this.villageData.getVisitorsData().getVisitorsCount();

			Font.small.printLeft(visitorsLabel, x, y);
			Font.small.printRight(visitorsText, x + indentX, y);
		}

		y += Font.small.fontRenderer.FONT_HEIGHT;

		String enemiesLabel = TextUtils.translate("tektopiaBook.enemies.total");
		String enemiesText = "";

		if (!StringUtils.isNullOrWhitespace(enemiesLabel)) {
			if (this.villageData.getResidentsData() != null)
				enemiesText += "" + this.villageData.getEnemiesData().getEnemiesCount();

			Font.small.printLeft(enemiesLabel, x, y);
			Font.small.printRight(enemiesText, x + indentX, y);
		}

		y += Font.small.fontRenderer.FONT_HEIGHT;


		// display map filters (draw in reverse order from bottom to top)
		y = this.y + MAP_PAGE_BOTTOM_Y - (LINE_SPACE_Y * 5);
		indentX = 16;

		GuiTexture buttonIcon = null;
		GuiButton button = null;
		String filterText = "";

		filterText = TextFormatting.DARK_BLUE + TextUtils.translate("tektopiaBook.map.filter.showplayer");
		if (!StringUtils.isNullOrWhitespace(filterText)) {
			buttonIcon = new GuiTexture(showMapPlayer ? mapCheckmarkTick : mapCheckmarkCross, x, y, 10, 10, 0, 0, 10, 10);
			button = new GuiButton(BUTTON_KEY_SHOWMAPPLAYER, buttonIcon);
			this.buttons.add(button);
			Font.small.printLeft(filterText, x + indentX, y + 1);
			y -= Font.small.fontRenderer.FONT_HEIGHT;
		}

		filterText = TextFormatting.DARK_BLUE + TextUtils.translate("tektopiaBook.map.filter.showenemies");
		if (!StringUtils.isNullOrWhitespace(filterText)) {
			buttonIcon = new GuiTexture(showMapEnemies ? mapCheckmarkTick : mapCheckmarkCross, x, y, 10, 10, 0, 0, 10, 10);
			button = new GuiButton(BUTTON_KEY_SHOWMAPENEMIES, buttonIcon);
			this.buttons.add(button);
			Font.small.printLeft(filterText, x + indentX, y + 1);
			y -= Font.small.fontRenderer.FONT_HEIGHT;
		}

		filterText = TextFormatting.DARK_BLUE + TextUtils.translate("tektopiaBook.map.filter.showvisitors");
		if (!StringUtils.isNullOrWhitespace(filterText)) {
			buttonIcon = new GuiTexture(showMapVisitors ? mapCheckmarkTick : mapCheckmarkCross, x, y, 10, 10, 0, 0, 10, 10);
			button = new GuiButton(BUTTON_KEY_SHOWMAPVISITORS, buttonIcon);
			this.buttons.add(button);
			Font.small.printLeft(filterText, x + indentX, y + 1);
			y -= Font.small.fontRenderer.FONT_HEIGHT;
		}

		filterText = TextFormatting.DARK_BLUE + TextUtils.translate("tektopiaBook.map.filter.showresidents");
		if (!StringUtils.isNullOrWhitespace(filterText)) {
			buttonIcon = new GuiTexture(showMapResidents ? mapCheckmarkTick : mapCheckmarkCross, x, y, 10, 10, 0, 0, 10, 10);
			button = new GuiButton(BUTTON_KEY_SHOWMAPRESIDENTS, buttonIcon);
			this.buttons.add(button);
			Font.small.printLeft(filterText, x + indentX, y + 1);
			y -= Font.small.fontRenderer.FONT_HEIGHT;
		}

		filterText = TextFormatting.DARK_BLUE + TextUtils.translate("tektopiaBook.map.filter.showstructures");
		if (!StringUtils.isNullOrWhitespace(filterText)) {
			buttonIcon = new GuiTexture(showMapStructures ? mapCheckmarkTick : mapCheckmarkCross, x, y, 10, 10, 0, 0, 10, 10);
			button = new GuiButton(BUTTON_KEY_SHOWMAPSTRUCTURES, buttonIcon);
			this.buttons.add(button);
			Font.small.printLeft(filterText, x + indentX, y + 1);
			y -= Font.small.fontRenderer.FONT_HEIGHT;
		}

		filterText = TextFormatting.DARK_BLUE + TextUtils.translate("tektopiaBook.map.filter.showhomes");
		if (!StringUtils.isNullOrWhitespace(filterText)) {
			buttonIcon = new GuiTexture(showMapHomes ? mapCheckmarkTick : mapCheckmarkCross, x, y, 10, 10, 0, 0, 10, 10);
			button = new GuiButton(BUTTON_KEY_SHOWMAPHOMES, buttonIcon);
			this.buttons.add(button);
			Font.small.printLeft(filterText, x + indentX, y + 1);
			y -= Font.small.fontRenderer.FONT_HEIGHT;
		}

		filterText = TextFormatting.DARK_BLUE + TextUtils.translate("tektopiaBook.map.filter.showtownhall");
		if (!StringUtils.isNullOrWhitespace(filterText)) {
			buttonIcon = new GuiTexture(showMapTownHall ? mapCheckmarkTick : mapCheckmarkCross, x, y, 10, 10, 0, 0, 10, 10);
			button = new GuiButton(BUTTON_KEY_SHOWMAPTOWNHALL, buttonIcon);
			this.buttons.add(button);
			Font.small.printLeft(filterText, x + indentX, y + 1);
			y -= Font.small.fontRenderer.FONT_HEIGHT;
		}

		filterText = TextFormatting.DARK_BLUE + TextUtils.translate("tektopiaBook.map.filter.showboundaries");
		if (!StringUtils.isNullOrWhitespace(filterText)) {
			buttonIcon = new GuiTexture(showMapBoundaries ? mapCheckmarkTick : mapCheckmarkCross, x, y, 10, 10, 0, 0, 10, 10);
			button = new GuiButton(BUTTON_KEY_SHOWMAPBOUNDARIES, buttonIcon);
			this.buttons.add(button);
			Font.small.printLeft(filterText, x + indentX, y + 1);
			y -= Font.small.fontRenderer.FONT_HEIGHT;
		}

		filterText = TextFormatting.DARK_BLUE + TextUtils.translate("tektopiaBook.map.filter");
		if (!StringUtils.isNullOrWhitespace(filterText)) {
			Font.normal.printLeft(filterText, x, y + 1);
			y -= Font.normal.fontRenderer.FONT_HEIGHT;
		}


		// display map boundary lines
		if (showMapBoundaries) {
			// display map external lines
			if (dataKey[0].equals(GuiMapQuadrant.ALL.name()) || dataKey[0].equals(GuiMapQuadrant.SOUTHWEST.name()) || dataKey[0].equals(GuiMapQuadrant.SOUTHEAST.name())) {
				super.drawHorizontalLine(this.x + MAP_PAGE_MIDDLE_X - MAP_AXIS_LENGTH_X, this.x + MAP_PAGE_MIDDLE_X + MAP_AXIS_LENGTH_X, this.y + MAP_PAGE_TOP_Y, Color.GRAY.getRGB());

				if (!dataKey[0].equals(GuiMapQuadrant.ALL.name())) {
					int iX = this.x + MAP_PAGE_MIDDLE_X - MAP_AXIS_LENGTH_X;
					for (int i = 0; i < MAP_AXIS_INTERVAL_X_COUNT + 1; i++) {
						super.drawVerticalLine(iX, this.y + MAP_PAGE_MIDDLE_Y - MAP_AXIS_LENGTH_X - 2, this.y + MAP_PAGE_MIDDLE_Y - MAP_AXIS_LENGTH_X + 2, Color.GRAY.getRGB()); 
						iX += MAP_AXIS_INTERVAL_X * 2;
					}
				}
			}
			if (dataKey[0].equals(GuiMapQuadrant.ALL.name()) || dataKey[0].equals(GuiMapQuadrant.NORTHWEST.name()) || dataKey[0].equals(GuiMapQuadrant.NORTHEAST.name())) {
				super.drawHorizontalLine(this.x + MAP_PAGE_MIDDLE_X - MAP_AXIS_LENGTH_X, this.x + MAP_PAGE_MIDDLE_X + MAP_AXIS_LENGTH_X, this.y + MAP_PAGE_BOTTOM_Y, Color.GRAY.getRGB());

				if (!dataKey[0].equals(GuiMapQuadrant.ALL.name())) {
					int iX = this.x + MAP_PAGE_MIDDLE_X - MAP_AXIS_LENGTH_X;
					for (int i = 0; i < MAP_AXIS_INTERVAL_X_COUNT + 1; i++) {
						super.drawVerticalLine(iX, this.y + MAP_PAGE_MIDDLE_Y + MAP_AXIS_LENGTH_X - 2, this.y + MAP_PAGE_MIDDLE_Y + MAP_AXIS_LENGTH_X + 2, Color.GRAY.getRGB()); 
						iX += MAP_AXIS_INTERVAL_X * 2;
					}
				}
			}

			if (dataKey[0].equals(GuiMapQuadrant.ALL.name()) || dataKey[0].equals(GuiMapQuadrant.NORTHEAST.name()) || dataKey[0].equals(GuiMapQuadrant.SOUTHEAST.name())) {
				super.drawVerticalLine(this.x + MAP_PAGE_MIDDLE_X - MAP_AXIS_LENGTH_X, this.y + MAP_PAGE_MIDDLE_Y - MAP_AXIS_LENGTH_Y, this.y + MAP_PAGE_MIDDLE_Y + MAP_AXIS_LENGTH_Y, Color.GRAY.getRGB());    	

				if (!dataKey[0].equals(GuiMapQuadrant.ALL.name())) {
					int iY = this.y + MAP_PAGE_MIDDLE_Y - MAP_AXIS_LENGTH_Y;
					for (int i = 0; i < MAP_AXIS_INTERVAL_Y_COUNT + 1; i++) {
						super.drawHorizontalLine(this.x + MAP_PAGE_MIDDLE_X - MAP_AXIS_LENGTH_X - 1, this.x + MAP_PAGE_MIDDLE_X - MAP_AXIS_LENGTH_X + 1, iY, Color.GRAY.getRGB());
						iY += MAP_AXIS_INTERVAL_Y * 2;
					}
				}
			}
			if (dataKey[0].equals(GuiMapQuadrant.ALL.name()) || dataKey[0].equals(GuiMapQuadrant.NORTHWEST.name()) || dataKey[0].equals(GuiMapQuadrant.SOUTHWEST.name())) {
				super.drawVerticalLine(this.x + MAP_PAGE_MIDDLE_X + MAP_AXIS_LENGTH_X, this.y + MAP_PAGE_MIDDLE_Y - MAP_AXIS_LENGTH_Y, this.y + MAP_PAGE_MIDDLE_Y + MAP_AXIS_LENGTH_Y, Color.GRAY.getRGB());    	

				if (!dataKey[0].equals(GuiMapQuadrant.ALL.name())) {
					int iY = this.y + MAP_PAGE_MIDDLE_Y - MAP_AXIS_LENGTH_Y;
					for (int i = 0; i < MAP_AXIS_INTERVAL_Y_COUNT + 1; i++) {
						super.drawHorizontalLine(this.x + MAP_PAGE_MIDDLE_X + MAP_AXIS_LENGTH_X - 1, this.x + MAP_PAGE_MIDDLE_X + MAP_AXIS_LENGTH_X + 1, iY, Color.GRAY.getRGB());
						iY += MAP_AXIS_INTERVAL_Y * 2;
					}
				}
			}

			// display map internal lines
			if (dataKey[0].equals(GuiMapQuadrant.ALL.name())) {
				super.drawHorizontalLine(this.x + MAP_PAGE_MIDDLE_X - MAP_AXIS_LENGTH_X, this.x + MAP_PAGE_MIDDLE_X + MAP_AXIS_LENGTH_X, this.y + MAP_PAGE_MIDDLE_Y, Color.GRAY.getRGB());

				int iX = this.x + MAP_PAGE_MIDDLE_X - MAP_AXIS_LENGTH_X + MAP_AXIS_INTERVAL_X;
				for (int i = 1; i < MAP_AXIS_INTERVAL_X_COUNT * 2; i++) {
					super.drawVerticalLine(iX, this.y + MAP_PAGE_MIDDLE_Y - 2, this.y + MAP_PAGE_MIDDLE_Y + 2, Color.GRAY.getRGB()); 
					iX += MAP_AXIS_INTERVAL_X;
				}
			}
			if (dataKey[0].equals(GuiMapQuadrant.ALL.name())) {
				super.drawVerticalLine(this.x + MAP_PAGE_MIDDLE_X, this.y + MAP_PAGE_MIDDLE_Y - MAP_AXIS_LENGTH_Y, this.y + MAP_PAGE_MIDDLE_Y + MAP_AXIS_LENGTH_Y, Color.GRAY.getRGB());    	

				int iY = this.y + MAP_PAGE_MIDDLE_Y - MAP_AXIS_LENGTH_Y + MAP_AXIS_INTERVAL_Y;
				for (int i = 1; i < MAP_AXIS_INTERVAL_Y_COUNT * 2; i++) {
					super.drawHorizontalLine(this.x + MAP_PAGE_MIDDLE_X - 1, this.x + MAP_PAGE_MIDDLE_X + 1, iY, Color.GRAY.getRGB());
					iY += MAP_AXIS_INTERVAL_Y;
				}
			}
		}

		GuiMapQuadrant quadrant = GuiMapQuadrant.valueOf(dataKey[0]);

		String quadrantName = null;
		int factor = 1;

		BlockPos mapOrigin = null;
		int xZeroPoint = 0;
		int yZeroPoint = 0;

		switch (quadrant) {
		case ALL:
			xZeroPoint = this.x + (MAP_PAGE_MIDDLE_X - (MAP_MARKER_SIZE / 2));
			yZeroPoint = this.y + (MAP_PAGE_MIDDLE_Y - (MAP_MARKER_SIZE * factor));
			mapOrigin = new BlockPos(xZeroPoint, 0, yZeroPoint);
			break;

		case NORTHEAST:
			quadrantName = TextFormatting.DARK_BLUE + TextUtils.translate("tektopiaBook.map.quadrantnortheast");
			factor = 2;

			xZeroPoint = this.x + MAP_PAGE_MIDDLE_X - MAP_AXIS_LENGTH_X - (MAP_MARKER_SIZE * factor / 2);
			yZeroPoint = this.y + MAP_PAGE_BOTTOM_Y - (MAP_MARKER_SIZE * factor);
			mapOrigin = new BlockPos(xZeroPoint, 0, yZeroPoint);
			break;

		case NORTHWEST:
			quadrantName = TextFormatting.DARK_BLUE + TextUtils.translate("tektopiaBook.map.quadrantnorthwest");
			factor = 2;

			xZeroPoint = this.x + MAP_PAGE_MIDDLE_X + MAP_AXIS_LENGTH_X - (MAP_MARKER_SIZE * factor / 2);
			yZeroPoint = this.y + MAP_PAGE_BOTTOM_Y - (MAP_MARKER_SIZE * factor);
			mapOrigin = new BlockPos(xZeroPoint, 0, yZeroPoint);
			break;

		case SOUTHEAST:
			quadrantName = TextFormatting.DARK_BLUE + TextUtils.translate("tektopiaBook.map.quadrantsoutheast");
			factor = 2;

			xZeroPoint = this.x + MAP_PAGE_MIDDLE_X - MAP_AXIS_LENGTH_X - (MAP_MARKER_SIZE * factor / 2);
			yZeroPoint = this.y + MAP_PAGE_TOP_Y - (MAP_MARKER_SIZE * factor);
			mapOrigin = new BlockPos(xZeroPoint, 0, yZeroPoint);

			break;

		case SOUTHWEST:
			quadrantName = TextFormatting.DARK_BLUE + TextUtils.translate("tektopiaBook.map.quadrantsouthwest");
			factor = 2;

			xZeroPoint = this.x + MAP_PAGE_MIDDLE_X + MAP_AXIS_LENGTH_X - (MAP_MARKER_SIZE * factor / 2);
			yZeroPoint = this.y + MAP_PAGE_TOP_Y - (MAP_MARKER_SIZE * factor);
			mapOrigin = new BlockPos(xZeroPoint, 0, yZeroPoint);

			break;

		default:
			break;
		}

		if (!StringUtils.isNullOrWhitespace(quadrantName)) {
			// display quadrant name
			Font.normal.printLeft(quadrantName, this.x + MAP_PAGE_LEFT_X, this.y + MAP_HEADER_Y + 5);
		}

		if (mapOrigin != null) {

			for (GuiMapMarker mapMarker : mapMarkers) {

				if (!mapMarker.isInQuadrant(quadrant))
					continue;

				mapMarker.addPosition(townHallStructure.getFramePosition());

				if (mapMarker.getTooltip() != null) {
					mapMarker.getTooltip().multiplySize(factor);
					mapMarker.getTooltip().multiplyPosition(factor).multipleOffset(factor).addPosition(mapOrigin);

					this.tooltips.add(0, mapMarker.getTooltip());
				}

				if (mapMarker.getIcon() != null) {
					mapMarker.getIcon().multiplySize(factor);
					mapMarker.getIcon().multiplyPosition(factor).addPosition(mapOrigin);
				}

				this.buttons.add(mapMarker);
			}
		}
	}

	private void drawPageProfession(int mouseX, int mouseY, float partialTicks, GuiPage guiPage) {
		drawPageHeader(mouseX, mouseY, partialTicks, guiPage);
		drawPageFooter(mouseX, mouseY, partialTicks, guiPage); 

		int y = this.y + PAGE_BODY_Y;
		int indentX = 10;

		String[] dataKey = getPageKeyParts(guiPage.getDataKey());
		ResidentsData residentsData = this.villageData.getResidentsData();
		String continued = TextUtils.translate("tektopiaBook.continued");

		String typeHeader = TextUtils.translate("tektopiaBook.professions.professiontypes");

		if (!StringUtils.isNullOrWhitespace(typeHeader)) {
			if (!dataKey[1].equals("0") && !StringUtils.isNullOrWhitespace(continued)) {
				typeHeader += " " + continued;
			}
			typeHeader = TextFormatting.DARK_BLUE + typeHeader;

			if (guiPage.isLeftPage()) {
				Font.normal.printLeft(typeHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
			}

			if (guiPage.isRightPage()) {
				Font.normal.printLeft(typeHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
			}
		} 

		y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

		Map<String, Integer> professionTypeCounts = residentsData != null ? residentsData.getProfessionTypeCounts() : null;

		if (professionTypeCounts != null) {
			final int[] maxLength = { 0 };

			int page = 0;
			try {
				page = Integer.parseInt(dataKey[1]);
			}
			catch (NumberFormatException e) {
				page = 0;
			}
			int startIndex = page * PROFESSIONTYPES_PER_PAGE;
			int index = 0;

			String nameHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.name");
			String countHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.count");

			maxLength[0] += Font.small.getStringWidth(countHeader);

			if (guiPage.isLeftPage()) {
				Font.small.printLeft(nameHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
				Font.small.printLeft(countHeader, this.x + PAGE_LEFTPAGE_CENTER_X, y);
			}

			if (guiPage.isRightPage()) {
				Font.small.printLeft(nameHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
				Font.small.printLeft(countHeader, this.x + PAGE_RIGHTPAGE_CENTER_X, y);
			}

			y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

			for (String professionType : TektopiaUtils.getProfessionTypeNames()) {
				if (index >= startIndex && index < startIndex + PROFESSIONTYPES_PER_PAGE) {
					String typeName = getTypeName(professionType);
					int typeCount = 0;

					int x1 = 0;
					int x2 = 0;

					if (professionTypeCounts.containsKey(professionType)) {
						typeCount = professionTypeCounts.get(professionType);
					}

					if (guiPage.isLeftPage()) {
						Font.small.printLeft(typeName, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
						Font.small.printRight(typeCount, this.x + PAGE_LEFTPAGE_CENTER_X + maxLength[0], y); 

						x1 = this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX;
						x2 = x1 + Font.small.getStringWidth(typeName);
					}

					if (guiPage.isRightPage()) {
						Font.small.printLeft(typeName, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
						Font.small.printRight(typeCount, this.x + PAGE_RIGHTPAGE_CENTER_X + maxLength[0], y); 

						x1 = this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX;
						x2 = x1 + Font.small.getStringWidth(typeName);
					}

					if (!this.isSubPageOpen() && typeCount > 0) {
						GuiHyperlink button = new GuiHyperlink(BUTTON_KEY_PROFESSIONLINK, getHyperlinkData(GuiPageType.PROFESSIONTYPE, getProfessionDetailPageKey(professionType)));
						button.setIcon(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT);
						this.buttons.add(button);

						GuiTooltip toolTip = new GuiTooltip(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT, TextUtils.translate("tektopiaBook.links.professiondetails"));
						this.tooltips.add(toolTip);
					}

					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
				}  
				index++;
			}
		}
	}

	private void drawPageProfessionType(int mouseX, int mouseY, float partialTicks, GuiPage guiPage) {
		drawPageHeader(mouseX, mouseY, partialTicks, guiPage);
		drawPageFooter(mouseX, mouseY, partialTicks, guiPage); 

		int y = this.y + PAGE_BODY_Y;
		int indentX = 10;

		String[] dataKey = getPageKeyParts(guiPage.getDataKey());
		String professionType = dataKey[0];

		ResidentsData residentsData = this.villageData.getResidentsData();
		Map<String, Integer> professionTypeCounts = residentsData != null ? residentsData.getProfessionTypeCounts() : null;
		List<ResidentData> residents = residentsData != null ? residentsData.getResidentsByType(professionType) : null;

		String typeName = getTypeName(professionType);
		String continued = TextUtils.translate("tektopiaBook.continued");
		String summary = TextUtils.translate("tektopiaBook.summary");

		if (!StringUtils.isNullOrWhitespace(typeName)) {
			typeName += " " + summary;
			if (!dataKey[1].equals("0") && !StringUtils.isNullOrWhitespace(continued)) {
				typeName += " " + continued;
			}
			typeName = TextFormatting.DARK_BLUE + typeName;

			if (guiPage.isLeftPage()) {
				Font.normal.printLeft(typeName, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
			}

			if (guiPage.isRightPage()) {
				Font.normal.printLeft(typeName, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
			}
		} 

		y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

		if (dataKey[1].equals("0")) {

			String totalLabel = TextUtils.translate("tektopiaBook.professiontypes.total");
			String totalText = "";

			if (!StringUtils.isNullOrWhitespace(totalLabel)) {
				if (professionTypeCounts != null && professionTypeCounts.containsKey(professionType)) {
					totalText += "" + professionTypeCounts.get(professionType);
				}

				if (guiPage.isLeftPage()) {
					Font.small.printLeft(totalLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
					Font.small.printRight(totalText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y); 
				}

				if (guiPage.isRightPage()) {
					Font.small.printLeft(totalLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
					Font.small.printRight(totalText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y); 
				}
			}
		} 

		y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y + LINE_SPACE_Y_HEADER;

		String header = TextUtils.translate("tektopiaBook.professiontypes.residents");

		if (!StringUtils.isNullOrWhitespace(header)) {
			header = TextFormatting.DARK_BLUE + header;

			if (guiPage.isLeftPage()) {
				Font.small.printLeft(header, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
			}

			if (guiPage.isRightPage()) {
				Font.small.printLeft(header, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
			}
		} 

		y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

		if (residents != null) {
			final int[] maxLength = { 0 };
			maxLength[0] += LABEL_TRAILINGSPACE_X;

			int page = 0;
			try {
				page = Integer.parseInt(dataKey[1]);
			}
			catch (NumberFormatException e) {
				page = 0;
			}
			int startIndex = page * PROFESSIONS_PER_PAGE;
			int index = 0;

			String bedHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.bed");    
			String residentHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.resident");
			String levelHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.level");

			if (guiPage.isLeftPage()) {
				Font.small.printLeft(bedHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y);
				Font.small.printLeft(residentHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y);
				Font.small.printRight(levelHeader, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y);
			}

			if (guiPage.isRightPage()) {
				Font.small.printLeft(bedHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y);
				Font.small.printLeft(residentHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y);
				Font.small.printRight(levelHeader, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y);
			}

			y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y; 

			for (ResidentData resident : residents) {
				if (index >= startIndex && index < startIndex + PROFESSIONS_PER_PAGE) {

					String residentBed = "";
					String residentName = "";
					String residentLevel = "";

					int x1 = 0;
					int x2 = 0;

					if (resident.getCanHaveBed())
						residentBed = (resident.hasBed() ? TextUtils.SYMBOL_GREENTICK : TextUtils.SYMBOL_REDCROSS);
					residentName = formatResidentName(resident.isMale(), resident.getName(), true);

					ProfessionType pt = TektopiaUtils.getProfessionType(professionType);
					if (pt != null) {
						switch (pt) {
						case CHILD:
						case NITWIT:
							break;
						default:
							residentLevel = formatResidentLevel(resident.getLevel(), resident.getBaseLevel(), false, false);
							break;
						}
					}

					if (guiPage.isLeftPage()) {
						Font.small.printRight(residentBed, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
						Font.small.printLeft(residentName, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y); 
						Font.small.printRight(residentLevel, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y);

						x1 = this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0];
						x2 = x1 + Font.small.getStringWidth(residentName);
					}

					if (guiPage.isRightPage()) {
						Font.small.printRight(residentBed, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
						Font.small.printLeft(residentName, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y); 
						Font.small.printRight(residentLevel, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y);

						x1 = this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0];
						x2 = x1 + Font.small.getStringWidth(residentName);
					}

					if (!this.isSubPageOpen()) {
						GuiHyperlink button = new GuiHyperlink(BUTTON_KEY_RESIDENTLINK, getHyperlinkData(GuiPageType.RESIDENT, getResidentDetailPageKey(resident.getId())));
						button.setIcon(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT);
						this.buttons.add(button);

						GuiTooltip toolTip = new GuiTooltip(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT, TextUtils.translate("tektopiaBook.links.residentdetails"));
						this.tooltips.add(toolTip);
					}

					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;           			
				}
				index++;
			}
		}
	}

	private void drawPageResident(int mouseX, int mouseY, float partialTicks, GuiPage guiPage) {
		drawPageHeader(mouseX, mouseY, partialTicks, guiPage);
		drawPageFooter(mouseX, mouseY, partialTicks, guiPage); 

		int y = this.y + PAGE_BODY_Y;
		int indentX = 10;

		GuiHyperlink button = null;
		GuiTooltip toolTip = null;
		int x1 = 0;
		int x2 = 0;
		int x3 = 0;
		int x4 = 0;

		String[] dataKey = getPageKeyParts(guiPage.getDataKey());
		ResidentsData residentsData = this.villageData.getResidentsData();
		StructuresData structuresData = this.villageData.getStructuresData();

		if (dataKey[0].equals("")) {
			// resident list

			String pageHeader = TextUtils.translate("tektopiaBook.residents.residents");

			if (!StringUtils.isNullOrWhitespace(pageHeader)) {
				pageHeader = TextFormatting.DARK_BLUE + pageHeader;

				if (guiPage.isLeftPage()) {
					Font.normal.printLeft(pageHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
				}

				if (guiPage.isRightPage()) {
					Font.normal.printLeft(pageHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
				}
			} 

			y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

			List<ResidentData> residents = residentsData.getResidents();

			if (residents != null) {
				final int[] maxLength = { 0 };
				maxLength[0] += LABEL_TRAILINGSPACE_X;

				int page = 0;
				try {
					page = Integer.parseInt(dataKey[1]);
				}
				catch (NumberFormatException e) {
					page = 0;
				}
				int startIndex = page * RESIDENTS_PER_PAGE;
				int index = 0;

				String bedHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.bed");    
				String sleepHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.sleep");    
				String residentHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.resident");
				String professionHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.profession");
				String levelHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.level");

				if (guiPage.isLeftPage()) {
					Font.small.printLeft(bedHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y);
					Font.small.printLeft(sleepHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + maxLength[0] + 5, y);
					Font.small.printLeft(residentHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0] + indentX + 7, y);
					Font.small.printLeft(professionHeader, this.x + PAGE_LEFTPAGE_CENTER_X + indentX + indentX + 15, y);
					Font.small.printRight(levelHeader, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y);
				}

				if (guiPage.isRightPage()) {
					Font.small.printLeft(bedHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y);
					Font.small.printLeft(sleepHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + maxLength[0] + 5, y);
					Font.small.printLeft(residentHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0] + indentX + 7, y);
					Font.small.printLeft(professionHeader, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX + indentX + 15, y);
					Font.small.printRight(levelHeader, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y);
				}

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y; 

				for (ResidentData resident : residentsData.getResidents()) {
					if (index >= startIndex && index < startIndex + RESIDENTS_PER_PAGE) {
						String residentBed = "";
						String residentSleep = "";
						String residentName = "";
						String residentProfession = "";
						String residentLevel = "";

						if (resident.getCanHaveBed())
							residentBed += (resident.hasBed() ? TextUtils.SYMBOL_GREENTICK : TextUtils.SYMBOL_REDCROSS);
						if (resident.getCanHaveBed())
							residentSleep += (resident.isSleeping() ? TextUtils.SYMBOL_GREENTICK : TextUtils.SYMBOL_REDCROSS);
						residentName += formatResidentName(resident.isMale(), resident.getName(), true);
						residentProfession += getTypeName(resident.getProfessionType());

						ProfessionType professionType = TektopiaUtils.getProfessionType(resident.getProfessionType());
						if (professionType != null) {
							switch (professionType) {
							case CHILD:
							case NITWIT:
								break;
							default:
								residentLevel = formatResidentLevel(resident.getLevel(), resident.getBaseLevel(), false, false);
								break;
							}
						}

						if (guiPage.isLeftPage()) {
							Font.small.printRight(residentBed, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
							Font.small.printRight(residentSleep, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0] + 7, y);
							Font.small.printLeft(residentName, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0] + indentX + 7, y);
							Font.small.printLeft(residentProfession, this.x + PAGE_LEFTPAGE_CENTER_X + indentX + indentX + 15, y);
							Font.small.printRight(residentLevel, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y);

							x1 = this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0] + indentX + 7;
							x2 = x1 + Font.small.getStringWidth(residentName);
							x3 = this.x + PAGE_LEFTPAGE_CENTER_X + indentX + indentX + 15;
							x4 = x3 + Font.small.getStringWidth(residentProfession);
						}

						if (guiPage.isRightPage()) {
							Font.small.printRight(residentBed, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
							Font.small.printRight(residentSleep, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0] + 7, y);
							Font.small.printLeft(residentName, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0] + indentX + 7, y);
							Font.small.printLeft(residentProfession, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX + indentX + 15, y);
							Font.small.printRight(residentLevel, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y);

							x1 = this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0] + indentX + 7;
							x2 = x1 + Font.small.getStringWidth(residentName);
							x3 = this.x + PAGE_RIGHTPAGE_CENTER_X + indentX + indentX + 15;
							x4 = x3 + Font.small.getStringWidth(residentProfession);
						}

						if (!this.isSubPageOpen()) {
							button = new GuiHyperlink(BUTTON_KEY_RESIDENTLINK, getHyperlinkData(GuiPageType.RESIDENT, getResidentDetailPageKey(resident.getId())));
							button.setIcon(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT);
							this.buttons.add(button);

							toolTip = new GuiTooltip(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT, TextUtils.translate("tektopiaBook.links.residentdetails"));
							this.tooltips.add(toolTip);

							button = new GuiHyperlink(BUTTON_KEY_PROFESSIONLINK, getHyperlinkData(GuiPageType.PROFESSIONTYPE, getProfessionDetailPageKey(residentProfession)));
							button.setIcon(x3, y, x4 - x3, Font.small.fontRenderer.FONT_HEIGHT);
							this.buttons.add(button);

							toolTip = new GuiTooltip(x3, y, x4 - x3, Font.small.fontRenderer.FONT_HEIGHT, TextUtils.translate("tektopiaBook.links.professiondetails"));
							this.tooltips.add(toolTip);
						}   	            	

						y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
					}  
					index++;
				}
			}
		}
		else {
			// resident

			int residentId = 0;
			try {
				residentId = Integer.parseInt(dataKey[0]);
			}
			catch (NumberFormatException e) {
				residentId = 0;
			}

			ResidentData resident = residentId > 0 ? residentsData.getResidentById(residentId) : null;

			if (resident != null) {

				String header = stripTextFormatting(resident.getName());

				if (!StringUtils.isNullOrWhitespace(header)) {
					// check if this is the villager we clicked on
					if (this.villageData.getEntityId() > 0 && this.villageData.getEntityId() == residentId) {
						header = TextFormatting.UNDERLINE + header;
					}      

					// check if this is the villager that owns the bed we clicked on
					if (this.villageData.getBedPosition() != null && this.villageData.getBedPosition().equals(resident.getBedPosition())) {
						header = TextFormatting.UNDERLINE + header;
					}

					if (resident.isMale())
						header = TextFormatting.BLUE + header + " " + TextUtils.SYMBOL_MALE;
					else
						header = TextFormatting.LIGHT_PURPLE + header + " " + TextUtils.SYMBOL_FEMALE;

					if (guiPage.isLeftPage()) {
						Font.normal.printLeft(header, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
					}

					if (guiPage.isRightPage()) {
						Font.normal.printLeft(header, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
					}
				} 

				if (!resident.isVendor()) {
					String aiFilterText = TextUtils.translate("tektopiaBook.residents.viewaifilters");

					if (!StringUtils.isNullOrWhitespace(aiFilterText)) {

						if (guiPage.isLeftPage()) {
							x1 = this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X - 22;
						}

						if (guiPage.isRightPage()) {
							x1 = this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X - 22;
						}

						if (isSubPageOpen()) {

							GlStateManager.pushMatrix();
							GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

							mc.getTextureManager().bindTexture(buttonAiFilter);
							super.drawModalRectWithCustomSizedTexture(x1, y - 8, 0, 0, 24, 24, 24, 24);

							GlStateManager.popMatrix();

						} else {
							GuiButton aiFilterButton = new GuiButton(BUTTON_KEY_AIFILTER);
							aiFilterButton.setIcon(buttonAiFilter, x1, y - 8, 24, 24, 0, 0, 24, 24);
							aiFilterButton.setButtonData(getAiFilterSubPageKey(residentId));
							this.buttons.add(aiFilterButton);

							GuiTooltip aiFilterToolTip = new GuiTooltip(x1, y - 8, 24, 24, aiFilterText);
							this.tooltips.add(aiFilterToolTip);
						}
					}
				}

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

				int tXL = this.x + (PAGE_LEFTPAGE_RIGHTMARGIN_X - PROFESSION_WIDTH);
				int tXR = this.x + (PAGE_RIGHTPAGE_RIGHTMARGIN_X - PROFESSION_WIDTH);
				int tY = y;

				String gender = resident.isMale() ? "m" : "f";
				ResourceLocation residentResource = new ResourceLocation(ModDetails.MOD_ID, "textures/professions/" + resident.getProfessionType() + "_" + gender + ".png");

				if (residentResource != null) {
					GlStateManager.pushMatrix();
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

					mc.getTextureManager().bindTexture(residentResource);

					if (guiPage.isLeftPage()) {
						super.drawModalRectWithCustomSizedTexture(tXL, tY, 0, 0, PROFESSION_WIDTH, PROFESSION_HEIGHT, 56, 90);
					}

					if (guiPage.isRightPage()) {
						super.drawModalRectWithCustomSizedTexture(tXR, tY, 0, 0, PROFESSION_WIDTH, PROFESSION_HEIGHT, 56, 90);
					}	              

					GlStateManager.popMatrix();  	
				}

				tXL = this.x + PAGE_LEFTPAGE_CENTER_X;
				tXR = this.x + PAGE_RIGHTPAGE_CENTER_X;
				tY = y + 10;

				if (!this.isSubPageOpen()) {

					// draw equipment slots
					for (ItemStack piece : resident.getEquipment()) {
						if (piece != null && piece != ItemStack.EMPTY) {
							List<String> tooltip = piece.getTooltip(null, TooltipFlags.NORMAL);
							if (piece.isItemEnchanted() && tooltip != null && tooltip.size() > 0) {
								tooltip.set(0, TextFormatting.AQUA + tooltip.get(0));
							}

							if (guiPage.isLeftPage()) {
								RenderUtils.renderItemAndEffectIntoGUI(piece, tXL, tY);
								RenderUtils.renderItemOverlayIntoGUI(super.itemRender, Font.normal.fontRenderer, piece, tXL, tY, null);
								if (tooltip != null && tooltip.size() > 0) {
									this.tooltips.add(new GuiTooltip(tXL, tY, 16, 16, tooltip));
								}
							}

							if (guiPage.isRightPage()) {
								RenderUtils.renderItemAndEffectIntoGUI(piece, tXR, tY);
								RenderUtils.renderItemOverlayIntoGUI(super.itemRender, Font.normal.fontRenderer, piece, tXR, tY, null);
								if (tooltip != null && tooltip.size() > 0) {
									this.tooltips.add(new GuiTooltip(tXR, tY, 16, 16, tooltip));
								}
							}
						}
						tY += 20;
					}

					tXL += 20;
					tXR += 20;
					tY = y + 70;

					// draw armor slots (armor stored backwards)
					for (ItemStack piece : resident.getArmor()) {
						if (piece != null && piece != ItemStack.EMPTY) {
							List<String> tooltip = piece.getTooltip(null, TooltipFlags.NORMAL);
							if (piece.isItemEnchanted() && tooltip != null && tooltip.size() > 0) {
								tooltip.set(0, TextFormatting.AQUA + tooltip.get(0));
							}

							if (guiPage.isLeftPage()) {
								RenderUtils.renderItemAndEffectIntoGUI(piece, tXL, tY);
								RenderUtils.renderItemOverlayIntoGUI(super.itemRender, Font.normal.fontRenderer, piece, tXL, tY, null);
								if (tooltip != null && tooltip.size() > 0) {
									this.tooltips.add(new GuiTooltip(tXL, tY, 16, 16, tooltip));
								}
							}

							if (guiPage.isRightPage()) {
								RenderUtils.renderItemAndEffectIntoGUI(piece, tXR, tY);
								RenderUtils.renderItemOverlayIntoGUI(super.itemRender, Font.normal.fontRenderer, piece, tXR, tY, null);
								if (tooltip != null && tooltip.size() > 0) {
									this.tooltips.add(new GuiTooltip(tXR, tY, 16, 16, tooltip));
								}
							}
						}
						tY -= 20;
					}
				}              

				String professionLabel = TextUtils.translate("tektopiaBook.residents.profession");
				String professionText = "";

				if (!StringUtils.isNullOrWhitespace(professionLabel)) {
					professionText += getTypeName(resident.getProfessionType());

					if (guiPage.isLeftPage()) {
						Font.small.printLeft(professionLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
						Font.small.printLeft(professionText, this.x + PAGE_LEFTPAGE_LEFTCENTER_X + indentX, y); 

						x1 = this.x + PAGE_LEFTPAGE_LEFTCENTER_X + indentX;
						x2 = x1 + Font.small.getStringWidth(professionLabel);
					}

					if (guiPage.isRightPage()) {
						Font.small.printLeft(professionLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
						Font.small.printLeft(professionText, this.x + PAGE_RIGHTPAGE_LEFTCENTER_X + indentX, y); 

						x1 = this.x + PAGE_RIGHTPAGE_LEFTCENTER_X + indentX;
						x2 = x1 + Font.small.getStringWidth(professionLabel);
					}

					if (!this.isSubPageOpen()) {
						button = new GuiHyperlink(BUTTON_KEY_PROFESSIONLINK, getHyperlinkData(GuiPageType.PROFESSIONTYPE, getProfessionDetailPageKey(professionText)));
						button.setIcon(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT);
						this.buttons.add(button);

						toolTip = new GuiTooltip(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT, TextUtils.translate("tektopiaBook.links.professiondetails"));
						this.tooltips.add(toolTip);
					} 
				}

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

				String levelLabel = TextUtils.translate("tektopiaBook.residents.level");
				String levelText = "";

				ProfessionType professionType = TektopiaUtils.getProfessionType(resident.getProfessionType());
				if (professionType != null) {
					if (!StringUtils.isNullOrWhitespace(levelLabel)) {
						switch (professionType) {
						case CHILD:
						case NITWIT:
							break;
						default:
							levelText = formatResidentLevel(resident.getLevel(), resident.getBaseLevel(), true, false);
							break;
						}

						if (guiPage.isLeftPage()) {
							Font.small.printLeft(levelLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
							Font.small.printLeft(levelText, this.x + PAGE_LEFTPAGE_LEFTCENTER_X + indentX, y); 
						}

						if (guiPage.isRightPage()) {
							Font.small.printLeft(levelLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
							Font.small.printLeft(levelText, this.x + PAGE_RIGHTPAGE_LEFTCENTER_X + indentX, y); 
						}
					}
				} 

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

				String daysAliveLabel = TextUtils.translate("tektopiaBook.residents.daysalive");
				String daysAliveText = "";

				if (!StringUtils.isNullOrWhitespace(daysAliveLabel)) {
					if (resident.getDaysAlive() > 0) {
						daysAliveText += "" + resident.getDaysAlive();
					}

					if (guiPage.isLeftPage()) {
						Font.small.printLeft(daysAliveLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
						Font.small.printLeft(daysAliveText, this.x + PAGE_LEFTPAGE_LEFTCENTER_X + indentX, y);
					}

					if (guiPage.isRightPage()) {
						Font.small.printLeft(daysAliveLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
						Font.small.printLeft(daysAliveText, this.x + PAGE_RIGHTPAGE_LEFTCENTER_X + indentX, y); 
					}
				}

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

				String healthLabel = TextUtils.translate("tektopiaBook.residents.health");
				String healthText = "";

				if (!StringUtils.isNullOrWhitespace(healthLabel)) {
					healthText += formatResidentStatistic(resident.getHealth(), resident.getMaxHealth(), true);

					if (guiPage.isLeftPage()) {
						Font.small.printLeft(healthLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
						Font.small.printLeft(healthText, this.x + PAGE_LEFTPAGE_LEFTCENTER_X + indentX, y);  
					}

					if (guiPage.isRightPage()) {
						Font.small.printLeft(healthLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
						Font.small.printLeft(healthText, this.x + PAGE_RIGHTPAGE_LEFTCENTER_X + indentX, y); 
					}
				}

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

				String hungerLabel = TextUtils.translate("tektopiaBook.residents.hunger");
				String hungerText = "";

				if (!StringUtils.isNullOrWhitespace(hungerLabel)) {
					hungerText += formatResidentStatistic(resident.getHunger(), resident.getMaxHunger(), true);

					if (guiPage.isLeftPage()) {
						Font.small.printLeft(hungerLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
						Font.small.printLeft(hungerText, this.x + PAGE_LEFTPAGE_LEFTCENTER_X + indentX, y); 
					}

					if (guiPage.isRightPage()) {
						Font.small.printLeft(hungerLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
						Font.small.printLeft(hungerText, this.x + PAGE_RIGHTPAGE_LEFTCENTER_X + indentX, y); 
					}
				}

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

				String happinessLabel = TextUtils.translate("tektopiaBook.residents.happiness");
				String happinessText = "";

				if (!StringUtils.isNullOrWhitespace(happinessLabel)) {
					happinessText += formatResidentStatistic(resident.getHappy(), resident.getMaxHappy(), true);

					if (guiPage.isLeftPage()) {
						Font.small.printLeft(happinessLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
						Font.small.printLeft(happinessText, this.x + PAGE_LEFTPAGE_LEFTCENTER_X + indentX, y);
					}

					if (guiPage.isRightPage()) {
						Font.small.printLeft(happinessLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
						Font.small.printLeft(happinessText, this.x + PAGE_RIGHTPAGE_LEFTCENTER_X + indentX, y); 
					}
				}

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

				String intelligenceLabel = TextUtils.translate("tektopiaBook.residents.intelligence");
				String intelligenceText = "";

				if (!StringUtils.isNullOrWhitespace(intelligenceLabel)) {
					intelligenceText += formatResidentStatistic(resident.getIntelligence(), resident.getMaxIntelligence(), true);

					if (guiPage.isLeftPage()) {
						Font.small.printLeft(intelligenceLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
						Font.small.printLeft(intelligenceText, this.x + PAGE_LEFTPAGE_LEFTCENTER_X + indentX, y); 
					}

					if (guiPage.isRightPage()) {
						Font.small.printLeft(intelligenceLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
						Font.small.printLeft(intelligenceText, this.x + PAGE_RIGHTPAGE_LEFTCENTER_X + indentX, y); 
					}
				}

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

				String blessedLabel = TextUtils.translate("tektopiaBook.residents.blessed");
				String blessedText = "";

				if (!StringUtils.isNullOrWhitespace(blessedLabel)) {
					blessedText += resident.isBlessed() ? TextUtils.SYMBOL_GREENTICK : TextUtils.SYMBOL_REDCROSS;

					if (guiPage.isLeftPage()) {
						Font.small.printLeft(blessedLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
						Font.small.printLeft(blessedText, this.x + PAGE_LEFTPAGE_LEFTCENTER_X + indentX, y); 
					}

					if (guiPage.isRightPage()) {
						Font.small.printLeft(blessedLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
						Font.small.printLeft(blessedText, this.x + PAGE_RIGHTPAGE_LEFTCENTER_X + indentX, y);
					}
				}

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

				if (!resident.isVendor()) {
					String taskLabel = TextUtils.translate("tektopiaBook.residents.task");
					String taskText = "";

					if (!StringUtils.isNullOrWhitespace(taskLabel)) {
						taskText += getAiTaskName(resident.getCurrentTask());

						if (guiPage.isLeftPage()) {
							Font.small.printLeft(taskLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
							Font.small.printLeft(taskText, this.x + PAGE_LEFTPAGE_LEFTCENTER_X + indentX, y); 
						}

						if (guiPage.isRightPage()) {
							Font.small.printLeft(taskLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
							Font.small.printLeft(taskText, this.x + PAGE_RIGHTPAGE_LEFTCENTER_X + indentX, y);
						}
					}
				}

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

				String totalArmorLabel = TextUtils.translate("tektopiaBook.residents.totalarmorvalue");
				String totalArmorText = "";

				if (!StringUtils.isNullOrWhitespace(totalArmorLabel)) {
					totalArmorText += "" + resident.getTotalArmorValue();

					if (guiPage.isLeftPage()) {
						Font.small.printLeft(totalArmorLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
						Font.small.printLeft(totalArmorText, this.x + PAGE_LEFTPAGE_LEFTCENTER_X + indentX, y); 
					}

					if (guiPage.isRightPage()) {
						Font.small.printLeft(totalArmorLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
						Font.small.printLeft(totalArmorText, this.x + PAGE_RIGHTPAGE_LEFTCENTER_X + indentX, y);
					}
				}

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y + LINE_SPACE_Y_HEADER;

				HomeData homeData = null;
				StructureData structureData = null;

				if (resident.hasBed() && this.villageData.getHomesData() != null) {
					homeData = this.villageData.getHomesData().getHomeByBedPosition(resident.getBedPosition());
				} else if (resident.getHomePosition() != null && this.villageData.getStructuresData() != null) {
					// resident does not have a bed, but has an assigned home (structure frame position)
					structureData = this.villageData.getStructuresData().getStructureByFramePosition(resident.getHomePosition());
				}

				String homeStructureLabel = TextUtils.translate("tektopiaBook.residents.home");
				String homeStructureText = "";
				String homeStructurePosition = "";

				if (!StringUtils.isNullOrWhitespace(homeStructureLabel)) {
					int structureId = 0;
					int textLength = 0;

					if (homeData != null) {
						homeStructureText += " " + homeData.getStructureTypeName();
						homeStructurePosition += " (" + formatBlockPos(homeData.getFramePosition()) + ")";

						structureId = homeData.getHomeId();
					} else if (structureData != null) {
						homeStructureText += " " + structureData.getStructureTypeName();
						homeStructurePosition += " (" + formatBlockPos(structureData.getFramePosition()) + ")";

						//structureId = structureData.getStructureId();
					}

					textLength = Font.small.getStringWidth(homeStructureText);
					
					if (guiPage.isLeftPage()) {
						Font.small.printLeft(homeStructureLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
						Font.small.printLeft(homeStructureText, this.x + PAGE_LEFTPAGE_CENTER_X - indentX, y); 
						Font.small.printLeft(homeStructurePosition, this.x + PAGE_LEFTPAGE_CENTER_X - indentX + textLength, y);

						x1 = this.x + PAGE_LEFTPAGE_CENTER_X - indentX;
						x2 = x1 + Font.small.getStringWidth(homeStructureText);
						x3 = this.x + PAGE_LEFTPAGE_CENTER_X - indentX + textLength;
						x4 = x3 + Font.small.getStringWidth(homeStructurePosition);
					}

					if (guiPage.isRightPage()) {
						Font.small.printLeft(homeStructureLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
						Font.small.printLeft(homeStructureText, this.x + PAGE_RIGHTPAGE_CENTER_X - indentX, y); 
						Font.small.printLeft(homeStructurePosition, this.x + PAGE_RIGHTPAGE_CENTER_X - indentX + textLength, y);

						x1 = this.x + PAGE_RIGHTPAGE_CENTER_X - indentX;
						x2 = x1 + Font.small.getStringWidth(homeStructureText);
						x3 = this.x + PAGE_RIGHTPAGE_CENTER_X - indentX + textLength;
						x4 = x3 + Font.small.getStringWidth(homeStructurePosition);
					}

					if (!this.isSubPageOpen() && structureId > 0) {
						button = new GuiHyperlink(BUTTON_KEY_HOMELINK, getHyperlinkData(GuiPageType.HOME, getStructureDetailPageKey(structureId)));
						button.setIcon(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT);
						this.buttons.add(button);

						toolTip = new GuiTooltip(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT, TextUtils.translate("tektopiaBook.links.homedetails"));
						this.tooltips.add(toolTip);
						
						button = new GuiHyperlink(BUTTON_KEY_MAPLINK, getHyperlinkData(GuiPageType.HOME, getStructureDetailPageKey(structureId)));
						button.setIcon(x3, y, x4 - x3, Font.small.fontRenderer.FONT_HEIGHT);
						this.buttons.add(button);

						toolTip = new GuiTooltip(x3, y, x4 - x3, Font.small.fontRenderer.FONT_HEIGHT, TextUtils.translate("tektopiaBook.links.mapdetails"));
						this.tooltips.add(toolTip);
					}
				}

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

				if (resident.getCanHaveBed()) {
					String bedPositionLabel = TextUtils.translate("tektopiaBook.residents.bed");
					String bedPositionText = "";

					if (!StringUtils.isNullOrWhitespace(bedPositionLabel)) {
						if (resident.hasBed()) {
							bedPositionText += " " + formatBlockPos(resident.getBedPosition());
						}

						if (guiPage.isLeftPage()) {
							Font.small.printLeft(bedPositionLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
							Font.small.printLeft(bedPositionText, this.x + PAGE_LEFTPAGE_CENTER_X - indentX, y); 
						}

						if (guiPage.isRightPage()) {
							Font.small.printLeft(bedPositionLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
							Font.small.printLeft(bedPositionText, this.x + PAGE_RIGHTPAGE_CENTER_X - indentX, y); 
						}
					}
				}

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y + LINE_SPACE_Y_HEADER;

				String currentStructureLabel = TextUtils.translate("tektopiaBook.residents.structure");
				String currentStructureText = "";
				String currentStructurePosition = "";

				if (!StringUtils.isNullOrWhitespace(currentStructureLabel)) {
					int structureId = 0;
					int textLength = 0;

					if (structuresData != null && resident.getCurrentStructure() != null) {
						structureData = structuresData.getStructureByFramePosition(resident.getCurrentStructure());
						if (structureData != null) {
							currentStructureText += " " + structureData.getStructureTypeName();
							currentStructurePosition += " (" + formatBlockPos(resident.getCurrentStructure()) + ")";

							structureId = structureData.getStructureId();
						}
					} else {

						if (homeData != null && resident.getCurrentPosition() != null && resident.getBedPosition() != null) {
							BlockPos positionCurrent = resident.getCurrentPosition();
							BlockPos positionCurrent2 = positionCurrent.down(1);
							BlockPos positionBed = resident.getBedPosition();

							if (positionCurrent.equals(positionBed) || positionCurrent2.equals(positionBed)) {
								currentStructureText += " " + homeData.getStructureTypeName();
								currentStructurePosition += " (" + formatBlockPos(homeData.getFramePosition()) + ")";

								structureId = homeData.getHomeId();
							}
						}
					}

					textLength = Font.small.getStringWidth(homeStructureText);

					if (guiPage.isLeftPage()) {
						Font.small.printLeft(currentStructureLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
						Font.small.printLeft(currentStructureText, this.x + PAGE_LEFTPAGE_CENTER_X - indentX, y); 
						Font.small.printLeft(currentStructurePosition, this.x + PAGE_LEFTPAGE_CENTER_X - indentX + textLength, y);

						x1 = this.x + PAGE_LEFTPAGE_CENTER_X - indentX;
						x2 = x1 + Font.small.getStringWidth(currentStructureText);
						x3 = this.x + PAGE_LEFTPAGE_CENTER_X - indentX + textLength;
						x4 = x3 + Font.small.getStringWidth(currentStructurePosition);
					}

					if (guiPage.isRightPage()) {
						Font.small.printLeft(currentStructureLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
						Font.small.printLeft(currentStructureText, this.x + PAGE_RIGHTPAGE_CENTER_X - indentX, y);  
						Font.small.printLeft(currentStructurePosition, this.x + PAGE_RIGHTPAGE_CENTER_X - indentX + textLength, y); 

						x1 = this.x + PAGE_RIGHTPAGE_CENTER_X - indentX;
						x2 = x1 + Font.small.getStringWidth(currentStructureText);
						x3 = this.x + PAGE_RIGHTPAGE_CENTER_X - indentX + textLength;
						x4 = x3 + Font.small.getStringWidth(currentStructurePosition);
					}

					if (!this.isSubPageOpen() && structureId > 0) {
						button = new GuiHyperlink(BUTTON_KEY_STRUCTURELINK, getHyperlinkData(GuiPageType.STRUCTURE, getStructureDetailPageKey(structureId)));
						button.setIcon(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT);
						this.buttons.add(button);

						toolTip = new GuiTooltip(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT, TextUtils.translate("tektopiaBook.links.structuredetails"));
						this.tooltips.add(toolTip);
						
						button = new GuiHyperlink(BUTTON_KEY_MAPLINK, getHyperlinkData(GuiPageType.STRUCTURE, getStructureDetailPageKey(structureId)));
						button.setIcon(x3, y, x4 - x3, Font.small.fontRenderer.FONT_HEIGHT);
						this.buttons.add(button);

						toolTip = new GuiTooltip(x3, y, x4 - x3, Font.small.fontRenderer.FONT_HEIGHT, TextUtils.translate("tektopiaBook.links.mapdetails"));
						this.tooltips.add(toolTip);
					}
				}

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

				String currentPositionLabel = TextUtils.translate("tektopiaBook.residents.position");
				String currentPositionText = "";

				if (!StringUtils.isNullOrWhitespace(currentPositionLabel)) {
					currentPositionText += " " + formatBlockPos(resident.getCurrentPosition());

					if (guiPage.isLeftPage()) {
						Font.small.printLeft(currentPositionLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
						Font.small.printLeft(currentPositionText, this.x + PAGE_LEFTPAGE_CENTER_X - indentX, y); 

						x1 = this.x + PAGE_LEFTPAGE_CENTER_X - indentX;
						x2 = x1 + Font.small.getStringWidth(currentPositionText);
					}

					if (guiPage.isRightPage()) {
						Font.small.printLeft(currentPositionLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
						Font.small.printLeft(currentPositionText, this.x + PAGE_RIGHTPAGE_CENTER_X - indentX, y);

						x1 = this.x + PAGE_RIGHTPAGE_CENTER_X - indentX;
						x2 = x1 + Font.small.getStringWidth(currentPositionText); 
					}

					if (!this.isSubPageOpen()) {
						button = new GuiHyperlink(BUTTON_KEY_MAPLINK, getHyperlinkData(GuiPageType.RESIDENT, getResidentDetailPageKey(resident.getId())));
						button.setIcon(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT);
						this.buttons.add(button);

						toolTip = new GuiTooltip(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT, TextUtils.translate("tektopiaBook.links.mapdetails"));
						this.tooltips.add(toolTip);
					}
				}

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y + LINE_SPACE_Y_HEADER;

				if (!resident.isVendor()) {
					String additionalProfessions = TextUtils.translate("tektopiaBook.residents.additionalprofessions");

					if (!StringUtils.isNullOrWhitespace(additionalProfessions)) {
						additionalProfessions = TextFormatting.DARK_BLUE + additionalProfessions;

						if (guiPage.isLeftPage()) {
							Font.small.printLeft(additionalProfessions, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
						}

						if (guiPage.isRightPage()) {
							Font.small.printLeft(additionalProfessions, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
						}
					}

					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

					String nameHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.name");
					String levelHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.level");

					if (guiPage.isLeftPage()) {
						Font.small.printLeft(nameHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
						Font.small.printRight(levelHeader, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y);
					}

					if (guiPage.isRightPage()) {
						Font.small.printLeft(nameHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
						Font.small.printRight(levelHeader, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y);
					}

					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

					int index = 1;
					for (Entry<String, Integer> additionalProfessionType : resident.getAdditionalProfessions().entrySet()) {
						if (index > ADDITIONALPROFESSIONS_PER_PAGE) {
							// only display the top additional professions
							break;
						}

						String additionalProfessionText = getTypeName(additionalProfessionType.getKey());
						String additionalProfessionLevel = formatResidentLevel(additionalProfessionType.getValue(), additionalProfessionType.getValue(), false, false);

						if (guiPage.isLeftPage()) {
							Font.small.printLeft(additionalProfessionText, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
							Font.small.printRight(additionalProfessionLevel, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y);

							x1 = this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX;
							x2 = x1 + Font.small.getStringWidth(additionalProfessionText);
						}

						if (guiPage.isRightPage()) {
							Font.small.printLeft(additionalProfessionText, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
							Font.small.printRight(additionalProfessionLevel, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y);

							x1 = this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX;
							x2 = x1 + Font.small.getStringWidth(additionalProfessionText);
						}

						if (!this.isSubPageOpen() && residentsData.getProfessionTypeCount(additionalProfessionText) > 0) {
							button = new GuiHyperlink(BUTTON_KEY_PROFESSIONLINK, getHyperlinkData(GuiPageType.PROFESSIONTYPE, getProfessionDetailPageKey(additionalProfessionText)));
							button.setIcon(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT);
							this.buttons.add(button);

							toolTip = new GuiTooltip(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT, TextUtils.translate("tektopiaBook.links.professiondetails"));
							this.tooltips.add(toolTip);
						} 

						y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
						index++;
					}
				}
			}
		}
	}

	private void drawPageStatistics(int mouseX, int mouseY, float partialTicks, GuiPage guiPage) {
		drawPageHeader(mouseX, mouseY, partialTicks, guiPage);
		drawPageFooter(mouseX, mouseY, partialTicks, guiPage); 

		int y = this.y + PAGE_BODY_Y;
		int indentX = 10;

		GuiHyperlink button = null;
		GuiTooltip toolTip = null;
		int x1 = 0;
		int x2 = 0;
		int x3 = 0;
		int x4 = 0;

		String[] dataKey = getStatisticsPageKeyParts(guiPage.getDataKey());
		ResidentsData residentsData = this.villageData.getResidentsData();
		StructuresData structuresData = this.villageData.getStructuresData();
		String continued = TextUtils.translate("tektopiaBook.continued");

		Map<Integer, List<ResidentData>> happinessMap = residentsData == null ? null : residentsData.getResidentHappinessStatistics();
		Map<Integer, List<ResidentData>> hungerMap = residentsData == null ? null : residentsData.getResidentHungerStatistics();

		if (dataKey[0].equals("")) {
			// statistics summary

			String happinessHeader = TextUtils.translate("tektopiaBook.statistics.happiness");

			if (!StringUtils.isNullOrWhitespace(happinessHeader)) {
				happinessHeader = TextFormatting.DARK_BLUE + happinessHeader;

				if (guiPage.isLeftPage()) {
					Font.normal.printLeft(happinessHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
				}

				if (guiPage.isRightPage()) {
					Font.normal.printLeft(happinessHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
				}

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

				if (happinessMap != null) {
					final int[] maxLength = { 0 , 0 };
					maxLength[0] = 40;

					String rangeHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.range");
					String residentsHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.statistics.residents");

					maxLength[1] += 30;

					if (guiPage.isLeftPage()) {
						Font.small.printRight(rangeHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y); 
						Font.small.printRight(residentsHeader, this.x + PAGE_LEFTPAGE_CENTER_X + maxLength[1], y);
					}

					if (guiPage.isRightPage()) {
						Font.small.printRight(rangeHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y); 
						Font.small.printRight(residentsHeader, this.x + PAGE_RIGHTPAGE_CENTER_X + maxLength[1], y);
					}

					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

					for (Entry<Integer, List<ResidentData>> happinessEntry : happinessMap.entrySet()) {
						String rangeName = "";
						String rangeValue = "";

						int rangeEnd = happinessEntry.getKey();
						rangeName = formatStatisticsRange(Math.max(0, rangeEnd - ResidentsData.STATISTICS_RANGE + 1), rangeEnd, 100);
						rangeValue = "" + happinessEntry.getValue().size();

						if (guiPage.isLeftPage()) {
							Font.small.printRight(rangeName, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y); 
							Font.small.printRight(rangeValue, this.x + PAGE_LEFTPAGE_CENTER_X + maxLength[1], y); 

							x2 = this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0];
							x1 = x2 - Font.small.getStringWidth(rangeName);
						}

						if (guiPage.isRightPage()) {
							Font.small.printRight(rangeName, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y);
							Font.small.printRight(rangeValue, this.x + PAGE_RIGHTPAGE_CENTER_X + maxLength[1], y); 

							x2 = this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0];
							x1 = x2 - Font.small.getStringWidth(rangeName);
						}

						if (!this.isSubPageOpen()) {
							button = new GuiHyperlink(BUTTON_KEY_STATISTICLINK, getHyperlinkData(GuiPageType.STATS, getStatisticsPageKey("happiness", happinessEntry.getKey(), 0)));
							button.setIcon(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT);
							this.buttons.add(button);

							toolTip = new GuiTooltip(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT, TextUtils.translate("tektopiaBook.links.statisticsdetails"));
							this.tooltips.add(toolTip);
						}

						y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
					}
				}

				y += LINE_SPACE_Y_HEADER;
			}

			String hungerHeader = TextUtils.translate("tektopiaBook.statistics.hunger");

			if (!StringUtils.isNullOrWhitespace(hungerHeader)) {
				hungerHeader = TextFormatting.DARK_BLUE + hungerHeader;

				if (guiPage.isLeftPage()) {
					Font.normal.printLeft(hungerHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
				}

				if (guiPage.isRightPage()) {
					Font.normal.printLeft(hungerHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
				}

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

				if (hungerMap != null) {
					final int[] maxLength = { 0 , 0 };
					maxLength[0] = 40;

					String rangeHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.range");
					String residentsHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.statistics.residents");

					maxLength[1] += 30;

					if (guiPage.isLeftPage()) {
						Font.small.printRight(rangeHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y); 
						Font.small.printRight(residentsHeader, this.x + PAGE_LEFTPAGE_CENTER_X + maxLength[1], y);
					}

					if (guiPage.isRightPage()) {
						Font.small.printRight(rangeHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y); 
						Font.small.printRight(residentsHeader, this.x + PAGE_RIGHTPAGE_CENTER_X + maxLength[1], y);
					}

					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

					for (Entry<Integer, List<ResidentData>> hungerEntry : hungerMap.entrySet()) {
						String rangeName = "";
						String rangeValue = "";

						int rangeEnd = hungerEntry.getKey();
						rangeName = formatStatisticsRange(Math.max(0, rangeEnd - ResidentsData.STATISTICS_RANGE + 1), rangeEnd, 100);
						rangeValue = "" + hungerEntry.getValue().size();

						if (guiPage.isLeftPage()) {
							Font.small.printRight(rangeName, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y); 
							Font.small.printRight(rangeValue, this.x + PAGE_LEFTPAGE_CENTER_X + maxLength[1], y); 

							x2 = this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0];
							x1 = x2 - Font.small.getStringWidth(rangeName);
						}

						if (guiPage.isRightPage()) {
							Font.small.printRight(rangeName, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y);
							Font.small.printRight(rangeValue, this.x + PAGE_RIGHTPAGE_CENTER_X + maxLength[1], y); 

							x2 = this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0];
							x1 = x2 - Font.small.getStringWidth(rangeName);
						}

						if (!this.isSubPageOpen()) {
							button = new GuiHyperlink(BUTTON_KEY_STATISTICLINK, getHyperlinkData(GuiPageType.STATS, getStatisticsPageKey("hunger", hungerEntry.getKey(), 0)));
							button.setIcon(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT);
							this.buttons.add(button);

							toolTip = new GuiTooltip(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT, TextUtils.translate("tektopiaBook.links.statisticsdetails"));
							this.tooltips.add(toolTip);
						}

						y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
					}
				}

				y += LINE_SPACE_Y_HEADER;
			}

			String overcrowdingHeader = TextUtils.translate("tektopiaBook.statistics.overcrowding");

			if (!StringUtils.isNullOrWhitespace(overcrowdingHeader)) {
				overcrowdingHeader = TextFormatting.DARK_BLUE + overcrowdingHeader;

				if (guiPage.isLeftPage()) {
					Font.normal.printLeft(overcrowdingHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
				}

				if (guiPage.isRightPage()) {
					Font.normal.printLeft(overcrowdingHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
				}

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

				List<StructureData> structures = structuresData.getStructures();

				if (structures != null && structures.size() > 0) {
					final int[] maxLength = { 0 , 0 };
					maxLength[0] = 40;

					String overcrowdedHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.statistics.overcrowded");
					String structuresHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.structures");

					maxLength[1] += 30;

					if (guiPage.isLeftPage()) {
						Font.small.printRight(overcrowdedHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y); 
						Font.small.printRight(structuresHeader, this.x + PAGE_LEFTPAGE_CENTER_X + maxLength[1], y);
					}

					if (guiPage.isRightPage()) {
						Font.small.printRight(overcrowdedHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y); 
						Font.small.printRight(structuresHeader, this.x + PAGE_RIGHTPAGE_CENTER_X + maxLength[1], y);
					}

					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

					String overcrowdedLabel = TextFormatting.DARK_RED + "Yes";
					String overcrowdedText = "" + structures.stream().filter((s) -> s.isOvercrowdedCurrent()).count();

					if (guiPage.isLeftPage()) {
						Font.small.printRight(overcrowdedLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y); 
						Font.small.printRight(overcrowdedText, this.x + PAGE_LEFTPAGE_CENTER_X + maxLength[1], y);

						x2 = this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0];
						x1 = x2 - Font.small.getStringWidth(overcrowdedLabel);
					}

					if (guiPage.isRightPage()) {
						Font.small.printRight(overcrowdedLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y); 
						Font.small.printRight(overcrowdedText, this.x + PAGE_RIGHTPAGE_CENTER_X + maxLength[1], y);

						x2 = this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0];
						x1 = x2 - Font.small.getStringWidth(overcrowdedLabel);
					}

					if (!this.isSubPageOpen()) {
						button = new GuiHyperlink(BUTTON_KEY_STATISTICLINK, getHyperlinkData(GuiPageType.STATS, getStatisticsPageKey("overcrowding", 0, 0)));
						button.setIcon(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT);
						this.buttons.add(button);

						toolTip = new GuiTooltip(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT, TextUtils.translate("tektopiaBook.links.statisticsdetails"));
						this.tooltips.add(toolTip);
					}

					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

					String undercrowdedLabel = TextFormatting.DARK_GREEN + "No";
					String undercrowdedText = "" + structures.stream().filter((s) -> !s.isOvercrowdedCurrent()).count();

					if (guiPage.isLeftPage()) {
						Font.small.printRight(undercrowdedLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y); 
						Font.small.printRight(undercrowdedText, this.x + PAGE_LEFTPAGE_CENTER_X + maxLength[1], y);
					}

					if (guiPage.isRightPage()) {
						Font.small.printRight(undercrowdedLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y); 
						Font.small.printRight(undercrowdedText, this.x + PAGE_RIGHTPAGE_CENTER_X + maxLength[1], y); 
					}

					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

				}

				y += LINE_SPACE_Y_HEADER;
			}

		} else {
			// statistics details

			Entry<Integer, List<ResidentData>> statisticsSetResidents = null;
			List<StructureData> statisticsSetStructures = null;
			String header = "";

			switch (dataKey[0]) {
			case "happiness":
				header = TextUtils.translate("tektopiaBook.statistics.happinessheader");
				statisticsSetResidents = happinessMap.entrySet().stream()
						.filter(e -> dataKey[1].equals(e.getKey().toString()))
						.findFirst().orElse(null);
				break;
			case "hunger":
				header = TextUtils.translate("tektopiaBook.statistics.hungerheader");
				statisticsSetResidents = hungerMap.entrySet().stream()
						.filter(e -> dataKey[1].equals(e.getKey().toString()))
						.findFirst().orElse(null); 
				break;
			case "overcrowding":
				header = TextUtils.translate("tektopiaBook.statistics.overcrowdingheader");
				statisticsSetStructures = structuresData.getStructuresOvercrowded();
				break;
			default:
				break;
			}

			if (!StringUtils.isNullOrWhitespace(header)) {
				if (!(dataKey[1].equals("0") && dataKey[2].equals("0")) && !StringUtils.isNullOrWhitespace(continued)) {
					header += " " + continued;
				}
				header = TextFormatting.DARK_BLUE + header;

				if (guiPage.isLeftPage()) {
					Font.normal.printLeft(header, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
				}

				if (guiPage.isRightPage()) {
					Font.normal.printLeft(header, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
				}
			}

			y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

			if (statisticsSetResidents != null) {
				String rangeLabel = TextUtils.translate("tektopiaBook.statistics.range");
				String rangeText = "";

				if (!StringUtils.isNullOrWhitespace(rangeLabel)) {
					if (statisticsSetResidents != null) {
						int rangeEnd = statisticsSetResidents.getKey();
						rangeText += formatStatisticsRange(Math.max(0, rangeEnd - ResidentsData.STATISTICS_RANGE + 1), rangeEnd, 100);

						if (!dataKey[2].equals("0") && !StringUtils.isNullOrWhitespace(continued)) {
							rangeText += " " + continued;
						}
					}

					if (guiPage.isLeftPage()) {
						Font.small.printLeft(rangeLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
						Font.small.printRight(rangeText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y); 
					}

					if (guiPage.isRightPage()) {
						Font.small.printLeft(rangeLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
						Font.small.printRight(rangeText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y); 
					}
				}

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

				if (dataKey[2].equals("0")) {
					String residentsLabel = TextUtils.translate("tektopiaBook.statistics.total");
					String residentsText = "";

					if (!StringUtils.isNullOrWhitespace(residentsLabel)) {
						if (statisticsSetResidents != null && statisticsSetResidents.getValue() != null) {
							residentsText += "" + statisticsSetResidents.getValue().size();
						}

						if (guiPage.isLeftPage()) {
							Font.small.printLeft(residentsLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
							Font.small.printRight(residentsText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y);
						}

						if (guiPage.isRightPage()) {
							Font.small.printLeft(residentsLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
							Font.small.printRight(residentsText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y);  
						}
					}
				}

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y + LINE_SPACE_Y_HEADER;

				String residentsHeader = TextUtils.translate("tektopiaBook.statistics.residents");

				if (!StringUtils.isNullOrWhitespace(residentsHeader)) {
					residentsHeader = TextFormatting.DARK_BLUE + residentsHeader;

					if (guiPage.isLeftPage()) {
						Font.small.printLeft(residentsHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
					}

					if (guiPage.isRightPage()) {
						Font.small.printLeft(residentsHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
					}
				}

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

				final int[] maxLength = { 0 };
				maxLength[0] += LABEL_TRAILINGSPACE_X;

				int page = 0;
				try {
					page = Integer.parseInt(dataKey[2]);
				}
				catch (NumberFormatException e) {
					page = 0;
				}
				int startIndex = page * STATRESIDENTS_PER_PAGE;
				int index = 0;

				String bedHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.bed");    
				String residentHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.resident");
				String professionHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.profession");
				String levelHeader = "";

				switch (dataKey[0]) {
				case "happiness":
					levelHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.statistics.happiness");
					break;
				case "hunger":
					levelHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.statistics.hunger");
					break;
				default:
					break;
				}

				if (guiPage.isLeftPage()) {
					Font.small.printLeft(bedHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y);
					Font.small.printLeft(residentHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y);
					Font.small.printLeft(professionHeader, this.x + PAGE_LEFTPAGE_CENTER_X + indentX + indentX, y);
					Font.small.printRight(levelHeader, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y);
				}

				if (guiPage.isRightPage()) {
					Font.small.printLeft(bedHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y);
					Font.small.printLeft(residentHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y);
					Font.small.printLeft(professionHeader, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX + indentX, y);
					Font.small.printRight(levelHeader, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y);
				}

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y; 

				for (ResidentData resident : statisticsSetResidents.getValue()) {
					if (index >= startIndex && index < startIndex + STATRESIDENTS_PER_PAGE) {
						String residentBed = "";
						String residentName = "";
						String residentProfession = "";
						String residentLevel = "";

						if (resident.getCanHaveBed())
							residentBed = (resident.hasBed() ? TextUtils.SYMBOL_GREENTICK : TextUtils.SYMBOL_REDCROSS);
						residentName = formatResidentName(resident.isMale(), resident.getName(), true);
						residentProfession += getTypeName(resident.getProfessionType());

						switch (dataKey[0]) {
						case "happiness":
							residentLevel = "" + formatResidentStatistic(resident.getHappy(), resident.getMaxHappy(), false);
							break;
						case "hunger":
							residentLevel = "" + formatResidentStatistic(resident.getHunger(), resident.getMaxHunger(), false);
							break;
						default:
							break;
						}

						if (guiPage.isLeftPage()) {
							Font.small.printRight(residentBed, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
							Font.small.printLeft(residentName, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y);
							Font.small.printLeft(residentProfession, this.x + PAGE_LEFTPAGE_CENTER_X + indentX + indentX, y);
							Font.small.printRight(residentLevel, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y);

							x1 = this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0];
							x2 = x1 + Font.small.getStringWidth(residentName);
							x3 = this.x + PAGE_LEFTPAGE_CENTER_X + indentX + indentX;
							x4 = x3 + Font.small.getStringWidth(residentProfession);
						}

						if (guiPage.isRightPage()) {
							Font.small.printRight(residentBed, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
							Font.small.printLeft(residentName, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y);
							Font.small.printLeft(residentProfession, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX + indentX, y);
							Font.small.printRight(residentLevel, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y);

							x1 = this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0];
							x2 = x1 + Font.small.getStringWidth(residentName);
							x3 = this.x + PAGE_RIGHTPAGE_CENTER_X + indentX + indentX;
							x4 = x3 + Font.small.getStringWidth(residentProfession);
						}

						if (!this.isSubPageOpen()) {
							button = new GuiHyperlink(BUTTON_KEY_RESIDENTLINK, getHyperlinkData(GuiPageType.RESIDENT, getResidentDetailPageKey(resident.getId())));
							button.setIcon(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT);
							this.buttons.add(button);

							toolTip = new GuiTooltip(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT, TextUtils.translate("tektopiaBook.links.residentdetails"));
							this.tooltips.add(toolTip);

							button = new GuiHyperlink(BUTTON_KEY_PROFESSIONLINK, getHyperlinkData(GuiPageType.PROFESSIONTYPE, getProfessionDetailPageKey(residentProfession)));
							button.setIcon(x3, y, x4 - x3, Font.small.fontRenderer.FONT_HEIGHT);
							this.buttons.add(button);

							toolTip = new GuiTooltip(x3, y, x4 - x3, Font.small.fontRenderer.FONT_HEIGHT, TextUtils.translate("tektopiaBook.links.professiondetails"));
							this.tooltips.add(toolTip); 
						}

						y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
					}
					index++;
				}
			}

			if (statisticsSetStructures != null) {

				if (dataKey[2].equals("0")) {
					String totalLabel = TextUtils.translate("tektopiaBook.structures.total");
					String totalText = "";

					if (!StringUtils.isNullOrWhitespace(totalLabel)) {
						if (statisticsSetStructures != null) {
							totalText += "" + statisticsSetStructures.size();
						}

						if (guiPage.isLeftPage()) {
							Font.small.printLeft(totalLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
							Font.small.printRight(totalText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y);
						}

						if (guiPage.isRightPage()) {
							Font.small.printLeft(totalLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
							Font.small.printRight(totalText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y);  
						}
					}
				}

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y + LINE_SPACE_Y_HEADER;

				String structuresHeader = TextUtils.translate("tektopiaBook.headers.structures");

				if (!StringUtils.isNullOrWhitespace(structuresHeader)) {
					structuresHeader = TextFormatting.DARK_BLUE + structuresHeader;

					if (guiPage.isLeftPage()) {
						Font.small.printLeft(structuresHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
					}

					if (guiPage.isRightPage()) {
						Font.small.printLeft(structuresHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
					}
				}

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

				final int[] maxLength = { 0, 0 };

				int page = 0;
				try {
					page = Integer.parseInt(dataKey[2]);
				}
				catch (NumberFormatException e) {
					page = 0;
				}
				int startIndex = page * STATSTRUCTURES_PER_PAGE;
				int index = 0;

				String tilesHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.statistics.tilesperoccupant");
				String structureHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.structure");
				String positionHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.position");
				String minimumHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.minimum");
				String currentHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.current");

				maxLength[0] += (Font.small.getStringWidth(minimumHeader) + Font.small.getStringWidth(currentHeader) + 5) / 2;
				maxLength[1] += Font.small.getStringWidth(currentHeader) + 5;

				if (guiPage.isLeftPage()) {
					Font.small.printCentered(tilesHeader, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X - maxLength[0], y);

					y += Font.small.fontRenderer.FONT_HEIGHT;

					Font.small.printLeft(structureHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
					Font.small.printLeft(positionHeader, this.x + PAGE_LEFTPAGE_CENTER_X - indentX - indentX - indentX, y);
					Font.small.printRight(minimumHeader, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X - maxLength[1], y);
					Font.small.printRight(currentHeader, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y);
				}

				if (guiPage.isRightPage()) {
					Font.small.printCentered(tilesHeader, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X - maxLength[0], y);

					y += Font.small.fontRenderer.FONT_HEIGHT;

					Font.small.printLeft(structureHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
					Font.small.printLeft(positionHeader, this.x + PAGE_RIGHTPAGE_CENTER_X - indentX - indentX - indentX, y);
					Font.small.printRight(minimumHeader, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X - maxLength[1], y);
					Font.small.printRight(currentHeader, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y);
				}

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y; 

				for (StructureData structure : statisticsSetStructures) {
					if (index >= startIndex && index < startIndex + STATSTRUCTURES_PER_PAGE) {
						String structureName = "";
						String structurePosition = "";
						String structureMinimum = "";
						String structureCurrent = "";

						structureName += structure.getStructureTypeName();
						structurePosition += formatBlockPos(structure.getFramePosition());
						structureMinimum += structure.getTilesPerVillager();
						structureCurrent += structure.getDensityRatio();

						if (guiPage.isLeftPage()) {
							Font.small.printLeft(structureName, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
							Font.small.printLeft(structurePosition, this.x + PAGE_LEFTPAGE_CENTER_X - indentX - indentX - indentX, y);
							Font.small.printRight(structureMinimum, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X - maxLength[1], y);
							Font.small.printRight(structureCurrent, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y);

							x1 = this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX;
							x2 = x1 + Font.small.getStringWidth(structureName);
							x3 = this.x + PAGE_LEFTPAGE_CENTER_X - indentX - indentX - indentX;
							x4 = x3 + Font.small.getStringWidth(structurePosition);
						}

						if (guiPage.isRightPage()) {
							Font.small.printLeft(structureName, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
							Font.small.printLeft(structurePosition, this.x + PAGE_RIGHTPAGE_CENTER_X - indentX - indentX - indentX, y);
							Font.small.printRight(structureMinimum, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X - maxLength[1], y);
							Font.small.printRight(structureCurrent, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y);

							x1 = this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX;
							x2 = x1 + Font.small.getStringWidth(structureName);
							x3 = this.x + PAGE_RIGHTPAGE_CENTER_X - indentX - indentX - indentX;
							x4 = x3 + Font.small.getStringWidth(structurePosition);
						}

						if (!this.isSubPageOpen()) {
							button = new GuiHyperlink(BUTTON_KEY_STRUCTURELINK, getHyperlinkData(GuiPageType.STRUCTURE, getStructureDetailPageKey(structure.getStructureId())));
							button.setIcon(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT);
							this.buttons.add(button);

							toolTip = new GuiTooltip(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT, TextUtils.translate("tektopiaBook.links.structuredetails"));
							this.tooltips.add(toolTip);

							button = new GuiHyperlink(BUTTON_KEY_MAPLINK, getHyperlinkData(GuiPageType.STRUCTURE, getStructureDetailPageKey(structure.getStructureId())));
							button.setIcon(x3, y, x4 - x3, Font.small.fontRenderer.FONT_HEIGHT);
							this.buttons.add(button);

							toolTip = new GuiTooltip(x3, y, x4 - x3, Font.small.fontRenderer.FONT_HEIGHT, TextUtils.translate("tektopiaBook.links.mapdetails"));
							this.tooltips.add(toolTip);
						}

						y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

					}
					index++;
				}
			}
		}
	}

	private void drawPageStructure(int mouseX, int mouseY, float partialTicks, GuiPage guiPage) {
		drawPageHeader(mouseX, mouseY, partialTicks, guiPage);
		drawPageFooter(mouseX, mouseY, partialTicks, guiPage); 

		int y = this.y + PAGE_BODY_Y;
		int indentX = 10;

		String[] dataKey = getPageKeyParts(guiPage.getDataKey());
		StructuresData structuresData = this.villageData.getStructuresData();
		Map<VillageStructureType, Integer> structureTypeCounts = structuresData != null ? structuresData.getStructureTypeCounts() : null;
		String continued = TextUtils.translate("tektopiaBook.continued");

		if (dataKey[0].equals("")) {
			// structure type

			String typeHeader = TextUtils.translate("tektopiaBook.structures.structuretypes");
			if (!dataKey[1].equals("0") && !StringUtils.isNullOrWhitespace(continued)) {
				typeHeader += " " + continued;
			}
			if (!StringUtils.isNullOrWhitespace(typeHeader)) {
				typeHeader = TextFormatting.DARK_BLUE + typeHeader;

				if (guiPage.isLeftPage()) {
					Font.normal.printLeft(typeHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
				}

				if (guiPage.isRightPage()) {
					Font.normal.printLeft(typeHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y);
				}
			} 

			y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

			if (structureTypeCounts != null) {
				final int[] maxLength = { 0 };

				int page = 0;
				try {
					page = Integer.parseInt(dataKey[1]);
				}
				catch (NumberFormatException e) {
					page = 0;
				}
				int startIndex = page * STRUCTURETYPES_PER_PAGE;
				int index = 0;

				String nameHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.name");
				String countHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.count");

				maxLength[0] += Font.small.getStringWidth(countHeader);

				if (guiPage.isLeftPage()) {
					Font.small.printLeft(nameHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
					Font.small.printLeft(countHeader, this.x + PAGE_LEFTPAGE_CENTER_X, y);
				}

				if (guiPage.isRightPage()) {
					Font.small.printLeft(nameHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
					Font.small.printLeft(countHeader, this.x + PAGE_RIGHTPAGE_CENTER_X, y);
				}

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

				for (VillageStructureType structureType : TektopiaUtils.getVillageStructureTypes()) {
					if (index >= startIndex && index < startIndex + STRUCTURETYPES_PER_PAGE) {
						String typeName = getStructureTypeName(structureType);
						int typeCount = 0;

						int x1 = 0;
						int x2 = 0;

						if (structureTypeCounts.containsKey(structureType)) {
							typeCount = structureTypeCounts.get(structureType);
						}

						if (guiPage.isLeftPage()) {
							Font.small.printLeft(typeName, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
							Font.small.printRight(typeCount, this.x + PAGE_LEFTPAGE_CENTER_X + maxLength[0], y);

							x1 = this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX;
							x2 = x1 + Font.small.getStringWidth(typeName);
						}

						if (guiPage.isRightPage()) {
							Font.small.printLeft(typeName, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
							Font.small.printRight(typeCount, this.x + PAGE_RIGHTPAGE_CENTER_X + maxLength[0], y);

							x1 = this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX;
							x2 = x1 + Font.small.getStringWidth(typeName);
						}

						if (!this.isSubPageOpen() && typeCount > 0) {
							GuiHyperlink button = new GuiHyperlink(BUTTON_KEY_STRUCTURELINK, getHyperlinkData(GuiPageType.STRUCTURETYPE, getStructureTypePageKey(structureType)));
							button.setIcon(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT);
							this.buttons.add(button);

							GuiTooltip toolTip = new GuiTooltip(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT, TextUtils.translate("tektopiaBook.links.structuredetails"));
							this.tooltips.add(toolTip);
						}

						y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
					}  
					index++;
				}
			}

		} else {
			// structure

			int structureId = 0;
			try {
				structureId = Integer.parseInt(dataKey[0]);
			}
			catch (NumberFormatException e) {
				structureId = 0;
			}

			StructureData structureData = structuresData.getStructureById(structureId);
			
			if (structureData != null) {

				GuiHyperlink button = null;
				GuiTooltip toolTip = null;
				int x1 = 0;
				int x2 = 0;
				int x3 = 0;
				int x4 = 0;

				String header = structureData.getStructureTypeName();

				if (!StringUtils.isNullOrWhitespace(header)) {
					// check if this is the villager we clicked on
					if (this.villageData.getStructureId() > 0 && this.villageData.getStructureId() == structureData.getStructureId()) {
						header = TextFormatting.UNDERLINE + header;
					}
					else if (this.villageData.getFramePosition() != null && this.villageData.getFramePosition().equals(structureData.getFramePosition())) {
						header = TextFormatting.UNDERLINE + header;
					}      
					if (!dataKey[1].equals("0") && !StringUtils.isNullOrWhitespace(continued)) {
						header += " " + continued;
					}
					header = TextFormatting.DARK_BLUE + header;

					if (guiPage.isLeftPage()) {
						Font.normal.printLeft(header, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
					}

					if (guiPage.isRightPage()) {
						Font.normal.printLeft(header, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
					}
				} 

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

				String framePositionLabel = TextUtils.translate("tektopiaBook.structures.frameposition");
				String framePositionText = "";

				if (!StringUtils.isNullOrWhitespace(framePositionLabel)) {
					framePositionText += formatBlockPos(structureData.getFramePosition());

					if (guiPage.isLeftPage()) {
						Font.small.printLeft(framePositionLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
						Font.small.printLeft(framePositionText, this.x + PAGE_LEFTPAGE_CENTER_X, y); 

						x1 = this.x + PAGE_LEFTPAGE_CENTER_X - indentX;
						x2 = x1 + Font.small.getStringWidth(framePositionText);
					}

					if (guiPage.isRightPage()) {
						Font.small.printLeft(framePositionLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
						Font.small.printLeft(framePositionText, this.x + PAGE_RIGHTPAGE_CENTER_X, y); 

						x1 = this.x + PAGE_RIGHTPAGE_CENTER_X - indentX;
						x2 = x1 + Font.small.getStringWidth(framePositionText); 
					}

					if (!this.isSubPageOpen()) {
						button = new GuiHyperlink(BUTTON_KEY_MAPLINK, getHyperlinkData(GuiPageType.STRUCTURE, getStructureDetailPageKey(structureData.getStructureId())));
						button.setIcon(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT);
						this.buttons.add(button);

						toolTip = new GuiTooltip(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT, TextUtils.translate("tektopiaBook.links.mapdetails"));
						this.tooltips.add(toolTip);
					}
				} 

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

				String floorTilesLabel = TextUtils.translate("tektopiaBook.structures.floortiles");
				String floorTilesText = "";

				if (!StringUtils.isNullOrWhitespace(floorTilesLabel)) {
					floorTilesText += "" + structureData.getFloorTileCount();

					if (guiPage.isLeftPage()) {
						Font.small.printLeft(floorTilesLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
						Font.small.printRight(floorTilesText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y); 
					}

					if (guiPage.isRightPage()) {
						Font.small.printLeft(floorTilesLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
						Font.small.printRight(floorTilesText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y); 
					}
				}

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

				String currentOccupantsLabel = TextUtils.translate("tektopiaBook.structures.currentoccupants");
				String currentOccupantsText = "";

				if (!StringUtils.isNullOrWhitespace(currentOccupantsLabel)) {
					currentOccupantsText += "" + structureData.getOccupantCount();

					if (guiPage.isLeftPage()) {
						Font.small.printLeft(currentOccupantsLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
						Font.small.printRight(currentOccupantsText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y); 
					}

					if (guiPage.isRightPage()) {
						Font.small.printLeft(currentOccupantsLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
						Font.small.printRight(currentOccupantsText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y); 
					}
				}

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y + LINE_SPACE_Y_HEADER;

				if (structureData.getTilesPerVillager() > 0) {

					String tilesperoccupantLabel = TextUtils.translate("tektopiaBook.structures.tilesperoccupant");

					if (!StringUtils.isNullOrWhitespace(tilesperoccupantLabel)) {
						tilesperoccupantLabel = TextFormatting.DARK_BLUE + tilesperoccupantLabel;

						if (guiPage.isLeftPage()) {
							Font.small.printLeft(tilesperoccupantLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y);
						}

						if (guiPage.isRightPage()) {
							Font.small.printLeft(tilesperoccupantLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
						}
					}

					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

					String minimumTilesLabel = TextUtils.translate("tektopiaBook.structures.minimumtiles");
					String minimumTilesText = "";

					if (!StringUtils.isNullOrWhitespace(minimumTilesLabel)) {
						minimumTilesText += "" + structureData.getTilesPerVillager();

						if (guiPage.isLeftPage()) {
							Font.small.printLeft(minimumTilesLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
							Font.small.printRight(minimumTilesText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y); 
						}

						if (guiPage.isRightPage()) {
							Font.small.printLeft(minimumTilesLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
							Font.small.printRight(minimumTilesText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y); 
						}
					}

					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

					String currentTilesLabel = TextUtils.translate("tektopiaBook.structures.currenttiles");
					String currentTilesText = "";

					if (!StringUtils.isNullOrWhitespace(currentTilesLabel)) {
						currentTilesText += "" + structureData.getDensityRatio();
						if (structureData.isOvercrowdedCurrent()) {
							currentTilesText = TextFormatting.DARK_RED + currentTilesText;
						} else {
							currentTilesText = TextFormatting.DARK_GREEN + currentTilesText;
						}

						if (guiPage.isLeftPage()) {
							Font.small.printLeft(currentTilesLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
							Font.small.printRight(currentTilesText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y); 
						}

						if (guiPage.isRightPage()) {
							Font.small.printLeft(currentTilesLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
							Font.small.printRight(currentTilesText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y); 
						}
					}

					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

					String overcrowdedLabel = TextUtils.translate("tektopiaBook.structures.overcrowded");
					String overcrowdedText = "";

					if (!StringUtils.isNullOrWhitespace(overcrowdedLabel)) {
						overcrowdedText += "" + (structureData.isOvercrowdedCurrent() 
								? TextFormatting.DARK_RED + "Yes" 
										: TextFormatting.DARK_GREEN + "No");

						if (guiPage.isLeftPage()) {
							Font.small.printLeft(overcrowdedLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
							Font.small.printRight(overcrowdedText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y); 
						}

						if (guiPage.isRightPage()) {
							Font.small.printLeft(overcrowdedLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
							Font.small.printRight(overcrowdedText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y); 
						}
					}

				} else {

					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

				}
				
				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y + LINE_SPACE_Y_HEADER;

				String occupantsText = TextUtils.translate("tektopiaBook.structures.occupants");

				if (!StringUtils.isNullOrWhitespace(occupantsText)) {
					occupantsText = TextFormatting.DARK_BLUE + occupantsText;

					if (guiPage.isLeftPage()) {
						Font.small.printLeft(occupantsText, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
					}

					if (guiPage.isRightPage()) {
						Font.small.printLeft(occupantsText, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
					}
				} 

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

				int page = 0;
				try {
					page = Integer.parseInt(dataKey[1]);
				}
				catch (NumberFormatException e) {
					page = 0;
				}
				int startIndex = page * STRUCTUREOCCUPANTS_PER_PAGE;
				int index = 0;

				String nameHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.name");
				String professionHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.profession");
				String levelHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.level");

				if (guiPage.isLeftPage()) {
					Font.small.printLeft(nameHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
					Font.small.printLeft(professionHeader, this.x + PAGE_LEFTPAGE_CENTER_X, y);
					Font.small.printRight(levelHeader, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y);
				}

				if (guiPage.isRightPage()) {
					Font.small.printLeft(nameHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
					Font.small.printLeft(professionHeader, this.x + PAGE_RIGHTPAGE_CENTER_X, y);
					Font.small.printRight(levelHeader, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y);
				}

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y; 

				for (ResidentData occupant : structureData.getOccupants()) {
					if (index >= startIndex && index < startIndex + STRUCTUREOCCUPANTS_PER_PAGE) {

						String occupantName = "";
						String occupantProfession = "";
						String occupantLevel = "";

						occupantName += formatResidentName(occupant.isMale(), occupant.getName(), true);
						occupantProfession += getTypeName(occupant.getProfessionType());

						ProfessionType professionType = TektopiaUtils.getProfessionType(occupant.getProfessionType());
						if (professionType != null) {
							switch (professionType) {
							case CHILD:
							case NITWIT:
								break;
							default:                       			
								occupantLevel = formatResidentLevel(occupant.getLevel(), occupant.getBaseLevel(), false, false);
								break;
							}
						}

						if (guiPage.isLeftPage()) {
							Font.small.printLeft(occupantName, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
							Font.small.printLeft(occupantProfession, this.x + PAGE_LEFTPAGE_CENTER_X, y);
							Font.small.printRight(occupantLevel, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y);

							x1 = this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX;
							x2 = x1 + Font.small.getStringWidth(occupantName);
							x3 = this.x + PAGE_LEFTPAGE_CENTER_X;
							x4 = x3 + Font.small.getStringWidth(occupantProfession);
						}

						if (guiPage.isRightPage()) {
							Font.small.printLeft(occupantName, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
							Font.small.printLeft(occupantProfession, this.x + PAGE_RIGHTPAGE_CENTER_X, y);
							Font.small.printRight(occupantLevel, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y);

							x1 = this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX;
							x2 = x1 + Font.small.getStringWidth(occupantName);
							x3 = this.x + PAGE_RIGHTPAGE_CENTER_X;
							x4 = x3 + Font.small.getStringWidth(occupantProfession);
						}

						if (!this.isSubPageOpen()) {
							
							if (occupant.isVisitor()) {
								
								button = new GuiHyperlink(BUTTON_KEY_VISITORLINK, getHyperlinkData(GuiPageType.VISITOR, getResidentDetailPageKey(occupant.getId())));
								button.setIcon(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT);
								this.buttons.add(button);

								toolTip = new GuiTooltip(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT, TextUtils.translate("tektopiaBook.links.visitordetails"));
								this.tooltips.add(toolTip);
								
							} else {
								
								button = new GuiHyperlink(BUTTON_KEY_RESIDENTLINK, getHyperlinkData(GuiPageType.RESIDENT, getResidentDetailPageKey(occupant.getId())));
								button.setIcon(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT);
								this.buttons.add(button);

								toolTip = new GuiTooltip(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT, TextUtils.translate("tektopiaBook.links.residentdetails"));
								this.tooltips.add(toolTip);

								button = new GuiHyperlink(BUTTON_KEY_PROFESSIONLINK, getHyperlinkData(GuiPageType.PROFESSIONTYPE, getProfessionDetailPageKey(occupantProfession)));
								button.setIcon(x3, y, x4 - x3, Font.small.fontRenderer.FONT_HEIGHT);
								this.buttons.add(button);

								toolTip = new GuiTooltip(x3, y, x4 - x3, Font.small.fontRenderer.FONT_HEIGHT, TextUtils.translate("tektopiaBook.links.professiondetails"));
								this.tooltips.add(toolTip); 
							}
						}

						y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

					}
					index++;
				}
			}
		}
	}

	private void drawPageStructureType(int mouseX, int mouseY, float partialTicks, GuiPage guiPage) {
		drawPageHeader(mouseX, mouseY, partialTicks, guiPage);
		drawPageFooter(mouseX, mouseY, partialTicks, guiPage); 

		int y = this.y + PAGE_BODY_Y;
		int indentX = 10;

		GuiHyperlink button = null;
		GuiTooltip toolTip = null;
		int x1 = 0;
		int x2 = 0;
		int x3 = 0;
		int x4 = 0;

		String[] dataKey = getPageKeyParts(guiPage.getDataKey());
		VillageStructureType structureType = VillageStructureType.valueOf(dataKey[0]);
		String continued = TextUtils.translate("tektopiaBook.continued");
		String summary = TextUtils.translate("tektopiaBook.summary");

		StructuresData structuresData = this.villageData.getStructuresData();
		Map<VillageStructureType, Integer> structureTypeCounts = structuresData != null ? structuresData.getStructureTypeCounts() : null;
		List<StructureData> structures = structuresData != null ? structuresData.getStructuresByType(structureType) : null;

		String typeName = getStructureTypeName(structureType);

		if (!StringUtils.isNullOrWhitespace(typeName)) {
			typeName += " " + summary;
			if (!dataKey[1].equals("0") && !StringUtils.isNullOrWhitespace(continued)) {
				typeName += " " + continued;
			}
			typeName = TextFormatting.DARK_BLUE + typeName;

			if (guiPage.isLeftPage()) {
				Font.normal.printLeft(typeName, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
			}

			if (guiPage.isRightPage()) {
				Font.normal.printLeft(typeName, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
			}
		} 

		y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

		if (dataKey[1].equals("0")) {

			String totalLabel = TextUtils.translate("tektopiaBook.structuretypes.total");
			String totalText = "";

			if (!StringUtils.isNullOrWhitespace(totalLabel)) {
				if (structureTypeCounts != null && structureTypeCounts.containsKey(structureType)) {
					totalText += "" + structureTypeCounts.get(structureType);
				}

				if (guiPage.isLeftPage()) {
					Font.small.printLeft(totalLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
					Font.small.printRight(totalText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y); 
				}

				if (guiPage.isRightPage()) {
					Font.small.printLeft(totalLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
					Font.small.printRight(totalText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y); 
				}
			}
		} 

		y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y + LINE_SPACE_Y_HEADER;

		String header = TextUtils.translate("tektopiaBook.structuretypes.frameposition");

		if (!StringUtils.isNullOrWhitespace(header)) {
			header = TextFormatting.DARK_BLUE + header;

			if (guiPage.isLeftPage()) {
				Font.small.printLeft(header, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
			}

			if (guiPage.isRightPage()) {
				Font.small.printLeft(header, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
			}
		} 

		y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

		if (structures != null) {
			final int[] maxLength = { 0 , 0 };
			maxLength[0] += LABEL_TRAILINGSPACE_X;

			int page = 0;
			try {
				page = Integer.parseInt(dataKey[1]);
			}
			catch (NumberFormatException e) {
				page = 0;
			}
			int startIndex = page * STRUCTURES_PER_PAGE;
			int index = 0;

			String indexHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.index");
			String positionHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.position");
			String floorTilesHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.structuretypes.floor");
			String validTextHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.valid");

			maxLength[1] += Font.small.getStringWidth(floorTilesHeader);

			if (guiPage.isLeftPage()) {
				Font.small.printRight(indexHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
				Font.small.printLeft(positionHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y); 
				Font.small.printLeft(floorTilesHeader, this.x + PAGE_LEFTPAGE_CENTER_X, y);
				Font.small.printRight(validTextHeader, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y);
			}

			if (guiPage.isRightPage()) {
				Font.small.printRight(indexHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
				Font.small.printLeft(positionHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y); 
				Font.small.printLeft(floorTilesHeader, this.x + PAGE_RIGHTPAGE_CENTER_X, y);
				Font.small.printRight(validTextHeader, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y);
			}

			y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y; 

			for (StructureData structure : structures) {
				if (index >= startIndex && index < startIndex + STRUCTURES_PER_PAGE) {

					String indexName = "" + (index + 1);
					String framePosition = formatBlockPos(structure.getFramePosition());
					if (this.villageData.getStructureId() > 0 && this.villageData.getStructureId() == structure.getStructureId()) {
						framePosition = TextFormatting.UNDERLINE + framePosition;
					} 
					else if (this.villageData.getFramePosition() != null && structure.getFramePosition() != null && this.villageData.getFramePosition().equals(structure.getFramePosition())) {
						framePosition = TextFormatting.UNDERLINE + framePosition;
					}
					String floorTiles = "" + (structure.getFloorTileCount() >= 0 ? structure.getFloorTileCount() : "0");
					String validText = "" + (structure.isValid() ? TextUtils.SYMBOL_GREENTICK : TextUtils.SYMBOL_REDCROSS);

					if (guiPage.isLeftPage()) {
						Font.small.printRight(indexName, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
						Font.small.printLeft(framePosition, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y);
						Font.small.printRight(floorTiles, this.x + PAGE_LEFTPAGE_CENTER_X + maxLength[1], y);
						Font.small.printRight(validText, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y);

						x2 = this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX;
						x1 = x2 - Font.small.getStringWidth(indexName);
						x3 = this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + maxLength[0];
						x4 = x3 + Font.small.getStringWidth(framePosition);
					}

					if (guiPage.isRightPage()) {
						Font.small.printRight(indexName, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
						Font.small.printLeft(framePosition, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0], y);
						Font.small.printRight(floorTiles, this.x + PAGE_RIGHTPAGE_CENTER_X + maxLength[1], y);
						Font.small.printRight(validText, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y);

						x2 = this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX;
						x1 = x2 - Font.small.getStringWidth(indexName);
						x3 = this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + maxLength[0];
						x4 = x3 + Font.small.getStringWidth(framePosition);
					}

					if (!this.isSubPageOpen()) {
						button = new GuiHyperlink(BUTTON_KEY_STRUCTURELINK, getHyperlinkData(GuiPageType.STRUCTURE, getStructureDetailPageKey(structure.getStructureId())));
						button.setIcon(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT);
						this.buttons.add(button);

						toolTip = new GuiTooltip(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT, TextUtils.translate("tektopiaBook.links.structuredetails"));
						this.tooltips.add(toolTip);
						
						button = new GuiHyperlink(BUTTON_KEY_MAPLINK, getHyperlinkData(GuiPageType.STRUCTURE, getStructureDetailPageKey(structure.getStructureId())));
						button.setIcon(x3, y, x4 - x3, Font.small.fontRenderer.FONT_HEIGHT);
						this.buttons.add(button);

						toolTip = new GuiTooltip(x3, y, x4 - x3, Font.small.fontRenderer.FONT_HEIGHT, TextUtils.translate("tektopiaBook.links.mapdetails"));
						this.tooltips.add(toolTip);
					}

					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;           			
				}
				index++;
			}
		}
	}

	private void drawPageSummary(int mouseX, int mouseY, float partialTicks, GuiPage guiPage) {
		drawPageHeader(mouseX, mouseY, partialTicks, guiPage);
		drawPageFooter(mouseX, mouseY, partialTicks, guiPage); 

		int y = this.y + PAGE_BODY_Y;
		int indentX = 10;

		String[] dataKey = getPageKeyParts(guiPage.getDataKey());
		String summaryName = "";
		String summaryInformation = "";
		String dataName = "";
		List<String> dataInformation = new ArrayList<String>();

		EconomyData economyData;
		EnemiesData enemiesData;
		HomesData homesData;
		ResidentsData residentsData;
		StructuresData structuresData;
		VisitorsData visitorsData;

		switch (dataKey[0]) {
		case BOOKMARK_KEY_ECONOMY:
			summaryName = TextUtils.translate("bookmark.economy.name");
			summaryInformation = TextUtils.translate("bookmark.economy.information");    
			dataName = TextUtils.translate("tektopiaBook.summary.header");

			economyData = this.villageData.getEconomyData();

			if (economyData != null) {
				String merchantSales = TextUtils.translate("tektopiaBook.economy.merchantsales");

				if (!StringUtils.isNullOrWhitespace(merchantSales)) {
					dataInformation.add(merchantSales + "|" + economyData.getMerchantSales() + "|");
				}

				String professionSales = TextUtils.translate("tektopiaBook.economy.professionsales");

				if (!StringUtils.isNullOrWhitespace(professionSales)) {
					dataInformation.add(professionSales + "|" + economyData.getProfessionSales() + "|");
				}
			}
			break;
		case BOOKMARK_KEY_ENEMIES:
			summaryName = TextUtils.translate("bookmark.enemies.name");
			summaryInformation = TextUtils.translate("bookmark.enemies.information");
			dataName = TextUtils.translate("tektopiaBook.summary.header");

			enemiesData = this.villageData.getEnemiesData();

			if (enemiesData != null) {
				String enemiesTotal = TextUtils.translate("tektopiaBook.enemies.total");

				if (!StringUtils.isNullOrWhitespace(enemiesTotal)) {
					dataInformation.add(enemiesTotal + "|" + enemiesData.getEnemiesCount() + "|");
				}
			}
			break;
		case BOOKMARK_KEY_HOMES:
			summaryName = TextUtils.translate("bookmark.homes.name");
			summaryInformation = TextUtils.translate("bookmark.homes.information");
			dataName = TextUtils.translate("tektopiaBook.summary.header");

			homesData = this.villageData.getHomesData();
			residentsData = this.villageData.getResidentsData();

			if (homesData != null) {
				String homesTotal = TextUtils.translate("tektopiaBook.homes.totalhomes");

				if (!StringUtils.isNullOrWhitespace(homesTotal)) {
					dataInformation.add(homesTotal + "|" + homesData.getHomesCount() + "|");
				}

				String homesTotalBeds = TextUtils.translate("tektopiaBook.homes.totalbeds");

				if (!StringUtils.isNullOrWhitespace(homesTotalBeds)) {
					dataInformation.add(homesTotalBeds + "|" + homesData.getTotalBeds() + "|");
				}
			}

			if (residentsData != null) {
				String homesTotalResidents = TextUtils.translate("tektopiaBook.homes.totalresidents");

				if (!StringUtils.isNullOrWhitespace(homesTotalResidents)) {
					dataInformation.add(homesTotalResidents + "|" + residentsData.getResidentsCount() + "|");
				}
			}
			break;
		case BOOKMARK_KEY_PROFESSIONS:
			summaryName = TextUtils.translate("bookmark.professions.name");
			summaryInformation = TextUtils.translate("bookmark.professions.information");
			dataName = TextUtils.translate("tektopiaBook.summary.header");

			residentsData = this.villageData.getResidentsData();

			if (residentsData != null) {
				String professionsTotal = TextUtils.translate("tektopiaBook.professions.total");

				if (!StringUtils.isNullOrWhitespace(professionsTotal)) {
					dataInformation.add(professionsTotal + "|" + residentsData.getResidentsCountAll() + "|");
				}
			}
			break;
		case BOOKMARK_KEY_RESIDENTS:
			summaryName = TextUtils.translate("bookmark.residents.name");
			summaryInformation = TextUtils.translate("bookmark.residents.information");
			dataName = TextUtils.translate("tektopiaBook.summary.header");

			residentsData = this.villageData.getResidentsData();

			if (residentsData != null) {
				String residentsTotal = TextUtils.translate("tektopiaBook.residents.total");

				if (!StringUtils.isNullOrWhitespace(residentsTotal)) {
					dataInformation.add(residentsTotal + "|" + residentsData.getResidentsCount() + "|");
				}

				String residentsTotalAdults = TextUtils.translate("tektopiaBook.residents.adults");

				if (!StringUtils.isNullOrWhitespace(residentsTotalAdults)) {
					dataInformation.add(residentsTotalAdults + "|" + residentsData.getAdultCount() + "|");
				}

				String residentsTotalChildren = TextUtils.translate("tektopiaBook.residents.children");

				if (!StringUtils.isNullOrWhitespace(residentsTotalChildren)) {
					dataInformation.add(residentsTotalChildren + "|" + residentsData.getChildCount() + "|");
				}

				String residentsTotalMales = TextUtils.translate("tektopiaBook.residents.males");

				if (!StringUtils.isNullOrWhitespace(residentsTotalMales)) {
					dataInformation.add(residentsTotalMales + "|" + residentsData.getMaleCount() + "|");
				}

				String residentsTotalFemales = TextUtils.translate("tektopiaBook.residents.females");

				if (!StringUtils.isNullOrWhitespace(residentsTotalFemales)) {
					dataInformation.add(residentsTotalFemales + "|" + residentsData.getFemaleCount() + "|");
				}

				String residentsNobedsLabel = TextUtils.translate("tektopiaBook.residents.nobed");
				String residentsNobedsText = "";

				if (!StringUtils.isNullOrWhitespace(residentsNobedsLabel)) {
					if (residentsData.getNoBedCount() > 0)
						residentsNobedsText += TextFormatting.DARK_RED;
					residentsNobedsText += residentsData.getNoBedCount();
					dataInformation.add(residentsNobedsLabel + "|" + residentsNobedsText + "|");
				}
			}
			break;
		case BOOKMARK_KEY_STATISTICS:
			summaryName = TextUtils.translate("bookmark.statistics.name");
			summaryInformation = TextUtils.translate("bookmark.statistics.information");
			dataName = TextUtils.translate("tektopiaBook.summary.header");

			residentsData = this.villageData.getResidentsData();
			structuresData = this.villageData.getStructuresData();

			if (residentsData != null) {
				String statisticsTotal = TextUtils.translate("tektopiaBook.residents.total");

				if (!StringUtils.isNullOrWhitespace(statisticsTotal)) {
					dataInformation.add(statisticsTotal + "|" + residentsData.getResidentsCountAll() + "|");
				}
			}

			if (structuresData != null) {
				String structuresTotal = TextUtils.translate("tektopiaBook.structures.total");

				if (!StringUtils.isNullOrWhitespace(structuresTotal)) {
					dataInformation.add(structuresTotal + "|" + structuresData.getStructuresCount() + "|");
				}
			}
			break;
		case BOOKMARK_KEY_STRUCTURES:
			summaryName = TextUtils.translate("bookmark.structures.name");
			summaryInformation = TextUtils.translate("bookmark.structures.information");
			dataName = TextUtils.translate("tektopiaBook.summary.header");

			structuresData = this.villageData.getStructuresData();

			if (structuresData != null) {
				String structuresTotal = TextUtils.translate("tektopiaBook.structures.total");

				if (!StringUtils.isNullOrWhitespace(structuresTotal)) {
					dataInformation.add(structuresTotal + "|" + structuresData.getStructuresCount() + "|");
				}
			}
			break;
		case BOOKMARK_KEY_VILLAGE:
			summaryName = TextUtils.translate("bookmark.village.name");
			summaryInformation = TextUtils.translate("bookmark.village.information");
			break;
		case BOOKMARK_KEY_VISITORS:
			summaryName = TextUtils.translate("bookmark.visitors.name");
			summaryInformation = TextUtils.translate("bookmark.visitors.information");
			dataName = TextUtils.translate("tektopiaBook.summary.header");

			visitorsData = this.villageData.getVisitorsData();

			if (visitorsData != null) {
				String visitorsTotal = TextUtils.translate("tektopiaBook.visitors.total");

				if (!StringUtils.isNullOrWhitespace(visitorsTotal)) {
					dataInformation.add(visitorsTotal + "|" + visitorsData.getVisitorsCount() + "|");
				}
			}
			break;
		}

		y += 50;

		if (!StringUtils.isNullOrWhitespace(summaryName)) {
			summaryName = TextFormatting.DARK_BLUE + summaryName;

			if (guiPage.isLeftPage()) {
				Font.normal.printCentered(summaryName, this.x + PAGE_LEFTPAGE_CENTER_X, y); 
			}

			if (guiPage.isRightPage()) {
				Font.normal.printCentered(summaryName, this.x + PAGE_RIGHTPAGE_CENTER_X, y); 
			}
		}

		y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
		y += Font.small.fontRenderer.FONT_HEIGHT + (LINE_SPACE_Y * 10);

		if (!StringUtils.isNullOrWhitespace(summaryInformation)) {
			if (guiPage.isLeftPage()) {
				List<String> textLines = StringUtils.split(summaryInformation, PAGE_LEFTPAGE_WIDTH, Font.small.fontRenderer);

				for (int lineIndex = 0; lineIndex < textLines.size(); lineIndex++) {
					Font.small.printCentered(textLines.get(lineIndex), this.x + PAGE_LEFTPAGE_CENTER_X, y);
					
					y += Font.small.fontRenderer.FONT_HEIGHT;
				}
			}

			if (guiPage.isRightPage()) {
				List<String> textLines = StringUtils.split(summaryInformation, PAGE_RIGHTPAGE_WIDTH, Font.small.fontRenderer);

				for (int lineIndex = 0; lineIndex < textLines.size(); lineIndex++) {
					Font.small.printCentered(textLines.get(lineIndex), this.x + PAGE_RIGHTPAGE_CENTER_X, y);

					y += Font.small.fontRenderer.FONT_HEIGHT;
				}
			}
		}

		y = this.y + PAGE_BODY_Y + ((PAGE_FOOTER_Y - PAGE_BODY_Y) / 2);

		if (!StringUtils.isNullOrWhitespace(dataName)) {
			dataName = TextFormatting.DARK_BLUE + dataName;

			if (guiPage.isLeftPage()) {
				Font.small.printCentered(dataName, this.x + PAGE_LEFTPAGE_CENTER_X, y); 
			}

			if (guiPage.isRightPage()) {
				Font.small.printCentered(dataName, this.x + PAGE_RIGHTPAGE_CENTER_X, y); 
			}
		}

		y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

		if (dataInformation != null && dataInformation.size() > 0) {
			for (String dataLine : dataInformation) {
				if (!StringUtils.isNullOrWhitespace(dataLine)) {
					String[] parts = dataLine.split("[|]");

					if (guiPage.isLeftPage()) {
						if (parts.length > 0 && !StringUtils.isNullOrWhitespace(parts[0]))
							Font.small.printLeft(parts[0], this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
						if (parts.length > 1 && !StringUtils.isNullOrWhitespace(parts[1]))
							Font.small.printRight(parts[1], this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y);  
						if (parts.length > 2 && !StringUtils.isNullOrWhitespace(parts[2]))
							Font.small.printRight(parts[2], this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y); 
					}

					if (guiPage.isRightPage()) {
						if (parts.length > 0 && !StringUtils.isNullOrWhitespace(parts[0]))
							Font.small.printLeft(parts[0], this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
						if (parts.length > 1 && !StringUtils.isNullOrWhitespace(parts[1]))
							Font.small.printRight(parts[1], this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y); 
						if (parts.length > 2 && !StringUtils.isNullOrWhitespace(parts[2]))
							Font.small.printRight(parts[2], this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y); 
					}

					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
				}
			}
		}
	}

	private void drawPageVillage(int mouseX, int mouseY, float partialTicks, GuiPage guiPage) {
		drawPageHeader(mouseX, mouseY, partialTicks, guiPage);
		drawPageFooter(mouseX, mouseY, partialTicks, guiPage); 

		int y = this.y + PAGE_BODY_Y;
		int indentX = 10;

		String header = TextUtils.translate("tektopiaBook.village.header");

		if (!StringUtils.isNullOrWhitespace(header)) {
			header = TextFormatting.DARK_BLUE + header;

			if (guiPage.isLeftPage()) {
				Font.normal.printLeft(header, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
			}

			if (guiPage.isRightPage()) {
				Font.normal.printLeft(header, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
			}
		} 

		y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

		if (this.villageData != null) {
			String originLabel = TextUtils.translate("tektopiaBook.village.origin");
			String originText = "";

			if (!StringUtils.isNullOrWhitespace(originLabel)) {
				originText += formatBlockPos(this.villageData.getVillageOrigin());

				if (guiPage.isLeftPage()) {
					Font.small.printLeft(originLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
					Font.small.printLeft(originText, this.x + PAGE_LEFTPAGE_CENTER_X, y); 
				}

				if (guiPage.isRightPage()) {
					Font.small.printLeft(originLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
					Font.small.printLeft(originText, this.x + PAGE_RIGHTPAGE_CENTER_X, y); 
				}
			}

			y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y + LINE_SPACE_Y_HEADER;

			String boundaries = TextUtils.translate("tektopiaBook.village.boundaries");

			if (!StringUtils.isNullOrWhitespace(boundaries)) {
				boundaries = TextFormatting.DARK_BLUE + boundaries;

				if (guiPage.isLeftPage()) {
					Font.normal.printLeft(boundaries, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
				}

				if (guiPage.isRightPage()) {
					Font.normal.printLeft(boundaries, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
				}
			}

			y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

			String boundaryLabel = TextUtils.translate("tektopiaBook.village.boundarynorthwest");
			String boundaryText = "";

			if (!StringUtils.isNullOrWhitespace(boundaryLabel)) {
				boundaryText += formatBlockPos(this.villageData.getVillageNorthWestCorner());

				if (guiPage.isLeftPage()) {
					Font.small.printLeft(boundaryLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
					Font.small.printLeft(boundaryText, this.x + PAGE_LEFTPAGE_CENTER_X, y); 
				}

				if (guiPage.isRightPage()) {
					Font.small.printLeft(boundaryLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
					Font.small.printLeft(boundaryText, this.x + PAGE_RIGHTPAGE_CENTER_X, y); 
				}
			}

			y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

			boundaryLabel = TextUtils.translate("tektopiaBook.village.boundarynortheast");
			boundaryText = "";

			if (!StringUtils.isNullOrWhitespace(boundaryLabel)) {
				boundaryText += formatBlockPos(this.villageData.getVillageNorthEastCorner());

				if (guiPage.isLeftPage()) {
					Font.small.printLeft(boundaryLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
					Font.small.printLeft(boundaryText, this.x + PAGE_LEFTPAGE_CENTER_X, y); 
				}

				if (guiPage.isRightPage()) {
					Font.small.printLeft(boundaryLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
					Font.small.printLeft(boundaryText, this.x + PAGE_RIGHTPAGE_CENTER_X, y); 
				}
			}

			y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

			boundaryLabel = TextUtils.translate("tektopiaBook.village.boundarysouthwest");
			boundaryText = "";

			if (!StringUtils.isNullOrWhitespace(boundaryLabel)) {
				boundaryText += formatBlockPos(this.villageData.getVillageSouthWestCorner());

				if (guiPage.isLeftPage()) {
					Font.small.printLeft(boundaryLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
					Font.small.printLeft(boundaryText, this.x + PAGE_LEFTPAGE_CENTER_X, y); 
				}

				if (guiPage.isRightPage()) {
					Font.small.printLeft(boundaryLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
					Font.small.printLeft(boundaryText, this.x + PAGE_RIGHTPAGE_CENTER_X, y); 
				}
			}

			y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

			boundaryLabel = TextUtils.translate("tektopiaBook.village.boundarysoutheast");
			boundaryText = "";

			if (!StringUtils.isNullOrWhitespace(boundaryLabel)) {
				boundaryText += formatBlockPos(this.villageData.getVillageSouthEastCorner());

				if (guiPage.isLeftPage()) {
					Font.small.printLeft(boundaryLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
					Font.small.printLeft(boundaryText, this.x + PAGE_LEFTPAGE_CENTER_X, y); 
				}

				if (guiPage.isRightPage()) {
					Font.small.printLeft(boundaryLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
					Font.small.printLeft(boundaryText, this.x + PAGE_RIGHTPAGE_CENTER_X, y); 
				}
			}

			y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y + LINE_SPACE_Y_HEADER;

			String totals = TextUtils.translate("tektopiaBook.village.totals");

			if (!StringUtils.isNullOrWhitespace(totals)) {
				totals = TextFormatting.DARK_BLUE + totals;

				if (guiPage.isLeftPage()) {
					Font.normal.printLeft(totals, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
				}

				if (guiPage.isRightPage()) {
					Font.normal.printLeft(totals, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
				}
			}

			y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

			String structuresLabel = TextUtils.translate("tektopiaBook.village.structures");
			String structuresText = "";

			if (!StringUtils.isNullOrWhitespace(structuresLabel)) {
				if (this.villageData.getStructuresData() != null)
					structuresText += "" + this.villageData.getStructuresData().getStructuresCount();

				if (guiPage.isLeftPage()) {
					Font.small.printLeft(structuresLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
					Font.small.printRight(structuresText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y); 
				}

				if (guiPage.isRightPage()) {
					Font.small.printLeft(structuresLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
					Font.small.printRight(structuresText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y); 
				}
			}

			y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

			String residentsLabel = TextUtils.translate("tektopiaBook.village.residents");
			String residentsText = "";

			if (!StringUtils.isNullOrWhitespace(residentsLabel)) {
				if (this.villageData.getResidentsData() != null)
					residentsText += "" + this.villageData.getResidentsData().getResidentsCount();

				if (guiPage.isLeftPage()) {
					Font.small.printLeft(residentsLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
					Font.small.printRight(residentsText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y); 
				}

				if (guiPage.isRightPage()) {
					Font.small.printLeft(residentsLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
					Font.small.printRight(residentsText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y);
				}
			}

			y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

			String visitorsLabel = TextUtils.translate("tektopiaBook.village.visitors");
			String visitorsText = "";

			if (!StringUtils.isNullOrWhitespace(visitorsLabel)) {
				if (this.villageData.getResidentsData() != null)
					visitorsText += "" + this.villageData.getVisitorsData().getVisitorsCount();

				if (guiPage.isLeftPage()) {
					Font.small.printLeft(visitorsLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
					Font.small.printRight(visitorsText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y); 
				}

				if (guiPage.isRightPage()) {
					Font.small.printLeft(visitorsLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
					Font.small.printRight(visitorsText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y);
				}
			}

			y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

			String enemiesLabel = TextUtils.translate("tektopiaBook.village.enemies");
			String enemiesText = "";

			if (!StringUtils.isNullOrWhitespace(enemiesLabel)) {
				if (this.villageData.getResidentsData() != null)
					enemiesText += "" + this.villageData.getEnemiesData().getEnemiesCount();

				if (guiPage.isLeftPage()) {
					Font.small.printLeft(enemiesLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
					Font.small.printRight(enemiesText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y); 
				}

				if (guiPage.isRightPage()) {
					Font.small.printLeft(enemiesLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
					Font.small.printRight(enemiesText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y);
				}
			}

			y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y + LINE_SPACE_Y_HEADER;

			String statistics = TextUtils.translate("tektopiaBook.village.statistics");

			if (!StringUtils.isNullOrWhitespace(statistics)) {
				statistics = TextFormatting.DARK_BLUE + statistics;

				if (guiPage.isLeftPage()) {
					Font.normal.printLeft(statistics, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
				}

				if (guiPage.isRightPage()) {
					Font.normal.printLeft(statistics, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
				}
			}

			y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

			String nobedsLabel = TextUtils.translate("tektopiaBook.village.nobed");
			String nobedsText = "";

			if (!StringUtils.isNullOrWhitespace(nobedsLabel)) {
				if (this.villageData.getResidentsData() != null) {
					int nobedsCount = this.villageData.getResidentsData().getNoBedCount();
					if (nobedsCount > 0)
						nobedsText += TextFormatting.DARK_RED;
					nobedsText += "" + nobedsCount;
				}

				if (guiPage.isLeftPage()) {
					Font.small.printLeft(nobedsLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
					Font.small.printRight(nobedsText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y); 
				}

				if (guiPage.isRightPage()) {
					Font.small.printLeft(nobedsLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
					Font.small.printRight(nobedsText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y);
				}
			}

			y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

			String professionSalesLabel = TextUtils.translate("tektopiaBook.village.professionsales");
			String professionSalesText = "";

			if (!StringUtils.isNullOrWhitespace(professionSalesLabel)) {
				if (this.villageData.getEconomyData() != null)
					professionSalesText += "" + this.villageData.getEconomyData().getProfessionSales();

				if (guiPage.isLeftPage()) {
					Font.small.printLeft(professionSalesLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
					Font.small.printRight(professionSalesText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y); 
				}

				if (guiPage.isRightPage()) {
					Font.small.printLeft(professionSalesLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
					Font.small.printRight(professionSalesText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y);
				}
			}

			y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

			String merchantSalesLabel = TextUtils.translate("tektopiaBook.village.merchantsales");
			String merchantSalesText = "";

			if (!StringUtils.isNullOrWhitespace(merchantSalesLabel)) {
				if (this.villageData.getEconomyData() != null)
					merchantSalesText += "" + this.villageData.getEconomyData().getMerchantSales();

				if (guiPage.isLeftPage()) {
					Font.small.printLeft(merchantSalesLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
					Font.small.printRight(merchantSalesText, this.x + PAGE_LEFTPAGE_CENTER_X + indentX, y); 
				}

				if (guiPage.isRightPage()) {
					Font.small.printLeft(merchantSalesLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
					Font.small.printRight(merchantSalesText, this.x + PAGE_RIGHTPAGE_CENTER_X + indentX, y); 
				}
			}

			y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
		}
	}

	private void drawPageVisitor(int mouseX, int mouseY, float partialTicks, GuiPage guiPage) {
		drawPageHeader(mouseX, mouseY, partialTicks, guiPage);
		drawPageFooter(mouseX, mouseY, partialTicks, guiPage); 

		int y = this.y + PAGE_BODY_Y;
		int indentX = 10;

		GuiHyperlink button = null;
		GuiTooltip toolTip = null;
		int x1 = 0;
		int x2 = 0;
		int x3 = 0;
		int x4 = 0;

		String[] dataKey = getPageKeyParts(guiPage.getDataKey());
		VisitorsData visitorsData = this.villageData.getVisitorsData();
		StructuresData structuresData = this.villageData.getStructuresData();

		if (dataKey[0].equals("")) {
			// visitor list

			String pageHeader = TextUtils.translate("tektopiaBook.visitors.visitors");

			if (!StringUtils.isNullOrWhitespace(pageHeader)) {
				pageHeader = TextFormatting.DARK_BLUE + pageHeader;

				if (guiPage.isLeftPage()) {
					Font.normal.printLeft(pageHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
				}

				if (guiPage.isRightPage()) {
					Font.normal.printLeft(pageHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
				}
			} 

			y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

			List<VisitorData> visitors = visitorsData.getVisitors();

			if (visitors != null) {
				final int[] maxLength = { 0 };
				maxLength[0] += LABEL_TRAILINGSPACE_X;

				int page = 0;
				try {
					page = Integer.parseInt(dataKey[1]);
				}
				catch (NumberFormatException e) {
					page = 0;
				}
				int startIndex = page * VISITORS_PER_PAGE;
				int index = 0;

				String visitorHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.visitor");
				String typeHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.type");
				String positionHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.position");

				if (guiPage.isLeftPage()) {
					Font.small.printLeft(visitorHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
					Font.small.printLeft(typeHeader, this.x + PAGE_LEFTPAGE_CENTER_X, y);
					Font.small.printRight(positionHeader, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y);
				}

				if (guiPage.isRightPage()) {
					Font.small.printLeft(visitorHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
					Font.small.printLeft(typeHeader, this.x + PAGE_RIGHTPAGE_CENTER_X, y);
					Font.small.printRight(positionHeader, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y);
				}

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y; 

				for (VisitorData visitor : visitors) {
					if (index >= startIndex && index < startIndex + VISITORS_PER_PAGE) {
						String visitorName = "";
						String visitorType = "";
						String visitorPosition = "";

						visitorName += formatResidentName(visitor.isMale(), visitor.getName(), true);
						visitorType = getTypeName(visitor.getProfessionType());
						visitorPosition += formatBlockPos(visitor.getCurrentPosition());

						if (guiPage.isLeftPage()) {
							Font.small.printLeft(visitorName, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
							Font.small.printLeft(visitorType, this.x + PAGE_LEFTPAGE_CENTER_X, y);
							Font.small.printRight(visitorPosition, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y);

							x1 = this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX;
							x2 = x1 + Font.small.getStringWidth(visitorName);
							x4 = this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X;
							x3 = x4 - Font.small.getStringWidth(visitorPosition);
						}

						if (guiPage.isRightPage()) {
							Font.small.printLeft(visitorName, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
							Font.small.printLeft(visitorType, this.x + PAGE_RIGHTPAGE_CENTER_X, y);
							Font.small.printRight(visitorPosition, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y);

							x1 = this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX;
							x2 = x1 + Font.small.getStringWidth(visitorName);
							x4 = this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X;
							x3 = x4 - Font.small.getStringWidth(visitorPosition);
						}

						if (!this.isSubPageOpen()) {
							button = new GuiHyperlink(BUTTON_KEY_VISITORLINK, getHyperlinkData(GuiPageType.VISITOR, getResidentDetailPageKey(visitor.getId())));
							button.setIcon(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT);
							this.buttons.add(button);

							toolTip = new GuiTooltip(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT, TextUtils.translate("tektopiaBook.links.visitordetails"));
							this.tooltips.add(toolTip);
							
							button = new GuiHyperlink(BUTTON_KEY_MAPLINK, getHyperlinkData(GuiPageType.VISITOR, getResidentDetailPageKey(visitor.getId())));
							button.setIcon(x3, y, x4 - x3, Font.small.fontRenderer.FONT_HEIGHT);
							this.buttons.add(button);

							toolTip = new GuiTooltip(x3, y, x4 - x3, Font.small.fontRenderer.FONT_HEIGHT, TextUtils.translate("tektopiaBook.links.mapdetails"));
							this.tooltips.add(toolTip);
						}

						y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
					}  
					index++;
				}
			}
		} else {
			// visitor

			int visitorId = 0;
			try {
				visitorId = Integer.parseInt(dataKey[0]);
			}
			catch (NumberFormatException e) {
				visitorId = 0;
			}

			VisitorData visitor = visitorId > 0 ? visitorsData.getVisitorById(visitorId) : null;

			if (visitor != null) {

				String header = stripTextFormatting(visitor.getName());

				if (!StringUtils.isNullOrWhitespace(header)) {
					// check if this is the villager we clicked on
					if (this.villageData.getEntityId() > 0 && this.villageData.getEntityId() == visitorId) {
						header = TextFormatting.UNDERLINE + header;
					}

					if (visitor.isMale())
						header = TextFormatting.BLUE + header + " " + TextUtils.SYMBOL_MALE;
					else
						header = TextFormatting.LIGHT_PURPLE + header + " " + TextUtils.SYMBOL_FEMALE;

					if (guiPage.isLeftPage()) {
						Font.normal.printLeft(header, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
					}

					if (guiPage.isRightPage()) {
						Font.normal.printLeft(header, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
					}
				} 

				y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

				// check if displaying the first page
				if (dataKey[1].equals("0")) {
					{
						int tXL = this.x + (PAGE_LEFTPAGE_RIGHTMARGIN_X - PROFESSION_WIDTH);
						int tXR = this.x + (PAGE_RIGHTPAGE_RIGHTMARGIN_X - PROFESSION_WIDTH);
						int tY = y;

						String modId = visitor.getModId();
						if (modId.equals(ModDetails.MOD_ID_TEKTOPIA))
							modId = ModDetails.MOD_ID;
						String className = visitor.getClassName();
						String gender = visitor.isMale() ? "m" : "f";

						ResourceLocation visitorResource = new ResourceLocation(modId, "textures/visitors/" + className + "_" + gender + ".png");

						if (visitorResource != null) {
							GlStateManager.pushMatrix();
							GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

							mc.getTextureManager().bindTexture(visitorResource);

							if (guiPage.isLeftPage()) {
								super.drawModalRectWithCustomSizedTexture(tXL, tY, 0, 0, PROFESSION_WIDTH, PROFESSION_HEIGHT, 56, 90);
							}

							if (guiPage.isRightPage()) {
								super.drawModalRectWithCustomSizedTexture(tXR, tY, 0, 0, PROFESSION_WIDTH, PROFESSION_HEIGHT, 56, 90);
							}	      

							GlStateManager.popMatrix();          	
						}
					}

					// display visitor details
					String typeLabel = TextUtils.translate("tektopiaBook.visitors.visitortype");
					String typeText = "";

					if (!StringUtils.isNullOrWhitespace(typeLabel)) {
						typeText += getTypeName(visitor.getProfessionType());

						if (guiPage.isLeftPage()) {
							Font.small.printLeft(typeLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
							Font.small.printLeft(typeText, this.x + PAGE_LEFTPAGE_LEFTCENTER_X + indentX, y);  
						}

						if (guiPage.isRightPage()) {
							Font.small.printLeft(typeLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
							Font.small.printLeft(typeText, this.x + PAGE_RIGHTPAGE_LEFTCENTER_X + indentX, y); 
						}
					}

					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

					// display visitor statistics
					String healthLabel = TextUtils.translate("tektopiaBook.residents.health");
					String healthText = "";

					if (!StringUtils.isNullOrWhitespace(healthLabel)) {
						healthText += formatResidentStatistic(visitor.getHealth(), visitor.getMaxHealth(), true);

						if (guiPage.isLeftPage()) {
							Font.small.printLeft(healthLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
							Font.small.printLeft(healthText, this.x + PAGE_LEFTPAGE_LEFTCENTER_X + indentX, y);  
						}

						if (guiPage.isRightPage()) {
							Font.small.printLeft(healthLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
							Font.small.printLeft(healthText, this.x + PAGE_RIGHTPAGE_LEFTCENTER_X + indentX, y); 
						}
					}

					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

					String hungerLabel = TextUtils.translate("tektopiaBook.residents.hunger");
					String hungerText = "";

					if (!StringUtils.isNullOrWhitespace(hungerLabel)) {
						hungerText += formatResidentStatistic(visitor.getHunger(), visitor.getMaxHunger(), true);

						if (guiPage.isLeftPage()) {
							Font.small.printLeft(hungerLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
							Font.small.printLeft(hungerText, this.x + PAGE_LEFTPAGE_LEFTCENTER_X + indentX, y); 
						}

						if (guiPage.isRightPage()) {
							Font.small.printLeft(hungerLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
							Font.small.printLeft(hungerText, this.x + PAGE_RIGHTPAGE_LEFTCENTER_X + indentX, y); 
						}
					}

					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

					String happinessLabel = TextUtils.translate("tektopiaBook.residents.happiness");
					String happinessText = "";

					if (!StringUtils.isNullOrWhitespace(happinessLabel)) {
						happinessText += formatResidentStatistic(visitor.getHappy(), visitor.getMaxHappy(), true);

						if (guiPage.isLeftPage()) {
							Font.small.printLeft(happinessLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
							Font.small.printLeft(happinessText, this.x + PAGE_LEFTPAGE_LEFTCENTER_X + indentX, y);
						}

						if (guiPage.isRightPage()) {
							Font.small.printLeft(happinessLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
							Font.small.printLeft(happinessText, this.x + PAGE_RIGHTPAGE_LEFTCENTER_X + indentX, y); 
						}
					}

					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

					String intelligenceLabel = TextUtils.translate("tektopiaBook.residents.intelligence");
					String intelligenceText = "";

					if (!StringUtils.isNullOrWhitespace(intelligenceLabel)) {
						intelligenceText += formatResidentStatistic(visitor.getIntelligence(), visitor.getMaxIntelligence(), true);

						if (guiPage.isLeftPage()) {
							Font.small.printLeft(intelligenceLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
							Font.small.printLeft(intelligenceText, this.x + PAGE_LEFTPAGE_LEFTCENTER_X + indentX, y); 
						}

						if (guiPage.isRightPage()) {
							Font.small.printLeft(intelligenceLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
							Font.small.printLeft(intelligenceText, this.x + PAGE_RIGHTPAGE_LEFTCENTER_X + indentX, y); 
						}
					}

					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

					String taskLabel = TextUtils.translate("tektopiaBook.residents.task");
					String taskText = "";

					if (!StringUtils.isNullOrWhitespace(taskLabel)) {
						taskText += getAiTaskName(visitor.getCurrentTask());

						if (guiPage.isLeftPage()) {
							Font.small.printLeft(taskLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
							Font.small.printLeft(taskText, this.x + PAGE_LEFTPAGE_LEFTCENTER_X + indentX, y); 
						}

						if (guiPage.isRightPage()) {
							Font.small.printLeft(taskLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
							Font.small.printLeft(taskText, this.x + PAGE_RIGHTPAGE_LEFTCENTER_X + indentX, y);
						}
					}

					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y + LINE_SPACE_Y_HEADER;
					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

					String currentPositionLabel = TextUtils.translate("tektopiaBook.residents.position");
					String currentPositionText = "";

					if (!StringUtils.isNullOrWhitespace(currentPositionLabel)) {
						currentPositionText += " " + formatBlockPos(visitor.getCurrentPosition());

						if (guiPage.isLeftPage()) {
							Font.small.printLeft(currentPositionLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
							Font.small.printLeft(currentPositionText, this.x + PAGE_LEFTPAGE_CENTER_X - indentX, y);
							
							x1 = this.x + PAGE_LEFTPAGE_CENTER_X - indentX;
							x2 = x1 + Font.small.getStringWidth(currentPositionText);
						}

						if (guiPage.isRightPage()) {
							Font.small.printLeft(currentPositionLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
							Font.small.printLeft(currentPositionText, this.x + PAGE_RIGHTPAGE_CENTER_X - indentX, y); 
							
							x1 = this.x + PAGE_RIGHTPAGE_CENTER_X - indentX;
							x2 = x1 + Font.small.getStringWidth(currentPositionText);
						}
						
						if (!isSubPageOpen()) {
							button = new GuiHyperlink(BUTTON_KEY_MAPLINK, getHyperlinkData(GuiPageType.VISITOR, getResidentDetailPageKey(visitor.getId())));
							button.setIcon(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT);
							this.buttons.add(button);

							toolTip = new GuiTooltip(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT, TextUtils.translate("tektopiaBook.links.mapdetails"));
							this.tooltips.add(toolTip);
						}
					}

					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

					String currentStructureLabel = TextUtils.translate("tektopiaBook.residents.structure");
					String currentStructureText = "";
					String currentStructurePosition = "";

					if (!StringUtils.isNullOrWhitespace(currentStructureLabel)) {
						int structureId = 0;
						int textLength = 0;

						if (structuresData != null && visitor.getCurrentStructure() != null) {
							StructureData structureData = structuresData.getStructureByFramePosition(visitor.getCurrentStructure());
							if (structureData != null) {
								currentStructureText += " " + structureData.getStructureTypeName();
								currentStructurePosition += " (" + formatBlockPos(visitor.getCurrentStructure()) + ")";

								structureId = structureData.getStructureId();
							}
						}
						
						textLength = Font.small.getStringWidth(currentStructureText);

						if (guiPage.isLeftPage()) {
							Font.small.printLeft(currentStructureLabel, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
							Font.small.printLeft(currentStructureText, this.x + PAGE_LEFTPAGE_CENTER_X - indentX, y);
							Font.small.printLeft(currentStructurePosition, this.x + PAGE_LEFTPAGE_CENTER_X - indentX + textLength, y);

							x1 = this.x + PAGE_LEFTPAGE_CENTER_X - indentX;
							x2 = x1 + Font.small.getStringWidth(currentStructureText);
							x3 = this.x + PAGE_LEFTPAGE_CENTER_X - indentX + textLength;
							x4 = x3 + Font.small.getStringWidth(currentStructurePosition);
						}

						if (guiPage.isRightPage()) {
							Font.small.printLeft(currentStructureLabel, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
							Font.small.printLeft(currentStructureText, this.x + PAGE_RIGHTPAGE_CENTER_X - indentX, y); 
							Font.small.printLeft(currentStructurePosition, this.x + PAGE_RIGHTPAGE_CENTER_X - indentX + textLength, y); 

							x1 = this.x + PAGE_RIGHTPAGE_CENTER_X - indentX;
							x2 = x1 + Font.small.getStringWidth(currentStructureText);
							x3 = this.x + PAGE_RIGHTPAGE_CENTER_X - indentX + textLength;
							x4 = x3 + Font.small.getStringWidth(currentStructurePosition);
						}

						if (!this.isSubPageOpen() && structureId > 0) {
							button = new GuiHyperlink(BUTTON_KEY_STRUCTURELINK, getHyperlinkData(GuiPageType.STRUCTURE, getStructureDetailPageKey(structureId)));
							button.setIcon(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT);
							this.buttons.add(button);

							toolTip = new GuiTooltip(x1, y, x2 - x1, Font.small.fontRenderer.FONT_HEIGHT, TextUtils.translate("tektopiaBook.links.structuredetails"));
							this.tooltips.add(toolTip);
							
							button = new GuiHyperlink(BUTTON_KEY_MAPLINK, getHyperlinkData(GuiPageType.STRUCTURE, getStructureDetailPageKey(structureId)));
							button.setIcon(x3, y, x4 - x3, Font.small.fontRenderer.FONT_HEIGHT);
							this.buttons.add(button);

							toolTip = new GuiTooltip(x2, y, x4 - x3, Font.small.fontRenderer.FONT_HEIGHT, TextUtils.translate("tektopiaBook.links.mapdetails"));
							this.tooltips.add(toolTip);
						}
					}

					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y + LINE_SPACE_Y_HEADER;
				}

				if (visitor.isVendor()) {
					// display vendor items

					String vendoritems = TextUtils.translate("tektopiaBook.visitors.vendoritems");
					String continued = TextUtils.translate("tektopiaBook.continued");
					if (!dataKey[1].equals("0") && !StringUtils.isNullOrWhitespace(continued)) {
						vendoritems += " " + continued;
					}

					if (!StringUtils.isNullOrWhitespace(vendoritems)) {
						vendoritems = TextFormatting.DARK_BLUE + vendoritems;

						if (guiPage.isLeftPage()) {
							Font.small.printLeft(vendoritems, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
						}

						if (guiPage.isRightPage()) {
							Font.small.printLeft(vendoritems, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
						}
					}

					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

					String sellHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.sell");
					String buyHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.buy");
					String timesHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.times");

					if (guiPage.isLeftPage()) {
						Font.small.printLeft(sellHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
						Font.small.printLeft(buyHeader, this.x + PAGE_LEFTPAGE_CENTER_X, y); 
						Font.small.printRight(timesHeader, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y);
					}

					if (guiPage.isRightPage()) {
						Font.small.printLeft(sellHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
						Font.small.printLeft(buyHeader, this.x + PAGE_RIGHTPAGE_CENTER_X, y); 
						Font.small.printRight(timesHeader, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y);
					}

					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y_IMAGE;

					MerchantRecipeList recipeList = visitor.getRecipeList();

					if (recipeList != null && recipeList.size() > 0) {
						int page = 0;
						try {
							page = Integer.parseInt(dataKey[1]);
						}
						catch (NumberFormatException e) {
							page = 0;
						}
						int startIndex = 0;
						int endIndex = Math.min(recipeList.size(), startIndex + VISITORVENDORLIST0_PER_PAGE);

						if (page > 0) {
							startIndex = VISITORVENDORLIST0_PER_PAGE + ((page - 1) * VISITORVENDORLIST_PER_PAGE);
							endIndex = Math.min(recipeList.size(), startIndex + VISITORVENDORLIST_PER_PAGE);
						}

						List<MerchantRecipe> recipes = recipeList.subList(startIndex, endIndex);

						for (MerchantRecipe recipe : recipes) {
							ItemStack buyItem1Stack = recipe.getItemToBuy();
							ItemStack buyItem2Stack = recipe.getSecondItemToBuy();
							ItemStack sellItemStack = recipe.getItemToSell();

							String buyTimes = recipe.getToolUses() + TextUtils.SEPARATOR_FSLASH + recipe.getMaxTradeUses();
							if (recipe.getToolUses() >= recipe.getMaxTradeUses())
								buyTimes = TextFormatting.RED + buyTimes;

							List<String> buyItem1Tooltip = buyItem1Stack.getTooltip(null, TooltipFlags.NORMAL);
							if (buyItem1Stack.isItemEnchanted() && buyItem1Tooltip != null && buyItem1Tooltip.size() > 0) {
								buyItem1Tooltip.set(0, TextFormatting.AQUA + buyItem1Tooltip.get(0));
							}
							List<String> buyItem2Tooltip = buyItem2Stack.getTooltip(null, TooltipFlags.NORMAL);
							if (buyItem2Stack.isItemEnchanted() && buyItem2Tooltip != null && buyItem2Tooltip.size() > 0) {
								buyItem2Tooltip.set(0, TextFormatting.AQUA + buyItem2Tooltip.get(0));
							}
							List<String> sellTooltip = sellItemStack.getTooltip(null, TooltipFlags.NORMAL);
							if (sellItemStack.isItemEnchanted() && sellTooltip != null && sellTooltip.size() > 0) {
								sellTooltip.set(0, TextFormatting.AQUA + sellTooltip.get(0));
							}

							if (guiPage.isLeftPage()) {
								Font.normal.printRight(buyTimes, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y);

								if (!buyItem1Stack.isEmpty()) {
									RenderUtils.renderItemIntoGUI(super.itemRender, buyItem1Stack, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y - 5);
									RenderUtils.renderItemOverlayIntoGUI(super.itemRender, Font.normal.fontRenderer, buyItem1Stack, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y - 5, null);
									if (buyItem1Tooltip != null && buyItem1Tooltip.size() > 0) {
										this.tooltips.add(new GuiTooltip(this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y - 5, 16, 16, buyItem1Tooltip));
									}
								}

								if (!buyItem2Stack.isEmpty()) {
									RenderUtils.renderItemIntoGUI(super.itemRender, buyItem2Stack, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + 20, y - 5);
									RenderUtils.renderItemOverlayIntoGUI(super.itemRender, Font.normal.fontRenderer, buyItem2Stack, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + 20, y - 5, null);
									if (buyItem2Tooltip != null && buyItem2Tooltip.size() > 0) {
										this.tooltips.add(new GuiTooltip(this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX + 20, y - 5, 16, 16, buyItem2Tooltip));
									}
								}

								if (!sellItemStack.isEmpty()) {
									RenderUtils.renderItemIntoGUI(super.itemRender, sellItemStack, this.x + PAGE_LEFTPAGE_CENTER_X, y - 5);
									RenderUtils.renderItemOverlayIntoGUI(super.itemRender, Font.normal.fontRenderer, sellItemStack, this.x + PAGE_LEFTPAGE_CENTER_X, y - 5, null);
									if (sellTooltip != null && sellTooltip.size() > 0) {
										this.tooltips.add(new GuiTooltip(this.x + PAGE_LEFTPAGE_CENTER_X, y - 5, 16, 16, sellTooltip));
									}
								}
							}

							if (guiPage.isRightPage()) {
								Font.normal.printRight(buyTimes, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y);

								if (!buyItem1Stack.isEmpty()) {
									RenderUtils.renderItemIntoGUI(super.itemRender, buyItem1Stack, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y - 5);
									RenderUtils.renderItemOverlayIntoGUI(super.itemRender, Font.normal.fontRenderer, buyItem1Stack, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y - 5, null);
									if (buyItem1Tooltip != null && buyItem1Tooltip.size() > 0) {
										this.tooltips.add(new GuiTooltip(this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y - 5, 16, 16, buyItem1Tooltip));
									}
								}

								if (!buyItem2Stack.isEmpty()) {
									RenderUtils.renderItemIntoGUI(super.itemRender, buyItem2Stack, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + 20, y - 5);
									RenderUtils.renderItemOverlayIntoGUI(super.itemRender, Font.normal.fontRenderer, buyItem2Stack, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + 20, y - 5, null);
									if (buyItem2Tooltip != null && buyItem2Tooltip.size() > 0) {
										this.tooltips.add(new GuiTooltip(this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX + 20, y - 5, 16, 16, buyItem2Tooltip));
									}
								}

								if (!sellItemStack.isEmpty()) {
									RenderUtils.renderItemIntoGUI(super.itemRender, sellItemStack, this.x + PAGE_RIGHTPAGE_CENTER_X, y - 5);
									RenderUtils.renderItemOverlayIntoGUI(super.itemRender, Font.normal.fontRenderer, sellItemStack, this.x + PAGE_RIGHTPAGE_CENTER_X, y - 5, null);
									if (sellTooltip != null && sellTooltip.size() > 0) {
										this.tooltips.add(new GuiTooltip(this.x + PAGE_RIGHTPAGE_CENTER_X, y - 5, 16, 16, sellTooltip));
									}
								}
							}

							y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y + LINE_SPACE_Y_IMAGE;
						}
					}

				} else {

					if (dataKey[1].equals("0")) {
						// display professions (top 4)

						String additionalProfessions = TextUtils.translate("tektopiaBook.visitors.professions");

						if (!StringUtils.isNullOrWhitespace(additionalProfessions)) {
							additionalProfessions = TextFormatting.DARK_BLUE + additionalProfessions;

							if (guiPage.isLeftPage()) {
								Font.small.printLeft(additionalProfessions, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X, y); 
							}

							if (guiPage.isRightPage()) {
								Font.small.printLeft(additionalProfessions, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X, y); 
							}
						}

						y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

						String nameHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.name");
						String levelHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.level");

						if (guiPage.isLeftPage()) {
							Font.small.printLeft(nameHeader, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y); 
							Font.small.printRight(levelHeader, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y);
						}

						if (guiPage.isRightPage()) {
							Font.small.printLeft(nameHeader, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y); 
							Font.small.printRight(levelHeader, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y);
						}

						y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

						int index = 1;
						for (Entry<String, Integer> additionalProfessionType : visitor.getAdditionalProfessions().entrySet()) {
							if (index > ADDITIONALPROFESSIONS_PER_PAGE) {
								// only display the top additional professions
								break;
							}

							String additionalProfessionText = getTypeName(additionalProfessionType.getKey());
							String additionalProfessionLevel = formatResidentLevel(additionalProfessionType.getValue(), additionalProfessionType.getValue(), false, false);

							if (guiPage.isLeftPage()) {
								Font.small.printLeft(additionalProfessionText, this.x + PAGE_LEFTPAGE_LEFTMARGIN_X + indentX, y);
								Font.small.printRight(additionalProfessionLevel, this.x + PAGE_LEFTPAGE_RIGHTMARGIN_X, y);
							}

							if (guiPage.isRightPage()) {
								Font.small.printLeft(additionalProfessionText, this.x + PAGE_RIGHTPAGE_LEFTMARGIN_X + indentX, y);
								Font.small.printRight(additionalProfessionLevel, this.x + PAGE_RIGHTPAGE_RIGHTMARGIN_X, y);
							}

							y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y; 
							index++;
						}
					}
				}
			}
		}
	}

	private void drawPageTitle(int mouseX, int mouseY, float partialTicks, GuiPage guiPage) {
		int y = this.y + PAGE_BODY_Y;

		String bookName = TextUtils.translate("tektopiaBook.name");
		String bookInformation = TextUtils.translate("tektopiaBook.information");

		y += 50;

		if (!StringUtils.isNullOrWhitespace(bookName)) {
			bookName = TextFormatting.DARK_RED + bookName;

			if (guiPage.isLeftPage()) {
				Font.normal.printCentered(bookName, this.x + PAGE_LEFTPAGE_CENTER_X, y); 
			}

			if (guiPage.isRightPage()) {
				Font.normal.printCentered(bookName, this.x + PAGE_RIGHTPAGE_CENTER_X, y); 
			}
		}

		y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

		if (this.villageData != null) {
			String villageName = this.villageData.getVillageName();
			String villageText = TextUtils.translate("tektopiaBook.village.name");

			if (!StringUtils.isNullOrWhitespace(villageName)) {
				if (!StringUtils.isNullOrWhitespace(villageText)) {
					villageText += " ";
				}
				villageText = TextFormatting.GOLD + "-----< " + villageText + villageName + " >-----";

				if (guiPage.isLeftPage()) {
					Font.small.printCentered(villageText, this.x + PAGE_LEFTPAGE_CENTER_X, y);
				}

				if (guiPage.isRightPage()) {
					Font.small.printCentered(villageText, this.x + PAGE_RIGHTPAGE_CENTER_X, y);
				}
			}
		}

		y += Font.small.fontRenderer.FONT_HEIGHT + (LINE_SPACE_Y * 10);
		
		if (!StringUtils.isNullOrWhitespace(bookInformation)) {

			if (guiPage.isLeftPage()) {
				List<String> textLines = StringUtils.split(bookInformation, PAGE_LEFTPAGE_WIDTH, Font.small.fontRenderer);

				for (int lineIndex = 0; lineIndex < textLines.size(); lineIndex++) {
					Font.small.printCentered(textLines.get(lineIndex), this.x + PAGE_LEFTPAGE_CENTER_X, y);

					y += Font.small.fontRenderer.FONT_HEIGHT;
				}
			}

			if (guiPage.isRightPage()) {
				List<String> textLines = StringUtils.split(bookInformation, PAGE_RIGHTPAGE_WIDTH, Font.small.fontRenderer);

				for (int lineIndex = 0; lineIndex < textLines.size(); lineIndex++) {
					Font.small.printCentered(textLines.get(lineIndex), this.x + PAGE_RIGHTPAGE_CENTER_X, y);

					y += Font.small.fontRenderer.FONT_HEIGHT;
				}
			}

			y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
		}
	}

	private void drawSubPages(int mouseX, int mouseY, float partialTicks) {
		if (!this.isSubPageOpen())
			return;

		String[] subKeyParts = getSubPageKeyParts(this.subPageKey);

		switch (subKeyParts[0]) {
		case SUBPAGE_KEY_AIFILTER:
			drawSubPageAiFilters(mouseX, mouseY, partialTicks);
			break;
		}
	}

	private void drawSubPageLandscapeBackground(int mouseX, int mouseY, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		// draw map page background
		mc.getTextureManager().bindTexture(page_landscape);
		super.drawModalRectWithCustomSizedTexture(this.xPageLandscape, this.yPageLandscape, 0, 0, PAGE_LANDSCAPE_WIDTH, PAGE_LANDSCAPE_HEIGHT, PAGE_LANDSCAPE_WIDTH, PAGE_LANDSCAPE_HEIGHT);

		GlStateManager.popMatrix();
	}

	private void drawSubPagePortraitBackground(int mouseX, int mouseY, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		// draw sub page background
		mc.getTextureManager().bindTexture(page_portrait);
		super.drawModalRectWithCustomSizedTexture(this.xPagePortrait, this.yPagePortrait, 0, 0, PAGE_PORTRAIT_WIDTH, PAGE_PORTRAIT_HEIGHT, PAGE_PORTRAIT_WIDTH, PAGE_PORTRAIT_HEIGHT);

		GlStateManager.popMatrix();
	}
	
	private void drawSubPagePortraitHeader(int mouseX, int mouseY, float partialTicks) {
		// close button
		GuiButton closeButton = new GuiButton(BUTTON_KEY_CLOSE);
		closeButton.setIcon(buttonClose, this.xPagePortrait + SUBPAGE_RIGHT_X - 13, this.yPagePortrait + SUBPAGE_TOP_Y - 4, 24, 24, 0, 0, 24, 24);
		this.buttons.add(closeButton);

		GuiTooltip closeToolTip = new GuiTooltip(this.xPagePortrait + SUBPAGE_RIGHT_X - 13, this.yPagePortrait + SUBPAGE_TOP_Y - 4, 24, 24, TextUtils.translate("button.close.name"));
		this.tooltips.add(closeToolTip);
	}
	
	private void drawSubPagePortraitFooter(int mouseX, int mouseY, float partialTicks) {
		Font.small.printCentered(this.subPageIndex, this.xPagePortrait + SUBPAGE_CENTER_X, this.yPagePortrait + SUBPAGE_BOTTOM_Y - 2); 
	}
	
	private void drawSubPagePortraitPageArrows(int mouseX, int mouseY, float partialTicks) {
		
		if (this.subPageCount > 1 && this.subPageIndex > 1) {
			// previous button

			GuiButton button = new GuiButton(BUTTON_KEY_PREVIOUSSUBPAGE);
			button.setIcon(buttonPreviousPage, this.xPagePortrait + SUBPAGE_LEFT_X, this.yPagePortrait + SUBPAGE_BOTTOM_Y - 6, 16, 16, 0, 0, 16, 16);
			this.buttons.add(button);

			String tooltipText = button.getDisplayName();
			if (!StringUtils.isNullOrWhitespace(tooltipText)) {
				this.tooltips.add(new GuiTooltip(this.xPagePortrait + SUBPAGE_LEFT_X, this.yPagePortrait + SUBPAGE_BOTTOM_Y - 6, 16, 16, tooltipText));
			}
		}

		if (this.subPageCount > 1 && this.subPageIndex < this.subPageCount) {
			// next button

			GuiButton button = new GuiButton(BUTTON_KEY_NEXTSUBPAGE);
			button.setIcon(buttonNextPage, this.xPagePortrait + SUBPAGE_RIGHT_X - 16, this.yPagePortrait + SUBPAGE_BOTTOM_Y - 6, 16, 16, 0, 0, 16, 16);
			this.buttons.add(button);

			String tooltipText = button.getDisplayName();
			if (!StringUtils.isNullOrWhitespace(tooltipText)) {
				this.tooltips.add(new GuiTooltip(this.xPagePortrait + SUBPAGE_RIGHT_X - 16, this.yPagePortrait + SUBPAGE_BOTTOM_Y - 6, 16, 16, tooltipText));
			}
		}
	}

	private void drawSubPageAiFilters(int mouseX, int mouseY, float partialTicks) {
		drawSubPagePortraitBackground(mouseX, mouseY, partialTicks);
		drawSubPagePortraitHeader(mouseX, mouseY, partialTicks);
		drawSubPagePortraitFooter(mouseX, mouseY, partialTicks);

		String[] subPageKeyParts = getSubPageKeyParts(this.subPageKey);

		int residentId = 0;
		try {
			residentId = Integer.parseInt(subPageKeyParts[1]);
		}
		catch (NumberFormatException e) {
			return;
		}

		ResidentsData residentsData = this.villageData.getResidentsData();
		ResidentData resident = residentsData != null ? residentsData.getResidentById(residentId) : null;

		if (resident == null) 
			return;

		int count = resident.getAiFiltersCount();
		int pages = count / AIFILTERLIST_PER_PAGE;
		if (count % AIFILTERLIST_PER_PAGE > 0) {
			pages++;
		}
		setSubPageCount(pages);
		
		drawSubPagePortraitPageArrows(mouseX, mouseY, partialTicks);

		int y = this.yPagePortrait + SUBPAGE_TOP_Y + 10;
		int indentX = 10;

		String residentName = stripTextFormatting(resident.getName());

		if (!StringUtils.isNullOrWhitespace(residentName)) {
			if (resident.isMale())
				residentName = TextFormatting.BLUE + residentName + " " + TextUtils.SYMBOL_MALE;
			else
				residentName = TextFormatting.LIGHT_PURPLE + residentName + " " + TextUtils.SYMBOL_FEMALE;

			Font.normal.printLeft(residentName, this.xPagePortrait + SUBPAGE_LEFT_X, y);
		}

		y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

		String professionLabel = TextUtils.translate("tektopiaBook.residents.profession");
		String professionText = "";

		if (!StringUtils.isNullOrWhitespace(professionLabel)) {
			professionText += getTypeName(resident.getProfessionType());

			Font.small.printLeft(professionLabel, this.xPagePortrait + SUBPAGE_LEFT_X + indentX, y); 
			Font.small.printRight(professionText, this.xPagePortrait + SUBPAGE_CENTER_X + indentX, y); 
		}

		y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y + LINE_SPACE_Y_HEADER;
		//y += LINE_SPACE_Y;

		String header = TextUtils.translate("tektopiaBook.aifilters.header");

		if (!StringUtils.isNullOrWhitespace(header)) {
			header = TextFormatting.DARK_BLUE + header;

			Font.small.printLeft(header, this.xPagePortrait + SUBPAGE_LEFT_X, y);
		} 

		y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

		final int[] maxLength = { 0 };

		int page = this.subPageIndex - 1;
		int startIndex = page * AIFILTERLIST_PER_PAGE;
		int index = 0;

		String enabledHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.enabled");
		String nameHeader = TextFormatting.UNDERLINE + TextUtils.translate("tektopiaBook.headers.name"); 

		maxLength[0] += Font.small.getStringWidth(enabledHeader);

		Font.small.printRight(enabledHeader, this.xPagePortrait + SUBPAGE_LEFT_X + maxLength[0], y);
		Font.small.printLeft(nameHeader, this.xPagePortrait + SUBPAGE_LEFT_X + indentX + maxLength[0], y);

		y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;

		if (resident.getAiFiltersCount() > 0) {

			for (Entry<String, Boolean> aiFilter : resident.getAiFilters().entrySet()) {
				
				if (index >= startIndex && index < startIndex + AIFILTERLIST_PER_PAGE) {
					String aiFilterName = getAiFilterName(aiFilter.getKey());
					String aiFilterValue = aiFilter.getValue() ? TextUtils.SYMBOL_GREENTICK : TextUtils.SYMBOL_REDCROSS;

					Font.small.printCentered(aiFilterValue, this.xPagePortrait + SUBPAGE_LEFT_X + (maxLength[0] / 2), y);
					Font.small.printLeft(aiFilterName, this.xPagePortrait + SUBPAGE_LEFT_X + indentX + maxLength[0], y); 

					y += Font.small.fontRenderer.FONT_HEIGHT + LINE_SPACE_Y;
				}
				index++;
			}
		}
	}

	private void actionPerformed(GuiBookmark bookmark) {
		if (bookmark == null)
			return;

		// set the page to the bookmark page
		setLeftPageIndex(bookmark.getPageIndex());
		setSubPage(null);
		clearPageHistory();
		this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(ModSounds.BOOK_PAGE_TURN, 1.0F));
	}

	private void actionPerformed(GuiButton button) {
		if (button == null)
			return;

		switch (button.getKey()) {
		case BUTTON_KEY_BACK:
			setSubPage(null);
			setLeftPageIndex(popPageHistory());
			this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(ModSounds.BOOK_PAGE_TURN, 1.0F));
			return;
		case BUTTON_KEY_PREVIOUSPAGE:
			setSubPage(null);
			setLeftPageIndex(this.leftPageIndex - 2);
			this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(ModSounds.BOOK_PAGE_TURN, 1.0F));
			return;
		case BUTTON_KEY_NEXTPAGE:
			setSubPage(null);
			setLeftPageIndex(this.leftPageIndex + 2);
			this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(ModSounds.BOOK_PAGE_TURN, 1.0F));
			return;
		case BUTTON_KEY_STARTBOOK:
			setSubPage(null);
			setLeftPageIndex(0);
			this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(ModSounds.BOOK_PAGE_TURN, 1.0F));
			return;
		case BUTTON_KEY_ENDBOOK:
			setSubPage(null);
			setLeftPageIndex(this.pages.size());
			this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(ModSounds.BOOK_PAGE_TURN, 1.0F));
			return;

		case BUTTON_KEY_CLOSE:
			setSubPage(null);
			this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(ModSounds.BOOK_PAGE_TURN, 1.0F));
			return;
		case BUTTON_KEY_PREVIOUSSUBPAGE:
			setSubPageIndex(this.subPageIndex - 1);
			this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(ModSounds.BOOK_PAGE_TURN, 1.0F));
			return;
		case BUTTON_KEY_NEXTSUBPAGE:
			setSubPageIndex(this.subPageIndex + 1);
			this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(ModSounds.BOOK_PAGE_TURN, 1.0F));
			return;

		case BUTTON_KEY_AIFILTER:
			setSubPage(button.getButtonData());
			this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(ModSounds.BOOK_PAGE_TURN, 1.0F));
			return;

		case BUTTON_KEY_SHOWMAPBOUNDARIES:
			showMapBoundaries = !showMapBoundaries;
			return;
		case BUTTON_KEY_SHOWMAPENEMIES:
			showMapEnemies = !showMapEnemies;
			return;
		case BUTTON_KEY_SHOWMAPHOMES:
			showMapHomes = !showMapHomes;
			return;
		case BUTTON_KEY_SHOWMAPVISITORS:
			showMapVisitors = !showMapVisitors;
			return;
		case BUTTON_KEY_SHOWMAPPLAYER:
			showMapPlayer = !showMapPlayer;
			return;
		case BUTTON_KEY_SHOWMAPRESIDENTS:
			showMapResidents = !showMapResidents;
			return;
		case BUTTON_KEY_SHOWMAPSTRUCTURES:
			showMapStructures = !showMapStructures;
			return;
		case BUTTON_KEY_SHOWMAPTOWNHALL:
			showMapTownHall = !showMapTownHall;
			return;
		default:
			break;
		}

		if (!isSubPageOpen()) {
			if (button instanceof GuiHyperlink) {
				GuiHyperlink hyperlink = (GuiHyperlink)button;

				String[] linkData = null;

				switch (button.getKey()) {
				case BUTTON_KEY_MAPLINK:
					linkData = getHyperlinkDataParts(hyperlink.getLinkData());
					
					for (GuiPage page : this.pages) {
						if (page.getGuiPageType() == GuiPageType.MAP) {
							if (linkData[0].equals(GuiPageType.RESIDENT.name()) || linkData[0].equals(GuiPageType.VISITOR.name()) || linkData[0].equals(GuiPageType.ENEMY.name())) {
								pushPageHistory();
								String[] pageData = getPageKeyParts(linkData[1]);
								this.villageData.setEntityId(Integer.parseInt(pageData[0]));
								setLeftPageIndex(page.getPageIndex());
								this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(ModSounds.BOOK_PAGE_TURN, 1.0F));
								break;
							}
							
							if (linkData[0].equals(GuiPageType.STRUCTURE.name()) || linkData[0].equals(GuiPageType.HOME.name())) {
								pushPageHistory();
								String[] pageData = getPageKeyParts(linkData[1]);
								this.villageData.setStructureId(Integer.parseInt(pageData[0]));
								setLeftPageIndex(page.getPageIndex());
								this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(ModSounds.BOOK_PAGE_TURN, 1.0F));
								break;
							}
						}
					}
					break;
				case BUTTON_KEY_HOMELINK:
					linkData = getHyperlinkDataParts(hyperlink.getLinkData());

					for (GuiPage page : this.pages) {
						if (page.getGuiPageType() == GuiPageType.HOME && page.getDataKey().equals(linkData[1])) {
							pushPageHistory();
							String[] pageData = getPageKeyParts(linkData[1]);
							this.villageData.setStructureId(Integer.parseInt(pageData[0]));
							setLeftPageIndex(page.getPageIndex());
							this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(ModSounds.BOOK_PAGE_TURN, 1.0F));
							break;
						}

						if (page.getGuiPageType() == GuiPageType.HOMETYPE && page.getDataKey().equals(linkData[1])) {
							pushPageHistory();
							setLeftPageIndex(page.getPageIndex());
							this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(ModSounds.BOOK_PAGE_TURN, 1.0F));
							break;
						}
					}
					break;
				case BUTTON_KEY_PROFESSIONLINK:
					linkData = getHyperlinkDataParts(hyperlink.getLinkData());

					for (GuiPage page : this.pages) {
						if (page.getGuiPageType() == GuiPageType.PROFESSIONTYPE && page.getDataKey().equals(linkData[1])) {
							pushPageHistory();
							setLeftPageIndex(page.getPageIndex());
							this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(ModSounds.BOOK_PAGE_TURN, 1.0F));
							break;
						}
					}
					break;
				case BUTTON_KEY_RESIDENTLINK:
					linkData = getHyperlinkDataParts(hyperlink.getLinkData());

					for (GuiPage page : this.pages) {
						if (page.getGuiPageType() == GuiPageType.RESIDENT && page.getDataKey().equals(linkData[1])) {
							pushPageHistory();
							String[] pageData = getPageKeyParts(linkData[1]);
							this.villageData.setEntityId(Integer.parseInt(pageData[0]));
							setLeftPageIndex(page.getPageIndex());
							this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(ModSounds.BOOK_PAGE_TURN, 1.0F));
							break;
						}
					}
					break;
				case BUTTON_KEY_STATISTICLINK:
					linkData = getHyperlinkDataParts(hyperlink.getLinkData());

					for (GuiPage page : this.pages) {
						if (page.getGuiPageType() == GuiPageType.STATS && page.getDataKey().equals(linkData[1])) {
							pushPageHistory();
							setLeftPageIndex(page.getPageIndex());
							this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(ModSounds.BOOK_PAGE_TURN, 1.0F));
							break;
						}
					}
					break;
				case BUTTON_KEY_STRUCTURELINK:
					linkData = getHyperlinkDataParts(hyperlink.getLinkData());

					for (GuiPage page : this.pages) {
						if (page.getGuiPageType() == GuiPageType.STRUCTURE && page.getDataKey().equals(linkData[1])) {
							pushPageHistory();
							String[] pageData = getPageKeyParts(linkData[1]);
							this.villageData.setStructureId(Integer.parseInt(pageData[0]));
							setLeftPageIndex(page.getPageIndex());
							this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(ModSounds.BOOK_PAGE_TURN, 1.0F));
							break;
						}

						if (page.getGuiPageType() == GuiPageType.STRUCTURETYPE && page.getDataKey().equals(linkData[1])) {
							pushPageHistory();
							setLeftPageIndex(page.getPageIndex());
							this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(ModSounds.BOOK_PAGE_TURN, 1.0F));
							break;
						}
					}
					break;
				case BUTTON_KEY_VISITORLINK:
					linkData = getHyperlinkDataParts(hyperlink.getLinkData());

					for (GuiPage page : this.pages) {
						if (page.getGuiPageType() == GuiPageType.VISITOR && page.getDataKey().equals(linkData[1])) {
							pushPageHistory();
							String[] pageData = getPageKeyParts(linkData[1]);
							this.villageData.setEntityId(Integer.parseInt(pageData[0]));
							setLeftPageIndex(page.getPageIndex());
							this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(ModSounds.BOOK_PAGE_TURN, 1.0F));
							break;
						}
					}
					break;
				}
			}

			if (button instanceof GuiMapMarker) {
				GuiMapMarker mapMarker = (GuiMapMarker)button;

				switch (mapMarker.getMarkerType()) {
				case PLAYER:
					break;
				case RESIDENT:
				case VISITOR:
				case ENEMY:
					String[] keyParts = getPageKeyParts(mapMarker.key);

					for (GuiPage page : this.pages) {
						if (page.getDataKey().equals(mapMarker.key)) {
							pushPageHistory();
							this.villageData.setEntityId(Integer.parseInt(keyParts[0]));
							setLeftPageIndex(page.getPageIndex());
							this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(ModSounds.BOOK_PAGE_TURN, 1.0F));
							break;
						}
					}
					break;
				case HOME:
					for (GuiPage page : this.pages) {
						if (page.getGuiPageType() == GuiPageType.HOME && page.getDataKey().equals(mapMarker.key)) {
							pushPageHistory();
							this.villageData.setFramePosition(mapMarker.getPosition());
							setLeftPageIndex(page.getPageIndex());
							this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(ModSounds.BOOK_PAGE_TURN, 1.0F));
							break;
						}
					}
					break;
				case STRUCTURE:
				case TOWNHALL:
					for (GuiPage page : this.pages) {
						if (page.getGuiPageType() == GuiPageType.STRUCTURETYPE && page.getDataKey().equals(mapMarker.key)) {
							pushPageHistory();
							this.villageData.setFramePosition(mapMarker.getPosition());
							setLeftPageIndex(page.getPageIndex());
							this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(ModSounds.BOOK_PAGE_TURN, 1.0F));
							break;
						}
					}
					break;
				default:
					break;
				}
			}
		}
	}

	private static String formatBlockPos(BlockPos blockPos) {
		if (blockPos == null) {
			return "";
		}

		return blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ();
	}

	private static String formatResidentLevel(int level, int baseLevel, boolean showBaseLevel, boolean showPrefix) {
		String residentLevel = "";
		if (level > 0) {
			if (showPrefix) {
				residentLevel += TextUtils.translate("tektopiaBook.residents.level") + " ";
			}

			if (level > baseLevel) {
				residentLevel += "" + TextFormatting.DARK_GREEN + level + TextFormatting.RESET + (showBaseLevel ? " (" + baseLevel + ")" : "");   		
			} else {
				residentLevel += "" + level;
			}
		}
		return residentLevel;
	}

	private static String formatResidentName(Boolean isMale, String name, Boolean includeGenderSymbol) {
		String residentName = (isMale ? TextFormatting.BLUE : TextFormatting.LIGHT_PURPLE) + stripTextFormatting(name);
		if (includeGenderSymbol)
			residentName += " " + (isMale ? TextUtils.SYMBOL_MALE : TextUtils.SYMBOL_FEMALE);
		return residentName;
	}

	private static String formatResidentStatistic(int value, int maxValue, boolean showMaxLevel) {
		int percentage = (100 / maxValue) * value;
		if (percentage <= 10) {
			return "" + TextFormatting.DARK_RED + value + TextFormatting.RESET + (showMaxLevel ? " (" + maxValue + ")" : "");
		}
		else if (percentage <= 50) {
			return "" + TextFormatting.GOLD + value + TextFormatting.RESET + (showMaxLevel ? " (" + maxValue + ")" : "");
		}
		else {
			return "" + TextFormatting.DARK_GREEN + value + TextFormatting.RESET + (showMaxLevel ? " (" + maxValue + ")" : "");
		}
	}

	private static String formatResidentStatistic(float value, float maxValue, boolean showMaxLevel) {
		float percentage = (100 / maxValue) * value;
		if (percentage <= 10) {
			return "" + TextFormatting.DARK_RED + value + TextFormatting.RESET + (showMaxLevel ? " (" + maxValue + ")" : "");
		}
		else if (percentage <= 50) {
			return "" + TextFormatting.GOLD + value + TextFormatting.RESET + (showMaxLevel ? " (" + maxValue + ")" : "");
		}
		else {
			return "" + TextFormatting.DARK_GREEN + value + TextFormatting.RESET + (showMaxLevel ? " (" + maxValue + ")" : "");
		}
	}

	private static String formatStatisticsRange(int lowValue, int highValue, int maxValue) {
		float percentage = (100 / maxValue) * highValue;
		if (percentage <= 10) {
			return "" + TextFormatting.DARK_RED + lowValue + (lowValue != highValue ? " - " + highValue : "");
		}
		else if (percentage <= 50) {
			return "" + TextFormatting.GOLD + lowValue + (lowValue != highValue ? " - " + highValue : "");
		}
		else {
			return "" + TextFormatting.DARK_GREEN + lowValue + (lowValue != highValue ? " - " + highValue : "");
		}
	}

	private static String getAiFilterName(String aiFilerKey) {
		if (StringUtils.isNullOrWhitespace(aiFilerKey))
			return "";

		return TextUtils.translate("ai.filter." + aiFilerKey, new Object[0]);
	}

	private static String getAiTaskName(String aiTaskKey) {
		if (StringUtils.isNullOrWhitespace(aiTaskKey))
			return "";

		String key = aiTaskKey;
		String result = "";

		if (key.startsWith("EntityAI")) {
			key = key.substring("EntityAI".length());
		}

		String delimiter = "";
		for (int index = 0; index < key.length(); index++) {
			char value = key.charAt(index);

			if (Character.isUpperCase(value)) {
				result += delimiter;
				delimiter = " ";
			}

			result += value;
		}

		result = result.replaceAll("Generic", "");
		result = result.replaceAll("Idle Check", "Idle");
		result = result.replaceAll("[0-9]", "");
		result = result.replaceAll("  ", " ");

		return result.trim();
	}

	private static String getPageKey(String pageKey, int pageNumber) {
		if (pageKey == null)
			pageKey = "";

		return pageKey + "@" + pageNumber;
	}

	private static String[] getPageKeyParts(String pageKey) {
		String[] result = new String[] { "", "0" };

		if (!StringUtils.isNullOrWhitespace(pageKey)) {
			if (pageKey.contains("@")) {
				String[] parts = pageKey.split("@");
				if (parts.length > 0)
					result[0] = parts[0];
				if (parts.length > 1)
					result[1] = parts[1];
			} else {
				result[0] = pageKey;
			}
		}

		return result;
	}

	private static String getStatisticsPageKey(String pageKey, int rangekey, int pageNumber) {
		if (pageKey == null)
			pageKey = "";

		return pageKey + "@" + rangekey + "@" + pageNumber;
	}

	private static String[] getStatisticsPageKeyParts(String pageKey) {
		String[] result = new String[] { "", "0", "0" };

		if (!StringUtils.isNullOrWhitespace(pageKey)) {
			if (pageKey.contains("@")) {
				String[] parts = pageKey.split("@");
				if (parts.length > 0)
					result[0] = parts[0];
				if (parts.length > 1)
					result[1] = parts[1];
				if (parts.length > 2)
					result[2] = parts[2];
			} else {
				result[0] = pageKey;
			}
		}

		return result;
	}

	private static String getStructureTypeName(VillageStructureType structureType) {
		if (structureType == null || structureType.itemStack == null) {
			return "";
		}

		return structureType.itemStack.getDisplayName();
	}

	private static String getTypeName(String typeName) {
		if (typeName == null) {
			return "";
		}

		String result = TextUtils.translate("entity." + typeName.toLowerCase() + ".name");
		return result == null || result.trim().isEmpty() ? typeName : result;
	}

	private void setLeftPageIndex(int pageIndex) {
		if (pageIndex < 0)
			pageIndex = 1;
		if (pageIndex >= this.pages.size())
			pageIndex = this.pages.size() - 1;

		boolean isEven = pageIndex % 2 == 0;

		this.leftPageIndex = isEven ? pageIndex : pageIndex - 1;    	
	}

	private static String stripTextFormatting(String value) {
		if (StringUtils.isNullOrWhitespace(value)) {
			return "";
		}

		return TextFormatting.getTextWithoutFormattingCodes(value);
	}

	private static String getHyperlinkData(GuiPageType pageType, String pageKey) {
		if (pageType == null)
			pageType = GuiPageType.BLANK;
		if (pageKey == null)
			pageKey = "";

		return pageType.name() + ":" + pageKey;
	}

	private static String[] getHyperlinkDataParts(String linkData) {
		String[] result = new String[] { "", "" };

		if (!StringUtils.isNullOrWhitespace(linkData)) {
			if (linkData.contains(":")) {
				String[] parts = linkData.split(":");
				if (parts.length > 0)
					result[0] = parts[0];
				if (parts.length > 1)
					result[1] = parts[1];
			} else {
				result[0] = linkData;
			}
		}

		return result;
	}

	private static String getProfessionDetailPageKey(String professionType) {
		return getProfessionDetailPageKey(professionType, 0);
	}

	private static String getProfessionDetailPageKey(String professionType, int page) {
		return getPageKey(professionType.toUpperCase(), page);
	}

	private static String getResidentDetailPageKey(int residentId) {
		return getPageKey("" + residentId, 0);
	}

	private static String getResidentDetailPageKey(int residentId, int page) {
		return getPageKey("" + residentId, page);
	}

	private static String getStructureDetailPageKey(int structureId) {
		return getStructureDetailPageKey(structureId, 0);
	}

	private static String getStructureDetailPageKey(int structureId, int page) {
		return getPageKey("" + structureId, page);
	}

	private static String getStructureTypePageKey(VillageStructureType structureType) {
		return getStructureTypePageKey(structureType, 0);
	}

	private static String getStructureTypePageKey(VillageStructureType structureType, int page) {
		return getPageKey(structureType.name(), page);
	}

	private Boolean isSubPageOpen() {
		return !StringUtils.isNullOrWhitespace(this.subPageKey);
	}

	private void clearPageHistory() {
		this.pageHistory.clear();
	}

	private int popPageHistory() {
		if (this.pageHistory.empty())
			return this.leftPageIndex;

		return this.pageHistory.pop();
	}

	private void pushPageHistory() {
		pushPageHistory(this.leftPageIndex);
	}

	private void pushPageHistory(int pageIndex) {
		if (pageIndex < 0)
			return;

		this.pageHistory.push(pageIndex);

		while (this.pageHistory.size() > 10) {
			this.pageHistory.remove(0);
		}
	}

	public static String getAiFilterSubPageKey(int residentId) {
		return getSubPageKey(SUBPAGE_KEY_AIFILTER, "" + residentId);
	}

	public static String getSubPageKey(String subPageKey, String subPageData) {
		return subPageKey + ":" + subPageData;
	}

	public static String[] getSubPageKeyParts(String subPageKey) {
		String[] result = new String[] { "", "" };

		if (!StringUtils.isNullOrWhitespace(subPageKey)) {
			if (subPageKey.contains(":")) {
				String[] parts = subPageKey.split(":");
				if (parts.length > 0)
					result[0] = parts[0];
				if (parts.length > 1)
					result[1] = parts[1];
			} else {
				result[0] = subPageKey;
			}
		}

		return result;
	}

	private void setSubPage(String subPageKey) {
		this.subPageKey = subPageKey;

		setSubPageCount(1);
		setSubPageIndex(1);
	}

	private void setSubPageIndex(int subPageIndex) {
		if (subPageIndex < 1)
			subPageIndex = 1;
		if (subPageIndex > this.subPageCount)
			subPageIndex = this.subPageCount;
		
		this.subPageIndex = subPageIndex;
	}
	
	private void setSubPageCount(int subPageCount) {
		if (subPageCount < 1)
			subPageCount = 1;
		
		this.subPageCount = subPageCount;
		
		if (subPageIndex > this.subPageCount)
			subPageIndex = this.subPageCount;
	}

}
